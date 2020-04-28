package com.spark.bitrade.controller;


import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SilkPayGlobalConfig;
import com.spark.bitrade.entity.SilkPayUserConfig;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.SilkPayCoinService;
import com.spark.bitrade.service.SilkPayGlobalConfigService;
import com.spark.bitrade.service.SilkPayUserConfigService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.MemberWalletVo;
import com.spark.bitrade.vo.UserConfigVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 支付账号(SilkPayAccount)控制层
 *
 * @author wsy
 * @since 2019-07-18 10:38:05
 */
@RestController
// @RequestMapping("v2/silkPayAccount")
@Api(description = "支付账号控制层")
public class SilkPayAccountController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private IMemberWalletApiService memberWalletApiService;
    @Resource
    private SilkPayCoinService silkPayCoinService;
    @Resource
    private SilkPayUserConfigService silkPayUserConfigService;
    @Resource
    private SilkPayGlobalConfigService globalConfigService;

    /**
     * 用户支持币种
     *
     * @param member 会员
     * @return 修改结果
     */
    @ApiOperation(value = "用户支持币种", notes = "获取支持币种接口")
    @RequestMapping(value = "api/v2/silkPayAccount/getSupportCoin", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult getSupportCoin(@MemberAccount Member member) {
        MessageRespResult<List<MemberWalletVo>> result = memberWalletApiService.getSupportCoinByMemberId(member.getId());
        List<MemberWalletVo> list = result.getData();
        List<MemberWalletVo> resultList = new ArrayList<>();
        List<MemberWalletVo> coinList = silkPayCoinService.findAllAbleUnits();
        if (list != null && list.size() > 0) {
            for (MemberWalletVo vo : coinList) {
                for (MemberWalletVo walletVo : list) {
                    if (vo.getUnit().equals(walletVo.getUnit())) {
                        walletVo.setScale(vo.getScale());
                        walletVo.setTradeMax(vo.getTradeMax());
                        walletVo.setTradeMin(vo.getTradeMin());
                        walletVo.setRateReductionFactor(vo.getRateReductionFactor());
                        resultList.add(walletVo);
                        break;
                    }
                }
            }
            result.setData(resultList);
        }
        return result;
    }

    @ApiOperation(value = "查询用户支付配置", notes = "查询用户支付配置接口")
    @RequestMapping(value = "api/v2/silkPayAccount/getUserConfig", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<UserConfigVo> getUserConfig(@MemberAccount Member member) {

        SilkPayGlobalConfig globalConfig = globalConfigService.getById(1);
        // 解限用户限额
        silkPayUserConfigService.resetSurplus(member.getId(), globalConfig);
        // 查询最新限制数据
        SilkPayUserConfig userConfig = silkPayUserConfigService.getById(member.getId());
        if (userConfig == null || userConfig.getDefaultConfig() == 1) {
            SilkPayUserConfig newUserConfig = new SilkPayUserConfig();
            newUserConfig.setMemberId(member.getId());
            newUserConfig.setDefaultConfig(userConfig == null ? 1 : userConfig.getDefaultConfig());
            newUserConfig.setQuotaDaily(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserSingleQuota() : userConfig.getQuotaDaily());
            newUserConfig.setQuotaTotal(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserTotalQuota() : userConfig.getQuotaTotal());
            newUserConfig.setLimitDaily(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserDailyMax() : userConfig.getLimitDaily());
            newUserConfig.setLimitTotal(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserTotalDaily() : userConfig.getLimitTotal());
            newUserConfig.setDailyNumber(userConfig == null ? 0 : userConfig.getDailyNumber());
            newUserConfig.setTotalNumber(userConfig == null ? 0 : userConfig.getTotalNumber());
            newUserConfig.setDailyAmount(userConfig == null ? BigDecimal.ZERO : userConfig.getDailyAmount());
            newUserConfig.setTotalAmount(userConfig == null ? BigDecimal.ZERO : userConfig.getTotalAmount());
            userConfig = newUserConfig;
        }

        UserConfigVo userConfigVo = new UserConfigVo();
        userConfigVo.setMemberId(userConfig.getMemberId());
        userConfigVo.setUserSingleMin(globalConfig.getUserSingleMin()); // 用户单笔最低限额
        userConfigVo.setDailyAmount(userConfig.getDailyAmount()); // 今日交易额
        userConfigVo.setDailyNumber(userConfig.getDailyNumber()); // 今日交易次数
        userConfigVo.setQuotaDaily(userConfig.getQuotaDaily()); // 每日最高额度CNY
        userConfigVo.setQuotaTotal(userConfig.getQuotaTotal()); // 交易限额总额CNY
        userConfigVo.setTotalAmount(userConfig.getTotalAmount()); // 用户已交易总额
        userConfigVo.setLimitDaily(userConfig.getLimitDaily()); // 用户每日最高交易次数
        userConfigVo.setLimitTotal(userConfig.getLimitTotal()); // 用户总交易次数限制
        userConfigVo.setTotalNumber(userConfig.getTotalNumber()); // 已交易总次数
        return success(userConfigVo);
    }

}
