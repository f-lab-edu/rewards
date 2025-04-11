package com.basestudy.rewards.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "user_coupon")
@EqualsAndHashCode(callSuper = false, of = "id")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCoupon {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //지연로딩 불가, DB의 auto increment에 키생성 위임
    private long id;

    @Id
    @Column(name = "coupon_id")
    private long couponId;

    @Id
    @Column(name = "member_id")
    private long memberId;

    @Column(nullable = false, name = "issue_date")
    private LocalDateTime issueDate;
    
    @Column(nullable = false, name = "exipred_date")
    private LocalDateTime expiredDate;

    @Column(nullable = false, name = "allow_dup")
    private boolean allowDup;
}
