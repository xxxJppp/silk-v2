package bitrade.controller;

import bitrade.client.TickerHttpClient;
import bitrade.entity.JuHeCurrencyApiData;
import bitrade.service.TickerService;
import com.google.common.base.Ticker;
import com.spark.bitrade.controller.CommonController;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.entity.ExchangeFastAccount;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.spark.bitrade.util.MessageRespResult.success;


/**
 * 提供法币汇率转换等数据
 *
 * @author lc
 * @date 2020/3/20 15:06
 */
@RestController
@RequestMapping("api")
public class TickerController extends CommonController {

    @Autowired
    private TickerService tickerService;

    //转换汇率前的货币代码
    private String toSymbol = "USD";

    /**
     * 指定法币币种与美元(USD)的汇率换算
     * 该方法主要提供给market获取法币与USD汇率
     *
     * @param symbol 需要进行换算的法币币种缩写
     * @return
     */
    @GetMapping(value = "/v1/exchange/currency")
    public MessageRespResult<JuHeCurrencyApiData> currency(@RequestParam("symbol") String symbol) {
        if (symbol.equals("USD")) {
            return MessageRespResult.error("不支持相同币种的汇率换算");
        }
        return tickerService.currency(symbol, toSymbol);
    }


    /**
     * (未使用)
     * 抓取coinmarketcap行情网站上的 数据
     * 配置文件converts法币列表和数字货币的汇率同步
     *
     * @param symbol
     */
    @PostMapping(value = "/v1/checkList")
    public void putListByTask(@RequestParam("symbol") String symbol) {
        tickerService.putListByTask(symbol);
    }


}
