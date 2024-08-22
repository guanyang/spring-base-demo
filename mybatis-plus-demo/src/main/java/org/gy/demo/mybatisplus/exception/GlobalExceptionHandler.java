package org.gy.demo.mybatisplus.exception;

import lombok.extern.slf4j.Slf4j;
import org.gy.framework.core.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

/**
 * @author gy
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Void> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        String msg = buildErrMsg(e);
        log.warn("参数类型错误: url={} msg={} ", request.getRequestURI(), msg, e);
        return Response.asError(HttpStatus.BAD_REQUEST.value(), msg);
    }

    private String buildErrMsg(ConstraintViolationException e) {
        return Optional.ofNullable(e).map(ConstraintViolationException::getConstraintViolations).map(constraintViolations -> {
            StringBuilder sb = new StringBuilder();
            constraintViolations.forEach(constraintViolation -> sb.append(constraintViolation.getMessage()).append(";"));
            return sb.toString();
        }).orElseGet(() -> "请求参数格式错误");
    }

    private String buildErrMsg(BindingResult br) {
        return Optional.ofNullable(br).map(BindingResult::getFieldError).map(fieldError -> {
            String msg = Optional.ofNullable(fieldError.getDefaultMessage()).filter(s -> s.length() < 80).orElseGet(() -> "参数数据类型转换异常");
            return String.format("【%s】%s", fieldError.getField(), msg);
        }).orElseGet(() -> "请求参数格式错误");
    }
}
