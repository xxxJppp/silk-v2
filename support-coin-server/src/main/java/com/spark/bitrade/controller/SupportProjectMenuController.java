package com.spark.bitrade.controller;

import com.spark.bitrade.biz.IProjectMenuService;
import com.spark.bitrade.constant.SectionTypeEnum;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportProjectMenu;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 09:15
 */
@Slf4j
@RestController
@RequestMapping("api/v2/menu")
@Api(description = "项目方后台总控菜单控制层")
public class SupportProjectMenuController extends ApiController {

    @Autowired
    private IProjectMenuService projectMenuService;
    @Autowired
    private SupportUpCoinApplyService supportUpCoinApplyService;

    /**
     * 获取项目方后台总控菜单列表
     *
     * @return 菜单列表
     * @author Zhong Jiang
     * @date 2019.11.05 9:19
     */
    @ApiOperation(value = "项目方后台总控菜单列表", tags = "项目方后台总控菜单列表")
    @PostMapping("/list")
    public MessageRespResult<List<SupportProjectMenu>> list(@MemberAccount Member member) {
        List<SupportProjectMenu> projectMenusList = projectMenuService.findProjectMenusList();
        try {
            SupportUpCoinApply app = supportUpCoinApplyService.findApprovedUpCoinByMember(member.getId());
            SectionTypeEnum type = app.getRealSectionType();
            Iterator<SupportProjectMenu> iterator = projectMenusList.iterator();
            while (iterator.hasNext()) {
                SupportProjectMenu next = iterator.next();
                String path = next.getPath();
                if (type == SectionTypeEnum.MAIN_ZONE) {
                    if ("drainage".equalsIgnoreCase(path) || "transfer".equalsIgnoreCase(path)) {
                        iterator.remove();
                    }
                }
//                if (type == SectionTypeEnum.SUPPORT_UP_ZONE) {
//                    Integer streamStatus = app.getStreamStatus();
//                    if(streamStatus==1){
//                        //引流开启的时候
//                        if ("drainage".equalsIgnoreCase(path)) {
//                            iterator.remove();
//                        }
//                    }
//                    if(streamStatus==0){
//                        //引流关闭的时候
//                        if ("transaction".equalsIgnoreCase(path) || "transfer".equalsIgnoreCase(path)) {
//                            iterator.remove();
//                        }
//                    }
//                }
                if (type == SectionTypeEnum.INNOVATION_ZONE) {
                    if ("drainage".equalsIgnoreCase(path)) {
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            return success(new ArrayList<>());
        }
        log.info("=========获取项目总控菜单列表=========");
        return success(projectMenusList);
    }
}
