package io.github.dmitriyutkin.tgbotstarter.state.repository.impl;

import io.github.dmitriyutkin.tgbotstarter.exception.StateException;
import io.github.dmitriyutkin.tgbotstarter.state.model.State;
import io.github.dmitriyutkin.tgbotstarter.state.repository.DefaultStateRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDefaultStateRepositoryImpl implements DefaultStateRepository {
    private final Map<String, State> states;

    public InMemoryDefaultStateRepositoryImpl() {
        states = new ConcurrentHashMap<>();
    }

    @Override
    public State getByChatId(String chatId) {
        return states.getOrDefault(chatId, null);
    }

    @Override
    public void save(State state) {
        if (states.containsKey(state.getChatId())) {
            throw new StateException("Cannot save a state that already exists");
        } else {
            states.put(state.getChatId(), state);
        }
    }

    @Override
    public void updateByChatId(String chatId, State state) {
        if (!states.containsKey(chatId)) {
            throw new StateException("State by this chatId does not exist");
        } else {
            states.put(chatId, state);
        }
    }

    @Override
    public void removeByChatId(String chatId) {
        if (!states.containsKey(chatId)) {
            throw new StateException("State by this chatId does not exist");
        } else {
            states.remove(chatId);
        }
    }
}
