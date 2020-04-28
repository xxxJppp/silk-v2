package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.IProjectMenuService;
import com.spark.bitrade.entity.SupportProjectMenu;
import com.spark.bitrade.service.SupportProjectMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 11:45
 */
@Service
public class ProjectMenuServiceImpl implements IProjectMenuService {

    @Autowired
    private SupportProjectMenuService projectMenuService;

    @Override
    public List<SupportProjectMenu> findProjectMenusList() {
        return projectMenuService.findList();
    }
}
