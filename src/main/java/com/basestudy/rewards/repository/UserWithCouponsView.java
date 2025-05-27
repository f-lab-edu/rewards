package com.basestudy.rewards.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.basestudy.rewards.controller.dto.UserWithCouponsDto;

public interface UserWithCouponsView {
    public List<UserWithCouponsDto> findAllUsersWithCoupons(Long memberId, int offset, int limit);
}
