package io.github.dmitriyutkin.tgbotstarter.config;

import io.github.dmitriyutkin.tgbotstarter.anotation.LogPerformanceSamplerAspect;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableAspect;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableType;
import io.github.dmitriyutkin.tgbotstarter.defaults.DefaultMessageName;
import io.github.dmitriyutkin.tgbotstarter.operation.CallbackQueryOperation;
import io.github.dmitriyutkin.tgbotstarter.operation.CommandOperation;
import io.github.dmitriyutkin.tgbotstarter.operation.MessageOperation;
import io.github.dmitriyutkin.tgbotstarter.state.model.State;
import io.github.dmitriyutkin.tgbotstarter.state.service.DefaultStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(OperationRegistry.class)
public class BotConfig extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUserName;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final OperationRegistry registry;

    private final DefaultStateManager defaultStateManager;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error("Error registering bot: ", e);
        }
        return botsApi;
    }

    @Override
    @LoggableAspect(type = LoggableType.OTHER)
    @LogPerformanceSamplerAspect
    public void onUpdateReceived(Update update) {
        String chatId = update.hasCallbackQuery() ? String.valueOf(update.getCallbackQuery().getMessage().getChatId()) : String.valueOf(update.getMessage().getChatId());
        if (update.hasCallbackQuery() && defaultStateManager.existsByChatIdAndStageNum(chatId, 1)) {
            if (!Objects.equals(defaultStateManager.getByChatId(chatId).getStateStageInfo().get(1), update.getCallbackQuery().getData())) {
                defaultStateManager.removeByChatId(chatId);
                log.info("Removed default state for chatId: {} due to mismatching of the 1st stage name", chatId);
            }
        }
        if (update.hasCallbackQuery() || defaultStateManager.existsByChatIdAndStageNum(chatId, 1)) {
            State currentState = defaultStateManager.getByChatId(chatId);
            String callBackOperationName;
            String callBackData = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : update.getMessage().getText();
            if (Objects.isNull(currentState)) {
                callBackOperationName = callBackData;
                defaultStateManager.save(State.builder().chatId(chatId).stateStageInfo(new HashMap<>(Map.of(1, callBackOperationName))).build());
            } else {
                callBackOperationName = currentState.getStateStageInfo().get(1);
            }
            CallbackQueryOperation callbackQueryOperation = registry.getCallbackQueryOperation(callBackOperationName);
            if (Objects.nonNull(callbackQueryOperation)) {
                callbackQueryOperation.handle(chatId, callBackData);
            } else {
                log.warn("CallbackQuery '{}' is not found", callBackOperationName);
                sendUnrecognizedMessage(chatId, callBackData);
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String input = update.getMessage().getText();
            if (input.startsWith("/")) {
                int inputLength = input.split(" ").length;
                String command = inputLength == 1 ? input : input.substring(0, input.indexOf(" "));
                CommandOperation commandOperation = registry.getCommandOperation(command);
                if (Objects.nonNull(commandOperation)) {
                    String inputMessage = inputLength == 1 ? "" : input.substring(input.indexOf(" ") + 1);
                    commandOperation.handle(chatId, inputMessage);
                } else {
                    log.warn("Command operation '{}' is not found", input);
                    sendUnrecognizedMessage(chatId, input);
                }
            } else {
                MessageOperation messageOperation = registry.getMessageOperation(input);

                if (Objects.nonNull(messageOperation)) {
                    messageOperation.handle(chatId, input);
                } else {
                    log.warn("Message operation '{}' is not handled", input);
                    sendUnrecognizedMessage(chatId, input);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void sendUnrecognizedMessage(String chatId, String input) {
        registry.getMessageOperation(DefaultMessageName.UNRECOGNIZED_INPUT.name()).handle(chatId, input);
    }
}
