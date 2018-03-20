package pip.project.relic;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String userId;

    private Map<Instant, String> mood;

    public User(String userID) {
        this.userId = userId;
        this.mood = new HashMap<>();
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
