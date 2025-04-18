package com.basestudy.rewards.constants;

public enum CouponStatus {
    STANDBY("대기"), //초기 등록, 변경가능
    ACTIVE("활성화"), //발급진행
    SUSPENDED("발급중단"), //발급중단, 사유작성
    EXHAUSTED("수량소진"), //발급중단, 수량소진
    DELETED("삭제"); //삭제된쿠폰

    private final String description;

    CouponStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
