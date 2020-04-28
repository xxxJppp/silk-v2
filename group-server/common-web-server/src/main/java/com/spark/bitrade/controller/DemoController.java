package com.spark.bitrade.controller;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *  demo类
 *
 * @author young
 * @time 2019.05.09 18:01
 */

@Slf4j
@RequestMapping
@RestController
//1、swagger文档
@Api(description = "demo控制器")
//2、所有control类基础 ApiController
public class DemoController extends CommonController {


    /**
     * 注释
     *
     * @param member
     * @return
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true)
    @RequestMapping(value = "/demo", method = {RequestMethod.GET, RequestMethod.POST})
    //通过“@MemberAccount Member member”获取用户信息，但需要在请求头添加“apiKey”参数，不需要添加忽略的注解“@ApiIgnore”
    public MessageRespResult<String> demo(@MemberAccount Member member) {
        log.info("用户信息 = {}", member);

        //断言-----------------------------------
        AssertUtil.notNull(null, CommonMsgCode.ERROR);
        AssertUtil.isTrue(true, CommonMsgCode.ERROR);

        //抛出异常,所有的业务异常都使用“MessageCodeException”类
        this.throwsMessageCodeException(CommonMsgCode.ERROR);
        ExceptionUitl.throwsMessageCodeException(CommonMsgCode.ERROR);

        //通过feign调用的接口返回了错误，调用方可通过以下方式传递错误码信息
        MessageRespResult result = null;
        ExceptionUitl.throwsMessageCodeException(CommonMsgCode.of(result.getCode(), result.getMessage()));

        //生成流水号
        //IdWorker.getId();

        //返回信息-------------------------------
        //返回成功
        this.success();
        //返回成功，并返回对象
        this.success(new Object());
        //返回成功，并返回分页数据
        //this.success(new Page<>(1, 10) );

        //返回失败
        failed(CommonMsgCode.BAD_REQUEST);

        return null;
    }
}
