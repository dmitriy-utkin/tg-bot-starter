package io.github.dmitriyutkin.tgbotstarter.defaults;

public enum DefaultCommandName {
    ECHO_COMMAND("/echo"),
    NOW_COMMAND("/now"),
    START_COMMAND("/start");

    DefaultCommandName(String commandName) {
        this.commandName = commandName;
    }

    private final String commandName;

    public String getCommandName() {
        return commandName;
    }
}
