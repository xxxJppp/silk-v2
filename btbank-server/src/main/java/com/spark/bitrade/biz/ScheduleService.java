package com.spark.bitrade.biz;

/**
 * @author shenzucai
 * @time 2019.10.24 16:35
 */
public interface ScheduleService {

    /**
     * 解锁资产
     * @author shenzucai
     * @time 2019.10.24 16:41
     * @return true
     */
    Boolean unLockAssert();


    /**
     * 自动派单
     * 作为派单定时任务，将24小时无人抢单的订单，根据规则分派一个符合条件的矿工（防止订单积压）。
     * 矿工的【矿池可用】必须大于等于【订单金额】，没有符合条件的失败，等待下次执行
     * 未完成订单最少的
     * 24小时内抢单次数最少的
     * 24小时抢单总金额最少的
     *
     * @author shenzucai
     * @time 2019.10.24 16:51
     * @return true
     */
    Boolean autoDispatch();
}
