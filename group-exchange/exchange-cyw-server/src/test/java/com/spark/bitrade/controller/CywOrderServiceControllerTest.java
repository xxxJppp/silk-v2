package com.spark.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.redis.PalceService;
import com.spark.bitrade.service.CywCompletedOrderService;
import com.spark.bitrade.service.impl.CywOrderServiceImpl;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *  
 *
 * @author young
 * @time 2019.09.02 13:51
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
public class CywOrderServiceControllerTest {

    @Autowired
    private CywOrderServiceController cywOrderServiceController;
    @Autowired
    private CywOrderServiceImpl orderService;
    @Autowired
    private PalceService palceService;
    @Autowired
    private CywCompletedOrderService completedOrderService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    /**
     * 测试委托下单
     * <p>
     * 验证点：
     * 1、订单存放到Redis中，key=data:cywOrder:t:交易对:用户id
     * 2、cyw_wallet_wal_record 表记录1条下单流水
     * 3、发送kafka下单消息
     */
    @Test
    public void createOrder() {
        String baseSymbol = "BT";
        String coinSymbol = "SLP";
        String symbol = coinSymbol + "/" + baseSymbol; //SLU/USDT

        cywOrderServiceController.createOrder(mockOrder(baseSymbol, coinSymbol, symbol));
    }

    /**
     * 批量委托订单
     */
    @Test
    public void createOrders() {
        long startTime = System.currentTimeMillis();
        String baseSymbol = "BT";
        String coinSymbol = "SLP";
        String symbol = coinSymbol + "/" + baseSymbol; //SLU/USDT

        for (int i = 0; i < 100; i++) {
            cywOrderServiceController.createOrder(mockOrder(baseSymbol, coinSymbol, symbol));
        }
        System.out.println("time:" + (System.currentTimeMillis() - startTime));
    }


    /**
     * 查询订单
     */
    @Test
    public void queryOrder() {
        long startTime = System.currentTimeMillis();
        //交易中的订单
        MessageRespResult<ExchangeOrder> respResult =
                cywOrderServiceController.queryOrder(71639L, "S1174601985903562754_SLPBT");
        assertEquals(respResult.isSuccess(), true);
        assertNotNull(respResult.getData());
        System.out.println(respResult);

        //撤销中的订单
        MessageRespResult<ExchangeOrder> respResult2 =
                cywOrderServiceController.queryOrder(71639L, "S230040302759444480_BTCUSDT");
        assertEquals(respResult2.isSuccess(), true);
        assertNotNull(respResult2.getData());
        System.out.println(respResult2);

        //入库订单
        MessageRespResult<ExchangeOrder> respResult3 =
                cywOrderServiceController.queryOrder(71639L, "S230026650287341568_BTCUSDT");
        assertEquals(respResult3.isSuccess(), true);
        assertNotNull(respResult3.getData());
        System.out.println(respResult3);

        System.out.println("time:" + (System.currentTimeMillis() - startTime));
    }


    /**
     * 获取正在交易的用户
     * 验证点：
     * 1、会员ID列表
     */
    @Test
    public void openMembers() {
        MessageRespResult<Set<Long>> respResult = cywOrderServiceController.openMembers("SLP/BT");
        System.out.println(respResult);
    }

    /**
     * 查询正在进行的订单
     */
    @Test
    public void openOrders() {
        MessageRespResult result = cywOrderServiceController.openOrders(71639L, "SLP/BT");
        System.out.println(result);
    }

    /**
     * 查询历史订单
     */
//    @Test
    public void historyOrders() {
        /*MessageRespResult<IPage<ExchangeCywOrder>> result =
                cywOrderServiceController.historyOrders(2, 0, "SLP/BT", 71639L);*/
        MessageRespResult<IPage<ExchangeOrder>> result =
                cywOrderServiceController.historyOrders(2, 0, "SLP/BT", 71639L);
        assertEquals(result.isSuccess(), true);
        System.out.println(result);
    }

    @Test
    public void historyOrders2() {
        /*MessageRespResult<IPage<ExchangeCywOrder>> result =
                cywOrderServiceController.historyOrders(2, 0, "SLP/BT", 71639L);*/
        MessageRespResult<PageData<ExchangeOrder>> result =
                cywOrderServiceController.historyOrders2(2, 0, "SLP/BT", 71639L);
        assertEquals(result.isSuccess(), true);
        System.out.println(result);
    }


