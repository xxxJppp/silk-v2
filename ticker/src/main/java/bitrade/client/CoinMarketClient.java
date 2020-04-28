package bitrade.client;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
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
@FeignClient( name = "coinMarket", url = "${api.coinmarketcap.url}", fallbackFactory = CoinMarketClientFallbackFactory.class)
public interface CoinMarketClient {

    /**
     * 获取ticker列表最新
     *
     * @param convert
     * @return
     */
    @RequestMapping(value="/v1/cryptocurrency/listings/latest",method= RequestMethod.GET)
    String tickerList(@RequestParam("convert") String convert);



}


@Component
@Slf4j
class CoinMarketClientFallbackFactory implements FallbackFactory<CoinMarketClient> {

    @Override
    public CoinMarketClient create(Throwable throwable) {
        return  new CoinMarketClient(){

            @Override
            public String tickerList(String convert) {
                log.error(throwable.getMessage());
                //throwable.printStackTrace();
                return "no";
            }
		};
    }

}

