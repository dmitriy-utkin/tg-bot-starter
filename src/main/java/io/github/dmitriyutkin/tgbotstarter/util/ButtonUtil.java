package io.github.dmitriyutkin.tgbotstarter.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@UtilityClass
public class ButtonUtil {
    public static InlineKeyboardButton create(String text, String callBackData) {
        return new InlineKeyboardButton(text, null, callBackData, null, null, null, null, null, null);
    }
}
