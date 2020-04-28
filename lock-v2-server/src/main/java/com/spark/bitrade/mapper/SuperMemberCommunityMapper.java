package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.SuperMemberCommunity;
import com.spark.bitrade.vo.CommunityMemberVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
public interface SuperMemberCommunityMapper extends BaseMapper<SuperMemberCommunity> {

    List<CommunityMemberVo> findCommunityMembers(@Param("communityId") Long communityId,Page page);


}
