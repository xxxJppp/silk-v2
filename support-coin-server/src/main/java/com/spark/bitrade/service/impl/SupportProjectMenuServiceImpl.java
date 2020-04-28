package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.SupportProjectMenu;
import com.spark.bitrade.mapper.SupportProjectMenuMapper;
import com.spark.bitrade.service.SupportProjectMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 项目方后台总控菜单表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportProjectMenuServiceImpl extends ServiceImpl<SupportProjectMenuMapper, SupportProjectMenu> implements SupportProjectMenuService {

    @Autowired
    private SupportProjectMenuMapper projectMenuMapper;

    @Override
    public List<SupportProjectMenu> findList() {
        QueryWrapper<SupportProjectMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SupportProjectMenu.MENU_STATUS, BooleanEnum.IS_TRUE)
                    .eq(SupportProjectMenu.DELETE_FLAG, BooleanEnum.IS_FALSE);
        return projectMenuMapper.selectList(queryWrapper);
    }
}
