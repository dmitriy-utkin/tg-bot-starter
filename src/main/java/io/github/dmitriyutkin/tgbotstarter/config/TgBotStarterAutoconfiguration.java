package io.github.dmitriyutkin.tgbotstarter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@Import({BotConfig.class, OperationRegistry.class, RedisStateManagerConfig.class, InMemoryStateManagerConfig.class})
public class TgBotStarterAutoconfiguration {
}
