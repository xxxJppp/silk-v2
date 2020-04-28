package com.spark.bitrade.vo;

import com.spark.bitrade.constant.InCommunityStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.17 09:58  
 */
@Data
@ApiModel("社区成员vo")
@Builder
public class CommunityMemberVo {

    @ApiModelProperty("成员id")
    private Long memberId;

    @ApiModelProperty("成员昵称")
    private String userName;

    @ApiModelProperty("加入时间")
    private Date joinTime;

    @ApiModelProperty("状态 0:在社区中 1:已退出")
    private InCommunityStatus status;
}














