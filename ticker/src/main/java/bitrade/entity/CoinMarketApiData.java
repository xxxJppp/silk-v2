package bitrade.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinMarketApiData {

    private  String id;

    //引用键,如BTC_CNY
    private  String quoteId;

    //币种英文名称
    private String name;

    //币种的简称 
    private String symbol;

    //最新价格 
    private String price;

    //行情更新时间(10位unix时间戳)
    private Long lastUpdated;
}
