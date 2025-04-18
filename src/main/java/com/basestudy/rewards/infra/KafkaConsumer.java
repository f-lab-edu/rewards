package com.basestudy.rewards.infra;

import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = "COUPON", groupId = "my-group") //동일 그룹내 소비자들 파티션 병렬처리
    public void consumeMessage(String message) {
        log.info("Consumed message: {}",message);
        //db저장
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UserCouponDto userCouponDto = objectMapper.readValue(message, UserCouponDto.class);
            userCouponService.saveUserCoupon(userCouponDto);
        } catch (JsonProcessingException e) {
            log.error("Consumed error: message={}, consued message={}",e.getMessage(), message);
            //TODO: objectmapper가 에러가 나면 key추출도 안되는거아닌가..
        }
    }
}
