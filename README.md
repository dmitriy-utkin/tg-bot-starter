# FRAMEWORK: tg-bot-starter

## Interfaces

## OperationRegistry logic

## Default components (can be used as an example)
These components will be registered in package 'io.github.dmitriyutkin.tgbotstarter' by default

## Aspect logic

## Properties
Common:
- `spring.main.allow-circular-references` - boolean, should be initialized as 'true' (bug in current version)

Component registry props:
- `telegram.component-package` - string, should be replaced by your package with components (by default will be used as null and no any custom comps will be detected)
- `telegram.default-comps.create` - boolean, flag of creation the default components (refer doc about default components)
- `telegram.default-comps.exclude-commands` - list string (example: "/default_now,/default_echo"), mark the default commands what will be registred

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
    exclude-commands: 

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
