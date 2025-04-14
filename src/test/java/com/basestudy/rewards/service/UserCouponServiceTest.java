package com.basestudy.rewards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.constants.CouponLockStatus;
import com.basestudy.rewards.constants.CouponStatus;
import com.basestudy.rewards.controller.dto.CouponLock;
import com.basestudy.rewards.controller.dto.UserCouponDto;
import com.basestudy.rewards.entity.Coupon;
import com.basestudy.rewards.entity.Member;
import com.basestudy.rewards.entity.UserCoupon;
import com.basestudy.rewards.repository.UserCouponRepository;


@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {
    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private RedisService redisService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private UserCouponServiceImpl userCouponService;

    private static final Long MEMBER_ID = 1L;
    private static final Long COUPON_ID = 100L;
    private static final String COUPON_LOCK_KEY = "coupon:lock:";

    @BeforeEach
    void setUp() {
        userCouponService = new UserCouponServiceImpl(
            userCouponRepository,
            kafkaProducer,
            redisService,
            couponService
        );
        userCouponService.setTtl(60);
        userCouponService.setCouponLockKey(COUPON_LOCK_KEY);
    }

    // ------------------------- distributeCoupon() 테스트 -------------------------

    @Test
    void distributeCoupon_쿠폰Lock없고_쿠폰미발급_상태() {
        // Given
        String key = COUPON_LOCK_KEY + MEMBER_ID;
        Member member = Member.builder().id(MEMBER_ID).build();
        when(redisService.get(key)).thenReturn(null);
        when(userCouponRepository.findByMemberIdAndCouponId(MEMBER_ID, COUPON_ID))
            .thenReturn(Optional.empty());

        // When
        ApiResponseWrapper<?> response = userCouponService.distributeCoupon(member, COUPON_ID);

        // Then
        assertEquals("발급되었습니다.", response.getData());
        verify(redisService).save(eq(key), any(CouponLock.class));
        verify(kafkaProducer).sendMessage(any(String.class));
    }

    @Test
    void distributeCoupon_쿠폰Lock없지만_이미_쿠폰발급됨() {
        // Given
        String key = COUPON_LOCK_KEY + MEMBER_ID;
        Member member = Member.builder().id(MEMBER_ID).build();
        when(redisService.get(key)).thenReturn(null);
        when(userCouponRepository.findByMemberIdAndCouponId(MEMBER_ID, COUPON_ID))
            .thenReturn(Optional.of(new UserCoupon()));

        // When
        ApiResponseWrapper<?> response = userCouponService.distributeCoupon(member, COUPON_ID);

        // Then
        verify(redisService, never()).save(any(), any());
        assertEquals("이미 쿠폰을 발급받았습니다", response.getData());
    }

    @Test
    void distributeCoupon_쿠폰Lock존재하고_DONE상태() {
        // Given
        String key = COUPON_LOCK_KEY + MEMBER_ID;
        Member member = Member.builder().id(MEMBER_ID).build();
        CouponLock lock = CouponLock.builder().status(CouponLockStatus.DONE).build();
        when(redisService.get(key)).thenReturn(lock);

        // When
        ApiResponseWrapper<?> response = userCouponService.distributeCoupon(member, COUPON_ID);

        // Then
        assertEquals("이미 쿠폰을 발급받았습니다", response.getData());
    }

    @Test
    void distributeCoupon_쿠폰Lock존재하고_PROCESSING상태() {
        // Given
        String key = COUPON_LOCK_KEY + MEMBER_ID;
        Member member = Member.builder().id(MEMBER_ID).build();
        CouponLock lock = CouponLock.builder().status(CouponLockStatus.PROCESSING).build();
        when(redisService.get(key)).thenReturn(lock);

        // When
        ApiResponseWrapper<?> response = userCouponService.distributeCoupon(member, COUPON_ID);

        // Then
        assertEquals("처리중입니다. 잠시후 다시 시도해주세요.", response.getData());
    }

    // ------------------------- saveCoupon() 테스트 -------------------------

    @Test
    void saveCoupon_정상동작() {
        // Given
        String key = COUPON_LOCK_KEY + MEMBER_ID;
        UserCouponDto dto = UserCouponDto.builder().couponId(COUPON_ID).memberId(MEMBER_ID).build();
        Coupon coupon = Coupon.builder()
                        .id(5L)
                        .name("10%할인")
                        .availableFrom(LocalDateTime.now())
                        .availableTo(LocalDateTime.now().plusDays(1))
                        .totalQuantity(100)
                        .useDays(5)
                        .status(CouponStatus.ACTIVE) // 상태 설정
                        .build();
        when(couponService.findCouponById(COUPON_ID)).thenReturn(coupon);

        // When
        userCouponService.saveUserCoupon(dto);

        // Then
        verify(userCouponRepository).save(any(UserCoupon.class));
        verify(redisService).save(eq(key), any(CouponLock.class));
    }

    // ------------------------- 예외 케이스 테스트 -------------------------

    @Test
    void saveCouponLockAndTriggerKafka_Kafka전송실패시_Lock삭제() {
        // Given
        String key = COUPON_LOCK_KEY + MEMBER_ID;
        Member member = Member.builder().id(MEMBER_ID).build();

        // Redis에 Lock이 없는 상태 설정
        when(redisService.get(key)).thenReturn(null);
        
        // Kafka 예외 강제 발생
        doThrow(new RuntimeException("Kafka error")).when(kafkaProducer).sendMessage(any());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userCouponService.distributeCoupon(member, COUPON_ID);
        });

        // 예외 메시지 검증
        assertEquals(exception.getMessage(), "Kafka 호출 중 오류가 발생했습니다.");
        
        // Redis Lock 삭제 검증
        verify(redisService).delete(key);
    }
}
