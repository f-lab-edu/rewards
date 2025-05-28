package com.basestudy.rewards.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basestudy.rewards.domain.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, UserWithCouponsView{
    Optional<UserCoupon> findByMemberIdAndCouponId(Long memberId, Long couponId);
    public List<UserCoupon> findByMemberId(Long memberId);
    
}
