package com.spark.bitrade.controller;

import com.spark.bitrade.constant.SysConstant;
import com.spark.bitrade.constants.UcMsgCode;
import com.spark.bitrade.entity.Country;
import com.spark.bitrade.entity.LoginByPhone;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.CountryService;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.sms.KafkaSMSProvider;
import com.spark.bitrade.system.TemplateHandler;
import com.spark.bitrade.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * RegisterPhoneController
 *
 * @author wsy
 * @since 2019/6/14 14:40
 */
@Slf4j
@RestController
@RequestMapping("/api/v2")
@Api(description = "手机号注册控制器")
public class RegisterPhoneController extends ApiController {

    @Resource
    private MemberService memberService;
    @Resource
    private CountryService countryService;
    @Resource
    private KafkaSMSProvider smsProvider;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 注册支持的国家
     */
    @PostMapping(value = {"/support/country", "/no-auth/support/country"})
    public MessageRespResult allCountry() {
        return success(countryService.list());
    }

    /**
     * 检查用户名是否重复
     */
    @PostMapping(value = {"/register/check/username", "/no-auth/register/check/username"})
    public MessageRespResult checkUsername(String username) {
        MessageRespResult<Object> result = success();
        if (memberService.usernameIsExist(username)) {
            result = failed(UcMsgCode.ACTIVATION_FAILS_USERNAME);
        }
        return result;
    }

    /**
     * 发送短信验证码
     *
     * @author wsy
     * @since 2019-6-18 11:40:55
     */
    @ApiOperation(value = "注册短信验证码", tags = "发送短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "手机号码", name = "phone", dataType = "String"),
            @ApiImplicitParam(value = "短信类型: 0-普通 1-语言", name = "type", dataType = "String")
    })
    @PostMapping(value = {"/register/phone/code", "/no-auth/register/phone/code"})
    public MessageRespResult sendRegPhoneCheckCode(String country, String phone, @RequestParam(name = "type", defaultValue = "0") Integer type) {
        AssertUtil.isTrue(!"中国".equals(country) || ValidateUtil.isMobilePhone(phone), UcMsgCode.PHONE_EMPTY_OR_INCORRECT);
        // 检测手机号是否存在
//        AssertUtil.isTrue(!memberService.phoneIsExist(phone), UcMsgCode.PHONE_ALREADY_EXISTS);
        AssertUtil.notNull(country, UcMsgCode.REQUEST_ILLEGAL);

        // 校验国家
        Country countryEntity = countryService.findone(country);
        AssertUtil.notNull(countryEntity, UcMsgCode.REQUEST_ILLEGAL);

        // 生成短信验证码
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String key = SysConstant.PHONE_REG_CODE_PREFIX + phone;
        String code = valueOperations.get(key);
        if (code == null) {
            code = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
            valueOperations.set(key, code, 10, TimeUnit.MINUTES);
        }

        // 发送短信
        String platform = getAppId();

        if (type == 0) {
            String content = TemplateHandler.getInstance().handler("member_code.ftl", Collections.singletonMap("code", code));
            smsProvider.sendSms(platform, countryEntity.getAreaCode(), phone, content);
        } else {
            smsProvider.sendVoiceSms(platform, countryEntity.getAreaCode(), phone, code);
        }
        return success(UcMsgCode.SEND_SMS_SUCCESS);
    }

    /**
     * 手机注册
     *
     * @author wsy
     * @since 2019-6-18 11:45:09
     */
    @PostMapping(value = {"/register/phone", "/no-auth/register/phone"})
    public MessageRespResult registerByPhone(@Valid LoginByPhone loginByPhone,
                                             @RequestParam(name = "bind", defaultValue = "0") Integer bind,
                                             BindingResult bindingResult, HttpServletRequest request) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return MessageRespResult.error(result.getCode(), result.getMessage());
        }
        AssertUtil.isTrue(!"中国".equals(loginByPhone.getCountry()) || ValidateUtil.isMobilePhone(loginByPhone.getPhone().trim()), UcMsgCode.PHONE_EMPTY_OR_INCORRECT);

        // 校验验证码
        String phone = loginByPhone.getPhone();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Object code = valueOperations.get(SysConstant.PHONE_REG_CODE_PREFIX + phone);
        AssertUtil.notNull(code, UcMsgCode.VERIFICATION_CODE_NOT_EXISTS);
        AssertUtil.isTrue(code.toString().equals(loginByPhone.getCode()), UcMsgCode.VERIFICATION_CODE_INCORRECT);
        valueOperations.getOperations().delete(SysConstant.PHONE_REG_CODE_PREFIX + phone);

        // 校验用户是否存在
        boolean exist = memberService.phoneOrEmailIsExist(loginByPhone.getUsername(), phone, null);
        AssertUtil.isTrue(bind != 0 || !exist, UcMsgCode.USERNAME_ALREADY_EXISTS);

        // 判断邀请码是否正确
        if (StringUtils.hasText(loginByPhone.getPromotion())) {
            AssertUtil.isTrue(memberService.checkPromotion(loginByPhone.getPromotion()), UcMsgCode.PROMOTION_CODE_ERRO);
        }

        if (!exist) {
            // 注册
            Member member = memberService.register(loginByPhone, null, IpUtils.getIp(request));
            AssertUtil.notNull(member, UcMsgCode.REGISTRATION_FAILED);
            // 更新推荐关系统计数据
            memberService.updateInviterTotal(member.getId());
            return success(UcMsgCode.REGISTRATION_SUCCESS);
        } else if (StringUtils.hasText(loginByPhone.getPromotion())) {
            // 绑定
            Member member = memberService.bindPromotion(loginByPhone.getPhone(), null, loginByPhone.getPassword(), loginByPhone.getPromotion());
            AssertUtil.notNull(member, UcMsgCode.BIND_PROMOTION_FAIL);
            // 更新推荐关系统计数据
            memberService.updateInviterTotal(member.getId());
            return success(UcMsgCode.BIND_PROMOTION_SUCCESS);
        } else {
            return failed(UcMsgCode.USERNAME_ALREADY_EXISTS);
        }
    }
}
