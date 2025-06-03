package com.basestudy.rewards.infra;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.basestudy.rewards.infra.dto.CouponIssueRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    
    private static final String TOPIC = "COUPON";
    private final KafkaTemplate<String, String> producer;
    private final ObjectMapper objectMapper;

    public void sendCouponRequest(Long couponId, Long userId) throws JsonProcessingException {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, String.valueOf(userId), objectMapper.writeValueAsString(new CouponIssueRequest(couponId, userId)));

        CompletableFuture<SendResult<String, String>> future = producer.send(record);
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
