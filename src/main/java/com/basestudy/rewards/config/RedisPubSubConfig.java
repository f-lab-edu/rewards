package com.basestudy.rewards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.basestudy.rewards.infra.CouponExhaustedSubscriber;
import com.basestudy.rewards.service.CouponService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {
        
    private static final String COUPON_EXHAUSTED_CHANNEL = "coupon-exhausted-channel";
    private final CouponService couponService;
    @Bean
    public MessageListenerAdapter couponExhaustedListenerAdapter() {
        return new MessageListenerAdapter(new CouponExhaustedSubscriber(couponService));
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter couponExhaustedListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(couponExhaustedListenerAdapter, new ChannelTopic(COUPON_EXHAUSTED_CHANNEL));
        return container;
    }
}
