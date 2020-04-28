package com.spark.bitrade.controller;

import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.service.LocaleMessageSourceService;
import com.spark.bitrade.util.AliyunUtil;
import com.spark.bitrade.util.GeneratorUtil;
import com.spark.bitrade.util.MessageResult;
import com.spark.bitrade.util.QrCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Slf4j
@Api(description = "图片上传控制器")
public class UploadController {

    private String allowedFormat = ".jpg,.png";

    @Autowired
    private LocaleMessageSourceService sourceService;

    @Autowired
    private AliyunConfig aliyunConfig;


    /**
     * 上传base64处理后的图片
     *
     * @param base64Data
     * @param verify
     * @return
     */
    @ApiOperation(value = "图片上传接口", notes = "图片上传接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "图片base64数据", name = "base64Data", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "是否验证二维码类型", name = "verify", dataTypeClass = Boolean.class)
    })
    @RequestMapping(value = "/upload/oss/base64", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult base64UpLoad(@RequestParam String base64Data, @RequestParam(required = false) Boolean verify) {
        MessageResult result = new MessageResult();
        try {
            log.info("上传文件的数据：" + base64Data);
            String dataPrix = "";
            String data = "";
            if (base64Data == null || "".equals(base64Data)) {
                throw new Exception(sourceService.getMessage("NOT_FIND_FILE"));
            } else {
                String[] d = base64Data.split("base64,");
                if (d != null && d.length == 2) {
                    dataPrix = d[0];
                    data = d[1];
                } else {
                    throw new Exception(sourceService.getMessage("DATA_ILLEGAL"));
                }
            }
            log.info("对数据进行解析，获取文件名和流数据");
            String suffix = "";
            if ("data:image/jpeg;".equalsIgnoreCase(dataPrix)) {
                //data:image/jpeg;base64,base64编码的jpeg图片数据
                suffix = ".jpg";
            } else if ("data:image/x-icon;".equalsIgnoreCase(dataPrix)) {
                //data:image/x-icon;base64,base64编码的icon图片数据
                suffix = ".ico";
            } else if ("data:image/gif;".equalsIgnoreCase(dataPrix)) {
                //data:image/gif;base64,base64编码的gif图片数据
                suffix = ".gif";
            } else if ("data:image/png;".equalsIgnoreCase(dataPrix)) {
                //data:image/png;base64,base64编码的png图片数据
                suffix = ".png";
            } else {
                throw new Exception(sourceService.getMessage("FORMAT_NOT_SUPPORT"));
            }
            String directory = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
            String key = directory + GeneratorUtil.getUUID() + suffix;

            //因为BASE64Decoder的jar问题，此处使用spring框架提供的工具包
            byte[] bs = Base64Utils.decodeFromString(data);
            //OSSClient ossClient = new OSSClient(aliyunConfig.getOssEndpoint(), aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
            try {
                //如果二维码参数不为空且满足条件，则验证是否是支付二维码
                if (!StringUtils.isEmpty(verify) && verify) {
                    InputStream inputStream1 = new ByteArrayInputStream(bs);
                    boolean isPay = QrCodeUtil.decodeQrCode(inputStream1);
                    if (!isPay) {
                        log.error("不是支付二维码");
                        return MessageResult.error("上传图片不是支付二维码");
                    }
                }

                //使用apache提供的工具类操作流
                InputStream is = new ByteArrayInputStream(bs);
                String uri = AliyunUtil.upLoadImg(aliyunConfig, is, key, false);
                MessageResult mr = new MessageResult(0, sourceService.getMessage("UPLOAD_SUCCESS"));
                mr.setData(uri);
                mr.setMessage(sourceService.getMessage("UPLOAD_SUCCESS"));
                log.info("上传成功,key:{}", key);
                return mr;
            } catch (Exception ee) {
                log.warn(ee.getMessage());
                throw new Exception(sourceService.getMessage("FAILED_TO_WRITE"));
            }
        } catch (Exception e) {
            log.error("上传失败," + e.getMessage());
            result.setCode(500);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
