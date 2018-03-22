package pip.project.relic.components;

public class Command {

    private CommandKey commandKey;
    private String value;

    public Command(CommandKey commandKey, String value) {
        this.commandKey = commandKey;
        this.value = value;
    }

    public CommandKey getCommandKey() {
        return commandKey;
    }

    public String getValue() {
        return value;
    }
}
