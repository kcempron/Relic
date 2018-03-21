package pip.project.relic.components;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class User {

    public String userId;

    public Map<Instant, String> mood;

    public User(String userId) {
        this.userId = userId;
        this.mood = new HashMap<>();
    }

    public User(String userId, Map<Instant, String> mood) {
        this.userId = userId;
        this.mood = mood;
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
}
