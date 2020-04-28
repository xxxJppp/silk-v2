package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportApplyRedPack;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.param.RedPackParam;
import com.spark.bitrade.param.RedRecieveParam;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;
import com.spark.bitrade.vo.ApplyRedPackListVo;
import com.spark.bitrade.vo.RedPackRecieveDetailVo;

import java.util.List;

/**
 * <p>
 * 红包申请表 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
public interface SupportApplyRedPackService extends IService<SupportApplyRedPack> {

    List<ApplyRedPackListVo> applyRedPackList(IPage<ApplyRedPackListVo> page, RedPackParam param, String coin);


    List<RedPackRecieveDetailVo> applyRedPackStatics(IPage<RedPackRecieveDetailVo> page, RedRecieveParam param, Long applyRedPackId);

    Integer findNewMemberCount(  Long applyRedPackId );
}
