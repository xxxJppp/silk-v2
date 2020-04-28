package bitrade.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Component
@RefreshScope
@Data
@ConfigurationProperties(prefix="api.coinmarketcap")
public class CoinmarketcapProps {

    //#法定通用货币
    private Map<String, String> converts = new HashMap<String, String>();

    //url
    private String url;

    //errorEmail
    private String errorEmail;


//    //errorEmail
//    private int  conversleep=1000;

 }
