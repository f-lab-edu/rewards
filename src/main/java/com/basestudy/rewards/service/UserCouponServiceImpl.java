package com.basestudy.rewards.service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.constants.CouponLockStatus;
import com.basestudy.rewards.controller.dto.CouponLock;
import com.basestudy.rewards.controller.dto.UserCouponDto;
import com.basestudy.rewards.domain.Coupon;
import com.basestudy.rewards.domain.Member;
import com.basestudy.rewards.infra.KafkaProducer;
import com.basestudy.rewards.infra.RedisRepository;
import com.basestudy.rewards.repository.UserCouponRepository;
import com.basestudy.rewards.service.mapper.UserCouponMapper;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Setter
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService{
    private final UserCouponRepository userCouponRepository;
    private final KafkaProducer kafkaProducer;
    private final RedisRepository redisRepository;
    private final CouponService couponService;
    private final RedissonClient redissonClient;
    @Value("${spring.data.redis.coupon.lock.prefix}")
    private String lockKey;

    public ApiResponseWrapper<?> distributeCoupon(Member member, Long couponId) {
        // 1. 권한 확인 (TODO)

        // 2. 중복 발급 빠른 확인 (락 획득 전)
        if (existsByMemberIdAndCouponId(member.getId(), couponId)) {
            return createAlreadyIssuedResponse();
        }
        
        // 3. 쿠폰 유효성 검증
        validateCoupon(couponId);
        
        // 4. 락 획득 및 쿠폰 발급 처리
        return executeWithLock(lockKey+couponId, () -> processCouponIssuance(member.getId(), couponId));
    }

    // 중복 발급 확인
    private boolean existsByMemberIdAndCouponId(Long memberId, Long couponId) {
        return userCouponRepository.findByMemberIdAndCouponId(memberId, couponId).isPresent();
    }

    // 쿠폰 유효성 검증
    private Coupon validateCoupon(Long couponId) {
        Coupon coupon = couponService.findCouponById(couponId);
        if (!coupon.canIssue(LocalDateTime.now())) {
            throw new RuntimeException("쿠폰을 발급할 수 없습니다.");
        }
        return coupon;
    }

    // 락 획득 후 실행되는 메소드
    private ApiResponseWrapper<?> executeWithLock(String lockKey, Supplier<ApiResponseWrapper<?>> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                log.debug("쿠폰 발급 대기 중...");
                return ApiResponseWrapper.createFail(null, "429", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("쿠폰 발급 중 인터럽트 발생");
            return ApiResponseWrapper.createFail(null, "500", "처리 중 인터럽트가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 실제 쿠폰 발급 처리 로직
    @Transactional
    private ApiResponseWrapper<?> processCouponIssuance(Long memberId, Long couponId) {
        try {
            // 1. 사용자 락 상태 확인

            CouponLock couponLock = redisRepository.getLockKey(memberId);
            
            if (couponLock != null && couponLock.getStatus().equals(CouponLockStatus.DONE)) {
                return createAlreadyIssuedResponse();
            }
            
            // 2. 쿠폰 수량 감소
            try {
                couponService.decreaseCouponQuantity(couponId);
            } catch (RuntimeException e) {
                log.error("쿠폰이 모두 소진되었습니다.");
                return ApiResponseWrapper.createFail(null, "400", "쿠폰이 모두 소진되었습니다.");
            }
            
            // 3. 락 상태 업데이트 및 Kafka 메시지 발행
            saveCouponLockAndTriggerKafka(couponId, memberId);
            
            return ApiResponseWrapper.createSuccess("발급되었습니다.");
            
        } catch (Exception e) {
            // 4. 실패 시 롤백 및 에러 처리
            rollbackCouponIssuance(memberId, couponId, e);
            log.error("쿠폰 발급 중 오류 발생: " + e.getMessage());
            return ApiResponseWrapper.createFail(null, "400", "쿠폰 발급에 실패했습니다.");
        }
    }

    // 중복 코드 제거를 위한 헬퍼 메소드
    private ApiResponseWrapper<String> createAlreadyIssuedResponse() {
        return ApiResponseWrapper.createSuccess("이미 쿠폰을 발급받았습니다");
    }

    // 예외 발생 시 롤백 처리
    private void rollbackCouponIssuance(Long memberId, Long couponId, Exception e) {
        log.error("쿠폰 발급 실패: memberId={}, message={}, e={}",memberId, e.getMessage(), e);
        redisRepository.increment(couponId);
        redisRepository.deleteLockKey(memberId);
    }

    private void saveCouponLockAndTriggerKafka(Long couponId, Long memberId) {
        redisRepository.saveLockKeyProcessing(memberId, couponId);
        kafkaProducer.sendCouponRequest(couponId, memberId);
    }

    @Transactional
    public void saveUserCoupon(Long couponId, Long memberId) {
        //발급db저장
        Coupon coupon = couponService.findCouponById(couponId);
        UserCouponDto userCouponDto = UserCouponDto.builder()
                    .couponId(couponId)
                    .memberId(memberId)
                    .issueDate(LocalDateTime.now())
                    .expiredDate(LocalDateTime.now().plusDays(coupon.getUseDays())).build();
        
        userCouponRepository.save(UserCouponMapper.toEntity(userCouponDto));
        redisRepository.saveLockKeyDone(userCouponDto.getMemberId(), userCouponDto.getCouponId());
    }

}
