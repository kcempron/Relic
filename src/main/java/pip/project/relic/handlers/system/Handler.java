package pip.project.relic.handlers.system;

import com.google.firebase.database.FirebaseDatabase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pip.project.relic.components.Command;
import pip.project.relic.utils.Sender;

@Component
public abstract class Handler {

    private final Sender sender;
    private final FirebaseDatabase database;

    @Autowired
    public Handler(Sender sender,
                   FirebaseDatabase database) {
        this.sender = sender;
        this.database = database;
    }

    public abstract void handleRequest(String userId, Command command);

    public abstract void handleResponse(String userId, Command command);

    public Sender getSender() {
        return sender;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }
}
