package common;

//@author: A0114368E

/**
 * Creates the message given the variable text.
 */
public class MessageCreator {

    public static String createMessage(String message, String variableText1,
            String variableText2) {
        return String.format(message, variableText1, variableText2);
    }

}
