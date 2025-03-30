package com.basestudy.rewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
// public class ResponseExceptionHandler{
//     @ExceptionHandler({Exception.class})
//     protected ResponseEntity<?> handleServerException(Exception e){
//         //TODO errorResponse 핸들러 
//         final ErrorResponse errorResponse = ErrorResponse.builder(e, null, e.getMessage()).build();
//         return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
//     }
// }
