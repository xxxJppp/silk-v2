package com.spark.bitrade.config;

import com.spark.bitrade.dsc.AlarmMonitor;
import com.spark.bitrade.dsc.DscContext;
import com.spark.bitrade.dsc.DscValidateHelper;
import com.spark.bitrade.dsc.Transfer;
import com.spark.bitrade.dsc.api.MessagePusher;
import com.spark.bitrade.dsc.api.SecurityKeyManager;
import com.spark.bitrade.dsc.spring.boot.autoconfigure.TransferCustomizer;
import com.spark.bitrade.dsc.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dsc配置
 */
@Configuration
public class DscConfiguration {

    @Value("${keys.publicKey}")
    private String publicKeyBase64;
    @Value("${keys.privateKey}")
    private String privateKeyBase64;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 自定义密钥管理器
     *
     * @return manager
     */
    @Bean
    public SecurityKeyManager buildSecurityKeyManager()
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        final RSAPublicKey publicKey = RSAUtils.getPublicKey(publicKeyBase64);
        final RSAPrivateKey privateKey = RSAUtils.getPrivateKey(privateKeyBase64);
        return new SecurityKeyManager() {
            @Override
            public RSAPublicKey getPublicKey() {
                return publicKey;
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                return privateKey;
            }
        };
    }

    /**
     * 告警消息推送实现
     *
     * @return pusher
     */
    @Bean // 使用默认的Kafka实现
    public MessagePusher buildMessagePusher() {
        return new DscMessagePusher();
    }

    /**
     * 添加自定义转换器，转换特定字段属性值为字符串
     *
     * @return customizer
     */
    @Bean
    public TransferCustomizer buildTransferCustomizer() {
        return registry -> {
            Transfer transfer = new Transfer() {
                @Override
                public Class<?> type() {
                    return BigDecimal.class;
                }

                @Override
                public String to(Object value) {
                    if (value == null) {
                        return "0";
                    }
                    BigDecimal val = (BigDecimal) value;
                    // 去除多余的0
                    return val.stripTrailingZeros().toPlainString();
                }
            };
            registry.add(transfer);

            // 日期转换
            registry.add(new Transfer() {
                @Override
                public Class<?> type() {
                    return Date.class;
                }

                @Override
                public String to(Object value) {
                    if (value instanceof Date) {
                        return dateFormat.format((Date) value);
                    }
                    return null;
                }
            });
        };

    }

    @Bean
    public DscValidateHelper buildDscValidateHelper(DscContext context, MessagePusher messagePusher) {
        return new DscValidateHelper(context, messagePusher);
    }

    @Slf4j
    public static class DscMessagePusher implements MessagePusher {

        @Override
        public void push(AlarmMonitor msg) {
            log.error("告警信息 >> {}", msg);
        }
    }
}
