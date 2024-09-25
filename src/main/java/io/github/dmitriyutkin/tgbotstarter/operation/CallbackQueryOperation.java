package io.github.dmitriyutkin.tgbotstarter.operation;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackQueryOperation extends Operation {
    void handle(Update update);
}
