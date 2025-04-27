package com.basestudy.rewards.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    
    BAD_REQUEST(400, "400", "", ""),
    UNAUTHORIZED(401, "401", "", ""),
    FORBIDDEN(403, "403", "", "");
    
    private int status;
    private String code;
    private String message;
    private String detail;
}
