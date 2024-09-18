package io.github.dmitriyutkin.tgbotstarter.defaults.command;

import io.github.dmitriyutkin.tgbotstarter.anotation.*;
import io.github.dmitriyutkin.tgbotstarter.defaults.DefaultCommandName;
import io.github.dmitriyutkin.tgbotstarter.operation.CommandOperation;
import io.github.dmitriyutkin.tgbotstarter.util.MarkupType;
import io.github.dmitriyutkin.tgbotstarter.util.SenderService;
import lombok.RequiredArgsConstructor;

@CommandComponent
@RequiredArgsConstructor
@DefaultComponent
public class EchoCommand implements CommandOperation {

    private final SenderService senderService;

    @Override
    public String getOperationIdentifier() {
        return DefaultCommandName.DEFAULT_ECHO_COMMAND.getCommandName();
    }

    @Override
    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.DEBUG)
    public void handle(String chatId, String input) {
        senderService.sendMessage(chatId, String.format("You said: %s", input), MarkupType.NONE);
    }
}

