package io.github.dmitriyutkin.tgbotstarter.config;

import io.github.dmitriyutkin.tgbotstarter.anotation.comp.*;
import io.github.dmitriyutkin.tgbotstarter.anotation.core.AnyMessageHandler;
import io.github.dmitriyutkin.tgbotstarter.anotation.core.MainMenuButtons;
import io.github.dmitriyutkin.tgbotstarter.anotation.core.UnprocessableMessageHandler;
import io.github.dmitriyutkin.tgbotstarter.aop.LogPerformanceSamplerAspect;
import io.github.dmitriyutkin.tgbotstarter.aop.LoggableAspect;
import io.github.dmitriyutkin.tgbotstarter.aop.props.LoggableLevelType;
import io.github.dmitriyutkin.tgbotstarter.aop.props.LoggableType;
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

    @Getter
    private MessageOperation unprocessableMessageHandler;

    @Getter
    private MessageOperation anyMessagesHandler;

    private final String DEFAULT_PACKAGE_NAME = "io.github.dmitriyutkin.tgbotstarter";

    @Value("${telegram.component-package:null}")
    private String componentPackage;

    @Value("${telegram.default-comps.create:true}")
    private Boolean withDefaultComps;


    public OperationRegistry(ApplicationContext applicationContext) {
        buttonProviders = new HashMap<>();
        commandOperations = new HashMap<>();
        messageOperations = new HashMap<>();
        callbackQueryOperations = new HashMap<>();
        this.applicationContext = applicationContext;
        mainMenuButtonProvider = null;
    }

    @LoggableAspect(type = LoggableType.BUTTON_OP, level = LoggableLevelType.TRACE)
    public ButtonProvider getButtonProvider(String name) {
        return buttonProviders.get(name);
    }

    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.TRACE)
    public CommandOperation getCommandOperation(String name) {
        return commandOperations.get(name);
    }

    @LoggableAspect(type = LoggableType.MESSAGE_OP, level = LoggableLevelType.TRACE)
    public MessageOperation getMessageOperation(String name) {
        return messageOperations.get(name);
    }

    @LoggableAspect(type = LoggableType.CALLBACK_QUERY_OP, level = LoggableLevelType.TRACE)
    public CallbackQueryOperation getCallbackQueryOperation(String name) {
        return callbackQueryOperations.get(name);
    }

    public List<ButtonProvider> getAllButtonProviders() {
        return new ArrayList<>(buttonProviders.values());
    }

    public List<CommandOperation> getAllCommandOperations() {
        return new ArrayList<>(commandOperations.values());
    }

    public List<MessageOperation> getAllMessageOperations() {
        return new ArrayList<>(messageOperations.values());
    }

    public List<CallbackQueryOperation> getAllCallbackOperations() {
        return new ArrayList<>(callbackQueryOperations.values());
    }

    public boolean isUnprocessableMessageHandlerExists() {
        return Objects.nonNull(unprocessableMessageHandler);
    }

    public boolean isAnyMessagesHandlerExists() {
        return Objects.nonNull(anyMessagesHandler);
    }

    @PostConstruct
    public void init() {
        log.info("WithDefaultComps parameter is {}", withDefaultComps);
        registryAllAnnotatedClasses(DEFAULT_PACKAGE_NAME);
        registryAllAnnotatedClasses(componentPackage);
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

    @LogPerformanceSamplerAspect
    private void registryAllAnnotatedClasses(String packageName) {
        if (Objects.isNull(packageName)) {
            log.info("Package name is null, initialization is skipped");
            return;
        }

        Reflections reflections = new Reflections(packageName);

        setMainMenuButtonProvider(reflections);
        setUnprocessableMessageHandler(reflections);
        setAnyMessageHandler(reflections);
        reflections.getTypesAnnotatedWith(ButtonComponent.class).forEach(this::registerButtonProvider);
        reflections.getTypesAnnotatedWith(CommandComponent.class).forEach(this::registerCommandOperation);
        reflections.getTypesAnnotatedWith(MessageComponent.class).forEach(this::registerMessageOperation);
        reflections.getTypesAnnotatedWith(CallbackComponent.class).forEach(this::registerCallbackQueryOperation);
    }

    @LoggableAspect(type = LoggableType.BUTTON_OP, level = LoggableLevelType.DEBUG)
    private void setMainMenuButtonProvider(Reflections reflections) {
        List<Class<?>> mainMenuAnnotatedClasses = new ArrayList<>(reflections.getTypesAnnotatedWith(MainMenuButtons.class));
        if (mainMenuAnnotatedClasses.size() > 1) {
            throw new Error("Application cannot contain more than one MainMenuButtons");
        } else if (mainMenuAnnotatedClasses.size() == 1) {
            Class<?> clazz = mainMenuAnnotatedClasses.get(0);
            log.debug("Try to register main menu button provider for {}", clazz.getSimpleName());
            mainMenuButtonProvider = validateByDefaultProperty(clazz) ? (ButtonProvider) applicationContext.getBean(clazz) : null;
        }
    }

    @LoggableAspect(type = LoggableType.MESSAGE_OP, level = LoggableLevelType.DEBUG)
    private void setUnprocessableMessageHandler(Reflections reflections) {
        List<Class<?>> unprocessableMessageHandlerClasses = new ArrayList<>(reflections.getTypesAnnotatedWith(UnprocessableMessageHandler.class));
        if (unprocessableMessageHandlerClasses.size() > 1) {
            throw new Error("Application cannot contain more than one UnprocessableMessageHandler");
        } else if (unprocessableMessageHandlerClasses.size() == 1) {
            Class<?> clazz = unprocessableMessageHandlerClasses.get(0);
            log.debug("Try to register unprocessable message handler for {}", clazz.getSimpleName());
            unprocessableMessageHandler = validateByDefaultProperty(clazz) ? (MessageOperation) applicationContext.getBean(clazz) : null;
        }
    }

    @LoggableAspect(type = LoggableType.MESSAGE_OP, level = LoggableLevelType.DEBUG)
    private void setAnyMessageHandler(Reflections reflections) {
        List<Class<?>> unprocessableMessageHandlerClasses = new ArrayList<>(reflections.getTypesAnnotatedWith(AnyMessageHandler.class));
        if (unprocessableMessageHandlerClasses.size() > 1) {
            throw new Error("Application cannot contain more than one AnyMessageHandler");
        } else if (unprocessableMessageHandlerClasses.size() == 1) {
            Class<?> clazz = unprocessableMessageHandlerClasses.get(0);
            log.debug("Try to register any message handler for {}", clazz.getSimpleName());
            anyMessagesHandler = validateByDefaultProperty(clazz) ? (MessageOperation) applicationContext.getBean(clazz) : null;
        }
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
        if (validateByDefaultProperty(clazz)) {
            log.debug("Try to register command operation for {}", clazz.getSimpleName());
            CommandOperation operation = (CommandOperation) applicationContext.getBean(clazz);
            commandOperations.put(operation.getOperationIdentifier(), operation);
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
