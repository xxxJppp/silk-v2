package com.spark.bitrade.mqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spark.bitrade.config.MQTTProperties;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.service.SilkPayDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author wsy
 * @since 2019/7/30 9:45
 */
@Slf4j
@Component
public class MqttRestApi {

    @Resource
    private MQTTProperties mqttProperties;
    @Resource
    private SilkPayDeviceService silkPayDeviceService;

    @PostConstruct
    public void init() {
        silkPayDeviceService.updateAllOffline();
        // 获取在线设备
        this.connections();
    }

    /**
     * 获取当前在线设备
     */
    private void connections() {
        String url = String.format("nodes/%s/connections/", mqttProperties.getNode());
        String json = httpGet(mqttProperties.getRestApi() + url);
        log.info("result : {}", json);
        if (StringUtils.isNotBlank(json)) {
            JSONObject data = JSONObject.parseObject(json);
            JSONArray array = data.getJSONArray("data");
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = array.getJSONObject(i);
                String username = item.getString("username");
                String clientId = item.getString("client_id");
                silkPayDeviceService.updateOnlineState(username, clientId, BooleanEnum.IS_TRUE);
            }
        }
    }

    private String httpGet(String url) {
        String result = null;
        try {
            String auth = mqttProperties.getRestAppId() + ":" + mqttProperties.getRestAppKey();
            byte[] rel = Base64.encodeBase64(auth.getBytes());

            URL realUrl = new URL(url);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) realUrl.openConnection();
            httpUrlConnection.setRequestProperty("Authorization", "Basic " + new String(rel));
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setConnectTimeout(60000);
            httpUrlConnection.setReadTimeout(60000);
            httpUrlConnection.connect();

            if (200 == httpUrlConnection.getResponseCode()) {
                try (InputStream is = httpUrlConnection.getInputStream();
                     ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while (-1 != (len = is.read(buffer))) {
                        os.write(buffer, 0, len);
                        os.flush();
                    }
                    result = os.toString("UTF-8");
                }
            } else {
                log.error("mqtt rest api request failure: {}", httpUrlConnection.getResponseCode());
            }
        } catch (Exception e) {
            log.error("http request exception", e);
        }
        return result;
    }
}
