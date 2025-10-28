package com.jing.admin.core.exception;
/**
 * 业务异常 - 检查型异常
 */
public class BusinessException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    // 默认构造器
    public BusinessException() {
        super();
    }

    // 带错误消息的构造器
    public BusinessException(String message) {
        super(message);
        this.errorMessage = message;
    }

    // 带错误码和错误消息的构造器
    public BusinessException(String errorCode, String errorMessage) {
        super(errorCode + ": " + errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // 带错误消息和原因的构造器
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    // 带错误码、错误消息和原因的构造器
    public BusinessException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode + ": " + errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}