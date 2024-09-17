package io.github.dmitriyutkin.tgbotstarter.defaults.buttons;

import io.github.dmitriyutkin.tgbotstarter.anotation.*;
import io.github.dmitriyutkin.tgbotstarter.operation.ButtonProvider;
import io.github.dmitriyutkin.tgbotstarter.util.ButtonUtil;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static io.github.dmitriyutkin.tgbotstarter.defaults.DefaultCallbackQueryName.DEFAULT_BUTTON_1_MAIN;

@MainMenuButtons
@ButtonComponent
@DefaultComponent
public class DefaultMainMenuButtonProvider implements ButtonProvider {

    @Override
    @LoggableAspect(type = LoggableType.BUTTON_OP, level = LoggableLevelType.DEBUG)
    public List<InlineKeyboardButton> getButtons() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(ButtonUtil.create(DEFAULT_BUTTON_1_MAIN.getButtonText(), DEFAULT_BUTTON_1_MAIN.getStageName()));
        return buttons;
    }

    @Override
    public String getOperationIdentifier() {
        return DEFAULT_BUTTON_1_MAIN.getButtonText();
    }
}