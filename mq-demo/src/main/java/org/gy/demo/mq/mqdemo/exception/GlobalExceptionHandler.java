package org.gy.demo.mq.mqdemo.exception;


import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.gy.framework.core.dto.Response;
import org.gy.framework.core.exception.BizException;
import org.gy.framework.core.exception.CommonException;
import org.gy.framework.lock.exception.DistributedLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;

/**
 * @author gy
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理一般异常。
     */
    @ExceptionHandler(Exception.class)
    public Response<Void> handle(Exception e) {
        log.error("全局异常处理：", e);
        return Response.asError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "网络繁忙，请稍后重试!");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public Response<Void> handle(HttpClientErrorException e) {
        log.error("依赖服务调用失败：", e);
        return Response.asError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器繁忙，请稍后重试!");
    }


    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Void> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String msg = constraintViolations.stream().findFirst().map(item -> String.format("【%s】%s", item.getPropertyPath(), item.getMessage())).orElseGet(() -> "Request parameter format error");
        log.warn("ConstraintViolation fail: url={}, msg={}", request.getRequestURI(), msg, e);
        return Response.asError(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 消息体解析异常返回处理
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public Response<Void> handleJsonParseException(HttpMessageConversionException e) {
        String msg = "解析消息体出现异常，请检查输入是否合法";
        log.warn("{} : {}", msg, e.getMessage(), e);
        return Response.asError(HttpStatus.BAD_REQUEST.value(), msg);
    }


    /**
     * 参数绑定异常返回处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleConstraintViolationException(MethodArgumentNotValidException e) {
        String msg = buildErrMsg(e.getBindingResult());
        log.warn("参数绑定异常: {}", msg, e);
        return Response.asError(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response<Void> handleConstraintViolationException(MethodArgumentTypeMismatchException e) {
        String msg = String.format("参数类型不匹配: %s", e.getMessage());
        log.warn(msg, e);
        return Response.asError(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 错误处理：缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String msg = String.format("缺少请求参数: %s", e.getMessage());
        log.warn(msg, e);
        return Response.asError(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 参数绑定异常返回处理
     */
    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(BindException e) {
        String msg = buildErrMsg(e.getBindingResult());
        log.warn("参数绑定异常: {}", msg, e);
        return Response.asError(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * METHOD_NOT_ALLOWED异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Response<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Response.asError(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
    }

    /**
     * HttpMediaTypeException异常
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleHttpMediaTypeException(HttpMediaTypeException e) {
        return Response.asError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BizException.class)
    public Response<Void> handleBizException(BizException e) {
        log.warn("业务异常捕获: code={},msg={}", e.getError(), e.getMsg(), e);
        return Response.asError(e);
    }

    /**
     * 业务异常处理
     */
    @ExceptionHandler(CommonException.class)
    public Response<Void> handleCommonException(CommonException e) {
        log.error("[CommonException]业务异常捕获: code={},msg={}", e.getError(), e.getMsg(), e);
        return Response.asError(e);
    }

    @ExceptionHandler(DistributedLockException.class)
    public Response<Void> handleBusinessException(DistributedLockException e) {
        log.warn("DistributedLock fail: {}.", e.getMessage(), e);
        return Response.asError(e.getCode(), e.getMessage());
    }

    private String buildErrMsg(HttpMessageConversionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            String fieldName = Optional.ofNullable(((InvalidFormatException) cause).getPath()).flatMap(list -> list.stream().findFirst()).map(Reference::getFieldName).orElseGet(() -> "Unknown");
            return String.format("【%s】%s", fieldName, "参数格式转换异常");
        } else {
            return cause.getMessage();
        }
    }


    private String buildErrMsg(BindingResult br) {
        return Optional.ofNullable(br).map(BindingResult::getFieldError).map(fieldError -> {
            String msg = Optional.ofNullable(fieldError.getDefaultMessage()).filter(s -> s.length() < 80).orElseGet(() -> "参数数据类型转换异常");
            return String.format("【%s】%s", fieldError.getField(), msg);
        }).orElseGet(() -> "请求参数格式错误");
    }

}
