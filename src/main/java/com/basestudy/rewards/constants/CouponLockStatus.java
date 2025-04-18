package com.basestudy.rewards.constants;

public enum CouponLockStatus {
    DONE("발급완료"),
    PROCESSING("발급진행중");

    private final String description;

    CouponLockStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
