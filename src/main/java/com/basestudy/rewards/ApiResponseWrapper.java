package com.basestudy.rewards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseWrapper<T> { 
    private boolean success;
    private String code; 
    private String messsage;
    private T Data;

   
    public static<T> ApiResponseWrapper<T> createSuccess(T result) {
        ApiResponseWrapper<T> response = new ApiResponseWrapper<>();
        response.setSuccess(true);
        response.setCode("200");
        response.setMesssage(null);
        response.setData(result);
        return response;
   }

    public static<T> ApiResponseWrapper<T> createFail(T result, String code, String message) {
        ApiResponseWrapper<T> response = new ApiResponseWrapper<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMesssage(message);
        response.setData(result);
        return response;
    }
}
