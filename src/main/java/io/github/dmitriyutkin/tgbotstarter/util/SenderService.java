package io.github.dmitriyutkin.tgbotstarter.util;

import io.github.dmitriyutkin.tgbotstarter.config.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class SenderService {

    private final ApplicationContext applicationContext;

    public void sendMessage(String chatId, String message) {
        sendMessage(chatId, message, MarkupType.NONE);
    }

    public void sendMessage(String chatId, String message, MarkupType type) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        if (Objects.nonNull(type.getParseModeName())) {
            sendMessage.setParseMode(type.getParseModeName());
        }
        try {
            executeMessage(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendButtons(String chatId, String message, List<InlineKeyboardButton> buttons, MarkupType messageType) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(buttons));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(markup);
        if (Objects.nonNull(messageType.getParseModeName())) {
            sendMessage.setParseMode(messageType.getParseModeName());
        }
        try {
            executeMessage(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendButtons(String chatId, String message, List<InlineKeyboardButton> buttons) {
        sendButtons(chatId, message, buttons, MarkupType.NONE);
    }

    private void executeMessage(SendMessage sendMessage) throws TelegramApiException {
        applicationContext.getBean(BotConfig.class).execute(sendMessage);
    }
}

