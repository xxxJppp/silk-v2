package bitrade.client;

import com.spark.bitrade.util.ExceptionUitl;
import feign.Headers;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *  远程调用客户端
 *
 * @author lc
 * @since 2020-03-20
 */
@FeignClient( name = "juhe", url = "${api.juhe.url}", fallbackFactory = TickerHttpClientFallbackFactory.class)
public interface TickerHttpClient {

    /**
     * 实时汇率转换
     *
     * @param from
     * @param to
     * @return
     */
    @RequestMapping(value="/onebox/exchange/currency",method= RequestMethod.GET)
    String currency(@RequestParam("key") String key,@RequestParam("from") String from,@RequestParam("to") String to);



}


@Component
@Slf4j
class TickerHttpClientFallbackFactory implements FallbackFactory<TickerHttpClient> {

    @Override
    public TickerHttpClient create(Throwable throwable) {
        return  new TickerHttpClient(){

            @Override
            public String currency(String key,String from,String to) {
                log.error(throwable.getMessage());
                //throwable.printStackTrace();
                return "no";
            }
		};
    }

}

