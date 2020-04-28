package com.spark.bitrade.controller;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.form.WarehouseUpgradeForm;
import com.spark.bitrade.service.IncubatorsFundDetailsService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * <p>
 * 孵化区-升仓
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@RestController
@RequestMapping("api/v2/incubatorsFundDetails")
@Api(description = "孵化区-升仓")
public class IncubatorsFundDetailsController {
    @Autowired
    private IncubatorsFundDetailsService incubatorsFundDetailsService;

    /**
     * 04升仓
     *
     * @param member 会员信息
     * @param form   提交表单
     * @return
     */
    @ApiOperation(value = "04升仓", notes = "04升仓")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "升仓form", name = "form", dataTypeClass = WarehouseUpgradeForm.class)
    })
    @PostMapping(value = "warehouseUpgrade")
    public MessageRespResult warehouseUpgrade(@ApiIgnore @MemberAccount Member member, @Valid WarehouseUpgradeForm form) {
        return incubatorsFundDetailsService.warehouseUpgrade(member, form);
    }
}

