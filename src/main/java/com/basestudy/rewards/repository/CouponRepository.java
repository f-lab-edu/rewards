package com.basestudy.rewards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basestudy.rewards.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>{

    
}