    /**
     * 机器人完成订单的处理
     * 验证点：
     * 1、redis订单移动： data:cywOrder:t:SLPBT:71639 -> data:cywOrder:s:SLPBT
     * 2、添加任务： data:cywTask:s
     */
    @Test
    public void completedOrder1() {
        Long memberId = 71639L;
        String orderId = "S1176329029750366210_SLPBT";
        BigDecimal tradedAmount = new BigDecimal(199.99).setScale(8, BigDecimal.ROUND_DOWN);
        BigDecimal turnover = new BigDecimal(99.99).setScale(8, BigDecimal.ROUND_DOWN);

        MessageRespResult<ExchangeOrder> result = cywOrderServiceController.completedOrder(memberId, orderId, tradedAmount, turnover);
        assertEquals(result.isSuccess(), true);
        assertNotNull(result.getData());
        System.out.println(result);

        //查询订单是否存在
        MessageRespResult<ExchangeOrder> respResult2 = cywOrderServiceController.queryOrder(memberId, orderId);
        assertEquals(respResult2.isSuccess(), true);
        assertNotNull(respResult2.getData());
        System.out.println(respResult2);
    }

    /**
     * 已完成订单入库测试（注意：应用启动后，会自动执行，此处调用会重复执行）
     * 验证点：
     * 1、订单入库（exchange_cyw_order）
     * 2、可能有wal记录
     */
    @Test
    public void completedOrder2() {
        String orderId = "S1174863727640453122_SLPBT";
        completedOrderService.completedOrder(orderId);
    }


    /**
     * 撮合明细处理（买方）
     */
    @Test
    public void tradeBuy() {
        MessageRespResult<ExchangeOrder> result = cywOrderServiceController.tradeBuy(mockExchangeTrade());
        assertEquals(result.isSuccess(), true);
        assertNotNull(result.getData());
        System.out.println(result);
    }

    /**
     * 撮合明细处理（买方）
     */
    @Test
    public void tradeBuy1() {
        kafkaTemplate.send("exchange-cyw-trade", "BUY", JSON.toJSONString(mockExchangeTrade()));
        System.out.println("--------tradeBuy1----------------");
    }

    /**
     * 撮合明细处理（卖方）
     */
    @Test
    public void tradeSell() {
        MessageRespResult<ExchangeOrder> result = cywOrderServiceController.tradeSell(mockExchangeTrade());
        assertEquals(result.isSuccess(), true);
        assertNotNull(result.getData());
        System.out.println(result);
    }

    /**
     * 撮合明细处理（卖方）
     */
    @Test
    public void tradeSell1() {
        kafkaTemplate.send("exchange-cyw-trade", "SELL", JSON.toJSONString(mockExchangeTrade()));
        System.out.println("--------tradeSell1----------------");
    }

    /**
     * 撤销申请
     * 验证点：
     * 1、在一定时间内，禁止重复提交
     * 2、提交 撤销 消息
     */
    @Test
    public void claimCancelOrder() {
        MessageRespResult<ExchangeOrder> result = cywOrderServiceController.claimCancelOrder(71639L, "S1174601985903562754_SLPBT");
        System.out.println(result);
    }


    /**
     * 机器人撤销订单的处理1-(部分成交)
     * 验证点：
     * 1、订单移动：data:cywOrder:t:交易对:会员ID ->  data:cywOrder:c:交易对:会员ID
     * 2、添加任务：data:cywTask:c
     */
    @Test
    public void canceledOrder1() {
        Long memberId = 71639L;
        String orderId = "S1176329030496952321_SLPBT";
        BigDecimal tradedAmount = new BigDecimal(189.99).setScale(8, BigDecimal.ROUND_DOWN);
        BigDecimal turnover = new BigDecimal(99.99).setScale(8, BigDecimal.ROUND_DOWN);

        MessageRespResult<ExchangeOrder> result = cywOrderServiceController.canceledOrder(memberId, orderId, tradedAmount, turnover);
        assertEquals(result.isSuccess(), true);
        assertNotNull(result.getData());
        System.out.println(result);

        //查询订单是否存在，订单在 撤单队列 中
        MessageRespResult<ExchangeOrder> respResult2 = cywOrderServiceController.queryOrder(memberId, orderId);
        assertEquals(respResult2.isSuccess(), true);
        assertNotNull(respResult2.getData());
        System.out.println(respResult2);
    }

    /**
     * 机器人撤销订单的处理2-(未成交)
     * 验证点：同上（交易量和成交额不一样）
     */
    @Test
    public void canceledOrder2() {
        Long memberId = 71639L;
        String orderId = "S1169857395892506626_SLPBT";
        BigDecimal tradedAmount = BigDecimal.ZERO;
        BigDecimal turnover = BigDecimal.ZERO;

        MessageRespResult<ExchangeOrder> result = cywOrderServiceController.canceledOrder(memberId, orderId, tradedAmount, turnover);
        assertEquals(result.isSuccess(), true);
        assertNotNull(result.getData());
        System.out.println(result);

        //查询订单是否存在
        MessageRespResult<ExchangeOrder> respResult2 = cywOrderServiceController.queryOrder(memberId, orderId);
        assertEquals(respResult2.isSuccess(), true);
        //订单不存在
        assertNull(respResult2.getData());
        System.out.println(respResult2);
    }

