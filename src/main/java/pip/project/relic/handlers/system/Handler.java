package pip.project.relic.handlers.system;

import com.google.firebase.database.FirebaseDatabase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pip.project.relic.components.Command;
import pip.project.relic.components.User;
import pip.project.relic.utils.Sender;
import pip.project.relic.utils.TransactionManager;

@Component
public abstract class Handler {

    private final Sender sender;
    private final FirebaseDatabase database;
    private final TransactionManager transactionManager;

    @Autowired
    public Handler(Sender sender,
                   FirebaseDatabase database,
                   TransactionManager transactionManager) {
        this.sender = sender;
        this.database = database;
        this.transactionManager = transactionManager;
    }

    public abstract void handleRequest(User user, Command command);

    public abstract void handleResponse(User user, Command command);
}
