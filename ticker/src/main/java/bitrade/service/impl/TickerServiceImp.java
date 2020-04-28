
package bitrade.service.impl;

import bitrade.client.CoinMarketClient;
import bitrade.client.TickerHttpClient;
import bitrade.config.CoinmarketcapProps;
import bitrade.config.CommonProps;
import bitrade.entity.CoinMarketApiData;
import bitrade.entity.JuHeCurrencyApiData;
import bitrade.service.TickerService;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@RefreshScope
@Slf4j
public class TickerServiceImp implements TickerService {


    @Autowired
    private TickerHttpClient tickerHttpClient;

    @Autowired
    private CoinMarketClient coinMarketClient;

//	//币种与id fild
//	String symbolsKeyFild="quotes:symbols";

    //法币分组存储
    String convertsKeyFild = "ticker:converts:";

    @Value("${api.juhe.key}")
    private String key;

    @Autowired
    CommonProps commonProps;

    @Autowired
    CoinmarketcapProps coinmarketcapProps;

    private int conversleep = 1000;

    /**
     * 缓存默认15分钟过期,保证被调用次数不会超过外部API限制请求次数
     */
    private TimedCache<String, JuHeCurrencyApiData> timedCache = CacheUtil.newTimedCache(15 * 60 * 1000);


    @Override
    public MessageRespResult<JuHeCurrencyApiData> currency(final String symbol, final String ToSymbol) {
        if (!commonProps.getConverts().containsKey(symbol)) {
            return MessageRespResult.error("请确认是否新添加币种，未同步添加");
        }
        JuHeCurrencyApiData currencyApiData = timedCache.get(symbol);
        if (currencyApiData == null) {
            String jsondata = tickerHttpClient.currency(key, symbol, ToSymbol);
            if ("no".equals(jsondata)) {
                //汇率接口请求失败
                return MessageRespResult.error("货币实时汇率换算接口Feign请求失败");
            }
            JSONObject jsonObj = JSONObject.parseObject(jsondata);
            if (jsonObj.getString("error_code").equals("0")) {
                JSONObject data = (JSONObject) jsonObj.getJSONArray("result").get(0);
                currencyApiData = JuHeCurrencyApiData.builder().baseCoin(data.getString("currencyF"))
                        .symbol(data.getString("currencyT"))
                        .rate(data.getString("exchange"))
                        .lastUpdated(System.currentTimeMillis()).build();
                timedCache.put(symbol, currencyApiData);
            } else {
                log.error("货币实时汇率换算结果获取失败,reason{},base:{},symbol:{}", jsonObj.get("reason"), symbol, ToSymbol);
                return MessageRespResult.error("货币实时汇率换算结果获取失败,不支持的货币种类");
            }
        }
        return MessageRespResult.success4Data(currencyApiData);
    }


    @Override
    @Async
    public void putListByTask(final String symbol) {
        //if (redisClient.hExists(symbolsKeyFild,symbol)) {
        for (Map.Entry<String, String> convertMap : coinmarketcapProps.getConverts().entrySet()) {
            String convert = convertMap.getValue();
            long stattime = System.currentTimeMillis();
            String jsondata = coinMarketClient.tickerList(convert);
            long endtime = System.currentTimeMillis();
            if ("no".equals(jsondata)) {
                long time = (endtime - stattime) / 1000;
                log.error("coinmarketcap数据获取," +
                                "convert:{}; stattime:{} ;endtime:{} ;时间消耗:time{}",
                        convert,
                        stattime,
                        endtime,
                        time);
            }
            returnReult(jsondata, convert, symbol);
            //控制评论暂停1秒
            try {
                Thread.sleep(conversleep);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//		}else {
//			log.error("端点将显示具体加密货币的代码数据symbol：{};请确认是否新添加币种，未同步添加",symbol);
//		}
    }


    /**
     * 行情数据返回 解析
     *
     * @param jsondat
     */
    @Async
    public void returnReult(String jsondata, String convert, String symbol) {
        if (!"no".equals(jsondata)) {
            JSONObject jsonObj = JSONObject.parseObject(jsondata);
            //data
            if (!StringUtils.isEmpty(jsonObj.get("data"))) {
                JSONArray jsonArray = jsonObj.getJSONArray("data");
                Iterator<Object> it = jsonArray.iterator();
                while (it.hasNext()) {
                    JSONObject data = (JSONObject) it.next();
                    if (symbol.equals(data.getString("symbol"))) {
                        if (!StringUtils.isEmpty(data.get("quote"))) {
                            JSONObject quotes = (JSONObject) data.get("quote");
                            if (!StringUtils.isEmpty(quotes.get(convert + ""))) {
                                JSONObject quotesData = (JSONObject) quotes.get(convert + "");
                                CoinMarketApiData apiDate = CoinMarketApiData.builder().id(data.get("id").toString()).quoteId(data.get("symbol") + "_" + convert.toUpperCase())
                                        .symbol(data.getString("symbol")).name(data.get("name").toString()).price(quotesData.get("price").toString()).lastUpdated(System.currentTimeMillis()).build();
                                //数据存储处理
                                String keyFild = convertsKeyFild + convert;
                                log.info("apiDate:{}", apiDate);
                                //存入redis
                                //redisClient.hset(keyFild,key,FastJsonUtil.objToJsonStr(dataMap));
                            }
                        }
                    }
                }
            }
        }
        /**
         * {
         *     "status": {
         *         "timestamp": "2020-03-23T16:45:47.078Z",
         *         "error_code": 0,
         *         "error_message": null,
         *         "elapsed": 17,
         *         "credit_count": 1,
         *         "notice": null
         *     },
         *     "data": [
         *         {
         *             "id": 1,
         *             "name": "Bitcoin",
         *             "symbol": "BTC",
         *             "slug": "bitcoin",
         *             "num_market_pairs": 7802,
         *             "date_added": "2013-04-28T00:00:00.000Z",
         *             "tags": [
         *                 "mineable"
         *             ],
         *             "max_supply": 21000000,
         *             "circulating_supply": 18281962,
         *             "total_supply": 18281962,
         *             "platform": null,
         *             "cmc_rank": 1,
         *             "last_updated": "2020-03-23T16:44:40.000Z",
         *             "quote": {
         *                 "CNY": {
         *                     "price": 43031.531498626726,
         *                     "volume_24h": 282251494695.48193,
         *                     "percent_change_1h": 0.237957,
         *                     "percent_change_24h": -1.72166,
         *                     "percent_change_7d": 14.02841499,
         *                     "market_cap": 786700823659.6968,
         *                     "last_updated": "2020-03-23T16:45:00.000Z"
         *                 }
         *             }
         *         }
         *     ]
         */

    }

}

