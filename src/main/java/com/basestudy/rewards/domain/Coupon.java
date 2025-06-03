package com.basestudy.rewards.domain;

import java.time.Duration;
import java.time.LocalDateTime;

import com.basestudy.rewards.constants.CouponStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="coupon")
@EqualsAndHashCode(callSuper = false, of = "id")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "available_from")
    private LocalDateTime availableFrom; //발급시작일

    @Column(nullable = false, name = "available_to")
    private LocalDateTime availableTo; //발급종료일

    @Column(nullable = false)
    private int useDays; // 사용가능일수

    @Enumerated(EnumType.STRING)
    private CouponStatus status; //쿠폰 상태

    @Column
    private String suspensionReason; //발급 중단 사유 (필요 시)

    @Embedded
    private Quantity quantity;

    public void activate() {
        if (this.status != CouponStatus.STANDBY) {
            throw new IllegalStateException("STANDBY 상태에서만 활성화할 수 있습니다.");
        }
        this.status = CouponStatus.ACTIVE;
    }

    public void suspend(String reason) {
        if (!isActive()) throw new IllegalStateException("활성 상태가 아닙니다.");
        if (reason.isBlank()) throw new IllegalArgumentException("사유 필요");
        this.status = CouponStatus.SUSPENDED;
    }

    public void delete(){
        if (isActive()) throw new IllegalArgumentException("발급중인 쿠폰입니다. 발급중단을 이용하세요.");
        this.status = CouponStatus.DELETED;
    }

    public void update(LocalDateTime availableFrom, LocalDateTime availableTo, int totalQuantity, int useDays){
        if (isActive()) throw new IllegalArgumentException("발급중인 쿠폰입니다. 발급중단을 이용하세요.");
        if (!isStandby()) throw new IllegalArgumentException("대기 상태의 쿠폰 정보만 변경 가능합니다.");
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.setQuantity(quantity);
        this.useDays = useDays;
    }

    public boolean isIssuable(LocalDateTime now) {
        return status == CouponStatus.ACTIVE && now.isAfter(availableFrom) && now.isBefore(availableTo);
    }

    public void markAsDeleted() {
        this.status = CouponStatus.DELETED;
    }

    public boolean isStandby() {
        return this.status == CouponStatus.STANDBY;
    }

    public boolean isExhausted() {
        return this.status == CouponStatus.EXHAUSTED;
    }

    public boolean isActive() {
        return this.status == CouponStatus.ACTIVE;
    }

    public void checkAndUpdateStatus() {
        if (quantity.isExhausted()) {
            this.status = CouponStatus.EXHAUSTED;
        }
    }

    public long getAvailableSeconds(){
        return Duration.between(this.getAvailableFrom(), this.getAvailableTo()).getSeconds();
    }

    public boolean canIssue(LocalDateTime now) {
        if(!CouponPolicy.canIssue(this, now)){
            return false;
        }
        return true;
    }
}
