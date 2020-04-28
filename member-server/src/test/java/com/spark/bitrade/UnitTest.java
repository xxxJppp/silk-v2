package com.spark.bitrade;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.spark.bitrade.consumer.MemberFeeDailyStatConsumer;
import com.spark.bitrade.consumer.RecommendCommisionConsumer;
import com.spark.bitrade.producer.Producer;
import com.spark.bitrade.service.GlobalConfService;
import com.spark.bitrade.service.MemberBenefitsSettingService;
import com.spark.bitrade.service.MemberFeeDayStatService;
import com.spark.bitrade.service.MemberRecommendCommisionService;
import com.spark.bitrade.utils.TimeUtil;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=MemberApplication.class)
@Transactional
@Slf4j
public class UnitTest {
    @Autowired
    ApplicationContext applicationContext;
    
    @Autowired
    private MemberFeeDayStatService memberFeeDayStatService;
    
    
    @Autowired
    private MemberRecommendCommisionService memberRecommendCommisionService;
    
    @Autowired
    private Producer p;
    
    @Autowired
    private GlobalConfService globalConfService;
    
    @Autowired
    private MemberBenefitsSettingService memberBenefitsSettingService;
    
    @Autowired
    private MemberFeeDailyStatConsumer memberFeeDailyStatConsumer;
    @Autowired
    private RecommendCommisionConsumer mecommendCommisionConsumer;
    
    String lock = "{\"id\":18,\"memberExtendId\":3,\"orderNumber\":\"1199984647065399297\",\"operateType\":30,\"originLevel\":3,\"destLevel\":3,\"startTime\":1574933609154,\"endTime\":1577525609000,\"payType\":20,\"amount\":12000,\"unit\":\"BT\",\"appId\":1,\"payTime\":1574933611086,\"createTime\":1574933611087,\"updateTime\":1574933611087,\"levelName\":null}";
   
