package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.vo.PromotionMemberVO;

import java.util.List;

/**
 * (Member)表服务接口
 *
 * @author archx
 * @since 2019-06-11 17:28:17
 */
public interface MemberService extends IService<Member> {

    boolean emailIsExist(String email);

    boolean phoneIsExist(String phone);

    boolean usernameIsExist(String username);

    boolean phoneOrEmailIsExist(String username, String phone, String email);

    boolean checkPromotion(String promotion);

    Member register(LoginByPhone loginByPhone, LoginByEmail loginByEmail, String registerIP);

    void updateInviterTotal(Long memberId);

    /**
     * 绑定推荐关系
     *
     * @param phone         手机号
     * @param email         邮箱地址
     * @param password      登录密码
     * @param promotionCode 推荐码
     * @return 是否绑定成功
     */
    Member bindPromotion(String phone, String email, String password, String promotionCode);

    /**
     * 登录
     */
    Member login(String username, String password);

    /**
     * 根据userId 和 用户名 查询用户
     *
     * @param userId   用户id
     * @param username 用户名
     */
    Member loginWithUserId(Long userId, String username);

    /**
     * 获取账户信息
     *
     * @param mode    0为手机验证，1为邮箱验证
     * @param account 账号
     */
    Member findByPhoneOrEmail(int mode, String account);

    /**
     * 根据推荐码查询用户
     *
     * @param promotion 推荐码
     * @return 用户
     */
    Member findByPromotion(String promotion);

    /**
     * 修改用户密码
     *
     * @param member   账户
     * @param password 新密码
     */
    boolean resetPassword(Member member, String password);


    /**
     * 处理会员的密码
     *
     * @param inputPassword 会员输入的密码
     * @param salt          盐
     * @return
     * @author yangch
     * @since 2019-06-20 14:05:18
     */
    String simpleHashPassword(String inputPassword, String salt);

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
    boolean confirmPassword(String storagePassword, String inputPassword, String salt);


    /**
     * 批量查询用户信息
     *
     * @param memberIds
     * @return true
     * @author shenzucai
     * @time 2019.07.04 9:10
     */
    List<Member> listMembersByIds(List<Long> memberIds);

    /**
     * 获取会员详情
     *
     * @param id 会员ID
     * @return 会员详情
     */
    Member getMemberById(Long id);

    /**
     * 获取直推部门
     *
     * @param memberId 会员ID
     * @param current   页数
     * @param size 条数
     * @return 直推部门
     */
    IPage<PromotionMemberVO> findPromotionMember(Integer current, Integer size, Long memberId);


    /**
     * 查询邀请记录
     *
     * @param memberId 会员ID
     * @param current  页数
     * @param size     条数
     * @return
     */
    PageMemberVo findInvitationRecord(Long memberId, Integer current, Integer size);

}