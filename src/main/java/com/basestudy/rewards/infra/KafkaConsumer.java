package com.basestudy.rewards.infra;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.basestudy.rewards.controller.dto.UserCouponDto;
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


    @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}") //동일 그룹내 소비자들 파티션 병렬처리
    public void listen(ConsumerRecord<Long, Long> record) {
        Long couponId = record.key();
        Long userId = record.value();
        log.debug("kafkaListener : TOPIC = {}, couponID = {}, userId = {}", TOPIC, couponId, userId);
        userCouponService.saveUserCoupon(couponId, userId);
    }
}
