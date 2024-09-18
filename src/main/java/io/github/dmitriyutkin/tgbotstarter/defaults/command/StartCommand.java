package io.github.dmitriyutkin.tgbotstarter.defaults.command;

import io.github.dmitriyutkin.tgbotstarter.anotation.*;
import io.github.dmitriyutkin.tgbotstarter.config.OperationRegistry;
import io.github.dmitriyutkin.tgbotstarter.defaults.DefaultCommandName;
import io.github.dmitriyutkin.tgbotstarter.operation.ButtonProvider;
import io.github.dmitriyutkin.tgbotstarter.operation.CommandOperation;
import io.github.dmitriyutkin.tgbotstarter.util.SenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@DefaultComponent
@CommandComponent
@RequiredArgsConstructor
public class StartCommand implements CommandOperation {

    private final SenderService senderService;
    private final OperationRegistry operationRegistry;

    @Value("${telegram.bot.greetings:null}")
    private String greetings;

    @Override
    public String getOperationIdentifier() {
        return DefaultCommandName.DEFAULT_START_COMMAND.getCommandName();
    }

    @Override
    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.DEBUG)
    public void handle(String chatId, String input) {
        if (Objects.isNull(greetings)) {
            greetings = "Hello world!";
        }
        ButtonProvider mainMenuButtonProvider = operationRegistry.getMainMenuButtonProvider();
        List<InlineKeyboardButton> buttons = Objects.nonNull(mainMenuButtonProvider) ? mainMenuButtonProvider.getButtons() : Collections.emptyList();
        senderService.sendButtons(chatId, greetings, buttons);
    }
}
