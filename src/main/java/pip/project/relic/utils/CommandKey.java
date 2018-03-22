package pip.project.relic.utils;

import java.util.HashMap;
import java.util.Map;

public enum CommandKey {
    NEWUSER("new user"),
    RESETUSER("reset user"),
    MOOD("mood"),
    THOUGHT("thought"),
    DEFAULT("default");

    private String command;

    CommandKey(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    //****** Reverse Lookup Implementation************//

    //Lookup table
    private static final Map<String, CommandKey> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(CommandKey env : CommandKey.values())
        {
            lookup.put(env.getCommand(), env);
        }
    }

    //This method can be used for reverse lookup purpose
    public static CommandKey get(String command)
    {
        return lookup.get(command);
    }
}
