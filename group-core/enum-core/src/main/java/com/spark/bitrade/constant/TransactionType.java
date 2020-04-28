package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType implements BaseEnum {
    /**
     * 0 充值
     */
    RECHARGE("充值"),
    /**
     * 1 提现
     */
    WITHDRAW("提现"),
    /**
     * 2 转账
     */
    TRANSFER_ACCOUNTS("转账"),
    /**
     * 3 币币交易
     */
    EXCHANGE("币币交易"),
    /**
     * 4 法币买入
     */
    OTC_BUY("法币买入"),
    /**
     * 5 法币卖出
     */
    OTC_SELL("法币卖出"),
    /**
     * 6 活动奖励
     */
    ACTIVITY_AWARD("活动奖励"),
    /**
     * 7 推广奖励
     */
    PROMOTION_AWARD("推广奖励"),
    /**
     * 8 划转
     */
    TRANSFER("划转"),
    /**
     * 9 投票
     */
    VOTE("投票"),
    /**
     * 10 人工充值
     */
    ADMIN_RECHARGE("人工充值"),
    /**
     * 11 配对
     */
    MATCH("配对"),
    /**
     * 12 币币交易返佣奖励
     */
    EXCHANGE_PROMOTION_AWARD("币币交易返佣奖励"),
    /**
     * 13 币币交易合伙人奖励
     */
    EXCHANGE_PARTNER_AWARD("币币交易合伙人奖励"),
    /**
     * 14 商家认证保证金
     */
    BUSINESS_DEPOSIT("商家认证保证金"),
    /**
     * 15 锁仓充值
     */
    ADMIN_LOCK_RECHARGE("锁仓充值"),
    /**
     * 16 锁仓活动
     */
    ADMIN_LOCK_ACTIVITY("锁仓活动"),
    /**
     * 17 手动调账
     */
    ADMIN_ADJUST_BALANCE("手动调账"),
    /**
     * 18 理财锁仓
     */
    FINANCIAL_ACTIVITY("理财锁仓"),
    /**
     * 19 三方支付
     */
    THIRD_PAY("三方支付"),
    /**
     * 20 SLB节点产品
     */
    QUANTIFY_ACTIVITY("SLB节点产品"),
    /**
     * 21 SLB节点产品共识奖励
     */
    LOCK_COIN_PROMOTION_AWARD("SLB节点产品共识奖励"),
    /**
     * 22 STO锁仓
     */
    STO_ACTIVITY("STO锁仓"),
    /**
     * 23 STO推荐奖励
     */
    LOCK_COIN_PROMOTION_AWARD_STO("STO推荐奖励"),
    /**
     * 24 广告手续费
     */
    ADVERTISE_FEE("广告手续费"),
    /**
     * 25 闪兑
     */
    EXCHANGE_FAST("闪兑"),
    /**
     * 26 IEO锁仓活动
     */
    IEO_ACTIVITY("IEO锁仓活动"),
    /**
     * 27 活期宝活动
     */
    HQB_ACTIVITY("活期宝活动"),
    /**
     * 28 本人锁仓奖励
     */
    GOLD_KEY_OWN("本人锁仓奖励"),
    /**
     * 29 金钥匙团队锁仓
     */
    GOLD_KEY_TEAM("团队锁仓奖励"),
    /**
     * 30 BCC赋能计划
     */
    ENERGIZE_LOCK("BCC赋能计划"),
    /**
     * 31 参与布朗活动
     */
    SLP_LOCK("参与布朗活动"),
    /**
     * 32 布朗活动释放收益
     */
    SLP_LOCK_RELEASE("布朗活动释放收益"),
    /**
     * 33 超级合伙人手续费20%奖励
     */
    SUPER_PARTNER_AWARD("超级合伙人手续费20%奖励"),
    /**
     * 34 超级合伙人锁仓
     */
    SUPER_PARTNER_LOCK("超级合伙人锁仓"),
    /**
     * 35 退出超级合伙人
     */
    SUPER_PARTNER_EXIT("退出超级合伙人"),
    /**
     * 36 违约退出社区
     */
    SUPER_EXIT_COMMUNITY("违约退出社区"),
    /**
     * 37 超级合伙人活跃成员奖励
     */
    SUPER_PARTNER_ACTIVE_AWARD("超级合伙人活跃成员奖励"),
    /**
     * 38 微信/支付宝-直接支付
     */
    DIRECT_PAY("微信/支付宝-直接支付"),
    /**
     * 39 UTT活动锁仓
     */
    LOCK_UTT("UTT活动锁仓"),
    /**
     * 40 UTT活动释放
     */
    UNLOCK_UTT("UTT活动释放"),
    /**
     * 41 微信/支付宝-直接支付收益归集
     */
    DIRECT_PAY_PROFIT("微信/支付宝-直接支付收益归集"),
    /**
     * 42 法币交易手续费归集
     */
    OTC_JY_RATE_FEE("法币交易手续费归集"),
    /**
     * 43 孵化区锁仓
     */
    INCUBOTORS_LOCK("孵化区锁仓"),
    /**
     * 44 孵化区解仓
     */
    INCUBOTORS_UNLOCK("孵化区解仓"),
    /**
     * 法币广告手续费归集
     */
    ADVERTISE_FEE_COLLECTION("法币广告手续费归集"),
    /**
     * BB交易手续费归集
     */
    EXCHANGE_FEE_COLLECTION("BB交易手续费归集"),
    /**
     * 提币手续费归集 47
     */
    UP_COIN_FEE_COLLECTION("提币手续费归集"),
    /**
     * 项目方中心冻结金额48
     */
    SUPPORT_PROJECT_RETURN("项目方中心服务退回"),
    /**
     * 项目方中心服务支出49
     */
    SUPPORT_PROJECT_PAY("项目方中心服务支出"),
    /**
     * 50 资金账户划转
     */
    FUND_TRANSFER("资金账户划转"),
    /**
     * 51 币币账户划转
     */
    EXCHANGE_TRANSFER("币币账户划转"),
    /**
     * 52 OTC账户划转
     */
    OTC_TRANSFER("OTC账户划转"),
    /**
     * 53 活期宝划转
     */
    HQB_TRANSFER("活期宝划转"),
    /**
     * 54
     */
    LOCK_BTLF_PAY("BTLF锁仓"),
    /**
     * 55
     */
    RELEASE_BTLF("BTLF释放"),
    /**
     * 56 开通/续费/升级会员
     */
    MEMBER_VIP_OPENING("开通/续费/升级会员"),
    /**
     * 57
     */
    BUY_MEMBER_LOCK("购买会员锁仓"),
    /**
     * 58
     */
    BUY_MEMBER_UNLOCK("购买会员锁仓到期返还"),
    /**
     * 59
     */
    RELEASE_ESP("ESP释放"),
    /**
     * 60
     */
    LOCK_ESP("ESP充值锁仓"),
    /**
     * 61 小牛快跑参赛
     */
    BUY_LUCKY_BULL("小牛快跑参赛"),
    /**
     * 62  小牛快跑中奖
     */
    LUCKY_WIN_BULL("小牛快跑中奖"),
    /**
     * 63 小牛快跑开奖资金返还
     */
    LUCKY_RETURN_BULL("小牛快跑退款"),
    /**
     * 64 小牛快跑追加奖金发放
     */
    LUCKY_APPEND_WX_BULL("小牛快跑追加奖金发放"),
    /**
     * 65 购买幸运宝幸运号
     */
    BUY_LUCKY_NUMBER("幸运号参赛"),
    /**
     * 66  幸运宝幸运号中奖
     */
    LUCKY_WIN_NUMBER("幸运号中奖"),
    /**
     * 67 幸运宝幸运号取消开奖资金返还
     */
    LUCKY_RETURN_NUMBER("幸运号退款"),
    /**
     * 68 幸运宝幸运号追加奖金发放
     */
    LUCKY_APPEND_WX_NUMBER("幸运宝幸运号追加奖金发放"),
    /**
     * 69 会员费归集总账号
     */
    MEMBER_ADD_TOTAL_ACCOUNT("会员费归集"),
    /**
     * 70 年终活动奖励发放
     */
    FESTIVAL_NUMBER_LOCK("年终活动奖励锁仓"),
    /**
     * 71 年终活动奖励释放
     */
    FESTIVAL_NUMBER_LOCK_RELEASED("年终活动奖励锁仓释放"),

    /**
     * 72
     */
    RED_PACK_COST("红包活动支出"),
    /**
     * 73
     */
    RED_PACK_RETURN("红包活动退回"),
       /**
     * 74 修改默认法币
     */
    CHANGE_CURRENCY("修改默认法币"),
    /**
     * 75 广告上架冻结
     */
    PUT_ON_SHELVES_FROZEN("广告上架冻结"),
    /**
     * 76 广告下架解冻
     */
    PUT_OFF_SHELVES_FROZEN("广告下架解冻"),
    /**
     * 77 DCC锁仓
     */
    LOCK_DCC("DCC锁仓"),
    /**
     * 78 DCC释放
     */
    RELEASE_DCC("DCC释放"),
    /**
     * 79 经纪人快捷购币
     */
    AGENT_BUY_USDC("经纪人快捷购币"),
    /**
     * 80
     */
    CURRENCY_GET("法币交易返佣") ,

    EXT9("占位，扩展9");




    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    public static TransactionType valueOfOrdinal(int ordinal) {
        TransactionType[] values = TransactionType.values();
        for (TransactionType transactionType : values) {
            int o = transactionType.getOrdinal();
            if (o == ordinal) {
                return transactionType;
            }
        }
        return null;
    }
}
