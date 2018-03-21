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

import pip.project.relic.utils.Sender;

@Component
public class MoodHandler {

    private static final Logger logger = LoggerFactory.getLogger(MoodHandler.class);


    private final Sender sender;
    private final FirebaseDatabase database;

    @Autowired
    public MoodHandler(Sender sender,
                       FirebaseDatabase database) {
        this.sender = sender;
        this.database = database;
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
}
