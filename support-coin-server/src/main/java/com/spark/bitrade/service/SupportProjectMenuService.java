package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportProjectMenu;

import java.util.List;

/**
 * <p>
 * 项目方后台总控菜单表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportProjectMenuService extends IService<SupportProjectMenu> {

    /**
     * 查询菜单列表
     *
     * @author Zhong Jiang
     * @date 2019.11.05 9:21
     * @return
     */
    List<SupportProjectMenu> findList();
}
