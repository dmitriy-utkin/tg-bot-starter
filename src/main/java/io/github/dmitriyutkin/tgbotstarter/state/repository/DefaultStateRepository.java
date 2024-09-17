package io.github.dmitriyutkin.tgbotstarter.state.repository;

import io.github.dmitriyutkin.tgbotstarter.state.model.State;

public interface DefaultStateRepository {
    State getByChatId(String chatId);

    void save(State state);

    void updateByChatId(String chatId, State state);

    void removeByChatId(String chatId);
}
