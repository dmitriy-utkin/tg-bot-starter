package io.github.dmitriyutkin.tgbotstarter.state.repository.impl;

import io.github.dmitriyutkin.tgbotstarter.state.model.State;
import io.github.dmitriyutkin.tgbotstarter.state.repository.DefaultStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class RedisDefaultStateRepositoryImpl implements DefaultStateRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${telegram.bot.state.redis.ttl-in-min:5}")
    private Long stateTtl;

    @Override
    public State getByChatId(String chatId) {
        return (State) redisTemplate.opsForValue().get(chatId);
    }

    @Override
    public void save(State state) {
        saveWithExpireTime(state.getChatId(), state);
    }

    @Override
    public void updateByChatId(String chatId, State state) {
        saveWithExpireTime(chatId, state);
    }

    @Override
    public void removeByChatId(String chatId) {
        redisTemplate.delete(chatId);
    }

    private void saveWithExpireTime(String chatId, State state) {
        redisTemplate.opsForValue().set(chatId, state);
        redisTemplate.expire(chatId, stateTtl, TimeUnit.MINUTES);
    }
}
