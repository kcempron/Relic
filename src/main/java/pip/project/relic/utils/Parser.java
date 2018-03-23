package pip.project.relic.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import pip.project.relic.components.Command;
import pip.project.relic.components.CommandKey;

public final class Parser {

    private Parser() {
        //Utility Class
    }

    public static Command parseCommand(String messageText) {
        if (messageText.contains(":")) {
            List<String> values = Arrays.stream(messageText.split(":")).map(String::trim).collect(Collectors.toList());

           if (validateCommand(values)) {
                return new Command(CommandKey.get(values.get(0).toLowerCase(Locale.US)), values.get(1));
           }
        }
        return new Command(CommandKey.DEFAULT, messageText);
    }

    private static boolean validateCommand(List<String> values) {
        return values.size() == 2 && CommandKey.get(values.get(0).toLowerCase(Locale.US)) != null;
    }
}
