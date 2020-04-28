package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.vo.MemberWalletVo;
import com.spark.bitrade.vo.MembertVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员钱包表数据库访问层
 *
 * @author yangch
 * @since 2019-06-15 16:14:18
 */
public interface MemberWalletMapper extends BaseMapper<MemberWallet> {

    /**
     * 加减钱包余额
     *
     * @param walletId           钱包ID
     * @param tradeBalance       交易余额，整数为加/负数为减
     * @param tradeFrozenBalance 交易冻结余额，整数为加/负数为减
     * @param tradeLockBalance   交易锁仓余额，整数为加/负数为减
     * @return
     */
    Integer trade(@Param("walletId") Long walletId, @Param("tradeBalance") BigDecimal tradeBalance,
                  @Param("tradeFrozenBalance") BigDecimal tradeFrozenBalance,
                  @Param("tradeLockBalance") BigDecimal tradeLockBalance);

    /**
     * 查询用户silkPayCoin支持的币种
     * @param memberId
     * @return
     */
    @Select(" select mw.balance,mw.coin_id as unit from member_wallet mw   where mw.member_id = #{memberId}   ")
    List<MemberWalletVo>  findSilkPayCoinWalletByMemberId(@Param("memberId") Long memberId);

    /**
     * 项目方持仓统计
     *
     * @param coinId
     * @param balanceStart
     * @param balanceEnd
     * @return
     */
    List<MembertVo> findMemberWalletDetail(Page<MembertVo> page, @Param("coinId") String coinId, @Param("balanceStart") BigDecimal balanceStart, @Param("balanceEnd") BigDecimal balanceEnd);

}