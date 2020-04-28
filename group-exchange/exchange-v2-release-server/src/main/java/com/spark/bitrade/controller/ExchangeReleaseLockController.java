package com.spark.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dto.request.ExchangeReleaseLockRequestDTO;
import com.spark.bitrade.service.ExchangeReleaseLockRecordService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.validate.BeanValidators;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.groups.Default;


/**
 *  币币交易释放锁仓(内部服务)
 *
 * @author lc
 * @since 2019/12/17
 */
@RestController
@RequestMapping("/service/v2/lock")
@Slf4j
public class ExchangeReleaseLockController extends  ApiController{

    @Autowired
    private ExchangeReleaseLockRecordService exchangeReleaseLockRecordService;


    @PostMapping(value = {"/exchangeReleaseLock"})
    @ApiOperation(value = "链上充值锁仓")
    @ApiImplicitParam(value = "ExchangeReleaseLockRequestDTO", dataTypeClass = ExchangeReleaseLockRequestDTO.class)
    public MessageRespResult exchangeReleaseLock(@RequestParam("exchangeReleaseLockRequestDTO") String exchangeReleaseLockRequestDTO) {
        ExchangeReleaseLockRequestDTO requestDTO = JSON.parseObject(exchangeReleaseLockRequestDTO, new TypeReference<ExchangeReleaseLockRequestDTO>() {
        });
        String errorMsg = BeanValidators.validateWithString(requestDTO, Default.class);
        if (StringUtils.isNotBlank(errorMsg)) {
            log.error("notify.matchNotify.error-{}", errorMsg);
            return MessageRespResult.error(CommonMsgCode.REQUIRED_PARAMETER.getCode(), errorMsg);
        }
        return exchangeReleaseLockRecordService.rechargeLock(requestDTO);
    }


}
