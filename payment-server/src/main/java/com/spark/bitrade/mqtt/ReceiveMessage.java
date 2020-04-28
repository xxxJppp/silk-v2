package com.spark.bitrade.mqtt;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.mqtt.annotations.MqttMapping;
import com.spark.bitrade.mqtt.annotations.MqttPayload;
import com.spark.bitrade.mqtt.annotations.MqttVariable;
import com.spark.bitrade.mqtt.mapping.ClientsHandler;
import com.spark.bitrade.mqtt.mapping.HeartbeatHandler;
import com.spark.bitrade.mqtt.mapping.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.core.MethodParameter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author wsy
 * @since 2019-7-18 16:08:26
 */
@Slf4j
@Component
public class ReceiveMessage {

    private AntPathMatcher matcher = new AntPathMatcher();
    private Map<String, HandlerMethod> mappingLookup = new HashMap<>();
    @Resource
    private ClientsHandler clientsHandler;
    @Resource
    private HeartbeatHandler heartbeatHandler;
    @Resource
    private TaskHandler taskHandler;

    @PostConstruct
    public void init() {
        initMqttHandler(heartbeatHandler, taskHandler, clientsHandler);
    }

    /**
     * 初始Mqtt地址映射关系
     */
    private void initMqttHandler(Object... objects) {
        Arrays.stream(objects).forEachOrdered(object -> {
            Class clazz = object.getClass();
            MqttMapping clazzMapping = (MqttMapping) clazz.getAnnotation(MqttMapping.class);
            Method[] methods = clazz.getMethods();
            Arrays.stream(methods).forEachOrdered(method -> {
                HandlerMethod handlerMethod = new HandlerMethod(object, method);
                if (handlerMethod.hasMethodAnnotation(MqttMapping.class)) {
                    MqttMapping mqttMapping = handlerMethod.getMethodAnnotation(MqttMapping.class);
                    if (clazzMapping == null) {
                        Arrays.stream(mqttMapping.value()).forEachOrdered(mapping -> {
                            String url = mapping.replaceAll("^/+", "");
                            log.info("Mqtt mapping [ {} ] into {}", url, method);
                            mappingLookup.put(url, handlerMethod);
                        });
                    } else {
                        Arrays.stream(clazzMapping.value()).forEachOrdered(p -> {
                            String prefix = p.replaceAll("/+$", "").replaceAll("^/+", "");
                            Arrays.stream(mqttMapping.value()).forEachOrdered(c -> {
                                String suffix = c.replaceAll("^/+", "");
                                String url = prefix + "/" + suffix;
                                log.info("Mqtt mapping [ {} ] into {}", url, method);
                                mappingLookup.put(url, handlerMethod);
                            });
                        });
                    }
                }
            });
        });
    }

    /**
     * 解析topic到对应的方法
     *
     * @param topic   频道
     * @param payload 数据
     */
    @Async
    public void resolveArgument(String topic, String payload) {
        try {
            Optional<String> optional = mappingLookup.keySet().stream().filter(i -> matcher.match(i, topic)).findFirst();
            if (optional.isPresent()) {
                String key = optional.get();
                HandlerMethod handlerMethod = mappingLookup.get(key);
                // 路径上的参数
                Map<String, String> result = matcher.extractUriTemplateVariables(key, topic);
                // 反射处理参数
                MethodParameter[] parameters = handlerMethod.getMethodParameters();
                Object[] args = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    MethodParameter parameter = parameters[i];
                    if (parameter.hasParameterAnnotation(MqttVariable.class)) {
                        MqttVariable mqttVariable = parameter.getParameterAnnotation(MqttVariable.class);
                        Class type = parameter.getParameterType();
                        String value = result.get(mqttVariable.value());
                        args[i] = ConvertUtils.convert(value, type);
                    } else if (parameter.hasParameterAnnotation(MqttPayload.class)) {
                        args[i] = JSON.parseObject(payload, parameter.getParameterType());
                    }
                }
                // 调用方法
                handlerMethod.getMethod().invoke(handlerMethod.getBean(), args);
            } else {
                log.error("not found mqtt handler bean");
            }
        } catch (Exception e) {
            log.error("handler topic exception", e);
        }

    }
}
