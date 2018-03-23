package pip.project.relic.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pip.project.relic.components.CommandKey;
import pip.project.relic.handlers.system.AuthHandler;
import pip.project.relic.handlers.system.Handler;
import pip.project.relic.handlers.system.MoodHandler;

@Component
public class SystemMapper {

    private final Map<CommandKey, Handler> handlerMap;

    @Autowired
    private SystemMapper(MoodHandler moodHandler,
                         AuthHandler authHandler) {
        handlerMap = new HashMap<>();
        handlerMap.put(CommandKey.MOOD, moodHandler);
        handlerMap.put(CommandKey.NEWUSER, authHandler);
        handlerMap.put(CommandKey.RESETUSER, authHandler);
    }

    public Map<CommandKey, Handler> getHandlerMap() {
        return handlerMap;
    }

    public Handler getHandler(CommandKey command) {
        return handlerMap.get(command);
    }
}
