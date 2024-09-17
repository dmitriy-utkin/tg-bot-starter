package io.github.dmitriyutkin.tgbotstarter.config;

import io.github.dmitriyutkin.tgbotstarter.anotation.*;
import io.github.dmitriyutkin.tgbotstarter.operation.ButtonProvider;
import io.github.dmitriyutkin.tgbotstarter.operation.CallbackQueryOperation;
import io.github.dmitriyutkin.tgbotstarter.operation.CommandOperation;
import io.github.dmitriyutkin.tgbotstarter.operation.MessageOperation;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Configuration
public class OperationRegistry {

    private final ApplicationContext applicationContext;
    private final Map<String, ButtonProvider> buttonProviders;
    private final Map<String, CommandOperation> commandOperations;
    private final Map<String, MessageOperation> messageOperations;
    private final Map<String, CallbackQueryOperation> callbackQueryOperations;

    @Getter
    private ButtonProvider mainMenuButtonProvider;

    @Value("${telegram.component-package}")
    private String packageName;

    @Value("${telegram.default-comps.create:true}")
    private Boolean withDefaultComps;

    @Value("${telegram.default-comps.exclude-commands:[]}")
    private List<String> excludeCommands;

    public OperationRegistry(ApplicationContext applicationContext) {
        buttonProviders = new HashMap<>();
        commandOperations = new HashMap<>();
        messageOperations = new HashMap<>();
        callbackQueryOperations = new HashMap<>();
        this.applicationContext = applicationContext;
        mainMenuButtonProvider = null;
    }

    @LoggableAspect(type = LoggableType.BUTTON_OP, level = LoggableLevelType.DEBUG)
    public ButtonProvider getButtonProvider(String name) {
        return buttonProviders.get(name);
    }

    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.DEBUG)
    public CommandOperation getCommandOperation(String name) {
        return commandOperations.get(name);
    }

    @LoggableAspect(type = LoggableType.MESSAGE_OP, level = LoggableLevelType.DEBUG)
    public MessageOperation getMessageOperation(String name) {
        return messageOperations.get(name);
    }

    @LoggableAspect(type = LoggableType.CALLBACK_QUERY_OP, level = LoggableLevelType.DEBUG)
    public CallbackQueryOperation getCallbackQueryOperation(String name) {
        return callbackQueryOperations.get(name);
    }

    @PostConstruct
    public void init() {
        registryAllAnnotatedClasses();
    }

    @LogPerformanceSamplerAspect
    private void registryAllAnnotatedClasses() {
        log.info("WithDefaultComps parameter is {}", withDefaultComps);

        Reflections reflections = new Reflections(packageName);

        List<Class<?>> mainMenuAnnotatedClasses = new ArrayList<>(reflections.getTypesAnnotatedWith(MainMenuButtons.class));
        if (mainMenuAnnotatedClasses.size() > 1) {
            throw new Error("Application cannot contain more than one MainMenuButtons");
        } else if (mainMenuAnnotatedClasses.size() == 1) {
            mainMenuButtonProvider = getMainMenuButtonProvider(mainMenuAnnotatedClasses.get(0));
        }
        reflections.getTypesAnnotatedWith(ButtonComponent.class).forEach(this::registerButtonProvider);
        reflections.getTypesAnnotatedWith(CommandComponent.class).forEach(this::registerCommandOperation);
        reflections.getTypesAnnotatedWith(MessageComponent.class).forEach(this::registerMessageOperation);
        reflections.getTypesAnnotatedWith(CallbackComponent.class).forEach(this::registerCallbackQueryOperation);

        log.info("""
                         \nAll operations have been registered:
                         \t-> ButtonComponent: {} realisations;
                         \t-> CommandComponent: {} realisations;
                         \t-> MessageComponent: {} realisations;
                         \t-> CallbackComponent: {} realisations;
                         \t-> MainMenuButtonProvider registration result: {}
                         """,
                 buttonProviders.size(),
                 commandOperations.size(),
                 messageOperations.size(),
                 callbackQueryOperations.size(),
                 Objects.nonNull(mainMenuButtonProvider) ? "REGISTERED" : "NOT DEFINED");
    }

    @LoggableAspect(type = LoggableType.BUTTON_OP, level = LoggableLevelType.DEBUG)
    private ButtonProvider getMainMenuButtonProvider(Class<?> clazz) {
        log.debug("Try to register main menu button provider for {}", clazz.getSimpleName());
        return validateByDefaultProperty(clazz) ? (ButtonProvider) applicationContext.getBean(clazz) : null;
    }

    @LoggableAspect(type = LoggableType.BUTTON_OP, level = LoggableLevelType.DEBUG)
    private void registerButtonProvider(Class<?> clazz) {
        if (validateByDefaultProperty(clazz)) {
            log.debug("Try to register button provider for {}", clazz.getSimpleName());
            ButtonProvider provider = (ButtonProvider) applicationContext.getBean(clazz);
            buttonProviders.put(provider.getOperationIdentifier(), provider);
        }
    }

    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.DEBUG)
    private void registerCommandOperation(Class<?> clazz) {
        if (validateByDefaultProperty(clazz) || !CollectionUtils.isEmpty(excludeCommands)) {
            log.debug("Try to register command operation for {}", clazz.getSimpleName());
            CommandOperation operation = (CommandOperation) applicationContext.getBean(clazz);
            boolean isExcludeCommand = CollectionUtils.isEmpty(excludeCommands) || excludeCommands.contains(operation.getOperationIdentifier());
            if (isExcludeCommand) {
                commandOperations.put(operation.getOperationIdentifier(), operation);
            }
        }
    }

    @LoggableAspect(type = LoggableType.MESSAGE_OP, level = LoggableLevelType.DEBUG)
    private void registerMessageOperation(Class<?> clazz) {
        if (validateByDefaultProperty(clazz)) {
            log.debug("Try to register message operation for {}", clazz.getSimpleName());
            MessageOperation operation = (MessageOperation) applicationContext.getBean(clazz);
            messageOperations.put(operation.getOperationIdentifier(), operation);
        }
    }

    @LoggableAspect(type = LoggableType.CALLBACK_QUERY_OP, level = LoggableLevelType.DEBUG)
    private void registerCallbackQueryOperation(Class<?> clazz) {
        if (validateByDefaultProperty(clazz)) {
            log.debug("Try to register callback query operation for {}", clazz.getSimpleName());
            CallbackQueryOperation operation = (CallbackQueryOperation) applicationContext.getBean(clazz);
            callbackQueryOperations.put(operation.getOperationIdentifier(), operation);
        }
    }

    private boolean validateByDefaultProperty(Class<?> clazz) {
        if (withDefaultComps) {
            return true;
        }
        return !clazz.isAnnotationPresent(DefaultComponent.class);
    }
}
