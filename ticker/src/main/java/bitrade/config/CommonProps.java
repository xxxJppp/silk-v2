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
@ConfigurationProperties(prefix="api")
public class CommonProps {

    //支持换算的法币币种
    private Map<String, String> converts = new HashMap<String, String>();



 }
