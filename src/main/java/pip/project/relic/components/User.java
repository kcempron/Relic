package pip.project.relic.components;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String userId;

    private Map<Instant, String> mood;

    private CommandKey commandLock;

    public User(String userId) {
        this.userId = userId;
        this.mood = new HashMap<>();
        this.commandLock = CommandKey.DEFAULT;
    }

    public User(String userId, Map<Instant, String> mood, CommandKey commandKey) {
        this.userId = userId;
        this.mood = mood;
        this.commandLock = commandKey;
    }

    public User() {
        this.userId = null;
        this.mood = null;
        this.commandLock = null;
    }

    public void setValues(User user) {
        this.userId = user.getUserId();
        this.mood = user.getMood();
        this.commandLock = user.getCommandLock();
    }

    public String getUserId() {
        return userId;
    }

    public Map<Instant, String> getMood() {
        return mood;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMood(Map<Instant, String> mood) {
        this.mood = mood;
    }

    public CommandKey getCommandLock() {
        return commandLock;
    }

    public void setCommandLock(CommandKey commandLock) {
        this.commandLock = commandLock;
    }

    @Override
    public String toString() {
        return "User{" +
            "userId='" + userId + '\'' +
            ", mood=" + mood +
            ", commandLock=" + commandLock +
            '}';
    }
}
