package com.xuecheng.exception;

import lombok.Data;

@Data
public class GlobalException extends RuntimeException{
    private String errMessage;
    public GlobalException() {
    }

    public GlobalException(String message) {
        super(message);
        this.errMessage = message;
    }
    public GlobalException(CommonError error) {
        super(error.errMessage);
        this.errMessage = error.errMessage;
    }
    public static void cast(CommonError error){
        throw new GlobalException(error);
    }
}
