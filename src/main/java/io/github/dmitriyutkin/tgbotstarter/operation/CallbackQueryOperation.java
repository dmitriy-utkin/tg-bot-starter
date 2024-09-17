package io.github.dmitriyutkin.tgbotstarter.operation;

public interface CallbackQueryOperation extends Operation {
    void handle(String chatId, String input);
}
