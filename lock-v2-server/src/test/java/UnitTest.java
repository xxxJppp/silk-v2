

import java.math.BigDecimal;
import java.text.ParseException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.spark.bitrade.LockApplication;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.LockCoinDetailService;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=LockApplication.class)
//@Transactional
public class UnitTest {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private LockCoinDetailService lockCoinDetailService;
   
    
    @Test()
    public void lock() throws ParseException {
    	Member member =new Member();
    	member.setId(360515l);
//    	LockCoinDetail lockCoinDetail = lockCoinDetailService.simplelock(member, LockType.BY_MEMBER_LOCK,TransactionType.BUY_MEMBER_LOCK, new BigDecimal(234), "BT",20,0L);
//    	System.out.println(lockCoinDetail);
    }

    
}