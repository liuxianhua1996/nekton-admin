package com.jing.admin.core;

import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * @author lxh
 * @date 2025/9/18
 **/
@Data
public class HttpResult<T> implements Serializable {
    public HttpResult() {
    }
    public T data;
    public boolean success;
    private String traceId;

    public String error;
    public String errorCode;
    public String message;
    HttpResult(T data){
        this.data= data;
        this.success = true;
        this.error = "";
        this.message = "";
        this.traceId =MDC.get(ThreadMdcUtils.getTraceId());
    }
    HttpResult(T data,String error,String message){
        this.data= data;
        this.success = false;
        this.error = error;
        this.message = message;
        this.traceId = MDC.get(ThreadMdcUtils.getTraceId());
    }
    HttpResult(String error,String message){
        this.success = false;
        this.error = error;
        this.message = message;
        this.traceId = MDC.get(ThreadMdcUtils.getTraceId());
    }

    public static <T> HttpResult<T> success(T data){
        return new HttpResult(data);
    }
    public static <T> HttpResult<T> success(){
        return new HttpResult();
    }
    public static <T> HttpResult<T> fail(T data,String error,String message){
        return new HttpResult(data,error,message);
    }
    public static <T> HttpResult<T> fail(String error,String message){
        return new HttpResult(error,message);
    }
    public static <T> HttpResult<T> fail(String message){
        return new HttpResult("500",message);
    }
    public static <T> HttpResult<T> error(String message){
        return new HttpResult("500",message);
    }

}
