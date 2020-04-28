package com.spark.bitrade.config;

import com.spark.bitrade.mqtt.ReceiveMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;
import java.util.Date;

/**
 * MQTT配置
 *
 * @author wsy
 */
@Slf4j
@Configuration
public class MQTTConfiguration {

    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel"; // 发布的bean名称
    private static final String CHANNEL_NAME_IN = "mqttInboundChannel"; // 订阅的bean名称
    private static final String WILL_TOPIC = "disconnected";
    private static final byte[] WILL_DATA = "disconnected".getBytes();
    private static final boolean WILL_RETAINED = true;
    private static final int WILL_QOS = 2;
    @Resource
    private MQTTProperties mqttProperties;
    @Resource
    private ReceiveMessage receiveMessage;

    /**
     * MQTT客户端
     *
     * @return {@link MqttPahoClientFactory}
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setCleanSession(true);
        factory.setUserName(StringUtils.isNotBlank(mqttProperties.getUsername()) ? mqttProperties.getUsername() : null);
        factory.setPassword(StringUtils.isNotBlank(mqttProperties.getPassword()) ? mqttProperties.getPassword() : null);
        factory.setServerURIs(StringUtils.split(mqttProperties.getServerUri(), ","));
        factory.setConnectionTimeout(10);
        factory.setKeepAliveInterval(2);
        DefaultMqttPahoClientFactory.Will will = new DefaultMqttPahoClientFactory.Will(WILL_TOPIC, WILL_DATA, WILL_QOS, WILL_RETAINED);
        factory.setWill(will);
        return factory;
    }

    /**
     * MQTT信息通道（生产者）
     *
     * @return {@link MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT消息处理器（生产者）
     *
     * @return {@link MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttProperties.getProducerClientId(), mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic((mqttProperties.getProducerDefaultTopic()));
        messageHandler.setDefaultRetained(true);
        messageHandler.setCompletionTimeout(5000);
        return messageHandler;
    }

    /**
     * MQTT信息通道（消费者）
     *
     * @return {@link MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }


    /**
     * MQTT消息订阅绑定（消费者）
     *
     * @return {@link MessageProducer}
     */
    @Bean
    public MessageProducer inbound(MqttPahoClientFactory mqttClientFactory, MessageChannel mqttInboundChannel) {
        // 可以同时消费（订阅）多个Topic
        String[] topics = StringUtils.split((mqttProperties.getConsumerDefaultTopic()), ",");
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getConsumerClientId(), mqttClientFactory, topics);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInboundChannel);
        return adapter;
    }

    /**
     * MQTT消息处理器（消费者）
     *
     * @return {@link MessageHandler}
     */
    @Bean("messageHandler")
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    public MessageHandler handler() {
        return message -> {
            try {
                String id = message.getHeaders().getId().toString();
                String topic = message.getHeaders().get(MqttHeaders.TOPIC, String.class);
                Integer qos = message.getHeaders().get(MqttHeaders.QOS, Integer.class);
                String time = DateFormatUtils.format(new Date(message.getHeaders().getTimestamp()), "yyyy-MM-dd HH:mm:ss");
                log.info("收到消息[ {} -> {} ] [ {} - {} ] ：{}", time, id, qos, topic, message.getPayload());
                // 异步调用
                receiveMessage.resolveArgument(topic, (String) message.getPayload());
            } catch (Exception e) {
                log.error("处理消息异常", e);
            }
        };
    }
}
