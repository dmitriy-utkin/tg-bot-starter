package io.github.dmitriyutkin.tgbotstarter.defaults.command;

import io.github.dmitriyutkin.tgbotstarter.anotation.*;
import io.github.dmitriyutkin.tgbotstarter.defaults.DefaultCommandName;
import io.github.dmitriyutkin.tgbotstarter.operation.CommandOperation;
import io.github.dmitriyutkin.tgbotstarter.util.MarkupType;
import io.github.dmitriyutkin.tgbotstarter.util.SenderService;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@CommandComponent
@RequiredArgsConstructor
@DefaultComponent
public class NowCommand implements CommandOperation {

    private final SenderService senderService;

    @Override
    public String getOperationIdentifier() {
        return DefaultCommandName.NOW_COMMAND.getCommandName();
    }

    @Override
    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.DEBUG)
    public void handle(String chatId, String input) {
        String currentTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        senderService.sendMessage(chatId, String.format("Current time: %s", currentTime), MarkupType.NONE);
    }
}
