package com.xuecheng.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获我们抛出的异常
     */
    @ExceptionHandler(GlobalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse  handlerMyException(GlobalException e){
        log.error(e.getErrMessage());
        return new RestErrorResponse(e.getErrMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse  handlerNotReadableException(MethodArgumentNotValidException e){
        log.error("参数异常:{}",e.getMessage());
        return new RestErrorResponse(e.getMessage());
    }

    /**
     * 捕获除我们抛出外的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse  handlerException(Exception e){
        log.error("系统异常:{}",e.getMessage());
        return new RestErrorResponse(CommonError.COMMON_ERROR.errMessage);
    }

}
