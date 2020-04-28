package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.dto.IncubatorsBasicInformationDto;
import com.spark.bitrade.dto.IncubatorsEntranceDto;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.form.UpCoinForm;
import com.spark.bitrade.mapper.IncubatorsBasicInformationMapper;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AliyunUtil;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.LockUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 孵化区-上币申请表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@Slf4j
@Service
public class IncubatorsBasicInformationServiceImpl extends ServiceImpl<IncubatorsBasicInformationMapper, IncubatorsBasicInformation>
        implements IncubatorsBasicInformationService {
    @Autowired
    private IncubatorsApplicationReviewService reviewService;
    @Autowired
    private IncubatorsFundDetailsService detailsService;
    @Autowired
    private LockCoinDetailService lockCoinDetailService;
    @Autowired
    private SuperMemberCommunityService superMemberCommunityService;
    @Autowired
    private SuperPartnerCommunityService superPartnerCommunityService;
    @Autowired
    private AliyunConfig aliyunConfig;
    @Autowired
    private ISilkDataDistApiService silkDataDistApiService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upCoinApply(Member member, UpCoinForm form, String coinUnit, BigDecimal lockAmount) {
        //创建申请记录
        IncubatorsBasicInformation information = new IncubatorsBasicInformation();
        information.setProName(form.getProName())
                .setCommunityName(form.getCommunityName())
                .setContactPerson(form.getContactPerson())
                .setProDesc(form.getProDesc())
                .setTelPhone(form.getTelPhone())
                .setWechatNum(form.getWechatNum())
                .setWechatCode(LockUtil.decodeUrl(form.getWechatCode()))
                .setLockUpNum(lockAmount)
                .setMemberId(member.getId())
                .setStatus(IncubatorsBasicStatus.UP_COIN_PENDING)
                .setCreateTime(new Date());
        boolean in = this.save(information);
        AssertUtil.isTrue(in, CommonMsgCode.UNCHECKED_ERROR);
        //创建审核记录
        IncubatorsApplicationReview review = new IncubatorsApplicationReview();
        review.setIncubatorsId(information.getId())
                .setMemberId(member.getId())
                .setProName(form.getProName())
                .setOperationType(IncubatorsOpType.UP_COIN_APPLY)
                .setProDesc(form.getProDesc())
                .setStatus(SuperAuditStatus.PENDING)
                .setCreateTime(new Date());
        boolean re = reviewService.save(review);
        AssertUtil.isTrue(re, CommonMsgCode.UNCHECKED_ERROR);
        //创建锁仓明细
        IncubatorsFundDetails details = new IncubatorsFundDetails();
        details.setIncubatorsId(information.getId())
                .setOperationType(IncubatorsLockType.LOCK)
                .setCoinId(coinUnit)
                .setNum(lockAmount)
                .setStatus(IncubatorsDetailStatus.INIT_LOCK)
                .setOperationId(member.getId())
                .setOperationName(member.getUsername())
                .setCreateTime(new Date());
        boolean de = detailsService.save(details);
        AssertUtil.isTrue(de, CommonMsgCode.UNCHECKED_ERROR);
        //创建lock_coin_detail
        LockCoinDetail coinDetail = new LockCoinDetail();
        coinDetail.setCoinUnit(coinUnit);
        coinDetail.setMemberId(member.getId());
        coinDetail.setRemainAmount(lockAmount);
        coinDetail.setStatus(LockStatus.LOCKED);
        coinDetail.setTotalAmount(lockAmount);
        coinDetail.setType(LockType.INCUBOTORS_LOCK);
        coinDetail.setRemark("孵化区锁仓");
        coinDetail.setRefActivitieId(information.getId());
        coinDetail.setLockTime(new Date());
        boolean co = lockCoinDetailService.save(coinDetail);
        AssertUtil.isTrue(co, CommonMsgCode.UNCHECKED_ERROR);
        //调用资金接口 生成流水
        WalletTradeEntity addW = new WalletTradeEntity();
        addW.setType(TransactionType.INCUBOTORS_LOCK);
        //社区负责人
        addW.setMemberId(member.getId());
        addW.setCoinUnit(coinUnit);
        //减少的数量
        addW.setTradeBalance(BigDecimal.ZERO.subtract(lockAmount));
        //冻结 锁仓余额
        addW.setTradeLockBalance(lockAmount);
        addW.setTradeFrozenBalance(BigDecimal.ZERO);
        addW.setComment("孵化区上币申请锁仓");
        addW.setServiceCharge(new ServiceChargeEntity());
        superMemberCommunityService.traceWallet(addW);

    }

    @Override
    public void exitCoinApply(Member member, String reason, IncubatorsBasicInformation information) {
        // 修改上币申请的状态
        // 创建退出上币申请记录
    }

    /**
     * 审核详情
     *
     * @param memberId 会员信息
     * @return
     */
    @Override
    public IncubatorsBasicInformationDto getIncubatorsBasicInformationByMemberId(Long memberId) {
        String coinUnit = null;
        //验证全局配置
        MessageRespResult<List<SilkDataDist>> sk = silkDataDistApiService.list("INCUBATORS_CONFIG");
        AssertUtil.isTrue(sk.isSuccess(), CommonMsgCode.SERVICE_UNAVAILABLE);
        for (SilkDataDist d : sk.getData()) {
            if ("BASE_SYMBOL".equals(d.getDictKey())) {
                coinUnit = d.getDictVal();
            }
        }
        AssertUtil.isTrue(StringUtils.hasText(coinUnit), LockMsgCode.INCUBATORS_CONFIG_NOT_FIND);
        IncubatorsBasicInformationDto incubatorsBasicInformationDto = baseMapper.getIncubatorsBasicInformationByMemberId(memberId);
        if (!StringUtils.isEmpty(incubatorsBasicInformationDto)) {
            // 上币申请审核拒绝，原因填充
            IncubatorsApplicationReview incubatorsApplicationReview = reviewService.getIncubatorsApplicationReviewById(IncubatorsOpType.UP_COIN_APPLY, incubatorsBasicInformationDto.getId());
            if (!StringUtils.isEmpty(incubatorsApplicationReview)) {
                incubatorsBasicInformationDto.setAuditOpinion(incubatorsApplicationReview.getAuditOpinion() + "\n\n已解锁锁仓金额，请补充完整资料可进行重新申请");
            }
            // 上币申请通过，获取升仓数量
            IncubatorsFundDetails incubatorsFundDetails = detailsService.getIncubatorsFundDetailsById(IncubatorsDetailStatus.ADD_LOCK, incubatorsBasicInformationDto.getId());
            if (!StringUtils.isEmpty(incubatorsFundDetails)) {
                incubatorsBasicInformationDto.setNum(incubatorsFundDetails.getNum());
                incubatorsBasicInformationDto.setCoinId(incubatorsFundDetails.getCoinId());
                incubatorsBasicInformationDto.setLiftStatus(true);
            } else {
                incubatorsBasicInformationDto.setLiftStatus(false);
            }
            incubatorsBasicInformationDto.setLockCoinId(coinUnit);
            incubatorsBasicInformationDto.setWechatCode(generateImageUrl(LockUtil.decodeUrl(incubatorsBasicInformationDto.getWechatCode())));
        }
        return incubatorsBasicInformationDto;
    }

    /**
     * 获取阿里云私有地址
     *
     * @param url
     * @return
     */
    private String generateImageUrl(String url) {
        String urlEnd = null;
        try {
            urlEnd = AliyunUtil.getPrivateUrl(aliyunConfig, url);
        } catch (Exception e) {
            log.error("转化图片地址失败!");
        }
        return urlEnd;
    }

    /**
     * 入口
     *
     * @param memberId 会员信息
     * @return
     */
    @Override
    public IncubatorsEntranceDto getMemberStatus(Long memberId) {
        IncubatorsEntranceDto retIncubatorsEntranceDto = new IncubatorsEntranceDto();
        SuperPartnerCommunity superPartnerCommunity = superPartnerCommunityService.getSuperPartnerCommunityByMemmberId(memberId, BooleanEnum.IS_TRUE, SuperRegistration.INITIATIVE);
        if (!StringUtils.isEmpty(superPartnerCommunity)) {
            retIncubatorsEntranceDto.setIncubatorsBasicStatus(IncubatorsBasicStatus.CREATE_COMMUNITY);
        } else {
            IncubatorsBasicInformationDto incubatorsBasicInformationDto = baseMapper.getIncubatorsBasicInformationByMemberId(memberId);
            if (StringUtils.isEmpty(incubatorsBasicInformationDto)) {
                retIncubatorsEntranceDto.setIncubatorsBasicStatus(IncubatorsBasicStatus.NORMAL);
            } else {
                retIncubatorsEntranceDto.setIncubatorsBasicStatus(incubatorsBasicInformationDto.getStatus());
            }
        }
        return retIncubatorsEntranceDto;
    }

    /**
     * 修改孵化区申请表锁仓数量
     *
     * @param id  id
     * @param num 锁仓数量
     * @return
     */
    @Override
    public Integer updateIncubatorsBasicInformation(Long id, BigDecimal num) {
        return baseMapper.updateIncubatorsBasicInformation(id, num);
    }

}
















