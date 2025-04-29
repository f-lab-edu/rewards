package com.basestudy.rewards.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ActiveProfiles("test")
public class EmbeddedRedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void embeddedRedis_정상동작_테스트() {
        System.out.println("Embedded Redis Test");
        redisTemplate.opsForValue().set("testKey", "testValue");
        String value = redisTemplate.opsForValue().get("testKey");
        assertEquals("testValue", value);
    }
}