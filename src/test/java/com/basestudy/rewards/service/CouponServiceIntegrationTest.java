package com.basestudy.rewards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.config.EmbeddedRedisConfig;
import com.basestudy.rewards.constants.CouponStatus;
import com.basestudy.rewards.domain.Coupon;
import com.basestudy.rewards.domain.Member;
import com.basestudy.rewards.domain.Quantity;
import com.basestudy.rewards.infra.RedisRepository;
import com.basestudy.rewards.repository.CouponRepository;
import com.basestudy.rewards.repository.UserCouponRepository;

@SpringBootTest(
    properties = {
        "zookeeper.jmx.log4j.disable=true",
        "zookeeper.root.logger=INFO, CONSOLE"
    }
)
@ActiveProfiles("test")
@EmbeddedKafka(
    topics = "test-topic",
    bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@Import(EmbeddedRedisConfig.class) 
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2 설정 강제 적용
public class CouponServiceIntegrationTest {

    // 서비스들 자동 주입
    @Autowired
    private UserCouponServiceImpl userCouponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private RedisRepository redisRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
 

    final int THREAD_COUNT = 100; // 100명이 동시에 요청
    final int COUPON_QUANTITY = 10; // 쿠폰은 10개만 있음
    
    @Test
    void 멀티스레드_환경에서_쿠폰발급_동시성제어_테스트() throws InterruptedException {
        // given
        AtomicInteger successCount = new AtomicInteger(0);

        Coupon coupon = Coupon.builder()
                            .availableFrom(LocalDateTime.now().minusDays(3))
                            .availableTo(LocalDateTime.now().plusDays(3))
                            .quantity(new Quantity(COUPON_QUANTITY, 0))
                            .status(CouponStatus.ACTIVE)
                            .useDays(20)
                            .name("테스트 쿠폰")
                            .build();          

        couponRepository.save(coupon);
        redisRepository.saveCountKey(coupon.getId(), COUPON_QUANTITY);
            
        // when
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final long memberId = i + 1;
            
            executor.submit(() -> {
                try {
                    Member threadSafeMember = new Member();
                    threadSafeMember.setId(memberId);
                    ApiResponseWrapper<?> result = userCouponService.distributeCoupon(threadSafeMember, coupon.getId());
                    System.out.println("==== thread 결과 ====memberId:{"+memberId+"}===={"+result.isSuccess()+"}"+"===={"+result.getData()+"}");
                    if(result.isSuccess() && result.getData().equals("발급되었습니다.")) {
                        successCount.incrementAndGet();
                    }
                }catch (Exception e) {
                    System.out.println("스레드 예외: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 모든 스레드 완료 대기
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // then
        assertEquals(10, successCount.get());
    }
}
