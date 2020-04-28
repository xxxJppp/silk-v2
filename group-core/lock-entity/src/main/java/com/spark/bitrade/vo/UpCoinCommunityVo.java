package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.09.05 16:10  
 */
@Data
public class UpCoinCommunityVo {


    @ApiModelProperty(value = "是否有上币申请,或已经上币 true已存在 false不存在")
    private Boolean hasUpCoin;
    @ApiModelProperty(value = "加入社区的情况")
    private MemberCommunityCaseVo memberCommunityCaseVo;
}
