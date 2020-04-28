package bitrade.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JuHeCurrencyApiData {

    //转换汇率前的货币代码
    private String baseCoin;

    //转换汇率后的货币代码
    private String symbol;

    //汇率结果
    private String rate;

    //行情更新时间(10位unix时间戳)
    private Long lastUpdated;

}
