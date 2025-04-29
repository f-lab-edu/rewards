package com.basestudy.rewards.infra;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    
    private static final String TOPIC = "COUPON";
    private final KafkaTemplate<Long, Long> producer;

    public void sendCouponRequest(Long couponId, Long userId) {
        ProducerRecord<Long, Long> record = new ProducerRecord<>(TOPIC, couponId, userId);
        // 쿠폰ID를 키로 설정하여 동일 쿠폰은 항상 같은 파티션으로
        CompletableFuture<SendResult<Long, Long>> future = producer.send(record);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.debug("쿠폰 요청 성공: 쿠폰ID={}, 파티션={}, 오프셋={}", 
                            couponId, metadata.partition(), metadata.offset());
                } else {
                    log.error("쿠폰 요청 실패: {}", ex.getMessage(), ex);
                    throw new RuntimeException("쿠폰 요청 실패", ex);
                }
            });
    }
}
