package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.SupportApplyRedPack;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.param.RedPackParam;
import com.spark.bitrade.param.RedRecieveParam;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;
import com.spark.bitrade.vo.ApplyRedPackListVo;
import com.spark.bitrade.vo.RedPackRecieveDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 红包申请表 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
public interface SupportApplyRedPackMapper extends BaseMapper<SupportApplyRedPack> {

    List<ApplyRedPackListVo> applyRedPackList(@Param("page") IPage<ApplyRedPackListVo> page, @Param("param") RedPackParam param, @Param("coin") String coin);


    List<RedPackRecieveDetailVo> applyRedPackStatics(@Param("page") IPage<RedPackRecieveDetailVo> page, @Param("param") RedRecieveParam param, @Param("applyRedPackId") Long applyRedPackId);

    @Select("select count(1) from red_pack_receive_record where redpack_id = #{applyRedPackId} and user_type=1")
    Integer findNewMemberCount(@Param("applyRedPackId") Long applyRedPackId );

    @Select("SELECT SUM(receive_amount) FROM red_pack_receive_record WHERE redpack_id=#{redPackId} AND receive_status=1")
    BigDecimal findReceivedPack(@Param("redPackId") Long applyRedPackId );
}
