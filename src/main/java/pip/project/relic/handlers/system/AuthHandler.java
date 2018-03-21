package pip.project.relic.handlers.system;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pip.project.relic.utils.Sender;
import pip.project.relic.components.User;

@Component
public class AuthHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    private final Sender sender;
    private final FirebaseDatabase database;

    @Autowired
    public AuthHandler(Sender sender, FirebaseDatabase database) {
        this.sender = sender;
        this.database = database;
    }

    public void verifyNewUser(String userId) {
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
}
