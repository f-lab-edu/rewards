package com.basestudy.rewards.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
    topics = "test-topic",
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:0",
        "port=0"
    }
)
public class EmbeddedKafkaTest {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<Long, Long> kafkaTemplate;

    @Test
    void testKafkaConnection() {
        String brokers = embeddedKafkaBroker.getBrokersAsString();
        System.out.println("브로커 주소: " + brokers); // 예: localhost:63543
        kafkaTemplate.send("test-topic", 234L);
        // 메시지 전송 성공 시 연결 정상
    }
}
