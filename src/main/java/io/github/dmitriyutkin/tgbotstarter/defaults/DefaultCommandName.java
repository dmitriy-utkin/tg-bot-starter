package io.github.dmitriyutkin.tgbotstarter.defaults;

public enum DefaultCommandName {
    DEFAULT_ECHO_COMMAND("/default_echo"),
    DEFAULT_NOW_COMMAND("/default_now"),
    DEFAULT_START_COMMAND("/default_start");

    DefaultCommandName(String commandName) {
        this.commandName = commandName;
    }

    private final String commandName;

    public String getCommandName() {
        return commandName;
    }
}
