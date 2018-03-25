package pip.project.relic.handlers.rest;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.exceptions.MessengerVerificationException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.events.AccountLinkingEvent;
import com.github.messenger4j.receive.handlers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Date;

import pip.project.relic.components.Command;
import pip.project.relic.components.CommandKey;
import pip.project.relic.components.User;
import pip.project.relic.utils.Parser;
import pip.project.relic.utils.Sender;
import pip.project.relic.handlers.system.AuthHandler;
import pip.project.relic.utils.SystemMapper;
import pip.project.relic.utils.TransactionManager;

@RestController
@RequestMapping("/callback")
public class CallBackHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallBackHandler.class);

    public static final String GOOD_ACTION = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION";
    public static final String NOT_GOOD_ACTION = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_ACTION";

    private final MessengerReceiveClient receiveClient;

    private final Sender sender;
    private final TransactionManager transactionManager;
    private final SystemMapper systemMapper;

    /**
     * Constructs the {@code CallBackHandler} and initializes the {@code MessengerReceiveClient}.
     *
     * @param appSecret   the {@code Application Secret}
     * @param verifyToken the {@code Verification Token} that has been provided by you during the setup of the {@code
     *                    Webhook}
     */
    @Autowired
    public CallBackHandler(@Value("${messenger4j.appSecret}") final String appSecret,
                           @Value("${messenger4j.verifyToken}") final String verifyToken,
                           final Sender sender,
                           final TransactionManager transactionManager,
                           final SystemMapper systemMapper) {

        logger.debug("Initializing MessengerReceiveClient - appSecret: {} | verifyToken: {}", appSecret, verifyToken);
        this.receiveClient = MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken)
            .onTextMessageEvent(newTextMessageEventHandler())
            .onQuickReplyMessageEvent(newQuickReplyMessageEventHandler())
            .onPostbackEvent(newPostbackEventHandler())
            .onAccountLinkingEvent(newAccountLinkingEventHandler())
            .onOptInEvent(newOptInEventHandler())
            .onEchoMessageEvent(newEchoMessageEventHandler())
            .onMessageDeliveredEvent(newMessageDeliveredEventHandler())
            .onMessageReadEvent(newMessageReadEventHandler())
            .fallbackEventHandler(newFallbackEventHandler())
            .build();
        this.sender = sender;
        this.transactionManager = transactionManager;
        this.systemMapper = systemMapper;
    }

    /**
     * Webhook verification endpoint.
     *
     * The passed verification token (as query parameter) must match the configured verification token.
     * In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebhook(@RequestParam("hub.mode") final String mode,
                                                @RequestParam("hub.verify_token") final String verifyToken,
                                                @RequestParam("hub.challenge") final String challenge) {

        logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode,
            verifyToken, challenge);
        try {
            return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode, verifyToken, challenge));
        } catch (MessengerVerificationException e) {
            logger.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final String payload,
                                               @RequestHeader("X-Hub-Signature") final String signature) {

        logger.debug("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
            this.receiveClient.processCallbackPayload(payload, signature);
            logger.debug("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessengerVerificationException e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private TextMessageEventHandler newTextMessageEventHandler() {
        return event -> {
            final String messageText = event.getText();
            final String senderId = event.getSender().getId();

            Command command = Parser.parseCommand(messageText);
            User user;

            try {
                user = transactionManager.getUser(senderId);
            } catch (InterruptedException e) {
                logger.error("user data retrieval was interrupted: " + e);
                return;
            }

            if (user == null) {
                if (command.getCommandKey() == CommandKey.NEWUSER) {
                    systemMapper.getHandler(CommandKey.NEWUSER).handleRequest(new User(senderId), command);
                } else {
                    sender.sendTextMessage(senderId, "You should create a new user by calling the \"new user:\" command!");
                }
                return;
            }

            if (transactionManager.lockExists(user)) {
                if (transactionManager.verifyLock(user, command.getCommandKey())) {
                    systemMapper.getHandler(command.getCommandKey()).handleResponse(user, command);
                } else {
                    transactionManager.sendLockResponse(user);
                }
                return;
            } else {
                transactionManager.setLock(user, command.getCommandKey());
            }

            try {
                switch (command.getCommandKey()) {
                    case NEWUSER:
                        systemMapper.getHandler(CommandKey.NEWUSER).handleRequest(user, command);
                        break;

                    case RESETUSER:
                        systemMapper.getHandler(CommandKey.RESETUSER).handleRequest(user, command);
                        break;

                    case MOOD:
                        systemMapper.getHandler(CommandKey.MOOD).handleRequest(user, command);
                        break;

                    case THOUGHT:
                        sender.sendTextMessage(senderId, "You're trying to send a thought message.");
                        break;

                    default:
                        sender.sendReadReceipt(senderId);
                        sender.sendTypingOn(senderId);
                        sender.sendTextMessage(senderId, "don't say that.");
                        sender.sendQuickReply(senderId);
                        sender.sendTypingOff(senderId);
                }
            } catch (MessengerApiException | MessengerIOException e) {
                handleSendException(e);
            }
        };
    }

    private QuickReplyMessageEventHandler newQuickReplyMessageEventHandler() {
        return event -> {
            logger.debug("Received QuickReplyMessageEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String messageId = event.getMid();
            final String quickReplyPayload = event.getQuickReply().getPayload();

            logger.info("Received quick reply for message '{}' with payload '{}'", messageId, quickReplyPayload);


            try {
                if(quickReplyPayload.equals(GOOD_ACTION))
                    sender.sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
                else
                    sender.sendGifMessage(senderId, "https://media.giphy.com/media/26ybx7nkZXtBkEYko/giphy.gif");
            } catch (MessengerApiException e) {
                handleSendException(e);
            } catch (MessengerIOException e) {
                handleIOException(e);
            }

            sender.sendTextMessage(senderId, "Let's try another one :D!");
        };
    }

    private PostbackEventHandler newPostbackEventHandler() {
        return event -> {
            logger.debug("Received PostbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String payload = event.getPayload();
            final Date timestamp = event.getTimestamp();

            logger.info("Received postback for user '{}' and page '{}' with payload '{}' at '{}'",
                senderId, recipientId, payload, timestamp);

            sender.sendTextMessage(senderId, "Postback called");
        };
    }

    private AccountLinkingEventHandler newAccountLinkingEventHandler() {
        return event -> {
            logger.debug("Received AccountLinkingEvent: {}", event);

            final String senderId = event.getSender().getId();
            final AccountLinkingEvent.AccountLinkingStatus accountLinkingStatus = event.getStatus();
            final String authorizationCode = event.getAuthorizationCode();

            logger.info("Received account linking event for user '{}' with status '{}' and auth code '{}'",
                senderId, accountLinkingStatus, authorizationCode);
        };
    }

    private OptInEventHandler newOptInEventHandler() {
        return event -> {
            logger.debug("Received OptInEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String passThroughParam = event.getRef();
            final Date timestamp = event.getTimestamp();

            logger.info("Received authentication for user '{}' and page '{}' with pass through param '{}' at '{}'",
                senderId, recipientId, passThroughParam, timestamp);

            sender.sendTextMessage(senderId, "Authentication successful");
        };
    }

    private EchoMessageEventHandler newEchoMessageEventHandler() {
        return event -> {
            logger.debug("Received EchoMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String recipientId = event.getRecipient().getId();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received echo for message '{}' that has been sent to recipient '{}' by sender '{}' at '{}'",
                messageId, recipientId, senderId, timestamp);
        };
    }

    private MessageDeliveredEventHandler newMessageDeliveredEventHandler() {
        return event -> {
            logger.debug("Received MessageDeliveredEvent: {}", event);

            final List<String> messageIds = event.getMids();
            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            if (messageIds != null) {
                messageIds.forEach(messageId -> {
                    logger.info("Received delivery confirmation for message '{}'", messageId);
                });
            }

            logger.info("All messages before '{}' were delivered to user '{}'", watermark, senderId);
        };
    }

    private MessageReadEventHandler newMessageReadEventHandler() {
        return event -> {
            logger.debug("Received MessageReadEvent: {}", event);

            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            logger.info("All messages before '{}' were read by user '{}'", watermark, senderId);
        };
    }

    /**
     * This handler is called when either the message is unsupported or when the event handler for the actual event type
     * is not registered. In this showcase all event handlers are registered. Hence only in case of an
     * unsupported message the fallback event handler is called.
     */
    private FallbackEventHandler newFallbackEventHandler() {
        return event -> {
            logger.debug("Received FallbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            logger.info("Received unsupported message from user '{}'", senderId);
        };
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }

    private void handleIOException(Exception e) {
        logger.error("Could not open Spring.io page. An unexpected error occurred.", e);
    }
}

