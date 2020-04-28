package com.spark.bitrade.biz.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spark.bitrade.api.SuperPartnerCommunityApi;
import com.spark.bitrade.biz.IBenefitsOrderService;
import com.spark.bitrade.biz.IPayService;
import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.BenefitsOrderForm;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.producer.Producer;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.utils.KeyGenerator;
import com.spark.bitrade.utils.MemberUtil;
import com.spark.bitrade.vo.CurrentAmountVo;
import com.spark.bitrade.vo.MQMessage;
import com.spark.bitrade.vo.MemberBenefitsExtendsVo;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 10:30
 */
@Service
@Slf4j
public class BenefitsOrderServiceImpl implements IBenefitsOrderService {

    @Autowired
    private MemberBenefitsExtendsService benefitsExtendsService;

    @Autowired
    private MemberBenefitsOrderService benefitsOrderService;

    @Autowired
    private SuperPartnerCommunityApi partnerCommunityApi;

    @Autowired
    private MemberRequireConditionService requireConditionService;

    @Autowired
    private IPayService payService;

    @Autowired
    private Producer producer;

    @Autowired
    private RocketMQCfg rocketMQCfg;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMemberVip(Long memberId, BenefitsOrderForm benefitsOrderForm) throws ParseException {
        // 创建开通订单
        MemberBenefitsOrder order = new MemberBenefitsOrder();
        MemberUtil.validataOpenVipForm(benefitsOrderForm);
        // 获取原会员等级
        MemberBenefitsExtends benefits = benefitsExtendsService.getMemberrBenefitsByMemberId(memberId);
        List<MemberRequireCondition> newRecords = requireConditionService.getRequireConditionBylevelId(benefitsOrderForm.getVipLevel());
        Boolean amountBoolean = cheakAmount(memberId, benefitsOrderForm);
        log.info("=============== 费用是否相等 ================ {}", amountBoolean);
        if (!amountBoolean) {
            throw new MessageCodeException(MemberMsgCode.PRICE_MISTAKE);
        }
        Date temp = null;
        if (benefits != null && (benefits.getStartTime().compareTo(benefits.getEndTime()) == 0)) {
            // 首次开通 redis 存标识
            redisTemplate.opsForValue().set("member:firstOpening:" + memberId, memberId);
        }
        if (benefits != null) {
            // 会员原数据存在, 相应数据存入订单
            order.setOriginLevel(benefits.getLevelId());
            order.setMemberExtendId(benefits.getId());
            benefits.setLevelId(benefitsOrderForm.getVipLevel());
            if (benefitsOrderForm.getOperateType() == OperateTypeEnum.UPGRADE.getCode()) {
                // 升级操作 不改变到期时间
                benefits.setUpdateTime(new Date());
            } else if (benefitsOrderForm.getOperateType() == OperateTypeEnum.RENEW.getCode()) {
                // 续费
                temp = benefits.getEndTime();
                benefits.setEndTime(MemberUtil.plusDay(benefitsOrderForm.getDuration(), benefits.getEndTime()));
            } else {
                // 开通
                benefits.setStartTime(new Date());
                benefits.setEndTime(MemberUtil.plusDay(benefitsOrderForm.getDuration(), new Date()));
            }
        }
        // 生成流水号
        long orderNumber = IdWorker.getId();
        log.info("--- order number is --- {}", orderNumber);
        order.setStartTime(benefits.getStartTime());
        order.setEndTime(benefits.getEndTime());
        order.setPayType(benefitsOrderForm.getPayType());
        order.setOrderNumber(String.valueOf(orderNumber));
        order.setOperateType(benefitsOrderForm.getOperateType());
        // 新会员等级
        order.setDestLevel(benefitsOrderForm.getVipLevel());
        order.setAppId(benefitsOrderForm.getAppId());
        order.setUnit(benefitsOrderForm.getUnit());
        order.setAmount(benefitsOrderForm.getAmount());
        Long lid = 0L;
        Long operType = 99L;
        // 锁仓续费
        if (benefitsOrderForm.getOperateType() == OperateTypeEnum.RENEW.getCode() && benefitsOrderForm.getPayType() == PayTypeEnum.LOCK.getCode()) {
            List<MemberBenefitsOrder> list = benefitsOrderService.findBenefitsOrderByMemberIdAndOperate(benefits.getId());
            if (list != null && list.size() > 0) {
                lid = list.get(0).getLockDetailId();
            }
            operType = 0L;
        }
        // 锁仓升级
        if (benefitsOrderForm.getOperateType() == OperateTypeEnum.UPGRADE.getCode() && benefitsOrderForm.getPayType() == PayTypeEnum.LOCK.getCode()) {
            List<MemberBenefitsOrder> list = benefitsOrderService.findBenefitsOrderByMemberIdAndOperate(benefits.getId());
            if (list != null && list.size() > 0) {
                lid = list.get(0).getLockDetailId();
            }
            operType = 1L;
        }
        Long locukId = payService.purchaseVipAmount(orderNumber, memberId, benefitsOrderForm.getUnit(),
                benefitsOrderForm.getPayType(), benefitsOrderForm.getAmount(), benefitsOrderForm.getDuration(), lid, operType);
        order.setLockDetailId(locukId);
        order.setPayTime(new Date());
        // 升级设置订单属性
        if (benefitsOrderForm.getOperateType() == OperateTypeEnum.UPGRADE.getCode()) {
            benefits.setStartTime(order.getPayTime());
            order.setStartTime(order.getPayTime());
        }
        // 续费设置订单属性
        if (benefitsOrderForm.getOperateType() == OperateTypeEnum.RENEW.getCode()) {
            order.setStartTime(temp);
        }
        boolean saveOrder = benefitsOrderService.save(order);
        Map<String, Object> map = redisTemplate.opsForHash().entries(KeyGenerator.getMemberOpenVipAoumt(memberId));
        // 当前购买viP的价格存入   redis
        BigDecimal putRedis = null;
        Integer days = 0;
        if (benefitsOrderForm.getOperateType() != OperateTypeEnum.RENEW.getCode()) {
            for (MemberRequireCondition newRecord : newRecords) {
                if (newRecord.getType() == PayTypeEnum.BUY.getCode() && benefitsOrderForm.getPayType() == PayTypeEnum.BUY.getCode()) {
                    putRedis = newRecord.getQuantity();
                    days = newRecord.getDuration();
                    if (newRecord.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        putRedis = putRedis.multiply(BigDecimal.ONE.subtract(newRecord.getDiscount()));
                    }
                }  else if (newRecord.getType() == PayTypeEnum.LOCK.getCode() && benefitsOrderForm.getPayType() == PayTypeEnum.LOCK.getCode()) {
                    putRedis = newRecord.getQuantity();
                    if (newRecord.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        putRedis = putRedis.multiply(BigDecimal.ONE.multiply(BigDecimal.ONE.subtract(newRecord.getDiscount())));
                    }
                }
            }
            if (map == null || map.size() <= 0) {
                Map<String, Object> saveMap = new HashMap<>();
                saveMap.put("payType", order.getPayType());
                saveMap.put("amount", putRedis);
                saveMap.put("days", days);
                redisTemplate.opsForHash().putAll(KeyGenerator.getMemberOpenVipAoumt(memberId), saveMap);
            } else {
                map.put("amount", putRedis);
                map.put("days", days);
                redisTemplate.opsForHash().putAll(KeyGenerator.getMemberOpenVipAoumt(memberId), map);
            }
        }
        // 存储校验
        AssertUtil.isTrue(saveOrder, MemberMsgCode.SAVE_BENEFITS_ORDER_FAILED);
        // 对开通用户有返佣广播消息
        if (benefitsOrderForm.getOperateType() == OperateTypeEnum.OPENING.getCode() && redisTemplate.opsForValue().get("member:firstOpening:" + memberId) != null) {
            redisTemplate.delete("member:firstOpening:" + memberId);
            MQMessage message = new MQMessage();
            message.setTopic(rocketMQCfg.getMemberTopic());
            message.setTag(rocketMQCfg.getMemberTag());
            ObjectMapper objectMapper = new ObjectMapper();
            String orderToJson = null;
            try {
                orderToJson = objectMapper.writeValueAsString(order);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            message.setMessage(orderToJson);
            log.info("\n\n\n=========== 对 MQ 广播消息 =========> {}", message);
            String mqid = producer.send(message);
            order.setOrderMqId(mqid);
            benefitsOrderService.saveOrUpdate(order);
        }
        // 付款, 消息广播后 更新数据
        if (benefits != null) {
            benefitsExtendsService.saveOrUpdate(benefits);
        } else {
            benefits.setLevelId(benefitsOrderForm.getVipLevel());
            benefitsExtendsService.saveOrUpdate(benefits);
        }

        log.info("\n ========== 开始更新缓存 ===========");
        // 更新缓存
        benefitsExtendsService.updateMemberrBenefitsCache(benefits);
    }

    /**
     * 校验费用
     *
     * @return
     */
    private Boolean cheakAmount(Long memberId, BenefitsOrderForm fitsOrderForm) throws ParseException {
        log.info("----- 开始校验费用 ------");
        // 新费用
        List<MemberRequireCondition> newRecords = requireConditionService.getRequireConditionBylevelId(fitsOrderForm.getVipLevel());
        MemberBenefitsExtends member = benefitsExtendsService.getMemberrBenefitsByMemberId(memberId);
        if (member == null) {
            throw new MessageCodeException(MemberMsgCode.CANNOT_OPENING);
        }
        BigDecimal oldAmount = null; // 当前vip等级的钱
        BigDecimal newAmount = null; // 新vip等级的钱

        // 开通会员操作
        if (fitsOrderForm.getOperateType() == OperateTypeEnum.OPENING.getCode()) {
            for (MemberRequireCondition record : newRecords) {
                // 购买
                if (fitsOrderForm.getPayType() == PayTypeEnum.BUY.getCode() && record.getType() == PayTypeEnum.BUY.getCode()) {
                    int temp = fitsOrderForm.getDuration() / record.getDuration();
                    newAmount = record.getQuantity().multiply(new BigDecimal(temp));
                    // 新vip等级有折扣优惠
                    if (record.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        // 新价格 = 新价格 * （1 - 录入折扣）
                        newAmount = newAmount.multiply(new BigDecimal(BigInteger.ONE).subtract(record.getDiscount()));
                    }
                }
                // 锁仓
                if (fitsOrderForm.getPayType() == PayTypeEnum.LOCK.getCode() && record.getType() == PayTypeEnum.LOCK.getCode()) {
                    newAmount = record.getQuantity();
                    if (record.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        newAmount = newAmount.multiply(new BigDecimal(BigInteger.ONE).subtract(record.getDiscount()));
                    }
                }
            }
            log.info("\n\n\n\n======= 开通、续费 会员的方式为: {}, 价格为：{}", fitsOrderForm.getPayType(), newAmount);
            return newAmount.compareTo(fitsOrderForm.getAmount()) == 0 ? true : false;
        }

        // 会员续费操作
        if (fitsOrderForm.getOperateType() == OperateTypeEnum.RENEW.getCode()) {
            for (MemberRequireCondition record : newRecords) {
                // 购买
                if (fitsOrderForm.getPayType() == PayTypeEnum.BUY.getCode() && record.getType() == PayTypeEnum.BUY.getCode()) {
                    int temp = fitsOrderForm.getDuration() / record.getDuration();
                    newAmount = record.getQuantity().multiply(new BigDecimal(temp));
                    // 新vip等级有折扣优惠
                    if (record.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        // 新价格 = 新价格 * （1 - 录入折扣）
                        newAmount = newAmount.multiply(new BigDecimal(BigInteger.ONE).subtract(record.getDiscount()));
                    }
                    log.info("\n\n\n\n======= 开通、续费 会员的方式为: {}, 价格为：{}", fitsOrderForm.getPayType(), newAmount);
                    return newAmount.compareTo(fitsOrderForm.getAmount()) == 0 ? true : false;
                }
                // 锁仓
                if (fitsOrderForm.getPayType() == PayTypeEnum.LOCK.getCode() && record.getType() == PayTypeEnum.LOCK.getCode()) {
                    return BigDecimal.ZERO.compareTo(fitsOrderForm.getAmount()) == 0 ? true : false;
                }
            }
        }
        // 升级操作
        if (fitsOrderForm.getOperateType() == OperateTypeEnum.UPGRADE.getCode()) {
            // 计算公式： 剩余时长 * (目标vip等级单日价格 - 当前vip等级单日价格)
            // 剩余时长
            Integer surplus = MemberUtil.daysBetween(new Date(), member.getEndTime());
            for (MemberRequireCondition record : newRecords) {
                if (fitsOrderForm.getPayType() == PayTypeEnum.BUY.getCode() && record.getType() == PayTypeEnum.BUY.getCode()) {
                    // 目标vip日价格
                    newAmount = record.getQuantity().divide(new BigDecimal(record.getDuration()), 4, RoundingMode.HALF_UP);
                    if (record.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        newAmount = newAmount.multiply(new BigDecimal(BigInteger.ONE).subtract(record.getDiscount()));
                    }
                }
                // 锁仓方式
                if (fitsOrderForm.getPayType() == PayTypeEnum.LOCK.getCode() && record.getType() == PayTypeEnum.LOCK.getCode()) {
                    // 锁仓的费用
                    newAmount = record.getQuantity();
                    if (record.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        newAmount = newAmount.multiply(new BigDecimal(BigInteger.ONE).subtract(record.getDiscount()));
                    }
                }
            }
            Map<String, Object> map = redisTemplate.opsForHash().entries(KeyGenerator.getMemberOpenVipAoumt(memberId));
            if (map == null || map.size() <= 0) {
                throw new MessageCodeException(MemberMsgCode.PRICE_MISTAKE);
            }
            // 购买
            BigDecimal result = BigDecimal.ZERO;
            if (fitsOrderForm.getPayType() == PayTypeEnum.BUY.getCode()) {
                Integer oldDays = (Integer) map.get("days");
                BigDecimal oldCountAmount = (BigDecimal) map.get("amount");
                oldAmount = oldCountAmount.divide(new BigDecimal(oldDays), 4, RoundingMode.HALF_UP);
                result = new BigDecimal(surplus).multiply(newAmount.subtract(oldAmount));
            } else {
                // 锁仓
                oldAmount = (BigDecimal) map.get("amount");
                result = newAmount.subtract(oldAmount);
            }
            log.info("\n\n\n\n======= 操作会员的方式为: {}, 价格为：{}", fitsOrderForm.getPayType(), newAmount);
            return result.setScale(1, BigDecimal.ROUND_HALF_DOWN).compareTo(fitsOrderForm.getAmount().setScale(1, BigDecimal.ROUND_HALF_DOWN)) == 0 ? true : false;
        }
        return false;
    }


    @Override
    public MemberBenefitsExtendsVo getCurrentMemberVip(Long memberId) {
        MemberBenefitsExtendsVo vo = new MemberBenefitsExtendsVo();
        MemberBenefitsExtends benefits = benefitsExtendsService.getMemberrBenefitsByMemberId(memberId);
        if (benefits == null) {
            benefits = new MemberBenefitsExtends();
            Date newDate = new Date();
            benefits.setLevelId(1);
            benefits.setStartTime(newDate);
            benefits.setEndTime(newDate);
            benefits.setUpdateTime(newDate);
            benefits.setCreateTime(newDate);
            benefits.setMemberId(memberId);
            benefitsExtendsService.save(benefits);
        }
        BeanUtils.copyProperties(benefits, vo);
        if (benefits != null) {
            log.info("\n ============== 获取社区人数 ===============");
            String apiKey = HttpRequestUtil.getApiKey();
            // 获取社区人数
            MessageRespResult<SuperPartnerCommunity> community = partnerCommunityApi.findPartnerCommunityNumber(0, apiKey);
            if (community.getData() == null) {
                vo.setCommunitySize(0);
            } else {
                log.info("\n 社区人数为 =========>>>>========== {} ", community.getData().getPeopleCount());
                log.info("\n 社区名字为 =========>>>>========== {} ", community.getData().getCommunityName());
                vo.setCommunitySize(community.getData().getPeopleCount());
                vo.setCommunityName(community.getData().getCommunityName());
            }
        }

        Map<String, Object> map = redisTemplate.opsForHash().entries(KeyGenerator.getMemberOpenVipAoumt(memberId));
        if (map == null || map.size() <= 0 && benefits.getLevelId() != MemberLevelTypeEnum.AGENT.getCode()) {
            vo.setOperationType(99);
        } else {
            vo.setOpenVipAmount((BigDecimal) map.get("amount"));
            vo.setOperationType((Integer) map.get("payType"));
            vo.setOpenVipDays((Integer) map.get("days"));
        }
        return vo;
    }

    @Override
    public IPage<MemberBenefitsOrder> getMemberBenefitsOrderHistory(Long memberId, PageParam param) throws ParseException {
        MemberBenefitsExtends member = benefitsExtendsService.getMemberrBenefitsByMemberId(memberId);
        IPage<MemberBenefitsOrder> page = benefitsOrderService.findMemberBenefitsOrdersList(member.getId(), param);
        List<MemberBenefitsOrder> list = page.getRecords();
        String language = HttpRequestUtil.getHttpServletRequest().getHeader("language");
        for (MemberBenefitsOrder order : list) {
            if ("zh_CN".equals(language)) {
                // 设置会员等级
                if (order.getOperateType() == OperateTypeEnum.UPGRADE.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(new Date(), order.getEndTime()));
                    order.setLevelName(MemberUtil.getValueByCode(order.getOriginLevel()) + OperateTypeEnum.UPGRADE.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.OPENING.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.OPENING.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.RENEW.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.RENEW.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                }
            } else if ("en_US".equals(language)) {
                if (order.getOperateType() == OperateTypeEnum.UPGRADE.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(new Date(), order.getEndTime()));
                    order.setLevelName(MemberUtil.getValueByCode(order.getOriginLevel()) + OperateTypeEnum.UPGRADE_EN.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.OPENING.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.OPENING_EN.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.RENEW.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.RENEW_EN.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                }
            } else if ("ko_KR".equals(language)) {
                if (order.getOperateType() == OperateTypeEnum.UPGRADE.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(new Date(), order.getEndTime()));
                    order.setLevelName(MemberUtil.getValueByCode(order.getOriginLevel()) + OperateTypeEnum.UPGRADE_KO.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.OPENING.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.OPENING_KO.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.RENEW.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.RENEW_KO.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                }
            } else if ("zh_HK".equals(language)) {
                if (order.getOperateType() == OperateTypeEnum.UPGRADE.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(new Date(), order.getEndTime()));
                    order.setLevelName(MemberUtil.getValueByCode(order.getOriginLevel()) + OperateTypeEnum.UPGRADE_HK.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.OPENING.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.OPENING_HK.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                } else if (order.getOperateType() == OperateTypeEnum.RENEW.getCode()) {
                    order.setValidityDays(MemberUtil.daysBetween(order.getStartTime(), order.getEndTime()));
                    order.setLevelName(OperateTypeEnum.RENEW_HK.getName() + MemberUtil.getValueByCode(order.getDestLevel()));
                }
            }
        }
        return page;
    }

    @Override
    public String confirmRemarks(String orederNumber) {
        String remarks = "";
        MemberBenefitsOrder order = benefitsOrderService.findMemberBenefitsOrderByOrderNumber(orederNumber);
        String language = HttpRequestUtil.getHttpServletRequest().getHeader("language");
        if (order != null) {
            if ("zh_CN".equals(language)) {
                if (order.getPayType() == PayTypeEnum.BUY.getCode()) {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_BUY_RETURN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_BUY_RETURN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_BUY_RETURN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_BUY_RETURN.getName();
                    }
                } else {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_LOCK_RETURN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_LOCK_RETURN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_LOCK_RETURN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_LOCK_RETURN.getName();
                    }
                }
            } else if ("en_US".equals(language)) {
                if (order.getPayType() == PayTypeEnum.BUY.getCode()) {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_BUY_RETURN_EN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_BUY_RETURN_EN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_BUY_RETURN_EN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_BUY_RETURN_EN.getName();
                    }
                } else {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_LOCK_RETURN_EN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_LOCK_RETURN_EN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_LOCK_RETURN_EN.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_LOCK_RETURN_EN.getName();
                    }
                }
            } else if ("ko_KR".equals(language)) {
                if (order.getPayType() == PayTypeEnum.BUY.getCode()) {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_BUY_RETURN_KO.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_BUY_RETURN_KO.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_BUY_RETURN_KO.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_BUY_RETURN_KO.getName();
                    }
                } else {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_LOCK_RETURN_KO.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_LOCK_RETURN_KO.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_LOCK_RETURN_KO.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_LOCK_RETURN_KO.getName();
                    }
                }
            } else if ("zh_HK".equals(language)) {
                if (order.getPayType() == PayTypeEnum.BUY.getCode()) {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_BUY_RETURN_HK.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_BUY_RETURN_HK.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_BUY_RETURN_HK.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_BUY_RETURN_HK.getName();
                    }
                } else {
                    if (order.getDestLevel() == MemberLevelTypeEnum.VIP1.getCode()) {
                        remarks = BizRemarksEnum.VIP1_LOCK_RETURN_HK.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP2.getCode()) {
                        remarks = BizRemarksEnum.VIP2_LOCK_RETURN_HK.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.VIP3.getCode()) {
                        remarks = BizRemarksEnum.VIP3_LOCK_RETURN_HK.getName();
                    } else if (order.getDestLevel() == MemberLevelTypeEnum.AGENT.getCode()) {
                        remarks = BizRemarksEnum.AGENT_LOCK_RETURN_HK.getName();
                    }
                }
            }
        }
        return remarks;
    }

    @Override
    public Integer giveMemberVip(Long memberId, Integer appId) throws ParseException {
        MemberBenefitsExtends benefits = benefitsExtendsService.getMemberrBenefitsByMemberId(memberId);
        if (benefits == null) {
            benefits = new MemberBenefitsExtends();
            Date newDate = new Date();
            benefits.setLevelId(1);
            benefits.setStartTime(newDate);
            benefits.setEndTime(newDate);
            benefits.setUpdateTime(newDate);
            benefits.setCreateTime(newDate);
            benefits.setMemberId(memberId);
            benefitsExtendsService.save(benefits);
        }
        Integer memberLevel = benefits.getLevelId();
        MemberBenefitsOrder order = null;
        // 当前会员为普通会员
        if (memberLevel == MemberLevelTypeEnum.NORMAL.getCode()) {
            order = new MemberBenefitsOrder();
            // 生成流水号
            long orderNumber = IdWorker.getId();
            order.setStartTime(new Date());
            order.setEndTime(MemberUtil.plusDay(30, new Date()));
            order.setPayType(PayTypeEnum.NEW_YEAR_GIVE.getCode());
            order.setOrderNumber(String.valueOf(orderNumber));
            order.setOperateType(OperateTypeEnum.OPENING.getCode());
            // 新会员等级
            order.setDestLevel(MemberLevelTypeEnum.VIP1.getCode());
            order.setAppId(appId);
            order.setUnit("USDT");
            order.setAmount(BigDecimal.ZERO);
            order.setPayTime(new Date());
            order.setOriginLevel(benefits.getLevelId());
            order.setMemberExtendId(benefits.getId());
            benefitsOrderService.save(order);
            benefits.setEndTime(order.getEndTime());
            benefits.setLevelId(order.getDestLevel());
            benefitsExtendsService.saveOrUpdate(benefits);
            BigDecimal amout = null;
            List<MemberRequireCondition> newRecords = requireConditionService.getRequireConditionBylevelId(MemberLevelTypeEnum.VIP1.getCode());
            for (MemberRequireCondition newRecord : newRecords) {
                if (newRecord.getType() == PayTypeEnum.BUY.getCode()) {
                    amout = newRecord.getQuantity();
                }
            }
            Map<String, Object> map = redisTemplate.opsForHash().entries(KeyGenerator.getMemberOpenVipAoumt(memberId));
            if (map == null || map.size() <= 0) {
                Map<String, Object> saveMap = new HashMap<>();
                saveMap.put("payType", 10);
                saveMap.put("amount", amout);
                saveMap.put("days", 30);
                redisTemplate.opsForHash().putAll(KeyGenerator.getMemberOpenVipAoumt(memberId), saveMap);
            }
            return 0;
        } else if (memberLevel == MemberLevelTypeEnum.VIP1.getCode()) {
            // 当前为VIP1
            order = new MemberBenefitsOrder();
            // 生成流水号
            long orderNumber = IdWorker.getId();
            order.setStartTime(benefits.getEndTime());
            order.setEndTime(MemberUtil.plusDay(30, benefits.getEndTime()));
            order.setPayType(PayTypeEnum.NEW_YEAR_GIVE.getCode());
            order.setOrderNumber(String.valueOf(orderNumber));
            order.setOperateType(OperateTypeEnum.RENEW.getCode());
            // 新会员等级
            order.setDestLevel(MemberLevelTypeEnum.VIP1.getCode());
            order.setAppId(appId);
            order.setUnit("USDT");
            order.setOriginLevel(benefits.getLevelId());
            order.setAmount(BigDecimal.ZERO);
            order.setPayTime(new Date());
            order.setMemberExtendId(benefits.getId());
            benefitsOrderService.save(order);
            benefits.setEndTime(order.getEndTime());
            benefits.setLevelId(order.getDestLevel());
            benefitsExtendsService.saveOrUpdate(benefits);
            return 0;
        }
        return 99;
    }


    private String getOperateTypeName(Integer code) {
        for (OperateTypeEnum operateTypeEnum : OperateTypeEnum.values()) {
            if (code == operateTypeEnum.getCode()) {
                return operateTypeEnum.getName();
            }
        }
        return null;
    }
}
