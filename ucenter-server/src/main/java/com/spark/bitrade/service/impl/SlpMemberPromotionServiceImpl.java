package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.PromotionMemberDTO;
import com.spark.bitrade.entity.SlpMemberPromotion;
import com.spark.bitrade.mapper.SlpMemberPromotionMapper;
import com.spark.bitrade.service.SlpMemberPromotionService;
import com.spark.bitrade.vo.PromotionMemberVO;
import io.shardingsphere.api.HintManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 会员推荐关系表(SlpMemberPromotion)表服务实现类
 *
 * @author wsy
 * @since 2019-06-20 10:02:09
 */
@Service("slpMemberPromotionService")
@Slf4j
public class SlpMemberPromotionServiceImpl extends ServiceImpl<SlpMemberPromotionMapper, SlpMemberPromotion> implements SlpMemberPromotionService {

    @Autowired
    private DataSource dataSource;

    @Override
    public int findPromotionCount(Long memberId) {
        QueryWrapper<SlpMemberPromotion> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        return count(wrapper);
    }

    @Override
    public Page<PromotionMemberDTO> findPromotionList(Long memberId, int pageSize, int pageNo) {
        Page<PromotionMemberDTO> page = new Page<>(pageNo, pageSize);
        List<PromotionMemberDTO> list = getBaseMapper().selectInviterList(page, memberId);
        page.setRecords(list);
        return page;
    }

    @Override
    public boolean queryRecipt(Long memberId, Long cMemberId) {
        return getBaseMapper().queryRecipt(memberId, cMemberId) == 0;
    }

    @Override
    public void updateTotal(Long memberId) {
        // getBaseMapper().updateTotal(memberId);
        String sql = "select updataTotal(" + memberId + ") from dual";
        try (HintManager hintManager = HintManager.getInstance();
             Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            hintManager.setMasterRouteOnly();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    log.info("---------------- {} -------------", rs.getString(0));
                }
            }
        } catch (SQLException e) {
            log.error("变更推荐统计数据异常：", e);
        }
    }

    /**
     * 获取直推部门
     *
     * @param current  页数
     * @param size     条数
     * @param memberId 会员ID
     * @return 直推部门
     */
    @Override
    public IPage<SlpMemberPromotion> findPromotionMember(Integer current, Integer size, Long memberId) {
        Page<SlpMemberPromotion> slpMemberPromotionPage = new Page<>(current, size);
        QueryWrapper<SlpMemberPromotion> slpMemberPromotionQueryWrapper = new QueryWrapper<SlpMemberPromotion>()
                .and(wrapper -> wrapper.eq("inviter_id", memberId))
                .orderByDesc("create_time");
        IPage<SlpMemberPromotion> slpMemberPromotionIPage = page(slpMemberPromotionPage, slpMemberPromotionQueryWrapper);
        return slpMemberPromotionIPage;
    }

}