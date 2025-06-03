package com.basestudy.rewards.infra;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.basestudy.rewards.controller.dto.UserCouponDto;
import com.basestudy.rewards.infra.dto.CouponIssueRequest;
import com.basestudy.rewards.service.UserCouponService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final UserCouponService userCouponService;
    private static final String TOPIC = "COUPON";
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}") 
    public void listen(ConsumerRecord<String, String> record) {
        
        try{
            CouponIssueRequest request = objectMapper.readValue(record.value(), CouponIssueRequest.class);
            log.debug("kafkaListener : couponID = {}, userId = {}", request.getCouponId(), request.getUserId());
            userCouponService.saveUserCoupon(request.getCouponId(), request.getUserId());
        } catch (JsonProcessingException e) {
            log.error("KafkaConsumer::listen - JSON 파싱 오류: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 파싱 오류-DLQ전송", e);
        } catch (Exception e) {
            log.error("KafkaConsumer::listen - 쿠폰 발급 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("쿠폰 발급 처리 중 오류 발생-DLQ전송", e);
        }
    }
}
