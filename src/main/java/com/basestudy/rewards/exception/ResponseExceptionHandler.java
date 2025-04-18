package com.basestudy.rewards.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.basestudy.rewards.ApiResponseWrapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class ResponseExceptionHandler{
    //TODO exception 정의 및 핸들러 추가
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponseWrapper<?> handleServerException(Exception e){
        log.error("debug log for error ={}",e.getMessage(), e);
        
        return ApiResponseWrapper.createFail(e.getMessage(), "400", e.getMessage());
    }
}
