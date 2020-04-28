package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SilkPayGlobalConfig;
import com.spark.bitrade.entity.SilkPayUserConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 用户支付配置(SilkPayUserConfig)表数据库访问层
 *
 * @author wsy
 * @since 2019-08-21 14:22:00
 */
public interface SilkPayUserConfigMapper extends BaseMapper<SilkPayUserConfig> {

    @Insert("INSERT INTO silk_pay_user_config (member_id, default_config, enable_pay, quota_total, quota_daily, limit_total, limit_daily, total_number, daily_number, total_amount, daily_amount, quota_relieve, create_time)" +
            "VALUES(#{memberId}, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, date_format(date_add(NOW(), interval 1 day), '%Y-%m-%d 00:00:00'), NOW()) " +
            "ON DUPLICATE KEY UPDATE daily_number = IF(quota_relieve <= now(), 0, daily_number), daily_amount = IF(quota_relieve <= now(), 0, daily_amount), quota_relieve = values(quota_relieve)")
    int resetSurplus(@Param("memberId") Long memberId, @Param("global") SilkPayGlobalConfig globalConfig);

    @Update("UPDATE silk_pay_user_config SET total_number = total_number + 1, daily_number = daily_number + 1, total_amount = total_amount + #{money}, daily_amount = daily_amount + #{money} WHERE member_id = #{memberId}")
    int updateStat(@Param("memberId") Long memberId, @Param("money") BigDecimal money);

    @Select("select * from silk_pay_user_config where member_id = #{memberId}")
    SilkPayUserConfig getUserConfig(@Param("memberId") Long memberId);
}
