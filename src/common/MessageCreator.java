package common;

public class MessageCreator {

    public MessageCreator() {
        // TODO Auto-generated constructor stub
    }

    public static String createMessage(String message, String variableText1,
            String variableText2) {
        return String.format(message, variableText1, variableText2);
    }

}
