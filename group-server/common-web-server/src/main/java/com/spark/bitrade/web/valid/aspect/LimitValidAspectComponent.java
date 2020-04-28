package com.spark.bitrade.web.valid.aspect;

import com.spark.bitrade.constant.RiskLimitEvent;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.web.adapter.MemberAccountGetterAdapter;
import com.spark.bitrade.web.valid.LimitValidator;
import com.spark.bitrade.web.valid.annotation.LimitValid;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * LimitValidAspect
 *
 * @author archx
 * @since 2019/5/17 18:07
 */
@Component
@Aspect
@Slf4j
public class LimitValidAspectComponent extends MemberAccountGetterAdapter implements ApplicationContextAware {

    private ApplicationContext act;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        act = applicationContext;
        this.setMemberAccountService(act.getBean(MemberAccountService.class));
    }

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.spark.bitrade.web.valid.annotation.LimitValid)")
    public void validPointcut() {
    }

    @Before(value = "validPointcut() && @annotation(valid)", argNames = "jp,valid")
    public void beforeAdvice(JoinPoint jp, LimitValid valid) {

        Class<? extends LimitValidator>[] validClasses = valid.validBy();

        if (validClasses.length == 0) {
            return;
        }

        if (valid.limit() == RiskLimitEvent.NONE) {
            return;
        }

        Member member = getMember(jp, valid);

        Optional<LimitValidator[]> validators = getValidator(validClasses);
        if (validators.isPresent()) {
            for (LimitValidator validator : validators.get()) {
                MsgCode ret = validator.valid(member, valid.limit());
                if (ret.getCode() != 0) {
                    throw new MessageCodeException(ret);
                }
            }
        }
    }

    private Optional<LimitValidator[]> getValidator(Class<? extends LimitValidator>[] validClasses) {
        try {
            List<LimitValidator> validators = new ArrayList<>();
            for (Class<? extends LimitValidator> validClass : validClasses) {
                validators.add(act.getBean(validClass));
            }
            // 排序
            validators.sort(Comparator.comparingInt(LimitValidator::order));
            return Optional.of(validators.toArray(new LimitValidator[0]));
        } catch (BeansException ex) {
            log.error("获取LimitValidator实现出错", ex);
        }
        return Optional.empty();
    }

    private Member getMember(JoinPoint jp, LimitValid valid) {
        for (Object arg : jp.getArgs()) {
            if (arg instanceof Member) {
                return (Member) arg;
            }
        }

        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();

        String apiKey = request.getHeader(valid.value());

        return getCurrentMember(apiKey);
    }
}
