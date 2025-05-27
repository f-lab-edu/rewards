package com.basestudy.rewards.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.basestudy.rewards.controller.dto.UserWithCouponsDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserWithCouponsViewImpl implements UserWithCouponsView{
    private final JPAQueryFactory queryFactory;

    public List<UserWithCouponsDto> findAllUsersWithCoupons(Long memberId, int offset, int limit) {
        QMember member = QMember.member;
        QUserCoupon user_coupon = QUserCoupon.user_coupon;

        return queryFactory.select(Projections.constructor(
                    UserWithCouponsDto.class,
                    member.memberId,
                    user_coupon.coupons
                ))
                .from(member)
                .leftJoin(user_coupon).on(member.memberId.eq(user_coupon.memberId))
                .where(member.memberId.eq(memberId)) //memberid필터링
                .groupBy(member.memberId)
                .offset(offset) //페이징 시작점
                .limit(limit) //페이징 갯수
                .fetch();
        
        
        selectFrom(QUserWithCoupons.userWithCoupons)
                .fetch();
    }
}