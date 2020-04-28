package com.spark.bitrade.biz;

import com.spark.bitrade.common.NewYearExceptionMsg;
import com.spark.bitrade.constant.OperateTypeEnum;
import com.spark.bitrade.entity.NewYearMemberHas;
import com.spark.bitrade.entity.NewYearMemberRecord;
import com.spark.bitrade.entity.NewYearMineral;
import com.spark.bitrade.entity.NewYearProjectAdvertisement;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.vo.MiningMineralVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: Zhong Jiang
 * @date: 2019-12-31 11:36
 */
@Service
public class MemberMineralBizService {

    @Autowired
    private NewYearMineralService mineralService;

    @Autowired
    private NewYearMemberRecordService memberRecordService;

    @Autowired
    private NewYearProjectAdvertisementService projectAdvertisementService;

    @Autowired
    private NewYearMemberHasService memberHasService;

    @Autowired
    private NewYearMemberInfoService memberInfoService;

    /**
     * 挖到矿石后的操作
     *
     * @param memberId
     * @param type
     */
    @Transactional(rollbackFor = Exception.class)
    public MiningMineralVo doMiningAfter(Long memberId, Integer type) {
        NewYearMineral mineralRecord = mineralService.findAndupdateMineral(type);
        // 保存用户矿石记录表
        NewYearMemberRecord mineralMemberRecord = new NewYearMemberRecord();
        mineralMemberRecord.setMineralId(mineralRecord.getId());
        mineralMemberRecord.setMemberId(memberId);
        mineralMemberRecord.setMineralName(mineralRecord.getMineralName());
        mineralMemberRecord.setCount(1);
        mineralMemberRecord.setOpType(OperateTypeEnum.MINING.getCode());
        mineralMemberRecord.setRemark("挖矿获取");
        boolean result1 = memberRecordService.save(mineralMemberRecord);
        AssertUtil.isTrue(result1, NewYearExceptionMsg.SAVA_ERORR);
        // 随机获取一条广告语
        NewYearProjectAdvertisement randomOneRecord = projectAdvertisementService.findRandomOneRecord();
        // 保存用户矿石持有表
        NewYearMemberHas memberHas = new NewYearMemberHas();
        if (randomOneRecord != null) {
            memberHas.setSponsorDescription(randomOneRecord.getProjectIntroduction());
        }
        memberHas.setMemberId(memberId);
        memberHas.setMineralId(mineralRecord.getId());
        memberHas.setMineralName(mineralRecord.getMineralName());
        memberHas.setStatus(1);
        memberHas.setFromWhere(0);
        boolean result2 = memberHasService.save(memberHas);
        AssertUtil.isTrue(result2, NewYearExceptionMsg.SAVA_ERORR);
        // 用户挖矿次数 - 1 操作
        boolean result3 = memberInfoService.decrMemberMiningNumber(memberId);
        AssertUtil.isTrue(result3, NewYearExceptionMsg.MINING_FAIL);
        MiningMineralVo mineralVo = new MiningMineralVo();
        mineralVo.setMineralName(mineralRecord.getMineralName());
        if (randomOneRecord != null) {
            mineralVo.setProjectCoin(randomOneRecord.getCoinNam());
            mineralVo.setProjectIntroduce(randomOneRecord.getProjectIntroduction());
        }
        return mineralVo;
    }
}
