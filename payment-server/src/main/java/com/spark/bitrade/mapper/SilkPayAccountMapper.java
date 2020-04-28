package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.GpsLocation;
import com.spark.bitrade.entity.SilkPayAccount;
import com.spark.bitrade.entity.dto.SilkPayAccountDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 支付账号(SilkPayAccount)表数据库访问层
 *
 * @author wsy
 * @since 2019-07-18 10:38:05
 */
public interface SilkPayAccountMapper extends BaseMapper<SilkPayAccount> {

    /**
     * 查找最有的账户
     * @author shenzucai
     * @time 2019.07.27 9:40
     * @param gpsLocation
     * @param amount
     * @param payType
     * @return true
     */
    List<SilkPayAccountDto> findMostSuitableAccount(@Param("gpsLocation") GpsLocation gpsLocation, @Param("amount") BigDecimal amount, @Param("payType") Integer payType);


    /**
     * 查找匹配订单的设备编码
     * @author shenzucai
     * @time 2019.08.16 15:21
     * @param id
     * @return true
     */
    String findMatchRecordDeviceSerialNo(@Param("id") Long id);


    /**
     * 解除额度限制
     * @author shenzucai
     * @time 2019.08.17 17:29
     * @return true
     */
    Boolean resetSurplus();

    @Update("UPDATE silk_pay_account SET total_already = total_already + #{money}, number_already = number_already + 1 WHERE id = #{accountId}")
    void updateStat(@Param("accountId") Long accountId, @Param("money") BigDecimal money);
}