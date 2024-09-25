package io.github.dmitriyutkin.tgbotstarter.operation;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageOperation extends Operation {
    void handle(Update update);
}
