package com.basestudy.rewards.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.basestudy.rewards.controller.dto.UserWithCouponsDto;
import com.basestudy.rewards.domain.QCoupon;
import com.basestudy.rewards.domain.QMember;
import com.basestudy.rewards.domain.QUserCoupon;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserWithCouponsViewImpl implements UserWithCouponsView{
    private final JPAQueryFactory queryFactory;

    public List<UserWithCouponsDto> findAllUsersWithCoupons(Long memberId, int offset, int limit) {
        QMember member = QMember.member;
        QUserCoupon userCoupon = QUserCoupon.userCoupon;
        QCoupon coupon = QCoupon.coupon;

        return queryFactory.select(Projections.constructor(
                    UserWithCouponsDto.class,
                    member.id,
                    member.name,
                    coupon.id,
                    coupon.name,
                    userCoupon.issueDate,
                    userCoupon.expiredDate
                ))
                .from(userCoupon)
                .leftJoin(member).on(userCoupon.memberId.eq(member.id))
                .leftJoin(coupon).on(userCoupon.couponId.eq(coupon.id))
                .where(member.id.eq(memberId)) //memberid필터링
                // .groupBy(member.memberId)
                .offset(offset) //페이징 시작점
                .limit(limit) //페이징 갯수
                .fetch();
    }
}