package com.spark.bitrade.controller;

import com.spark.bitrade.config.MQTTProperties;
import com.spark.bitrade.service.SilkPayDeviceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wsy
 * @since 2019/7/23 14:17
 */
@Slf4j
@Api("MQTT认证")
@RestController
@RequestMapping("api/v2/no-auth/mqtt")
public class MqttManagerController extends ApiController {

    private AntPathMatcher matcher = new AntPathMatcher();
    @Resource
    private MQTTProperties mqttProperties;
    @Resource
    private SilkPayDeviceService silkPayDeviceService;

    /**
     * 普通用户认证
     *
     * @param clientId  设备标识
     * @param username  登录账号
     * @param password  登录密码
     * @param ipAddress 登录IP地址
     * @return 认证结果
     */
    @PostMapping("/auth")
    public String auth(@RequestParam(value = "clientid") String clientId,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "password") String password,
                       @RequestParam(value = "ipaddress", required = false) String ipAddress,
                       HttpServletResponse response) {
        log.info("[ {} ] mqtt auth: {}, {}, {}", ipAddress, clientId, username, password);
        boolean superUser = Objects.equals(mqttProperties.getUsername(), username);
        boolean superPass = Objects.equals(mqttProperties.getPassword(), password);
        boolean ok = (superUser && superPass) || silkPayDeviceService.login(clientId, username, password);
        response.setStatus(ok ? HttpStatus.OK.value() : HttpStatus.UNAUTHORIZED.value());
        return ok ? "success" : "failure";
    }

    /**
     * 超级用户认证
     *
     * @param username  登录账号
     * @param clientId  设备标识
     * @param ipAddress 登录IP地址
     * @return 认证结果
     */
    @PostMapping("/superuser")
    public String superuser(@RequestParam(value = "clientid") String clientId,
                            @RequestParam(value = "username") String username,
                            @RequestParam(value = "ipaddress", required = false) String ipAddress,
                            HttpServletResponse response) {
        log.info("[ {} ] mqtt superuser: {}, {}", ipAddress, clientId, username);
        boolean isSuper = Objects.equals(username, mqttProperties.getUsername());
        response.setStatus(isSuper ? HttpStatus.OK.value() : HttpStatus.FORBIDDEN.value());
        return isSuper ? "success" : "failure";
    }

    /**
     * 权限校验
     *
     * @param username  登录账号
     * @param clientId  设备标识
     * @param access    类型：1 = sub, 2 = pub
     * @param topic     频道
     * @param ipAddress 客户端IP地址
     * @return 校验结果
     */
    @PostMapping("/acl")
    public String acl(@RequestParam(value = "clientid") String clientId,
                      @RequestParam(value = "username") String username,
                      @RequestParam(value = "access") Integer access,
                      @RequestParam(value = "topic") String topic,
                      @RequestParam(value = "ipaddr", required = false) String ipAddress,
                      HttpServletResponse response) {
        log.info("[ {} ] mqtt acl: {}, {}, {}, {}", ipAddress, access, username, clientId, topic);
        boolean isSuper = Objects.equals(username, mqttProperties.getUsername());
        boolean isOk = isSuper || checkAcl(access, clientId, topic);
        response.setStatus(isOk ? HttpStatus.OK.value() : HttpStatus.FORBIDDEN.value());
        return isOk ? "success" : "failure";
    }

    /**
     * 校验是否具有权限
     *
     * @param access   类型：1 = sub, 2 = pub
     * @param clientId 设备标识
     * @param topic    频道
     */
    private boolean checkAcl(int access, String clientId, String topic) {
        String[] acl = access == 1 ? mqttProperties.getClientSubAcl() : mqttProperties.getClientPubAcl();
        Optional<String> optional = Arrays.stream(acl).filter(i -> checkAcl(clientId, i, topic)).findFirst();
        log.info("[{} : {}] {} ==> {}", clientId, access == 1 ? "sub" : "pub", topic, optional.orElse(null));
        return optional.isPresent();
    }

    /**
     * 校验单条规则
     *
     * @param clientId 设备标识
     * @param acl      规则
     * @param topic    频道
     */
    private boolean checkAcl(String clientId, String acl, String topic) {
        String pattern = acl.replace("#", "{all}");
        if (matcher.match(pattern, topic)) {
            Map<String, String> result = matcher.extractUriTemplateVariables(pattern, topic);
            log.info("extract uri variables ==> {}", result);
            return Objects.equals(clientId, result.get("clientId")) || Objects.equals(topic, "group/#");
        }
        return false;
    }
}
