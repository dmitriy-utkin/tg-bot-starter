package io.github.dmitriyutkin.tgbotstarter.operation;

public interface CommandOperation extends Operation {
    void handle(String chatId, String input);
}
