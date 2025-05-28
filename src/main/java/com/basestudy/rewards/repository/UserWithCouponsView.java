package com.basestudy.rewards.repository;

import java.util.List;

import com.basestudy.rewards.controller.dto.UserWithCouponsDto;

public interface UserWithCouponsView {
    public List<UserWithCouponsDto> findAllUsersWithCoupons(Long memberId, int offset, int limit);
}
