package pip.project.relic.handlers.system;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pip.project.relic.components.Command;
import pip.project.relic.components.User;
import pip.project.relic.utils.Sender;
import pip.project.relic.utils.TransactionManager;

@Component
public class MoodHandler extends Handler{

    private static final Logger logger = LoggerFactory.getLogger(MoodHandler.class);


    private final Sender sender;
    private final FirebaseDatabase database;
    private final TransactionManager transactionManager;

    @Autowired
    public MoodHandler(Sender sender,
                       FirebaseDatabase database,
                       TransactionManager transactionManager) {
        super(sender, database, transactionManager);
        this.sender = sender;
        this.database = database;
        this.transactionManager = transactionManager;
    }


    @Scheduled(cron="0 0 7,11,14,17,20 * * *")
    private void sendMoodMessage() {
        database.getReference("users").orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                sender.sendTextMessage(dataSnapshot.getKey(), "How are you feeling today?");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void handleRequest(User user, Command command) {
        String userId = user.getUserId();
        switch(command.getCommandKey()) {
            case MOOD:
                sender.sendTextMessage(user.getUserId(), "You're trying to send a mood message.");
                break;

            default:
                logger.debug("User got to MoodHandler Request with: {}, {}", userId, command.getCommandKey());
                break;
        }
    }

    @Override
    public void handleResponse(User user, Command command) {
        String userId = user.getUserId();
        switch(command.getCommandKey()) {
            case MOOD:
                sender.sendTextMessage(user.getUserId(), "Thanks for telling me how you're feeling.");
                transactionManager.removeLock(user);
                break;

            default:
                logger.debug("User got to MoodHandler Response with: {}, {}", userId, command.getCommandKey());
                break;
        }
    }
}
