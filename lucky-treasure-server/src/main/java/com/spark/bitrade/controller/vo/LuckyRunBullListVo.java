package com.spark.bitrade.controller.vo;

import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.LuckyManageCoin;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class LuckyRunBullListVo {

    @ApiModelProperty(value = "活动ID")
    private Long actId;
    @ApiModelProperty(value = "活动批次")
    private String name;
    @ApiModelProperty(value = "开奖时间")
    private Date luckyTime;
    @ApiModelProperty(value = "已参赛人数")
    private Integer joinMemberCount=0;
    @ApiModelProperty(value = "参赛总票数")
    private Integer joinTicketCount=0;
    @ApiModelProperty(value = "开始选牛时间")
    private Date startTime;
    @ApiModelProperty(value = "结束选牛时间")
    private Date endTime;
    @ApiModelProperty(value = "票面价值")
    private BigDecimal amount=BigDecimal.ZERO;
    @ApiModelProperty(value = "参赛币种")
    private String coins;
    @ApiModelProperty(value = "我参与的小牛快跑")
    private List<MyJoinBulls> myJoinBulls=new ArrayList<>();

    @ApiModelProperty(value = "小牛排行信息 赛牛结束之后会有数据")
    private List<LuckyManageCoin> manageCoins=new ArrayList<>();

    @ApiModelProperty(value = "参与总金额")
    private BigDecimal joinMemberAmount=BigDecimal.ZERO;
    /**
     * 中奖票数
     */
    @ApiModelProperty(value = "中奖总票数/中奖总注数")
    private Integer winNum=0;
    /**
     * 中奖总人数
     */
    @ApiModelProperty(value = "中奖总人数")
    private Integer winMemberCount=0;
    @ApiModelProperty(value = "每票中奖金额")
    private BigDecimal onceWinAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "票面币种")
    private String coinUnit;
    @ApiModelProperty(value = "中奖注数 赛牛结束之后会有数据")
    private Integer memberLuckyCount=0;
    @ApiModelProperty(value = "中奖金额 赛牛结束之后会有数据")
    private BigDecimal memberLuckyAmount=BigDecimal.ZERO;
    @ApiModelProperty(value = "追加中奖金额 赛牛结束之后会有数据")
    private BigDecimal memberAddLuckyAmount=BigDecimal.ZERO;
    @ApiModelProperty(value = "是否分享 赛牛结束之后会有数据")
    private BooleanEnum isShare;

    @Data
    public static class MyJoinBulls{

        private String coinUnit;

        private Integer ticketNums;
    }

    public void coinsChange(){
        if(StringUtils.isNotBlank(this.coins)){
            this.coins=this.coins.replaceAll(",","/");
        }
    }
}
