package io.github.dmitriyutkin.tgbotstarter.config;

import io.github.dmitriyutkin.tgbotstarter.state.repository.DefaultStateRepository;
import io.github.dmitriyutkin.tgbotstarter.state.repository.impl.InMemoryDefaultStateRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "telegram.bot.state.redis.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryStateManagerConfig {

    @Bean
    public DefaultStateRepository defaultStateRepository() {
        log.info("Creating In Memory Storage, that will be used as a Default State Manager Repository");
        return new InMemoryDefaultStateRepositoryImpl();
    }
}
