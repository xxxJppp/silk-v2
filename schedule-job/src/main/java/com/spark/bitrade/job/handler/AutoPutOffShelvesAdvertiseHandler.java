package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IOtcServer;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/***
  * 自动下架C2C的广告 任务（从otc-api模块迁移过来）
 *  任务周期：每半个小时执行一次（0 30 * * * ? *）
  * @author ss
  * @time 2020/04/05
  */
@JobHandler(value="autoPutOffShelvesAdvertiseHandler")
@Component
public class AutoPutOffShelvesAdvertiseHandler extends IJobHandler {

    @Resource
    private IOtcServer otcServer;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("=========开始检查自动下架的广告===========");
        MessageRespResult<Map<String, List<Long>>> messageResult = otcServer.autoPutOffShelvesAdvertise();
        if(messageResult.isSuccess()){
            List<Long> success = messageResult.getData().get("success");
            List<Long> fail = messageResult.getData().get("fail");
            if(success.size() > 0){
                success.stream().forEach(y -> XxlJobLogger.log("自动下架成功：{0}号广告", y));
            }
            if(fail.size() > 0){
                fail.stream().forEach(y -> XxlJobLogger.log("自动下架失败：{0}号广告", y));
            }
            XxlJobLogger.log("=========结束检查自动下架的广告===========");
            return SUCCESS;
        }
        XxlJobLogger.log("=========结束检查自动下架的广告===========");
        return FAIL;
    }
}
