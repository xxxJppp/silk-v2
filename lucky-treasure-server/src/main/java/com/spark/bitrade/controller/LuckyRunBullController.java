package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.LuckyRunBullBizService;
import com.spark.bitrade.common.LuckyBusinessUtil;
import com.spark.bitrade.controller.param.ListBullParam;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.LuckyManageCoin;
import com.spark.bitrade.entity.LuckyNumberManager;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.enums.LuckyErrorCode;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 * 幸运宝-小牛快跑控制器
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Slf4j
@Api(value = "小牛快跑控制器")
@RestController
@RequestMapping("api/v2/luckyBull")
public class LuckyRunBullController {

    @Autowired
    private LuckyRunBullBizService luckyRunBullBizService;

    /**
     * 查询列表
     *
     * @return
     */
    @ApiOperation(value = "查询小牛快跑列表", tags = "小牛快跑")
    @RequestMapping(value = "no-auth/listBulls", method = {RequestMethod.POST, RequestMethod.GET})
    public MessageRespResult<IPage<LuckyRunBullListVo>> listBulls(ListBullParam param) {
        if("0".equals(param.getOnlyMine())){
            param.setOnlyMine("");
        }
        AssertUtil.notNull(param.getStatus(), LuckyErrorCode.STATUS_MUST_NOT_BE_EMPTY);
        return MessageRespResult.success4Data(luckyRunBullBizService.listBulls(param));
    }

    /**
     * 查询小牛快跑活动详情
     *
     * @return
     */
    @ApiOperation(value = "查询小牛快跑活动结束详情", tags = "小牛快跑")
    @RequestMapping(value = "no-auth/bullDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public MessageRespResult<LuckyRunBullListVo> bullEndDetail(@ApiParam(value = "memberId") @RequestParam(required = false) Long memberId,
                                                                    @ApiParam(value = "活动ID") @RequestParam Long actId) {
        LuckyRunBullListVo vo = luckyRunBullBizService.bullDetail(memberId, actId);
        return MessageRespResult.success4Data(vo);
    }

    /**
     * 参加小牛快跑
     *
     * @return
     */
    @ApiOperation(value = "参加小牛快跑", tags = "小牛快跑")
    @RequestMapping(value = "joinBull", method = {RequestMethod.POST})
    @ForbidResubmit
    public MessageRespResult joinBull(@ApiIgnore @MemberAccount Member member,
                                      @ApiParam(value = "活动ID")  @RequestParam Long actId,
                                      @ApiParam(value = "选择币种") @RequestParam String coinUnit,
                                      @ApiParam(value = "资金密码") @RequestParam String moneyPassword,
                                      @ApiParam(value = "购买数量") @RequestParam Integer buyCount) {
        //验证资金密码
        LuckyBusinessUtil.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
        //验证能否购买
        LuckyNumberManager luckyNumberManager = luckyRunBullBizService.validateBuy(actId, buyCount,coinUnit,member.getId());
        //购买
        luckyRunBullBizService.buyBull(member.getId(),luckyNumberManager,buyCount,coinUnit);

        return MessageRespResult.success();
    }

    /**
     * 分享领追加-小牛快跑
     *
     * @return
     */
    @ApiOperation(value = "分享领追加-小牛快跑", tags = "小牛快跑")
    @RequestMapping(value = "shareBull", method = {RequestMethod.POST})
    @ForbidResubmit
    public MessageRespResult shareBull(@ApiIgnore @MemberAccount Member member,
                                       @ApiParam(value = "活动ID")  @RequestParam Long actId){

        luckyRunBullBizService.shareBull(member.getId(),actId);
        return MessageRespResult.success();
    }

    @RequestMapping(value = "no-auth/findRealCoinBulls", method = {RequestMethod.POST,RequestMethod.GET})
    public MessageRespResult<List<LuckyManageCoin>> findRealCoinBulls(Long actId){
        return MessageRespResult.success4Data(luckyRunBullBizService.findRealCoinBulls(actId));
    }
}










































