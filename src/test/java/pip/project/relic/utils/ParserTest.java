package pip.project.relic.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import pip.project.relic.components.Command;
import pip.project.relic.components.CommandKey;

public class ParserTest {

    private final String dummyMessage = "This is a dummy message.";

    @Test
    public void testInvalidMessage() {
        String messageText1 = " : : :";
        String messageText2 = "hello: hello: hello: hello:";
        String messageText3 = "hello:";

        int count = 0;

        try {
            Parser.parseCommand(messageText1);
        } catch (IllegalArgumentException e) {
            count += 1;
        }
        try {
            Parser.parseCommand(messageText2);
        } catch (IllegalArgumentException e) {
            count += 1;
        }
        try {
            Parser.parseCommand(messageText2);
        } catch (IllegalArgumentException e) {
            count += 1;
        }
        try {
            Parser.parseCommand(dummyMessage);
        } catch (IllegalArgumentException e) {
            count += 1;
        }
        assertEquals(3, count);
    }
    @Test
    public void testNoCommand() {
        String messageText = ":" + dummyMessage;

        Command command1 = Parser.parseCommand(dummyMessage);
        Command command2 = Parser.parseCommand(messageText);

        assertEquals(CommandKey.DEFAULT, command1.getCommandKey());
        assertEquals(dummyMessage, command1.getValue());
        assertEquals(CommandKey.DEFAULT, command2.getCommandKey());
        assertEquals(messageText, command2.getValue());
    }
    @Test
    public void testInvalidCommand() {
        String messageText1 = "hello:";
        String messageText2 = "hello: " + dummyMessage;

        Command command1 = Parser.parseCommand(messageText1);
        Command command2 = Parser.parseCommand(messageText2);

        assertEquals(CommandKey.DEFAULT, command1.getCommandKey());
        assertEquals(messageText1, command1.getValue());
        assertEquals(CommandKey.DEFAULT, command2.getCommandKey());
        assertEquals(messageText2, command2.getValue());
    }

    @Test
    public void testValidCommands() {
        String messageText1 = "new user: " + dummyMessage;
        String messageText2 = "reset user: " + dummyMessage;
        String messageText3 = "mood: " + dummyMessage;
        String messageText4 = "thought: " + dummyMessage;

        Command command1 = Parser.parseCommand(messageText1);
        Command command2 = Parser.parseCommand(messageText2);
        Command command3 = Parser.parseCommand(messageText3);
        Command command4 = Parser.parseCommand(messageText4);

        assertEquals(CommandKey.NEWUSER, command1.getCommandKey());
        assertEquals(CommandKey.RESETUSER, command2.getCommandKey());
        assertEquals(CommandKey.MOOD, command3.getCommandKey());
        assertEquals(CommandKey.THOUGHT, command4.getCommandKey());

        assertEquals(dummyMessage, command1.getValue());
        assertEquals(dummyMessage, command2.getValue());
        assertEquals(dummyMessage, command3.getValue());
        assertEquals(dummyMessage, command4.getValue());
    }
}