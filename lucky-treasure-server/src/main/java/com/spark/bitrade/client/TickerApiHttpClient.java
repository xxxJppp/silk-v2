package com.spark.bitrade.client;

import com.spark.bitrade.util.MessageRespResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *  抓取非小号ticker,接口请求限制每分钟
 * @author lc
 * @time 2019.12.18
 */
//@FeignClient(url = "https://public.bqi.com/public?code",fallbackFactory = TickerApiHttpClient.CoinPayApiHttpClientFallbackFactory.class)
public interface TickerApiHttpClient {


    @RequestMapping(value = "/v1/ticker", method = RequestMethod.GET)
    MessageRespResult<Object> getTicker( @RequestParam("code") String code);


    @Component
    class CoinPayApiHttpClientFallbackFactory implements FallbackFactory<TickerApiHttpClient> {
            @Override
            public TickerApiHttpClient create(Throwable throwable) {
                return new TickerApiHttpClient() {
                    @Override
                    public MessageRespResult<Object> getTicker(String code) {
                        return MessageRespResult.error(throwable.getMessage());
                    }
                };
            }
    }


}
