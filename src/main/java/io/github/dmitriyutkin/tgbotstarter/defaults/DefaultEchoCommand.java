package io.github.dmitriyutkin.tgbotstarter.defaults;

import io.github.dmitriyutkin.tgbotstarter.anotation.comp.CommandComponent;
import io.github.dmitriyutkin.tgbotstarter.anotation.comp.DefaultComponent;
import io.github.dmitriyutkin.tgbotstarter.aop.LoggableAspect;
import io.github.dmitriyutkin.tgbotstarter.aop.props.LoggableLevelType;
import io.github.dmitriyutkin.tgbotstarter.aop.props.LoggableType;
import io.github.dmitriyutkin.tgbotstarter.operation.CommandOperation;
import io.github.dmitriyutkin.tgbotstarter.util.SenderService;
import io.github.dmitriyutkin.tgbotstarter.util.SenderUtil;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@CommandComponent
@RequiredArgsConstructor
@DefaultComponent
public class DefaultEchoCommand implements CommandOperation {

    private final SenderService senderService;

    @Override
    public String getOperationIdentifier() {
        return "/default_echo";
    }

    @Override
    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.DEBUG)
    public void handle(Update update) {
        String preparedInput = SenderUtil.getInputWithCommandErasing(update);
        senderService.sendMessage(SenderUtil.getChatId(update), String.format("You said: %s", preparedInput));
    }

    @Override
    public String getDescription() {
        return "This command will return your input, so simple logic :)";
    }

    @Override
    public Integer getIndex() {
        return -1;
    }
}

