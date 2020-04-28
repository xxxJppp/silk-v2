package com.spark.bitrade.controller;

import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.BooleanStringEnum;
import com.spark.bitrade.constant.LoginType;
import com.spark.bitrade.constant.SysConstant;
import com.spark.bitrade.constants.UcMsgCode;
import com.spark.bitrade.entity.Location;
import com.spark.bitrade.entity.LoginInfo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberSecuritySet;
import com.spark.bitrade.jwt.HttpJwtToken;
import com.spark.bitrade.jwt.MemberClaim;
import com.spark.bitrade.service.CountryService;
import com.spark.bitrade.service.MemberLoginHistoryService;
import com.spark.bitrade.service.MemberSecuritySetService;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.system.GeetestLib;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.IpUtils;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * LoginController
 *
 * @author archx
 * @since 2019/6/11 17:36
 */
@Slf4j
@RestController
@RequestMapping("/api/v2")
@Api(description = "登录登出控制器")
public class LoginController extends ApiController {

    @Resource
    private GeetestLib gtSdk;
    @Resource
    private MemberService memberService;
    @Resource
    private CountryService countryService;
    @Resource
    private MemberSecuritySetService memberSecuritySetService;
    @Resource
    private MemberLoginHistoryService memberLoginHistoryService;
    @Value("${geetest.enabled:false}")
    private boolean geetestEnabled; //极验证开关
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 登录接口
     *
     * @since 2019-6-14 11:32:33
     *  
     */
    @ApiOperation(value = "APP登录接口", tags = "登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户名", name = "username", dataType = "String"),
            @ApiImplicitParam(value = "加密密码", name = "password", dataType = "String"),
            @ApiImplicitParam(value = "登录类型,0:web登录 1：Android登录 2：IOS登录 3：API接入", name = "type", dataType = "Enum")
    })
    @PostMapping(value = {"/login", "/no-auth/login"})
    public MessageRespResult<Object> login(HttpServletRequest request, String username, String password, LoginType type) {
        AssertUtil.hasText(username, UcMsgCode.MISSING_USERNAME);
        AssertUtil.hasText(password, UcMsgCode.MISSING_PASSWORD);

        if (geetestEnabled) {
            String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
            String validate = request.getParameter(GeetestLib.fn_geetest_validate);
            String secCode = request.getParameter(GeetestLib.fn_geetest_seccode);

            String ip = IpUtils.getIp(request);
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("user_id", username);
            //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
            param.put("client_type", "web");
            //传输用户请求验证时所携带的IP。备注：多IP请求会报错
            param.put("ip_address", ip.contains(",") ? ip.substring(0, ip.indexOf(",")) : ip);
            int gtResult = gtSdk.enhencedValidateRequest(challenge, validate, secCode, param);
            AssertUtil.isTrue(gtResult == 1, UcMsgCode.GEETEST_FAIL);
        }

        return loginCheck(request, username, password, type);
    }

    @RequestMapping("/check/login")
    public MessageRespResult<LoginInfo> checkLogin(@MemberAccount Member member) {
        //设置登录成功后的返回信息
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setLocation(new Location().convert(member));
        loginInfo.setMemberLevel(member.getMemberLevel());
        loginInfo.setUsername(member.getUsername());
        loginInfo.setRealName(member.getRealName());
        loginInfo.setCountry(countryService.findone(member.getLocal()));
        loginInfo.setAvatar(member.getAvatar());
        loginInfo.setPromotionCode(member.getPromotionCode());
        loginInfo.setId(member.getId());
        loginInfo.setPhone(member.getMobilePhone());
        return success(loginInfo);
    }

    @GetMapping("/logout")
    public MessageRespResult<String> logout(@MemberAccount Member member) {
        log.info("[{} -> {} -> {}] logout", member.getId(), member.getUsername(), member.getEmail());
        return success("ok");
    }

    /***
     * 登录验证逻辑处理
     *@author wsy
     *@since 2019-6-14 15:14:28
     */
    private MessageRespResult<Object> loginCheck(HttpServletRequest request, String username, String password, LoginType type) {
        Member member = memberService.login(username, password);
        String thirdMark = getAppId();

        // 需要安全验证的才查询安全权限设置
        MemberSecuritySet memberSecuritySet = memberSecuritySetService.findByMemberId(member.getId());

        MemberClaim claim = MemberClaim.builder()
                .userId(member.getId())
                .username(member.getUsername())
                //系统时间差兼容，提前20秒
                .issuedAt(new Date(System.currentTimeMillis() - 20 * 1000))
                .audience(thirdMark)
                .build();
        String token = HttpJwtToken.getInstance().createTokenWithClaim(claim);

        // 设备属性
        Map<String, String> property = new HashMap<>();
        property.put("producers", request.getHeader("producers"));
        property.put("systemVersion", request.getHeader("systemVersion"));
        property.put("model", request.getHeader("model"));
        property.put("uuid", request.getHeader("uuid"));
        property.put("loginIP", IpUtils.getIp(request));

        // 异步记录登录日志
        memberLoginHistoryService.saveHistory(member, property, type, thirdMark, BooleanEnum.IS_FALSE);

        // 设置登录成功后的返回信息
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setToken(token);
        loginInfo.setId(member.getId());
        loginInfo.setUsername(member.getUsername());
        loginInfo.setEmail(member.getEmail());
        loginInfo.setPhone(member.getMobilePhone());
        loginInfo.setAvatar(member.getAvatar());
        loginInfo.setRealName(member.getRealName());
        loginInfo.setMemberLevel(member.getMemberLevel());
        loginInfo.setCountry(countryService.findone(member.getLocal()));
        loginInfo.setLocation(new Location().convert(member));
        loginInfo.setPromotionCode(member.getPromotionCode());
        // 没有设置安全校验的默认为不校验
        if (memberSecuritySet == null) {
            loginInfo.setIsOpenPhoneLogin(BooleanStringEnum.IS_FALSE);
            loginInfo.setIsOpenGoogleLogin(BooleanStringEnum.IS_FALSE);
            loginInfo.setIsOpenPhoneUpCoin(BooleanStringEnum.IS_FALSE);
            loginInfo.setIsOpenGoogleUpCoin(BooleanStringEnum.IS_FALSE);
        } else {
            loginInfo.setIsOpenPhoneLogin(memberSecuritySet.getIsOpenPhoneLogin());
            loginInfo.setIsOpenGoogleLogin(memberSecuritySet.getIsOpenGoogleLogin());
            loginInfo.setIsOpenPhoneUpCoin(memberSecuritySet.getIsOpenPhoneUpCoin());
            loginInfo.setIsOpenGoogleUpCoin(memberSecuritySet.getIsOpenGoogleUpCoin());
        }

        // 兼容登录接口
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        if (thirdMark != null) {
            valueOperations.set(SysConstant.MEMBER_LOGOUT + loginInfo.getId() + ":" + thirdMark, "0", 30, TimeUnit.MINUTES);
        } else {
            valueOperations.set(SysConstant.MEMBER_LOGOUT + loginInfo.getId(), "0", 30, TimeUnit.MINUTES);
        }
        return success(loginInfo);
    }

}
