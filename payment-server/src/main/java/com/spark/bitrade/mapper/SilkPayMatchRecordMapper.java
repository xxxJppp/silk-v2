package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SilkPayMatchRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 付款匹配记录(SilkPayMatchRecord)表数据库访问层
 *
 * @author wsy
 * @since 2019-07-18 10:38:51
 */
public interface SilkPayMatchRecordMapper extends BaseMapper<SilkPayMatchRecord> {

    /**
     * 获取下发中的订单
     *
     * @param serialNo
     * @return true
     * @author shenzucai
     * @time 2019.08.16 16:22
     */
    SilkPayMatchRecord findZeroRecords(@Param("serialNo") String serialNo);

    /**
     * 正在进行中的订单
     *
     * @return 正在进行
     */
    @Select("SELECT match_account, COUNT(*) AS count_rn FROM silk_pay_match_record WHERE state IN(0, 1) GROUP BY match_account")
    List<Map<String, Object>> getRunningPay();

    /**
     * 48小时内订单数
     *
     * @return 订单数
     */
    @Select("SELECT match_account, COUNT(*) AS count_48, SUM(payment_money) AS py_money FROM silk_pay_match_record WHERE state = 2 AND create_time > date_sub(NOW(), interval 2 day) GROUP BY match_account")
    List<Map<String, Object>> getCount48();
}