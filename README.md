# FRAMEWORK: tg-bot-starter

## Interfaces

## OperationRegistry logic

### Core operation:
1) @AnyMessageHandler [AnyMessageHandler.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcore%2FAnyMessageHandler.java)
2) @MainMenuButtons [MainMenuButtons.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcore%2FMainMenuButtons.java)
3) @UnprocessableMessageHandler [UnprocessableMessageHandler.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcore%2FUnprocessableMessageHandler.java)

### Components:
1) @ButtonComponent [ButtonComponent.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcomp%2FButtonComponent.java)
2) @CallbackComponent [CallbackComponent.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcomp%2FCallbackComponent.java)
3) @CommandComponent [CommandComponent.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcomp%2FCommandComponent.java)
4) @DefaultComponent [DefaultComponent.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcomp%2FDefaultComponent.java)
5) @MessageComponent [MessageComponent.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2Fcomp%2FMessageComponent.java)

## Default components (can be used as an example, also see 'Examples & Recommendations')
These components will be registered in package 'io.github.dmitriyutkin.tgbotstarter' by default

## Aspect logic
### @AddAdditionalButtonAspect ([AddAdditionalButtonAspect.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2FAddAdditionalButtonAspect.java))

### @LoggableAspect ([LoggableAspect.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2FLoggableAspect.java))

### @LogPerformanceSamplerAspect ([LogPerformanceSamplerAspect.java](src%2Fmain%2Fjava%2Fio%2Fgithub%2Fdmitriyutkin%2Ftgbotstarter%2Fanotation%2FLogPerformanceSamplerAspect.java))

## Properties
Common:
- `spring.main.allow-circular-references` - boolean, should be initialized as 'true' (bug in current version)

Component registry props:
- `telegram.component-package` - string, should be replaced by your package with components (by default will be used as null and no any custom comps will be detected)
- `telegram.default-comps.create` - boolean, flag of creation the default components (refer doc about default components)

Bot props:
- `telegram.bot.token` - required, your bot token
- `telegram.bot.username` - required, your bot username (without '@')
- `telegram.bot.greetings` - string, greeting message
- `telegram.bot.state.redis.enabled` - boolean, settings for usage of redis as the repository state provider (if you use this option, please put the redis configuration to your application yaml)
- `telegram.bot.state.redis.ttl-in-min` - int, ttl time, by default = 5 (in minutes)

### Default props view:

```yaml
spring:
  main:
    allow-circular-references: true

telegram:
  component-package: ""
  default-comps:
    create: true
  bot:
    token: "TG_BOT_TOKEN"
    username: "TG_BOT_USERNAME"
    greetings: "Welcome to telegram bot framework, enjoy!"
    state:
      redis:
        enabled: false
        ttl-in-min: ${REDIS_STATE_TTL:5}
```

## How to use it?
Just add this library to your project, fill the required props and create a config class:

```java
@Configuration
@ComponentScan(basePackages = {"io.github.dmitriyutkin.tgbotstarter"})
public class BotLibraryConfig {}
```
## Examples & Recommendations

### Example: Message handler for unprocessable messages
```java
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
    public void handle(Update update) {
        senderService.sendMessage(String.valueOf(update.getMessage().getChatId()),
                                  String.format("Input \"%s\" cannot be processed", update.getMessage().getText()));
    }
}

public enum DefaultMessageName {
    UNRECOGNIZED_INPUT
}
```


### Example: command '/start'
```java
@CommandComponent
@RequiredArgsConstructor
public class DefaultStartCommand implements CommandOperation {

    private final SenderService senderService;
    private final OperationRegistry operationRegistry;

    @Value("${telegram.bot.greetings:Hello!}")
    private String greetings;

    @Override
    public String getOperationIdentifier() {
        return DefaultCommandName.DEFAULT_START_COMMAND.getCommandName();
    }

    @Override
    @LoggableAspect(type = LoggableType.COMMAND_OP, level = LoggableLevelType.DEBUG)
    public void handle(Update update) {
        ButtonProvider mainMenuButtonProvider = operationRegistry.getMainMenuButtonProvider();
        List<List<InlineKeyboardButton>> buttons = Objects.nonNull(mainMenuButtonProvider) ? mainMenuButtonProvider.getButtons() : Collections.emptyList();
        senderService.sendButtons(SenderUtil.getChatId(update), greetings, buttons);
    }
}
```

### Example: MainMenuButtonProvider
```java
@MainMenuButtons
@ButtonComponent
public class DefaultMainMenuButtonProvider implements ButtonProvider {

    @Override
    @LoggableAspect(type = LoggableType.BUTTON_OP, level = LoggableLevelType.DEBUG)
    public List<List<InlineKeyboardButton>> getButtons() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(SenderUtil.create(DEFAULT_BUTTON_1_MAIN.getButtonText(), DEFAULT_BUTTON_1_MAIN.getStageName()));
        buttons.add(row1);
        return buttons;
    }

    @Override
    public String getOperationIdentifier() {
        return DEFAULT_BUTTON_1_MAIN.getButtonText();
    }
}
```

### Example: CallbackQueryProvider
```java
@Getter
public enum DefaultCallbackQueryName {

    DEFAULT_BUTTON_1_MAIN("BUTTON_1", "Button 1"),
    DEFAULT_BUTTON_1_STAGE_1("BUTTON_1_STAGE_1", null),
    DEFAULT_BUTTON_1_STAGE_2("BUTTON_1_STAGE_2", null),
    DEFAULT_BUTTON_1_STAGE_3("BUTTON_1_STAGE_3", null),
    DEFAULT_BUTTON_1_STAGE_4("BUTTON_1_STAGE_4", null);

    private final String stageName;

    private final String buttonText;

    DefaultCallbackQueryName(String stageName, String buttonText) {
        this.stageName = stageName;
        this.buttonText = buttonText;
    }
}

@CallbackComponent
@RequiredArgsConstructor
public class DefaultCallbackQuery implements CallbackQueryOperation {

    private final DefaultStateManager stateManager;
    private final SenderService senderService;

    @Override
    @LoggableAspect(type = LoggableType.CALLBACK_QUERY_OP, level = LoggableLevelType.DEBUG)
    public void handle(Update update) {
        String chatId = SenderUtil.getChatId(update);
        String input = update.getMessage().getText();
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
```