package com.spark.bitrade.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSONObject;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.service.ApiSecretKeyService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.trans.Partner;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.DesECBUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 *  密钥服务
 *
 * @author yangch
 * @time 2019.05.06 19:34
 */

@Service
@Slf4j
public class ApiSecretKeyServiceImpl implements ApiSecretKeyService {
    @Value("${api.secret.key.url:https://bccmain.oss-cn-hongkong.aliyuncs.com/bccpara.data}")
    private String apiSecretKeyUrl;

    @Value("${api.secret.key.url.key:BCC19031}")
    private String apiSecretKeyUrlKey;

    @Autowired
    @Qualifier("httpClientTemplate")
    private RestTemplate httpClientTemplate;

    @Autowired
    private IMemberApiService memberApiService;

    /**
     * 创建缓存，默认30秒过期
     */
    private TimedCache<String, Integer> timedCache = CacheUtil.newTimedCache(30 * 1000);

    @Override
    public Integer apiSecretSalt() {
        Integer apiSecretSalt = timedCache.get("apiSecretSalt");
        if (apiSecretSalt == null) {
            try {
                apiSecretSalt = this.parseApiSecretSalt(apiSecretKeyUrl);
                timedCache.put("apiSecretSalt", apiSecretSalt);
            } catch (Exception ex) {
                log.error("解析Partner对象信息出错", ex);
            }
        } else {
            log.info("从缓存中获取apiSecretSalt");
        }

        return apiSecretSalt;
    }

    @Override
    public String apiSecretByApiKey(String apiKey) {
        MessageRespResult<Member> result = memberApiService.findMemberByApiKey(apiKey);
        if(result.isSuccess()){
            Member member = result.getData();
            AssertUtil.notNull(member, MessageCode.INVALID_AUTH_TOKEN);

            //真正用到接口加密解密验证时，【apiSecret】需要加上【apiSecretSalt】的值20190501再次哈希
            //md5(20190501cd5a438c3854df8e04286a5d931d0276)= 4f733940e314e0a72a8a779e29dc9ce5
            return ""; // TODO MD5Util.md5Encode(String.valueOf(this.apiSecretSalt()).concat(member.getApiSecret()));
        } else {
            log.warn("接口调用失败");
        }
        return null;
    }


    /**
     * 根据指定的URL解析Partner对象
     *
     * @param apiSecretKeyUrl
     * @return 返回apiSecretSalt
     * @throws Exception
     */
    private Partner.PartnerBean parsePartner(String apiSecretKeyUrl) throws Exception {
        ResponseEntity<String> result = httpClientTemplate.getForEntity(apiSecretKeyUrl, String.class);
        if (result.getStatusCode().value() == HttpStatus.OK.value()) {
            String encryptText = result.getBody();
            log.info("encryptText = {}", encryptText);

            String decryptText = DesECBUtil.decryptDES(encryptText, apiSecretKeyUrlKey);
            log.info("decryptText = {}", decryptText);

            Partner partner = JSONObject.parseObject(decryptText, Partner.class);
            if (!StringUtils.isEmpty(partner) && partner.getPartner().size() > 0) {
                return partner.getPartner().get(0);
            } else {
                log.warn("未获取到Partner对象信息");
            }
        } else {
            log.warn("请求报错：{}", result.toString());
        }

        return null;
    }

    /**
     * 根据指定的URL解析apiSecretSalt
     * @param apiSecretKeyUrl
     * @return
     * @throws Exception
     */
    private Integer parseApiSecretSalt(String apiSecretKeyUrl) throws Exception {
        Partner.PartnerBean partnerBean = this.parsePartner(apiSecretKeyUrl);
        if (partnerBean != null
                && partnerBean.getApiSecretSalt() != null) {
            return partnerBean.getApiSecretSalt();
        } else {
            log.warn("未获取到apiSecretSalt");
        }
        return null;
    }
}
