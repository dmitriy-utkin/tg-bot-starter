package io.github.dmitriyutkin.tgbotstarter.state.service;

import io.github.dmitriyutkin.tgbotstarter.state.model.State;

public interface DefaultStateManager {
    State getByChatId(String chatId);
    void save(State state);
    void updateByChatId(String chatId, State state);
    void removeByChatId(String chatId);
    boolean isExists(String chatId, Integer stageNum);
}
