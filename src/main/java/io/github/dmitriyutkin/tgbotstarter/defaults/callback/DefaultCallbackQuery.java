package io.github.dmitriyutkin.tgbotstarter.defaults.callback;

import io.github.dmitriyutkin.tgbotstarter.anotation.*;
import io.github.dmitriyutkin.tgbotstarter.defaults.DefaultCallbackQueryName;
import io.github.dmitriyutkin.tgbotstarter.operation.CallbackQueryOperation;
import io.github.dmitriyutkin.tgbotstarter.state.model.State;
import io.github.dmitriyutkin.tgbotstarter.state.service.DefaultStateManager;
import io.github.dmitriyutkin.tgbotstarter.util.MarkupType;
import io.github.dmitriyutkin.tgbotstarter.util.SenderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Slf4j
@DefaultComponent
@CallbackComponent
@RequiredArgsConstructor
public class DefaultCallbackQuery implements CallbackQueryOperation {

    private final DefaultStateManager stateManager;
    private final SenderService senderService;

    @Override
    @LoggableAspect(type = LoggableType.CALLBACK_QUERY_OP, level = LoggableLevelType.DEBUG)
    public void handle(String chatId, String input) {
        State state = stateManager.getByChatId(chatId);
        String currentStage = state.getStateStageInfo().getOrDefault(2, null);
        Map<String, Object> stateProprs = state.getStates();
        if (Objects.isNull(currentStage)) {
            state.getStateStageInfo().put(2, DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_1.getStageName());
            stateManager.updateByChatId(chatId, state);
            senderService.sendMessage(chatId, "Отправьте: Свойство 1", MarkupType.MARKDOWN);
        } else if (Objects.equals(DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_1.getStageName(), currentStage)) {
            state.getStates().put(Dto.Fields.s1, input);
            state.getStateStageInfo().put(2, DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_2.getStageName());
            stateManager.updateByChatId(chatId, state);
            senderService.sendMessage(chatId, "Отправьте: Свойство  2", MarkupType.MARKDOWN);
        } else if (Objects.equals(DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_2.getStageName(), currentStage)) {
            stateProprs.put(Dto.Fields.s2, input);
            state.getStateStageInfo().put(2, DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_3.getStageName());
            stateManager.updateByChatId(chatId, state);
            senderService.sendMessage(chatId, "Отправьте: Свойство  3", MarkupType.MARKDOWN);
        } else if (Objects.equals(DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_3.getStageName(), currentStage)) {
            stateProprs.put(Dto.Fields.s3, input);
            state.getStateStageInfo().put(2, DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_4.getStageName());
            stateManager.updateByChatId(chatId, state);
            senderService.sendMessage(chatId, "Отправьте: Свойство 4", MarkupType.MARKDOWN);
        } else if (Objects.equals(DefaultCallbackQueryName.DEFAULT_BUTTON_1_STAGE_4.getStageName(), currentStage)) {
            stateProprs.put(Dto.Fields.s4, input);
            senderService.sendMessage(chatId, "Больше нечего отправлять, вот что получилось:", MarkupType.NONE);
            senderService.sendMessage(chatId,
                                      String.valueOf(new Dto(
                                              (String) stateProprs.get(Dto.Fields.s1),
                                              (String) stateProprs.get(Dto.Fields.s2),
                                              (String) stateProprs.get(Dto.Fields.s3),
                                              (String) stateProprs.get(Dto.Fields.s4))),
                                      MarkupType.NONE);
            stateManager.removeByChatId(chatId);
        }
    }

    @Override
    public String getOperationIdentifier() {
        return DefaultCallbackQueryName.DEFAULT_BUTTON_1_MAIN.getStageName();
    }

    @FieldNameConstants
    private record Dto(String s1, String s2, String s3, String s4) {
    }
}
