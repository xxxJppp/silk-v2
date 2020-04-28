package com.spark.bitrade.trans;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  合伙人授权信息
 *
 * @author young
 * @time 2019.05.06 16:41
 */
@NoArgsConstructor
@Data
public class Partner {

    private List<PartnerBean> partner;

    @NoArgsConstructor
    @Data
    public static class PartnerBean {
        /**
         * apphelp_agree : https://gbpc2019.oss-cn-hongkong.aliyuncs.com/agree.html
         * apphelp_guide : https://gbpc2019.oss-cn-hongkong.aliyuncs.com/guide.html
         * apphelp_agree_en : https://gbpc2019.oss-cn-hongkong.aliyuncs.com/agree_en.html
         * apphelp_guide_en : https://gbpc2019.oss-cn-hongkong.aliyuncs.com/guide_en.html
         * apidomain : http://api.777zhifu.com:7910/bcc-api
         * accreditid : BCC01-GBPC
         * accreditkey : BCC1gbpc
         * apiSecretSalt : 20190501
         * symbol : GBPC
         * backurl : http://api.777zhifu.com:7910/callback
         * appver : 1.1.9
         * downurl_android : https://gbpc2019.oss-cn-hongkong.aliyuncs.com/gbpc-1.1.9.apk
         * downurl_ios : https://gbpc2019.oss-cn-hongkong.aliyuncs.com/gbpc-1.1.9.plist
         * upgrade_note_cn : 修改了GBPC的合约代码。
         * upgrade_note_en : Change GBPC contract address.
         */

        private String apphelp_agree;
        private String apphelp_guide;
        private String apphelp_agree_en;
        private String apphelp_guide_en;
        private String apidomain;
        private String accreditid;
        private String accreditkey;
        private Integer apiSecretSalt;
        private String symbol;
        private String backurl;
        private String appver;
        private String downurl_android;
        private String downurl_ios;
        private String upgrade_note_cn;
        private String upgrade_note_en;
    }
}
