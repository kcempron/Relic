package pip.project.relic;

import java.io.FileInputStream;
import java.io.IOException;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * Initializes the {@code MessengerSendClient}.
     *
     * @param pageAccessToken the generated {@code Page Access Token}
     */
    @Bean
    public MessengerSendClient messengerSendClient(@Value("${messenger4j.pageAccessToken}") String pageAccessToken) {
        logger.debug("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
        return MessengerPlatform.newSendClientBuilder(pageAccessToken).build();
    }

    /**
     * Initialize Firebase database.
     *
     * @param serviceAccountPath Path to Service Account credentials.
     * @param databaseName Name of Firebase database to connect to.
     * @return Firebase Instance
     * @throws IOException
     */
    @Bean
    public FirebaseDatabase firebaseConnection(@Value("${firebase.serviceAccountPath}") String serviceAccountPath,
                                               @Value("${firebase.databaseName}") String databaseName) throws IOException{
        FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(databaseName)
            .build();

        FirebaseApp.initializeApp(options);

        return FirebaseDatabase.getInstance();
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
