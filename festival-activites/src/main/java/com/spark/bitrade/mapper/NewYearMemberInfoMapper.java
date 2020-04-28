package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.NewYearMemberInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 用户矿石表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearMemberInfoMapper extends BaseMapper<NewYearMemberInfo> {

    @Update("update new_year_member_info set dig_times = dig_times - 1 where member_id = #{memberId}")
    void decrMemberInfo(@Param("memberId") Long memberId);

    @Update("update new_year_member_info set dig_times = dig_times + 1 where member_id = #{memberId}")
    void incrMemberInfo(@Param("memberId") Long memberId);

}
