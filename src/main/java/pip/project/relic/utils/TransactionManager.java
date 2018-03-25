package pip.project.relic.utils;

import java.util.concurrent.Semaphore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pip.project.relic.components.CommandKey;
import pip.project.relic.components.User;

@Component
public class TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);


    private final Sender sender;
    private final FirebaseDatabase database;

    public TransactionManager(Sender sender,
                              FirebaseDatabase database) {
        this.sender = sender;
        this.database = database;
    }

    public boolean lockExists(User user) {
        return user.getCommandLock() != null;
    }

    public boolean verifyLock(User user, CommandKey commandKey) {
        return user.getCommandLock() == commandKey;
    }

    public void setLock(User user, CommandKey commandKey) {
        if (commandKey != CommandKey.DEFAULT) {
            database.getReference("users")
                .child(user.getUserId())
                .child("commandLock")
                .setValueAsync(commandKey);
        }
    }

    public void removeLock(User user) {
        database.getReference("users")
            .child(user.getUserId())
            .child("commandLock")
            .setValueAsync(null);
    }

    public void sendLockResponse(User user) {
        sender.sendTextMessage(user.getUserId(), "You're currently engaged in a " + user.getCommandLock() + " related conversion. Please complete it or break out using \"break:\".");
    }

    public User getUser(String senderId) throws InterruptedException {
        final User[] user = {null};

        Semaphore semaphore = new Semaphore(0);
        database.getReference("users").child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user[0] = dataSnapshot.getValue(User.class);
                }
                semaphore.release();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        });

        semaphore.acquire();
        return user[0];
    }
}
