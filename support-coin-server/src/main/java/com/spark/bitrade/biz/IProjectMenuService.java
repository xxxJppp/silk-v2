package com.spark.bitrade.biz;

import com.spark.bitrade.entity.SupportProjectMenu;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 11:45
 */
public interface IProjectMenuService {

    /**
     * 查询项目方后台总控菜单列表
     *
     * @author Zhong Jiang
     * @date 2019.11.05 9:21
     * @return
     */
    List<SupportProjectMenu> findProjectMenusList();
}
