package pip.project.relic.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

public final class Parser {

    private Parser() {
        //Utility Class
    }
    public static Command parseCommand(String messageText) {
        List<String> values = Arrays.stream(messageText.split(":")).map(String::trim).collect(Collectors.toList());

        Preconditions.checkArgument(values.size() <= 2, "message contains multiple commands.");
        if (CommandKey.get(values.get(0).toLowerCase(Locale.US)) != null) {
            return new Command(CommandKey.get(values.get(0).toLowerCase(Locale.US)), values.get(1));
        } else {
            return new Command(CommandKey.DEFAULT, values.get(0));
        }
    }
}
