
package bitrade.service;


import bitrade.entity.JuHeCurrencyApiData;
import com.spark.bitrade.util.MessageRespResult;

/**
 * 行情处理
 *
 * @author lc
 * @date 2020/3/20 15:31
 */
public interface TickerService {

    /**
     * 货币实时汇率换算
     *
     * @param symbol   需要汇率换算的法币币种缩写
     * @param ToSymbol 指定进行换算的法币币种
     * @return
     */
    public MessageRespResult<JuHeCurrencyApiData> currency(final String symbol, final String ToSymbol);


    /**
     * 定时获取币种最新数据信息
     *
     * @param symbol
     * @return
     */
    public void putListByTask(final String symbol);

}
  


