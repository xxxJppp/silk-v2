package bitrade;

import com.spark.bitrade.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TickerApplication extends BaseApplication {

    public static void main(String[] args) {SpringApplication.run(TickerApplication.class, args); }

}
