package io.github.dmitriyutkin.tgbotstarter.defaults;

import lombok.Getter;

@Getter
public enum DefaultCallbackQueryName {

    DEFAULT_BUTTON_1_MAIN("BUTTON_1", "Button 1"),
    DEFAULT_BUTTON_1_STAGE_1("BUTTON_1_STAGE_1", null),
    DEFAULT_BUTTON_1_STAGE_2("BUTTON_1_STAGE_2", null),
    DEFAULT_BUTTON_1_STAGE_3("BUTTON_1_STAGE_3", null),
    DEFAULT_BUTTON_1_STAGE_4("BUTTON_1_STAGE_4", null);

    private final String stageName;

    private final String buttonText;

    DefaultCallbackQueryName(String stageName, String buttonText) {
        this.stageName = stageName;
        this.buttonText = buttonText;
    }
}