    /**
     * 机器人撤销订单的处理3-(无法获取成交额和成交量)
     * <p>
     * 验证点：
     * 1、同上（交易量和成交额不一样）
     * 2、成交量和成交额，从成交明细中汇总
     */
    @Test
    public void canceledOrder3() {
        Long memberId = 71639L;
        //tips：可以从data:cywOrder:SLPBT:71639 中获取订单号
        String orderId = "S1169858057699192833_SLPBT";

        MessageRespResult<ExchangeOrder> result = cywOrderServiceController.canceledOrder(memberId, orderId);
        assertEquals(result.isSuccess(), true);
        assertNotNull(result.getData());
        System.out.println(result);

        //查询订单是否存在（有交易则保留订单，无交易则保留订单）
        MessageRespResult<ExchangeOrder> respResult2 = cywOrderServiceController.queryOrder(memberId, orderId);
        assertEquals(respResult2.isSuccess(), true);
        //assertNotNull(respResult2.getData());
        System.out.println(respResult2);
    }

    /**
     * 撤销订单入库 测试
     * 验证点：
     * 1、删除 data:cywOrder:c:交易对:会员ID 中的订单
     * 2、部分成交的入库到exchange_cyw_order表
     * 3、未成交的不入库
     * 4、cyw_wallet_wal_record 表记录1条退账流水
     */
    @Test
    public void canceledOrder4() {
        orderService.canceledOrder("S1169858057699192833_SLPBT");
    }


    /**
     * Redis key 重复提交 性能
     */
    @Test
    public void resubmit() {
        palceService.place("key:test:t", 30);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            palceService.place("key:test:" + i, 30);
        }
        System.out.println("time:" + (System.currentTimeMillis() - startTime));
    }


    /**
     * 模拟订单
     *
     * @param baseSymbol
     * @param coinSymbol
     * @param symbol
     * @return
     */
    private ExchangeOrder mockOrder(String baseSymbol, String coinSymbol, String symbol) {
        //Order order = new Order();
        ExchangeOrder order = new ExchangeOrder();
        //order.setOrderId("S"+ IdWorker.getId());
        order.setOrderId(this.genOrderId(symbol));
        order.setMemberId(71639L);
        order.setAmount(new BigDecimal(200).setScale(8, BigDecimal.ROUND_DOWN));
        order.setDirection(ExchangeOrderDirection.SELL);
        order.setPrice(new BigDecimal(2.22).setScale(8, BigDecimal.ROUND_DOWN));
        order.setSymbol(symbol);
        order.setType(ExchangeOrderType.LIMIT_PRICE);

        order.setBaseSymbol(baseSymbol);
        order.setCoinSymbol(coinSymbol);
        order.setFreezeAmount(new BigDecimal(200).setScale(8, BigDecimal.ROUND_DOWN));
        return order;
    }

    /**
     * 创建订单号
     *
     * @param symbol
     * @return
     */
    private String genOrderId(String symbol) {
        //eg：S1168423154092716041_SLUUSDT
        return new StringBuilder("S").append(IdWorker.getId()).append("_").append(symbol.replace("/", "")).toString();
    }

    /**
     * 模拟交易撮合明细
     *
     * @return
     */
    private ExchangeTrade mockExchangeTrade() {
        ExchangeTrade trade = new ExchangeTrade();
        trade.setSymbol("SLP/BT");
        trade.setPrice(BigDecimalUtil.createBigDecimal(2));
        trade.setAmount(BigDecimalUtil.createBigDecimal(100));
        trade.setDirection(ExchangeOrderDirection.SELL);
        trade.setBuyMemberId(71639L);
        trade.setBuyOrderId("S1178545230119460866_SLPBT");
        trade.setBuyTurnover(BigDecimalUtil.createBigDecimal(50));
        trade.setSellMemberId(71639L);
        trade.setSellOrderId("S1178545232396967937_SLPBT");
        trade.setSellTurnover(BigDecimalUtil.createBigDecimal(50));
        trade.setTime(System.currentTimeMillis());
//        trade.setUnfinishedOrderId();
//        trade.setUnfinishedTradedAmount();
//        trade.setUnfinishedTradedTurnover();
        return trade;
    }
}