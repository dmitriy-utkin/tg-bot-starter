package io.github.dmitriyutkin.tgbotstarter.operation;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface ButtonProvider extends Operation {
    List<InlineKeyboardButton> getButtons();
}
