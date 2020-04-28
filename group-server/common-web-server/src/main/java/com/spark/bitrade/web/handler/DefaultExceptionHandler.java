package com.spark.bitrade.web.handler;

import com.netflix.client.ClientException;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.util.MessageRespResult;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * DefaultExceptionHandler
 *
 * @author archx
 * @since 2019/5/20 16:05
 */
@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class DefaultExceptionHandler {

    @ExceptionHandler(MessageCodeException.class)
    public ResponseEntity<MessageRespResult> handleMessageCodeException(HttpServletRequest request,
                                                                        Object target, MessageCodeException ex) {
        log.error("业务异常:exception handle [ uri = '{}', code = {}, message = '{}' ]", request.getRequestURI(), ex.getCode(), ex.getMessage());
        MessageRespResult resp = new MessageRespResult();
        resp.setCode(ex.getCode());
        resp.setMessage(ex.getMessage());
        log.error("exception :\n", ex);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * 拦截参数异常
     *
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<MessageRespResult> handlerException(HttpServletRequest request,
                                                              Object target, IllegalArgumentException ex) {
        log.error("参数异常:exception handle [ uri = '{}', message = '{}' ]", request.getRequestURI(), ex.getMessage());
        MessageRespResult resp = new MessageRespResult();
        resp.setCode(CommonMsgCode.INVALID_PARAMETER.getCode());
        resp.setMessage(CommonMsgCode.INVALID_PARAMETER.getMessage());
        log.error("exception :\n", ex);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * 拦截绑定参数异常
     */
    @ExceptionHandler(value = ServletRequestBindingException.class)
    public ResponseEntity<MessageRespResult> handlerException(HttpServletRequest request,
                                                              Object target, ServletRequestBindingException ex) {
        log.error("绑定参数异常:exception handle [ uri = '{}', message = '{}' ]", request.getRequestURI(), ex.getMessage());
        MessageRespResult resp = new MessageRespResult();
        resp.setCode(CommonMsgCode.REQUIRED_PARAMETER.getCode());
        resp.setMessage(CommonMsgCode.REQUIRED_PARAMETER.getMessage());
        log.error("exception :\n", ex);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * 错误请求方式异常
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<MessageRespResult> handlerException(HttpServletRequest request,
                                                              Object target, HttpRequestMethodNotSupportedException ex) {

        log.error("错误请求方式异常:exception handle [ uri = '{}', message = '{}' ]", request.getRequestURI(), ex.getMessage());
        MessageRespResult resp = new MessageRespResult();
        resp.setCode(CommonMsgCode.INVALID_REQUEST_METHOD.getCode());
        resp.setMessage(CommonMsgCode.INVALID_REQUEST_METHOD.getMessage());
        log.error("exception :\n", ex);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }


    /**
     * feign 服务调用异常，如"服务不可用：Connection refused" 和 “请求超时：Read timed out”
     */
    @ExceptionHandler(value = RetryableException.class)
    public ResponseEntity<MessageRespResult> handlerRetryableException(HttpServletRequest request,
                                                                       Object target, RetryableException ex) {

        log.error("feign服务调用异常:exception handle [ uri = '{}', message = '{}' ]", request.getRequestURI(), ex.getMessage());
        MessageRespResult resp = new MessageRespResult();
        if (ex.getMessage().startsWith("Connection refused")) {
            resp.setCode(CommonMsgCode.SERVICE_UNAVAILABLE.getCode());
            resp.setMessage(CommonMsgCode.SERVICE_UNAVAILABLE.getMessage());
        } else {
            resp.setMessage(CommonMsgCode.SERVICE_RESPONSE_TIMEOUT.getMessage());
            resp.setCode(CommonMsgCode.SERVICE_RESPONSE_TIMEOUT.getCode());
        }
        log.error("exception :\n", ex);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * ribbon 服务不可用异常
     */
    @ExceptionHandler(value = {ClientException.class, com.netflix.client.ClientException.class})
    public ResponseEntity<MessageRespResult> handlerClientException(HttpServletRequest request,
                                                                    Object target, ClientException ex) {

        log.error("ribbon服务不可用异常:exception handle [ uri = '{}', message = '{}' ]", request.getRequestURI(), ex.getMessage());
        MessageRespResult resp = new MessageRespResult();
        resp.setCode(CommonMsgCode.SERVICE_UNAVAILABLE.getCode());
        resp.setMessage(CommonMsgCode.SERVICE_UNAVAILABLE.getMessage());
        log.error("exception :\n", ex);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * 拦截异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<MessageRespResult> handlerException(HttpServletRequest request,
                                                              Object target, Exception ex) {

        log.error("未知异常:exception handle [ uri = '{}', message = '{}' ]", request.getRequestURI(), ex.getMessage());
        MessageRespResult resp = new MessageRespResult();
        if (Objects.nonNull(ex.getCause()) && ex.getCause() instanceof ClientException) {
            resp.setCode(CommonMsgCode.SERVICE_UNAVAILABLE.getCode());
            resp.setMessage(CommonMsgCode.SERVICE_UNAVAILABLE.getMessage());
        } else {
            resp.setCode(CommonMsgCode.UNKNOWN_ERROR.getCode());
            resp.setMessage(CommonMsgCode.UNKNOWN_ERROR.getMessage());
        }
        log.error("exception :\n", ex);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
