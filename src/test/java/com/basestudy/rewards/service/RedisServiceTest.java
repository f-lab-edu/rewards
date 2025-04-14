package com.basestudy.rewards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisServiceTest {
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        // 1. LettuceConnectionFactory 설정
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory("localhost", 6379);
        connectionFactory.afterPropertiesSet();

        // 2. RedisTemplate 생성 및 설정
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        // 3. RedisService 초기화
        redisService = new RedisService(redisTemplate);
    }

    @Test
    public void testSaveAndGet() {
        // Given: 저장할 키와 값 설정
        String key = "testKey";
        String value = "testValue";

        // When: 데이터를 저장하고 조회
        redisService.save(key, value);
        Object retrievedValue = redisService.get(key);
        
        // Then: 저장된 값과 조회된 값이 일치하는지 확인
        assertEquals(retrievedValue, value);
    }

    @Test
    public void testDelete() {
        // Given: 저장할 키와 값 설정
        String key = "testKeyToDelete";
        String value = "valueToDelete";

        // When: 데이터를 저장하고 삭제 후 조회
        redisService.save(key, value);
        redisService.delete(key);
        Object retrievedValue = redisService.get(key);

        // Then: 삭제 후 값이 null인지 확인
        assertNull(retrievedValue);
    }

    @Test
    public void testSaveWithTTL() throws InterruptedException {
        // Given: 저장할 키와 값 및 TTL 설정
        String key = "testKeyWithTTL";
        String value = "valueWithTTL";
        long ttlInSeconds = 2;

        // When: 데이터를 저장하고 TTL 확인 후 조회 대기
        redisService.save(key, value, ttlInSeconds);
        
        Thread.sleep(3000); // 3초 대기

        Object retrievedValue = redisService.get(key);

        // Then: TTL이 만료되어 값이 null인지 확인
        assertNull(retrievedValue);
    }
}
