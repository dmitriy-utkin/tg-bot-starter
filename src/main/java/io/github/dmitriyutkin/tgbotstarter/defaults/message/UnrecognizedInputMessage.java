package io.github.dmitriyutkin.tgbotstarter.defaults.message;

import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableAspect;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableLevelType;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableType;
import io.github.dmitriyutkin.tgbotstarter.anotation.MessageComponent;
import io.github.dmitriyutkin.tgbotstarter.defaults.DefaultMessageName;
import io.github.dmitriyutkin.tgbotstarter.operation.MessageOperation;
import io.github.dmitriyutkin.tgbotstarter.util.MarkupType;
import io.github.dmitriyutkin.tgbotstarter.util.SenderService;
import lombok.RequiredArgsConstructor;

@MessageComponent
@RequiredArgsConstructor
public class UnrecognizedInputMessage implements MessageOperation {

    private final SenderService senderService;

    @Override
    public String getOperationIdentifier() {
        return DefaultMessageName.UNRECOGNIZED_INPUT.name();
    }

    @Override
    @LoggableAspect(type = LoggableType.MESSAGE_OP, level = LoggableLevelType.DEBUG)
    public void handle(String chatId, String input) {
        senderService.sendMessage(chatId, String.format("Input \"%s\" cannot be processed", input), MarkupType.NONE);
    }
}

