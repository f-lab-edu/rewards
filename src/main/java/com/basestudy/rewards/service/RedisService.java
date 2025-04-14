package com.basestudy.rewards.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 데이터 저장 (TTL 포함)
    public void save(String key, Object value, long ttlInSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlInSeconds, TimeUnit.SECONDS);
    }

    // 데이터 저장 (TTL 없이)
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 데이터 조회
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 데이터 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // TTL 확인
    public Long getTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // 수량감소
    public long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }
    
    // pub
    public void sendMessage(String channel, Object message){
        redisTemplate.convertAndSend(channel, message);
        log.info("Message sent to channel={}, message={}", channel, message);
    }
    
}
