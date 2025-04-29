package com.basestudy.rewards.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.constants.CouponLockStatus;
import com.basestudy.rewards.constants.CouponStatus;
import com.basestudy.rewards.controller.dto.CouponLock;
import com.basestudy.rewards.controller.dto.UserCouponDto;
import com.basestudy.rewards.domain.Coupon;
import com.basestudy.rewards.domain.Member;
import com.basestudy.rewards.domain.Quantity;
import com.basestudy.rewards.domain.UserCoupon;
import com.basestudy.rewards.infra.KafkaProducer;
import com.basestudy.rewards.infra.RedisRepository;
import com.basestudy.rewards.repository.CouponRepository;
import com.basestudy.rewards.repository.UserCouponRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {
   
    @Mock
    private UserCouponRepository userCouponRepository;
    
    @Mock
    private KafkaProducer kafkaProducer;
    
    @Mock
    private RedisRepository redisRepository;
    
    @Mock
    private CouponService couponService;
    
    @Mock
    private RedissonClient redissonClient;
    
    @Mock
    private Coupon coupon;

    @InjectMocks
    private UserCouponServiceImpl userCouponService;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userCouponService, "lockKey", "coupon:lock:");
    }

    @Test
    void 쿠폰_발급_성공_테스트() {
        // given
        Member member = new Member();
        member.setId(1L);
        
        final int COUPON_QUANTITY = 10; // 쿠폰은 10개만 있음
        final Long couponId = 1L;

        Quantity quantity = new Quantity(COUPON_QUANTITY, 0);
        Coupon coupon = new Coupon();
        coupon.setId(couponId);
        coupon.setStatus(CouponStatus.ACTIVE);
        coupon.setAvailableFrom(LocalDateTime.now().minusDays(4));
        coupon.setAvailableTo(LocalDateTime.now().plusDays(5));
        coupon.setQuantity(quantity);
        coupon.setUseDays(30);
        
        // DB에서 쿠폰 발급 이력 없음
        when(userCouponRepository.findByMemberIdAndCouponId(member.getId(), couponId))
            .thenReturn(Optional.empty());
        
        // 쿠폰 유효성 검증 통과
        when(couponService.findCouponById(couponId)).thenReturn(coupon);
        // when(coupon.canIssue(LocalDateTime.now())).thenReturn(true);
        
        // 락 획득 성공
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        try {
            when(mockLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);
        doNothing().when(mockLock).unlock();
        
        // Redis에서 락 상태 확인
        when(redisRepository.getLockKey(member.getId())).thenReturn(null);
        
        // 쿠폰 수량 감소 성공
        when(couponService.decreaseCouponQuantity(couponId)).thenReturn(9L);
        
        // Kafka 메시지 발행 성공
        doNothing().when(redisRepository).saveLockKeyProcessing(member.getId(), couponId);
        
        // when
        ApiResponseWrapper<?> result = userCouponService.distributeCoupon(member, couponId);
        
        // then
        assertNotNull(result);
        assertEquals("200", result.getCode());
        assertEquals("발급되었습니다.", result.getData());
        
        // 주요 메소드 호출 검증
        verify(redisRepository).saveLockKeyProcessing(member.getId(), couponId);
        verify(kafkaProducer).sendCouponRequest(couponId, member.getId());
        verify(mockLock).unlock();
    }

    @Test
    void 이미_발급된_쿠폰_발급시도_테스트() throws InterruptedException {
        // given
        Member member = new Member();
        member.setId(1L);
        Long couponId = 1L;
        
        // DB에서 이미 발급된 쿠폰 이력 존재
        when(userCouponRepository.findByMemberIdAndCouponId(member.getId(), couponId))
            .thenReturn(Optional.of(new UserCoupon()));
    
        // when
        ApiResponseWrapper<?> result = userCouponService.distributeCoupon(member, couponId);
        System.out.println("==== 쿠폰 발급 실패 ====memberId:{"+member.getId()+"}===={"+result.getCode()+"}");
        // then
        // 락 획득 시도하지 않음 검증
        verify(redissonClient, never()).getLock(anyString());

        assertEquals("200", result.getCode());
        assertEquals("이미 쿠폰을 발급받았습니다", result.getData());
    }

    @Test
    void 유효하지_않은_쿠폰_발급시도_테스트() {
        // given
        Member member = new Member();
        member.setId(1L);
        Long couponId = 1L;

        // DB에서 쿠폰 발급 이력 없음
        when(userCouponRepository.findByMemberIdAndCouponId(member.getId(), couponId))
            .thenReturn(Optional.empty());
        
        // 쿠폰 유효성 검증 실패
        when(couponService.findCouponById(couponId)).thenReturn(coupon);
        when(coupon.canIssue(any(LocalDateTime.class))).thenReturn(false);
        
        // when, then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userCouponService.distributeCoupon(member, couponId);
        });
        
        assertEquals("쿠폰을 발급할 수 없습니다.", exception.getMessage());
    }

    @Test
    void 락_획득_실패_테스트() throws InterruptedException {
        // given
        Member member = new Member();
        member.setId(1L);
        Long couponId = 1L;
        
        // DB에서 쿠폰 발급 이력 없음
        when(userCouponRepository.findByMemberIdAndCouponId(member.getId(), couponId))
            .thenReturn(Optional.empty());
        
        // 쿠폰 유효성 검증 통과
        when(couponService.findCouponById(couponId)).thenReturn(coupon);
        when(coupon.canIssue(any(LocalDateTime.class))).thenReturn(true);
        
        // 락 획득 실패
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(5L, 10L, TimeUnit.SECONDS)).thenReturn(false);
       
        
        // when
        ApiResponseWrapper<?> result = userCouponService.distributeCoupon(member, couponId);
        
        // then
        assertNotNull(result);
        assertEquals("429", result.getCode());
        assertEquals("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", result.getMessage());
    }
    
    @Test
    void 쿠폰_소진_테스트() {
        // given
        Member member = new Member();
        member.setId(1L);
        Long couponId = 1L;
        
        // 쿠폰 조회 및 유효성 검증 통과
        when(userCouponRepository.findByMemberIdAndCouponId(member.getId(), couponId))
            .thenReturn(Optional.empty());
        when(couponService.findCouponById(couponId)).thenReturn(coupon);
        when(coupon.canIssue(any(LocalDateTime.class))).thenReturn(true);
        
        // 락 획득 성공
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        try {
            when(mockLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);
        
        // Redis에서 락 상태 확인
        when(redisRepository.getLockKey(member.getId())).thenReturn(null);
        
        // 쿠폰 수량 감소 실패 (소진)
        doThrow(new RuntimeException("쿠폰이 모두 소진되었습니다"))
            .when(couponService).decreaseCouponQuantity(couponId);
        
        // when
        ApiResponseWrapper<?> result = userCouponService.distributeCoupon(member, couponId);
        
        // then
        assertEquals("400", result.getCode());
        assertEquals("쿠폰이 모두 소진되었습니다.", result.getMessage());
        
        // 락 해제 검증
        verify(mockLock).unlock();
    }

}
