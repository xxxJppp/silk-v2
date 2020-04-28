package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.17 09:21  
 */
@Data
@ApiModel(description = "当前用户社区情况")
@Builder
public class MemberCommunityCaseVo {


    @ApiModelProperty(value = "拥有的社区", example = "")
    private SuperPartnerCommunityVo communityVo;

    @ApiModelProperty(value = "审批记录 当拥有的社区正在审核的时候会有记录")
    private SuperApplyRecordVo record;

}
