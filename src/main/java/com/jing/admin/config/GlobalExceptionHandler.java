package com.jing.admin.config;

import com.jing.admin.core.HttpResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lxh
 * @date 2025/9/18
 **/
@RestControllerAdvice // 全局异常处理器
public class GlobalExceptionHandler {
    /**
     * 处理参数校验异常（如 @Valid 注解触发的异常）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpResult handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        // 获取所有字段校验错误
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField()+error.getDefaultMessage());
        }
        return HttpResult.fail(String.valueOf(HttpStatus.BAD_REQUEST.value()), String.format("参数校验失败：%s",String.join(",",errors)));
    }

    /**
     * 处理运行时异常（如 NullPointerException、IllegalArgumentException 等）
     */
    @ExceptionHandler(RuntimeException.class)
    public HttpResult handleRuntimeExceptions(RuntimeException ex) {

        return HttpResult.fail(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage());
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public HttpResult handleGeneralExceptions(Exception ex) {
        return HttpResult.fail(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),  String.format("服务器内部错误：%s",ex.getMessage()));
    }
}
