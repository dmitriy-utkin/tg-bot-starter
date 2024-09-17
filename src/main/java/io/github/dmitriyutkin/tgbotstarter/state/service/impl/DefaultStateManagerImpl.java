package io.github.dmitriyutkin.tgbotstarter.state.service.impl;

import io.github.dmitriyutkin.tgbotstarter.anotation.LogPerformanceSamplerAspect;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableAspect;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableLevelType;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableType;
import io.github.dmitriyutkin.tgbotstarter.state.model.State;
import io.github.dmitriyutkin.tgbotstarter.state.repository.DefaultStateRepository;
import io.github.dmitriyutkin.tgbotstarter.state.service.DefaultStateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class DefaultStateManagerImpl implements DefaultStateManager {

    private DefaultStateRepository stateRepository;

    @Autowired
    public void setStateRepository(DefaultStateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    @LogPerformanceSamplerAspect
    @LoggableAspect(type = LoggableType.OTHER, level = LoggableLevelType.DEBUG)
    public State getByChatId(String chatId) {
        return stateRepository.getByChatId(chatId);
    }

    @Override
    @LogPerformanceSamplerAspect
    @LoggableAspect(type = LoggableType.OTHER, level = LoggableLevelType.DEBUG)
    public void save(State state) {
        stateRepository.save(state);
    }

    @Override
    @LogPerformanceSamplerAspect
    @LoggableAspect(type = LoggableType.OTHER, level = LoggableLevelType.DEBUG)
    public void updateByChatId(String chatId, State state) {
        stateRepository.updateByChatId(chatId, state);
    }

    @Override
    @LogPerformanceSamplerAspect
    @LoggableAspect(type = LoggableType.OTHER, level = LoggableLevelType.DEBUG)
    public void removeByChatId(String chatId) {
        stateRepository.removeByChatId(chatId);
    }

    @Override
    @LogPerformanceSamplerAspect
    @LoggableAspect(type = LoggableType.OTHER, level = LoggableLevelType.DEBUG)
    public boolean existsByChatIdAndStageNum(String chatId, Integer stageNum) {
        State state = getByChatId(chatId);
        return Objects.nonNull(state) && state.getStateStageInfo().containsKey(stageNum);
    }
}

