package com.spark.bitrade.controller;


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.UnlockCoinDetail;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.ILockCoinDetailService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.IUnlockCoinDetailService;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaoxianming
 * @since 2019-12-02
 */
@RestController
@Slf4j
@RequestMapping("v2/member")
public class LockCoinDetailController extends ApiController{
	@Autowired
	private ILockCoinDetailService lockCoinDetailService;
	
	@Autowired
	private IUnlockCoinDetailService unlockCoinDetailService;
	
	@Autowired
	private IMemberWalletApiService memberWalletApiService;

	@PostMapping("/benefits/unlock")
	public void unlockMemberBenefits() {
		Page<LockCoinDetail> page = new Page<LockCoinDetail>(1,50);
		QueryWrapper<LockCoinDetail> wrapper = new QueryWrapper<LockCoinDetail>();
		wrapper.eq("status", LockStatus.LOCKED.getOrdinal());
		wrapper.eq("type", LockType.BY_MEMBER_LOCK.getOrdinal());
		wrapper.lt("plan_unlock_time", new Date());
		
		List<LockCoinDetail> list = this.lockCoinDetailService.page(page, wrapper).getRecords();
		
		log.info("====================unlock count ==================" + list.size());
		for (LockCoinDetail lcd : list) {
			LockCoinDetail entity = new LockCoinDetail();
			try {
				BeanUtils.copyProperties(entity, lcd);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			entity.setUnlockTime(new Date());
			entity.setStatus(LockStatus.UNLOCKED.getOrdinal());
			entity.setRemainAmount(new BigDecimal(0));
			
			UpdateWrapper<LockCoinDetail> wrapper2 = new UpdateWrapper<LockCoinDetail>();
			wrapper2.eq("id", lcd.getId());
			boolean flag = this.transfer(lcd);
			if(flag) {
				this.lockCoinDetailService.update(entity, wrapper2);
				
				UnlockCoinDetail unlockCoinDetail = new UnlockCoinDetail();
				unlockCoinDetail.setLockCoinDetailId(lcd.getId());
				unlockCoinDetail.setRemainAmount(new BigDecimal(0));
				unlockCoinDetail.setCreateTime(new Date());
				unlockCoinDetail.setAmount(lcd.getTotalAmount());
				unlockCoinDetail.setPrice(new BigDecimal(0));
				this.unlockCoinDetailService.save(unlockCoinDetail);
			}
		}
	}
	
	public boolean transfer(LockCoinDetail lcd) {
		WalletTradeEntity tradeEntity = new WalletTradeEntity();
		MessageRespResult<String> coinIdResult = memberWalletApiService.getCoinNameByUnit(lcd.getCoinUnit());
        tradeEntity.setType(TransactionType.BUY_MEMBER_UNLOCK);
        tradeEntity.setRefId(String.valueOf(lcd.getId()));
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(lcd.getMemberId());
        tradeEntity.setComment(TransactionType.BUY_MEMBER_UNLOCK.name());
        tradeEntity.setTradeBalance(lcd.getTotalAmount());
        tradeEntity.setTradeLockBalance(lcd.getTotalAmount().negate());
        tradeEntity.setCoinUnit(lcd.getCoinUnit());
        tradeEntity.setCoinId(coinIdResult.getData());
		Long walletChangeRecordId = 0l;
        try {
            // try
            MessageRespResult<WalletChangeRecord> tradeResult = memberWalletApiService.tradeTccTry(tradeEntity);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
            AssertUtil.notNull(tradeResult.getData(), CommonMsgCode.ERROR);
            // 流水记录ID
            walletChangeRecordId = tradeResult.getData().getId();
            // confirm
            MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(lcd.getMemberId(), walletChangeRecordId);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
            AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);
            return true;
        } catch (Exception ex) {
            log.error("处理失败 [   err = '{}' ]", ex.getMessage());
            log.error("操作失败", ex);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        } finally {
            if (walletChangeRecordId != null) {
                // cancel
                try {
                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(lcd.getMemberId(), walletChangeRecordId);
                    // throw
                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                } catch (Exception ex) {
                    log.error("账户变动业务取消失败", ex);
                }
            }
        }
	}
}
