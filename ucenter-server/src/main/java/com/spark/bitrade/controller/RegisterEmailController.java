package com.spark.bitrade.controller;

import com.spark.bitrade.constant.SysConstant;
import com.spark.bitrade.constants.UcMsgCode;
import com.spark.bitrade.email.KafkaEmailProvider;
import com.spark.bitrade.entity.LoginByEmail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.system.TemplateHandler;
import com.spark.bitrade.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RegisterEmailController
 *
 * @author wsy
 * @since 2019/6/18 11:26
 */
@Slf4j
@RestController
@RequestMapping("/api/v2/register")
@Api(description = "邮件注册控制器")
public class RegisterEmailController extends ApiController {

    @Resource
    private MemberService memberService;
    @Resource
    private KafkaEmailProvider emailProvider;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 检查用户名是否重复
     */
    @ApiOperation(value = "注册邮箱验证码", tags = "发送邮箱验证码")
    @ApiImplicitParam(value = "邮箱地址", name = "email", dataType = "String")
    @PostMapping(value = {"/email/check","/email/no-auth/check"})
    public MessageRespResult checkEmail(String email) {
        MessageRespResult<Object> result = success();
        if (memberService.emailIsExist(email)) {
            result = failed(UcMsgCode.ACTIVATION_FAILS_USERNAME);
        }
        return result;
    }

    /**
     * 发送邮件注册验证码
     *
     * @author wsy
     * @since 2019-6-18 11:40:55
     */
    @ApiOperation(value = "注册邮箱验证码", tags = "发送邮箱验证码")
    @ApiImplicitParam(value = "邮箱地址", name = "email", dataType = "String")
    @PostMapping(value = {"/email/code","/email/no-auth/code"})
    public MessageRespResult sendRegEmailCheckCode(String email) {
        AssertUtil.isTrue(ValidateUtil.isEmail(email), UcMsgCode.WRONG_EMAIL);
        // 验证邮箱是否已存在
//        AssertUtil.isTrue(!memberService.emailIsExist(email), UcMsgCode.EMAIL_ALREADY_BOUND);

        // 生成邮箱验证码
        String key = SysConstant.EMAIL_REG_CODE_PREFIX + email;
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String code = valueOperations.get(key);
        if (code == null) {
            code = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
            valueOperations.set(key, code, 10, TimeUnit.MINUTES);
        }

        // 发送邮箱验证码
        String platform = getAppId();
        SpringContextUtil.getBean(RegisterEmailController.class).sendRegEmailCode(platform, email, code);
        return success(UcMsgCode.SENT_SUCCESS_TEN);
    }

    /**
     * 邮箱注册
     *
     * @param bind 是否为绑定操作 0-非绑定操作（存在则禁止操作，不存在则注册），1-绑定（存在则绑定，不存在则注册）
     * @author wsy
     * @since 2019-6-18 11:41:17
     */
    @PostMapping(value = {"/email","/email/", "/email/no-auth/"})
    public MessageRespResult registerByEmail(@Valid LoginByEmail loginByEmail,
                                             @RequestParam(name = "bind", defaultValue = "0") Integer bind,
                                             BindingResult bindingResult, HttpServletRequest request) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return MessageRespResult.error(result.getCode(), result.getMessage());
        }

        String email = loginByEmail.getEmail();
        AssertUtil.notNull(loginByEmail.getCode(), UcMsgCode.VERIFICATION_CODE_NOT_EXISTS);

        // 校验用户是否存在
        boolean exist = memberService.phoneOrEmailIsExist(loginByEmail.getUsername(), null, email);
        AssertUtil.isTrue(bind != 0 || !exist, UcMsgCode.USERNAME_ALREADY_EXISTS);

        // 校验验证码
        String key = SysConstant.EMAIL_REG_CODE_PREFIX + email;
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String code = valueOperations.get(key);
        AssertUtil.notNull(code, UcMsgCode.VERIFICATION_CODE_NOT_EXISTS);
        AssertUtil.isTrue(code.equals(loginByEmail.getCode()), UcMsgCode.VERIFICATION_CODE_INCORRECT);

        // 判断邀请码是否正确
        if (StringUtils.hasText(loginByEmail.getPromotion())) {
            AssertUtil.isTrue(memberService.checkPromotion(loginByEmail.getPromotion()), UcMsgCode.PROMOTION_CODE_ERRO);
        }

        if (!exist) {
            Member member = memberService.register(null, loginByEmail, IpUtils.getIp(request));
            AssertUtil.notNull(member, UcMsgCode.REGISTRATION_FAILED);
            // 更新推荐关系统计数据
            memberService.updateInviterTotal(member.getId());
            return success(UcMsgCode.REGISTRATION_SUCCESS);
        } else if (StringUtils.hasText(loginByEmail.getPromotion())) {
            Member member = memberService.bindPromotion(null, loginByEmail.getEmail(), loginByEmail.getPassword(), loginByEmail.getPromotion());
            AssertUtil.notNull(member, UcMsgCode.REGISTRATION_FAILED);
            // 更新推荐关系统计数据
            memberService.updateInviterTotal(member.getId());
            return success(UcMsgCode.BIND_PROMOTION_SUCCESS);
        } else {
            return failed(UcMsgCode.EMAIL_ALREADY_BOUND);
        }
    }

    @Async
    public void sendRegEmailCode(String platform, String email, String code) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("platform", platform);
            model.put("code", code);
            String html = TemplateHandler.getInstance().handler("registerCodeEmail.ftl", model);
            log.info("send email for {}, content:{}", email, html);
            emailProvider.sentEmailHtml(email, "title", html);
        } catch (Exception e) {
            log.error("处理邮件模板失败");
        }
    }
}
