package command;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Command;

/**
 * This class helps to recognize the command given by the user from its string
 * format, and format it into the common Command object that could then be
 * process easily by the other components of the project.
 *
 * @author xuanyi
 *
 */
public class CommandParser {

    private static final String FIRST_WORD_PATTERN = "^([\\w]+)";
    private static final String INDIVIDUAL_PARAM_PATTERN = "%1$s|";
    private static final String COMMAND_PATTERN = "(%1$s)(.*?)(?=%2$s$)";
    private static final String COMPLETE_PATTERN = "(%1$s|%2$s)(.*?)(?=%3$s$)";
    private static final String ESCAPE_SEQUENCE = "\\";

    private static final String INVALID_COMMAND_MESSAGE = "%1$s is not a valid command";

    private static final Integer ENUM_TYPE = 1;
    private static final Integer ENUM_ARGUMENT = 2;

    private Hashtable<String, CommandEnum> commandEnumTable = new Hashtable<String, CommandEnum>();
    private Hashtable<String, ParamEnum> paramEnumTable = new Hashtable<String, ParamEnum>();

    public CommandParser() {
        initializeCommandTable();
        initializeParamTable();
    }

    /**
     * THe operation parse a given command string into a command object
     *
     * @param commandString
     * @return command object representing the given command string
     * @throws Exception
     */
    public Command parseCommand(String commandString) throws Exception {
        assert commandString != null;
        CommandEnum commandType = getCommandType(commandString);
        String commandTypeString = getFirstWord(commandString);

        Command userCommand = new Command(commandType);
        parseCommandStringToCommand(userCommand, commandString,
                commandTypeString);
        return userCommand;
    }

    /**
     * This operation parse the command string arguments to the command object
     *
     * @param userCommand
     * @param commandString
     */
    private void parseCommandStringToCommand(Command userCommand,
            String commandString, String commandTypeString) {
        CommandEnum commandType = userCommand.getCommand();

        String commandPatternString = makeCommandPatternString(commandType,
                commandTypeString);
        String patternString = makePatternString(commandType, commandTypeString);

        addParams(userCommand, commandString, commandPatternString,
                patternString);
        addCommandString(userCommand, commandString);
    }

    /**
     * This operation map all the params of command string to its enum type
     *
     * @param userCommand
     * @param commandString
     * @param patternString
     */
    private void addParams(Command userCommand, String commandString,
            String commandPatternString, String patternString) {

        // commandString without the initial command and its argument
        String commandSubString = null;

        Pattern commandPattern = Pattern.compile(commandPatternString,
                Pattern.CASE_INSENSITIVE);
        Matcher commandMatcher = commandPattern.matcher(commandString);

        if (commandMatcher.find()) {
            String argumentEscape = escapeKeyword(commandMatcher.group(
                    ENUM_ARGUMENT).trim());
            userCommand.addCommandArgument(argumentEscape);
            commandSubString = commandString.substring(commandMatcher.end());
        }

        Pattern pattern = Pattern.compile(patternString,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(commandSubString);

        ParamEnum paramEnum;
        while (matcher.find()) {
            if (paramEnumTable.containsKey(matcher.group(ENUM_TYPE))) {
                paramEnum = paramEnumTable.get(matcher.group(ENUM_TYPE));
                String escaped = escapeKeyword(matcher.group(ENUM_ARGUMENT)
                        .trim());
                ParamEnum groupName = paramEnumTable.get(paramEnum.groupName());
                userCommand.addParam(groupName, escaped);
            }
        }
    }

    /**
     * This operation add the user command string to the command object
     *
     * @param userCommand
     * @param commandString
     */
    private void addCommandString(Command userCommand, String commandString) {
        userCommand.addCommandString(commandString);
    }

    private String makeCommandPatternString(CommandEnum commandType,
            String commandTypeString) {
        String paramsStartPatternString = makeParamsPatternString(commandType);
        String commandPatternString = String.format(COMMAND_PATTERN,
                commandTypeString, paramsStartPatternString);

        return commandPatternString;
    }

    /**
     * This operation return a pattern string for the given command type
     *
     * @param commandType
     * @return
     */
    private String makePatternString(CommandEnum commandType,
            String commandTypeString) {
        assert commandType != null;
        String paramsPatternString = makeParamsPatternString(commandType);
        String completePattern = String.format(COMPLETE_PATTERN,
                commandTypeString, paramsPatternString, paramsPatternString);
        return completePattern;
    }

    /**
     * This operation returns a pattern string consist of all the params of the
     * command type
     *
     * @param commandType
     * @return
     */
    private String makeParamsPatternString(CommandEnum commandType) {
        assert commandType != null;
        ParamEnum[] params = commandType.params();
        String paramsPattern = "";
        
        for (ParamEnum param : params) {
            paramsPattern += String.format(INDIVIDUAL_PARAM_PATTERN,
                    param.regex());
        }

        return paramsPattern;
    }

    private CommandEnum getCommandType(String commandString) throws Exception {
        assert commandString != null;
        String firstWord = getFirstWord(commandString).toLowerCase();
        if (commandEnumTable.containsKey(firstWord)) {
            return commandEnumTable.get(firstWord);
        } else {
            throw new Exception(String.format(INVALID_COMMAND_MESSAGE,
                    firstWord));
        }
    }

    private String getFirstWord(String commandString) {
        Pattern pattern = Pattern.compile(FIRST_WORD_PATTERN);
        Matcher matcher = pattern.matcher(commandString);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private void initializeCommandTable() {
        for (CommandAlias commandAlias : CommandAlias.values()) {
            for (String regex : commandAlias.alias()) {
                commandEnumTable.put(regex, commandAlias.command());
            }
        }
    }

    private void initializeParamTable() {
        for (ParamEnum param : ParamEnum.values()) {
            paramEnumTable.put(param.regex().replace("\\", ""), param);
        }
    }

    /**
     * This operation escape keyword found in the param string
     *
     * @param paramString
     * @return
     */
    private String escapeKeyword(String paramString) {
        String resultString = paramString.replace(ESCAPE_SEQUENCE, "");

        return resultString;
    }
}
