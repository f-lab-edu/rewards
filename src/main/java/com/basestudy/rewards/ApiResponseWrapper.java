package com.basestudy.rewards;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "API 응답 래퍼")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseWrapper<T> { 
    @Schema(description = "처리 성공 여부", type = "boolean")
    private boolean success;
    @Schema(description = "응답 상태 코드", type = "string", example = "200")
    private String code; 
    @Schema(description = "응답 메시지", type = "string")
    private String message;
    @Schema(description = "응답 데이터")
    private T Data;

   
    public static<T> ApiResponseWrapper<T> createSuccess(T result) {
        ApiResponseWrapper<T> response = new ApiResponseWrapper<>();
        response.setSuccess(true);
        response.setCode("200");
        response.setMessage(null);
        response.setData(result);
        return response;
   }

    public static<T> ApiResponseWrapper<T> createFail(T result, String code, String message) {
        ApiResponseWrapper<T> response = new ApiResponseWrapper<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        response.setData(result);
        return response;
    }
}