    String buy = "{\"id\":17,\"memberExtendId\":3,\"orderNumber\":\"1199984378286010369\",\"operateType\":30,\"originLevel\":3,\"destLevel\":3,\"startTime\":1574933544855,\"endTime\":1577525544000,\"payType\":10,\"amount\":9000,\"unit\":\"BT\",\"appId\":1,\"payTime\":1574933545371,\"createTime\":1574933545382,\"updateTime\":1574933545382,\"levelName\":null}";
//    @Test
//    public void getConsumeLock() {
//    	this.mecommendCommisionConsumer.consumeNonCanalMessage(lock);
//    }
//    
//    @Test
//    public void getConsumeBuy() {
//    	this.mecommendCommisionConsumer.consumeNonCanalMessage(buy);
//    }
//    
//    
//    @Test
//    public void getConsumeStatBuy() {
//    	this.memberFeeDailyStatConsumer.consumeNonCanalMessage(buy);
//    }
//    @Test
//    public void getConsumeStatLock() {
//    	this.memberFeeDailyStatConsumer.consumeNonCanalMessage(lock);
//    }
//    
//    
//    
//    @Test
//    public void testDistributeExchangeFee()  {
//    	ExchangeOrderReceipt o = new ExchangeOrderReceipt();
//    	o.setRefId("ORDER1438");
//    	o.setOrderMemberId(70090);
//    	o.setCommisionUnit("USDT");
//    	o.setCommisionQuantity(new BigDecimal(1264.38).setScale(8, RoundingMode.DOWN));
//    	o.setBizType(BizTypeEnum.TOKEN_EXCHANGE.getCode());
//    	o.setTxTime(new Date());
//
//    	this.memberRecommendCommisionService.distributeExchageOrderFee(o);
//    }
//
//    @Test
//    public void testMemberBenefitsBuyOrderFee()  {
//    	MemberBenefitsOrderReceipt o = new MemberBenefitsOrderReceipt();
//    	o.setRefId("ORDER1239");
//    	o.setOrderMemberId(70090);
//    	o.setCommisionUnit("USDT");
//    	o.setCommisionQuantity(new BigDecimal(120).setScale(8, RoundingMode.DOWN));
//    	o.setPayType(BizTypeEnum.MEMBER_BUY.getCode());
//    	o.setPayTime(new Date());
//
//    	this.memberRecommendCommisionService.distributeBenefitsOrder(o,null);
//    }
//    
//    @Test
//    public void testMemberBenefitsLockOrderFee()  {
//    	MemberBenefitsOrderReceipt o = new MemberBenefitsOrderReceipt();
//    	o.setRefId("ORDER1240");
//    	o.setOrderMemberId(70090);
//    	o.setCommisionUnit("USDT");
//    	o.setCommisionQuantity(new BigDecimal(120).setScale(8, RoundingMode.DOWN));
//    	o.setPayType(BizTypeEnum.MEMBER_LOCK.getCode());
//    	o.setPayTime(new Date());
//    	o.setLockDay(78);
//
//    	this.memberRecommendCommisionService.distributeBenefitsOrder(o,null);
//    }
//    
//    
//    
//    @Test
//    public void testSendMsg() {
//    	MQMessage m = new MQMessage();
//    	m.setMessage("123haha123");
//    	m.setTopic("1121test");
//    	m.setTag("");
//    	
//    	this.p.send(m);
//    }
//    
//    @Test
//    public void testUpdateStatus() {
//    	List<MemberRecommendCommision> list = Lists.newArrayList();
//    	
//    	MemberRecommendCommision c1 = new MemberRecommendCommision();
//    	c1.setId(4l);
//    	c1.setTransferId(123l);
//    	
//    	
//    	MemberRecommendCommision c2 = new MemberRecommendCommision();
//    	c2.setId(5l);
//    	c2.setTransferId(456l);
//    	
//    	list.add(c1);
//    	list.add(c2);
//    	
//    	this.memberRecommendCommisionService.updateDistributeStatus(list);
//    	
//    	
//    }
//    
//    @Test
//    public void testGetCurrentDateStat() {
//    	MemberFeeDayStat stat = this.memberFeeDayStatService.getMemberFeeDayStatByDay("2019-11-22");
//    	System.out.println(stat.toString());
//    }
//    
//    @Test()
//    public void updateStat() throws ParseException {
//    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    	
//    	MemberFeeDayStat stat1 = this.memberFeeDayStatService.getMemberFeeDayStatByDay("2019-11-22");
//    	
//    	
//    	MemberFeeDayStat stat = new MemberFeeDayStat();
//    	stat.setStatisticDate(sdf.parse("2019-11-22"));
//    	stat.setLockCount(11l);
//    	stat.setLockCommision(new BigDecimal(12.5));
//    	stat.setLockUnitQuantity(new BigDecimal(110));
//    	stat.setUnit(this.globalConfService.getPlatformToken().getName());
//    	stat.setVersion(stat1.getVersion());
//    	boolean flag = this.memberFeeDayStatService.updateDailyStat(stat);
//    	System.out.println("==================================" + flag);
//    }
    @Autowired
    private RestTemplate restTemplate;
    @Test
    public void t1() throws ClassNotFoundException, Exception {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date d = sdf.parse("2019-12-17 09:54:23");
    	long toTime = TimeUtil.getCurrentHourTime(d).getTime(),fromTime = TimeUtil.getHourTime(d, 2, "-").getTime();
		
		int timeInterval = 60;//K线小时线
		String url =  "https://www.silktraderdk.net/market/history?symbol=SLU/USDT&from="+ fromTime +"&to="+ toTime +"&resolution="+ timeInterval;
		 ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(url, String.class);
		
		String body = responseEntity.getBody();
	    HttpStatus statusCode = responseEntity.getStatusCode();
	    if(statusCode.is2xxSuccessful()) {
	    	System.out.println(body);
	    }
    
    }
    
    
    @Autowired
    private GlobalConfService confService;
    @Test
    public void testUnit() {
    	String k1 = this.confService.getMemberRecommendCommisionUnit();
    	String k2 = this.confService.getTokenExchangeFeeCommisionUnit();
    	
    	System.out.println("******************" + k1);
    	System.out.println("********************" + k2);
    }
    
}