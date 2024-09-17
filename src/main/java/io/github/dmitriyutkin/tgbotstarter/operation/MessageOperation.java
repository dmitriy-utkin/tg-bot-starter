package io.github.dmitriyutkin.tgbotstarter.operation;

public interface MessageOperation extends Operation {
    void handle(String chatId, String input);
}
