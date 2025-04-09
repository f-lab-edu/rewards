package com.basestudy.rewards.entity;

import java.time.LocalDateTime;

import com.basestudy.rewards.contants.CouponStatus;

import jakarta.persistence.Column;
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
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //TODO: name을 id로 변경하고 enum class로 관리하기
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "available_from")
    private LocalDateTime availableFrom; //발급시작일

    @Column(nullable = false, name = "available_to")
    private LocalDateTime availableTo; //발급종료일

    @Column(nullable = false, name = "total_quantity")
    private int totalQuantity; //총 발급 가능 수량

    @Column(nullable = false, name = "issued_quantity")
    private int issuedQuantity; //현재 발급된 수량

    @Column(nullable = false)
    private int useDays; // 사용가능일수

    @Enumerated(EnumType.STRING)
    private CouponStatus status; //쿠폰 상태

    @Column
    private String suspensionReason; //발급 중단 사유 (필요 시)
}
