package com.basestudy.rewards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basestudy.rewards.entity.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>{
    Optional<UserCoupon> findByMemberIdAndCouponId(long memberId, long couponId);
    
}
