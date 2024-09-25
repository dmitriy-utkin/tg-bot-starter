package io.github.dmitriyutkin.tgbotstarter.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Objects;

@UtilityClass
public class SenderUtil {
    public static InlineKeyboardButton create(String text, String callbackData) {
        return new InlineKeyboardButton(text, null, callbackData, null, null, null, null, null, null);
    }

    public static String getChatId(Update update) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        return chatId.toString();
    }

    public static String getSenderName(Update update, boolean withLastName) {
        User from = update.hasMessage() ? update.getMessage().getFrom() : update.getCallbackQuery().getFrom();
        String userFirstName = from.getFirstName();
        String userLastName = withLastName ? from.getLastName() : "";
        return String.format("%s %s", userFirstName, userLastName).trim();
    }

    public static String getUsername(Update update) {
        return update.hasMessage() ? update.getMessage().getFrom().getUserName() : update.getCallbackQuery().getFrom().getUserName();
    }

    public static String getInputWithCommandErasing(Update update) {
        String input = update.getMessage().getText();
        if (input.startsWith("/") && input.split(" ").length > 1) {
            input = input.substring(input.indexOf(""));
        }
        return input;
    }
}
