package com.basestudy.rewards.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {
    private RedisServer redisServer;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @PostConstruct
    public void startRedis() throws IOException {
        if (isArmMac()) {
            // 1. 리소스 경로에서 바이너리 파일 추출
            ClassPathResource resource = new ClassPathResource("binary/redis/redis-server-mac-arm64");
            File redisExec = resource.getFile();
            // 2. 실행 권한 보장
            redisExec.setExecutable(true);

            // 3. 바이너리 파일을 지정하여 RedisServer 생성
            redisServer = new RedisServer(redisExec, redisPort);
        } else {
            redisServer = new RedisServer(redisPort);
        }
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    private boolean isArmMac() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch");
        return os.contains("mac") && arch.contains("aarch64");
    }
}