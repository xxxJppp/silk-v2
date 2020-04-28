package com.spark.bitrade.biz;

import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.form.SupportUpCoinApplyForm;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.vo.CoinApplyVo;
import com.spark.bitrade.vo.NumberOfPeopleVo;
import com.spark.bitrade.vo.UpCoinApplyRecordVo;

import java.util.Map;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 09:23  
 */
public interface IUpCoinApplyService {

    /**
     * 验证是否能进行上币
     * @param memberId
     */
    void validateCanUpCoin(Long memberId,String coin);

    /**
     * 执行申请上币逻辑
     * @param memberId
     * @param form
     */
    void doUpCoinApply(Long memberId, SupportUpCoinApplyForm form);

    /**
     * 查询当前用户申请上币的状态
     * @param memberId
     * @return
     */
    UpCoinApplyRecordVo findUpCoinRecordByMember(Long memberId);

    /**
     * 币种基本信息修改获取币种信息
     * @param id 币种id
     * @return
     */
    CoinApplyVo getSupportUpCoinApply(Long memberId);

    /**
     * 根据id查询
     * @param upCoinId
     * @return
     */
    SupportUpCoinApply getById(Long upCoinId);

    /**
     * 根据memberId查询
     * @param memberId
     * @return
     */
    SupportUpCoinApply getByMemberId(Long memberId);


    Map<String,String> findUpCoinText();

    NumberOfPeopleVo numberOfPeople(String coin, String coinId, PageParam pageParam);
}
