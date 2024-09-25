package io.github.dmitriyutkin.tgbotstarter.operation;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandOperation extends Operation {
    void handle(Update update);
    String getDescription();

    Integer getIndex();
}
