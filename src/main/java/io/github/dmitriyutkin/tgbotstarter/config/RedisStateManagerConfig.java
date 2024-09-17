package io.github.dmitriyutkin.tgbotstarter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "telegram.bot.state.redis.enabled", havingValue = "true")
public class RedisStateManagerConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("Creating Redis connection factory, redis will be used as a Default State Manager Repository");
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}
