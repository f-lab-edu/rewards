package com.basestudy.rewards.contants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseMessage {
    ACCEPTED(202, "202", "", ""),
    CREATED(201, "201", "", "등록되었습니다.");

    private int status;
    private String code;
    private String message;
    private String detail;
}
