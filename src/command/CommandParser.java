package command;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Command;

public class CommandParser {

	private static final String FIRST_WORD_PATTERN = "^([\\w]+)";
	private static final String INDIVIDUAL_PARAM_PATTERN = "%1$s|";
	private static final String COMMAND_PATTERN = "(%1$s)(.*?)(?=%2$s$)";
	private static final String COMPLETE_PATTERN = "(%1$s|%2$s)(.*?)(?=%3$s$)";

	private static final String INVALID_COMMAND_MESSAGE = "%1$s is not a valid command";

	private static final Integer ENUM_TYPE = 1;
	private static final Integer ENUM_ARGUMENT = 2;

	private Hashtable<String, CommandEnum> commandEnumTable = new Hashtable<String, CommandEnum>();
	private Hashtable<String, ParamEnum> paramEnumTable = new Hashtable<String, ParamEnum>();

	public CommandParser() {
		initializeCommandTable();
		initializeParamTable();
	}

	public Command parseCommand(String commandString) throws Exception {
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
		
		String commandPatternString = makeCommandPatternString(commandType);
		String patternString = makePatternString(commandType);

		addParams(userCommand, commandString, commandPatternString, patternString);
		addCommandString(userCommand, commandString);
	}

	/**
	 * This operation map all the params of command string to its enum type
	 * @param userCommand
	 * @param commandString
	 * @param patternString
	 */
	private void addParams(Command userCommand, String commandString, String commandPatternString,
						   String patternString) {
		
		// commandString without the initial command and its argument
		String commandSubString = null;
		
		Pattern commandPattern = Pattern.compile(commandPatternString, Pattern.CASE_INSENSITIVE);
		Matcher commandMatcher = commandPattern.matcher(commandString);
		
		if (commandMatcher.find()) {
			userCommand.addCommandArgument(commandMatcher.group(ENUM_ARGUMENT).trim());
			commandSubString = commandString.substring(commandMatcher.end());
		}

		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(commandSubString);

		ParamEnum paramEnum;
		while (matcher.find()) {
			if (paramEnumTable.containsKey(matcher.group(ENUM_TYPE))) {
				paramEnum = paramEnumTable.get(matcher.group(ENUM_TYPE));
				if (paramEnum.deepRegex() == "") {
					userCommand.addParam(paramEnum, matcher.group(ENUM_ARGUMENT).trim());
				} else {
					Pattern paramPattern = Pattern.compile(paramEnum.deepRegex());
					Matcher paramMatcher = paramPattern.matcher(matcher.group(ENUM_ARGUMENT).trim());
					
					if (paramMatcher.find()) {
						for (String s: paramEnum.groupNames()) {
							userCommand.addParam(paramEnumTable.get(s), paramMatcher.group(s).trim());
						}
					}
				}
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
	
	private String makeCommandPatternString(CommandEnum commandType) {
		String paramsStartPatternString = makeStartParamsPatternString(commandType);
		String commandPatternString = String.format(COMMAND_PATTERN, commandType.regex(), paramsStartPatternString);
		
		return commandPatternString;
	}

	/**
	 * This operation return a pattern string for the given command type
	 * @param commandType
	 * @return
	 */
	private String makePatternString(CommandEnum commandType) {
		String paramsStartPatternString = makeStartParamsPatternString(commandType);
		String paramsEndPatternString = makeEndParamsPatternString(commandType);
		String completePattern = String.format(COMPLETE_PATTERN, commandType.regex(), paramsStartPatternString, paramsEndPatternString);
		return completePattern;
	}

	/**
	 * This operation returns a pattern string consist of all the params of the command type excluding the first params
	 * @param commandType
	 * @return
	 */
	private String makeEndParamsPatternString(CommandEnum commandType) {
		ParamEnum[] params = commandType.params();
		String paramsPattern = "";

		for (ParamEnum param : params) {
			paramsPattern += String.format(INDIVIDUAL_PARAM_PATTERN, param.regex());
		}

		return paramsPattern;
	}
	
	/**
	 * This operation returns a pattern string consist of all the params of the command typ
	 * @param commandType
	 * @return
	 */
	private String makeStartParamsPatternString(CommandEnum commandType) {
		ParamEnum[] params = commandType.params();
		String paramsPattern = "";
		
		for (ParamEnum param : params) {
			paramsPattern += String.format(INDIVIDUAL_PARAM_PATTERN, param.regex());
		}
		
		if (commandType.startParam() != null) {
			paramsPattern += String.format(INDIVIDUAL_PARAM_PATTERN, commandType.startParam().regex());
		}
		
		return paramsPattern;
		
	}

	private CommandEnum getCommandType(String commandString) throws Exception {
		String firstWord = getFirstWord(commandString).toLowerCase();
		if (commandEnumTable.containsKey(firstWord)) {
			return commandEnumTable.get(firstWord);
		} else {
			throw new Exception(String.format(INVALID_COMMAND_MESSAGE, firstWord));
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
}
