package com.spark.bitrade.system;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * TemplateHandler
 *
 * @author wsy
 * @since 2019/6/20 13:47
 */
@Slf4j
public class TemplateHandler {

    private static final TemplateHandler instance = new TemplateHandler();

    private TemplateHandler() {

    }

    public static TemplateHandler getInstance() {
        return instance;
    }

    public String handler(String templateFile, Map<String, Object> model) {
        String locale = "zh_CN";
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (null != request) {
                locale = request.getLocale().toString();
            }
        }

        // 处理默认语言
        if (!"zh_CN".equals(locale) && !"en_US".equals(locale)) {
            locale = "zh_CN";
        }

        String html = null;
        Map<String, Object> map = new HashMap<>(model);
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setClassForTemplateLoading(this.getClass(), "/templates/" + locale);
            Template template = cfg.getTemplate(templateFile);
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        } catch (Exception e) {
            log.error("处理邮件模板失败");
        }
        return html;
    }
}
