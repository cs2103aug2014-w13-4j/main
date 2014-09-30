package command;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser implements ICommandParser {
	
	private static final String FIRST_WORD_PATTERN = "^([\\w]+)";
	private static final String INDIVIDUAL_PARAM_PATTERN = "%1$s|";
	private static final String COMPLETE_PATTERN = "(%1$s|%2$s)(.*?)(?=%2$s$)";
	
	private static final Integer ENUM_TYPE = 1;
	private static final Integer ENUM_ARGUMENT = 2;
	
	private Hashtable<String, CommandEnum> commandEnumTable = new Hashtable<String, CommandEnum>();
	private Hashtable<String, ParamEnum> paramEnumTable = new Hashtable<String, ParamEnum>();
	
	public CommandParser() {
		initializeCommandTable();
		initializeParamTable();
	}

	@Override
	public Command parseCommand(String commandString) {
		CommandEnum commandType = getCommandType(commandString);
		
		Command userCommand = new Command(commandType);
		parseCommandStringToCommand(userCommand, commandString);
		return userCommand;
	}

	/**
	 * This operation parse the command string arguments to the command object
	 * @param userCommand
	 * @param commandString
	 */
	private void parseCommandStringToCommand(Command userCommand, String commandString) {
		CommandEnum commandType = userCommand.getCommand();
		String patternString = makePatternString(commandType);
		
		addParams(userCommand, commandString, patternString);
		addCommandString(userCommand, commandString);
	}

	/**
	 * This operation map all the params of command string to its enum type
	 * @param userCommand
	 * @param commandString
	 * @param patternString
	 */
	private void addParams(Command userCommand, String commandString,
			String patternString) {
		
		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(commandString);
		
		// Add command argument
		if (matcher.find()) {
			userCommand.addCommandArgument(matcher.group(ENUM_ARGUMENT).trim());
		}
		
		while (matcher.find()) {
			if (paramEnumTable.containsKey(matcher.group(ENUM_TYPE))) {
				userCommand.addParam(paramEnumTable.get(matcher.group(ENUM_TYPE)), matcher.group(ENUM_ARGUMENT).trim());
			}
		}
	}
	
	/**
	 * This operation add the user command string to the command object
	 * @param userCommand
	 * @param commandString
	 */
	private void addCommandString(Command userCommand, String commandString) {
		userCommand.addCommandString(commandString);
	}

	/**
	 * This operation return a pattern string for the given command type
	 * @param commandType
	 * @return
	 */
	private String makePatternString(CommandEnum commandType) {
		String paramsPatternString = makeParamsPatternString(commandType);
		String completePattern = String.format(COMPLETE_PATTERN, commandType.regex(), paramsPatternString);
		return completePattern;
	}

	/**
	 * This operation returns a pattern string consist of all the params of the command type
	 * @param commandType
	 * @return
	 */
	private String makeParamsPatternString(CommandEnum commandType) {
		ParamEnum[] params = commandType.params();
		String paramsPattern = "";
		
		for (ParamEnum param : params) {
			paramsPattern += String.format(INDIVIDUAL_PARAM_PATTERN, param.regex());
		}
		
		return paramsPattern;
	}

	private CommandEnum getCommandType(String commandString) {
		String firstWord = getFirstWord(commandString).toLowerCase();
		if (commandEnumTable.containsKey(firstWord)) {
			return commandEnumTable.get(firstWord);
		} else {
			throw null;
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
		for (CommandEnum command : CommandEnum.values()) {
			commandEnumTable.put(command.regex(), command);
		}
	}
	
	private void initializeParamTable() {
		for (ParamEnum param : ParamEnum.values()) {
			paramEnumTable.put(param.regex().replace("\\", ""), param);
		}
	}
	
	// Test
	public static void main(String[] args) {
		CommandParser cp = new CommandParser();
		
		Command userCommand = cp.parseCommand("Add CS2103T from tuesday to wednesday note MVP +tag +2nd");
		System.out.println(userCommand.getCommandArgument());
		CommandEnum commandType = userCommand.getCommand();
		System.out.println(commandType);
		Hashtable<ParamEnum, ArrayList<String>> params = userCommand.getParam();
		System.out.println(params);
		String commandString = userCommand.getCommandString();
		System.out.println(commandString);
	}

}
