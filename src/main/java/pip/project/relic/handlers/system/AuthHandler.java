package pip.project.relic.handlers.system;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pip.project.relic.components.Command;
import pip.project.relic.utils.Sender;
import pip.project.relic.components.User;
import pip.project.relic.utils.TransactionManager;

@Component
public class AuthHandler extends Handler{

    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    private final Sender sender;
    private final FirebaseDatabase database;
    private final TransactionManager transactionManager;

    @Autowired
    public AuthHandler(Sender sender,
                       FirebaseDatabase database,
                       TransactionManager transactionManager) {
        super(sender, database, transactionManager);
        this.sender = sender;
        this.database = database;
        this.transactionManager = transactionManager;
    }

    private void verifyNewUser(String userId) {
        database.getReference("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    sender.sendTextMessage(userId, "You've already registered as a user!");
                } else {
                    database.getReference("users").child(userId).setValueAsync(new User(userId));
                    sender.sendTextMessage(userId, "Welcome new user!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        });
    }

    @Override
    public void handleRequest(User user, Command command) {
        String userId = user.getUserId();
        switch(command.getCommandKey()) {
            case NEWUSER:
                sender.sendTextMessage(userId, "You're registering as a new user");
                verifyNewUser(user.getUserId());
                transactionManager.removeLock(user);
                break;

            case RESETUSER:
                sender.sendTextMessage(userId, "You're trying to reset your user account.");
                break;

            default:
                logger.debug("User got to AuthHandler Request with: {}, {}", userId, command.getCommandKey());
                break;
        }
    }


    @Override
    public void handleResponse(User user, Command command) {
        sender.sendTextMessage(user.getUserId(), "Thanks for authenticating.");
        String userId = user.getUserId();
        switch(command.getCommandKey()) {
            case NEWUSER:
                sender.sendTextMessage(user.getUserId(), "Thanks for authenticating.");
                break;

            case RESETUSER:
                sender.sendTextMessage(user.getUserId(), "Thanks for authenticating.");
                break;

            default:
                logger.debug("User got to AuthHandler Response with: {}, {}", userId, command.getCommandKey());
                break;
        }
    }
}
