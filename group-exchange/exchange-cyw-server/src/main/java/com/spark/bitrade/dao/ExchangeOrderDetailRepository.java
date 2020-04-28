//package com.spark.bitrade.dao;
//
//import com.spark.bitrade.entity.ExchangeOrderDetail;
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//import java.util.List;
//
///**
// * 订单交易明细
// *
// * @author yangch
// * @date 2019-09-05 11:07:52
// */
//public interface ExchangeOrderDetailRepository extends MongoRepository<ExchangeOrderDetail, String> {
//    List<ExchangeOrderDetail> findAllByOrderId(String orderId);
//
//    /**
//     * 查询第一个满足条件的明细
//     *
//     * @param orderId
//     * @param refOrderId
//     * @return
//     */
//    ExchangeOrderDetail findFirstByOrderIdAndRefOrderId(String orderId, String refOrderId);
//
//    /**
//     * 查询记录是否存在
//     *
//     * @param orderId
//     * @param refOrderId
//     * @return
//     */
//    boolean existsByOrderIdAndRefOrderId(String orderId, String refOrderId);
//
//    /**
//     * 删除指定订单ID和关联订单的记录
//     *
//     * @param orderId
//     * @param refOrderId
//     * @return
//     */
//    int deleteExchangeOrderDetailByOrderIdAndRefOrderId(String orderId, String refOrderId);
//}
