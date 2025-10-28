package com.jing.admin.core.exception;

/**
 * @author lxh
 * @date 2025/10/27
 **/
public class LoginException extends BusinessException{
    private String errorCode;
    private String errorMessage;

    public LoginException(){
        super();
    }

    public LoginException(String errorMessage){
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
