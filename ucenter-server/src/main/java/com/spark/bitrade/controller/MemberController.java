package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.SysConstant;
import com.spark.bitrade.constants.UcMsgCode;
import com.spark.bitrade.email.KafkaEmailProvider;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.service.CountryService;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.service.SlpMemberPromotionService;
import com.spark.bitrade.sms.KafkaSMSProvider;
import com.spark.bitrade.system.TemplateHandler;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.GeneratorUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import com.spark.bitrade.vo.PromotionMemberVO;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * (Member)控制层
 *
 * @author archx
 * @since 2019-06-11 17:28:21
 */
@Slf4j
@RestController
@RequestMapping("api/v2/member")
@Api(description = "会员控制器")
public class MemberController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private MemberService memberService;
    @Resource
    private SlpMemberPromotionService slpMemberPromotionService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private CountryService countryService;
    @Resource
    private KafkaSMSProvider smsProvider;
    @Resource
    private KafkaEmailProvider emailProvider;

    /**
     * 处理会员的密码
     *
     * @param inputPassword 会员输入的密码
     * @param salt          盐
     * @return
     * @author yangch
     * @since 2019-06-20 14:05:18
     */
    @RequestMapping(value = {"/simpleHashPassword","/no-auth/simpleHashPassword"}, method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<String> simpleHashPassword(@RequestParam("inputPassword") String inputPassword,
                                                        @RequestParam("salt") String salt) {
        return success(this.memberService.simpleHashPassword(inputPassword, salt));
    }

    /**
     * 密码确认
     *
     * @param storagePassword 存储的密码
     * @param inputPassword   会员输入的密码
     * @param salt            盐
     * @return true=一样/false=不一样
     * @author yangch
     * @since 2019-06-20 14:05:18
     */
    @RequestMapping(value = {"/confirmPassword","/no-auth/confirmPassword"}, method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Boolean> confirmPassword(@RequestParam("storagePassword") String storagePassword,
                                                      @RequestParam("inputPassword") String inputPassword,
                                                      @RequestParam("salt") String salt) {
        return MessageRespResult.success4Data(this.memberService.confirmPassword(storagePassword, inputPassword, salt));
    }


    /**
     * 重置密码
     */
    @ApiOperation(value = "忘记密码发送验证码", tags = "个人信息设置")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "0为手机验证,1为邮箱验证", name = "mode", dataType = "String"),
            @ApiImplicitParam(value = "手机或邮箱", name = "account", dataType = "String"),
    })
    @PostMapping(value={"/reset/login/password/code","/no-auth/reset/login/password/code"})
    public MessageRespResult resetCode(int mode, String account) {
        Member member = memberService.findByPhoneOrEmail(mode, account);
        AssertUtil.notNull(member, UcMsgCode.MEMBER_NOT_EXISTS);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String code = valueOperations.get(SysConstant.RESET_PASSWORD_CODE_PREFIX + account);
        if (code == null) {
            code = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
            valueOperations.set(SysConstant.RESET_PASSWORD_CODE_PREFIX + account, code, 10, TimeUnit.MINUTES);
        }

        String platform = getAppId();
        // 发送短信
        if (mode == 0) {
            Country country = countryService.findone(member.getLocal());
            String content = TemplateHandler.getInstance().handler("member_code.ftl", Collections.singletonMap("code", code));
            smsProvider.sendSms(platform, country.getAreaCode(), account, content);
        } else if (mode == 1) {
            // 发送邮件
            SpringContextUtil.getBean(MemberController.class).sendRegEmailCode(platform, account, code);
        } else {
            return failed();
        }
        return success();
    }

    /**
     * 修改登录密码
     *
     * @param mode     0为手机验证,1为邮箱验证
     * @param account  手机或邮箱
     * @param code     验证码
     * @param password 新密码
     */
    @ApiOperation(value = "忘记密码后重置密码", tags = "个人信息设置")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "0为手机验证,1为邮箱验证", name = "mode", dataType = "String"),
            @ApiImplicitParam(value = "手机或邮箱", name = "account", dataType = "String"),
            @ApiImplicitParam(value = "验证码", name = "code", dataType = "String"),
            @ApiImplicitParam(value = "新密码", name = "password", dataType = "String")
    })
    @PostMapping(value={"/reset/login/password","/no-auth/reset/login/password"})
    public MessageRespResult resetPassword(int mode, String account, String code, String password) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Object redisCode = valueOperations.get(SysConstant.RESET_PASSWORD_CODE_PREFIX + account);
        AssertUtil.isTrue(Objects.equals(redisCode, code), UcMsgCode.VERIFICATION_CODE_INCORRECT);

        Member member = memberService.findByPhoneOrEmail(mode, account);
        AssertUtil.notNull(member, UcMsgCode.MEMBER_NOT_EXISTS);

        // 修改密码
        boolean ret = memberService.resetPassword(member, password);

        valueOperations.getOperations().delete(SysConstant.RESET_PASSWORD_CODE_PREFIX + account);
        return ret ? success() : failed();
    }

    /**
     * 获取直推用户人数
     */
    @ApiOperation(value = "获取直推用户人数", tags = "获取直推用户人数")
    @PostMapping("/promotion/record")
    public MessageRespResult getPromotionCount(@MemberAccount Member member,
                                               @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<PromotionMemberDTO> count = slpMemberPromotionService.findPromotionList(member.getId(), pageSize, pageNo);
        return success(count);
    }

    /**
     * 根据用户id获取用户
     */
    @ApiOperation(value = "根据用户id获取用户", tags = "根据用户id获取用户")
    @PostMapping(value={"/listMembersByIds","/no-auth/listMembersByIds"})
    public MessageRespResult<List<Member>> listMembersByIds(@RequestBody List<Long> memberIds) {
        return success(memberService.listMembersByIds(memberIds));
    }

    /**
     * 根据用手机号或者邮箱获取用户 0为手机,1为邮箱
     */
    @ApiOperation(value = "根据用手机号或者邮箱获取用户", tags = "根据用手机号或者邮箱获取用户")
    @PostMapping(value = {"/getMemberByPhoneOrEmail", "/no-auth/getMemberByPhoneOrEmail"})
    public MessageRespResult<Member> getMemberByPhoneOrEmail(@RequestParam("type") Integer type, @RequestParam("param") String param) {
        return success(memberService.findByPhoneOrEmail(type, param));
    }

    @Async
    public void sendRegEmailCode(String platform, String email, String code) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("platform", platform);
            model.put("code", code);
            String html = TemplateHandler.getInstance().handler("resetPasswordCodeEmail.ftl", model);
            emailProvider.sentEmailHtml(email, "title", html);
            log.info("send email for {}, content:{}", email, html);
        } catch (Exception e) {
            log.error("处理邮件模板失败");
        }
    }

    /**
     * 获取直推部门（布朗计划,加速释放）
     *
     * @param member  会员ID
     * @param current 页数
     * @param size    条数
     * @return 直推部门
     * @author zhongxj
     */
    @ApiOperation(value = "获取直推部门（布朗计划,加速释放）", notes = "获取直推部门（布朗计划,加速释放）")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页,每页数量.eg:10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页,当前页码.eg:从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true)
    })
    @RequestMapping(value = {"/findPromotionMember"}, method = RequestMethod.POST)
    public MessageRespResult<IPage<PromotionMemberVO>> findPromotionMember(@MemberAccount Member member,
                                                                           @RequestParam(value = "current", defaultValue = "1") Integer current,
                                                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Long memberId = member.getId();
        log.info("获取直推部门（布朗计划,加速释放）,接收参数size={},current={},memberId={}", size, current, memberId);
        return success(memberService.findPromotionMember(current, size, memberId));
    }


    /**
     * 会员体系 注册邀请记录查看
     * 需要查看 3个层级
     *
     * @return
     */
    @ApiOperation(value = "注册邀请记录查看", notes = "注册邀请记录查看")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页,每页数量.eg:10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页,当前页码.eg:从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true)
    })
    @RequestMapping(value = {"/findInvitationRecord"}, method = RequestMethod.GET)
    public MessageRespResult<PageMemberVo> findInvitationRecord(@MemberAccount Member member,
                                                                   @RequestParam(value = "current", defaultValue = "1") Integer current,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageMemberVo pageMemberVo = memberService.findInvitationRecord(member.getId(), current, size);
        return success(pageMemberVo);
    }
}