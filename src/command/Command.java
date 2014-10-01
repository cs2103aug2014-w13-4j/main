package command;

import java.util.ArrayList;
import java.util.Hashtable;

public class Command implements ICommand {
	
	private String commandString;
	private CommandEnum commandType;
	private String commandArgument;
	private Hashtable<ParamEnum, ArrayList<String>> params;
	
	public Command(CommandEnum commandType) {
		this.commandType = commandType;
		this.params = new Hashtable<ParamEnum, ArrayList<String>>();
	}
	
	public void addCommandString(String userCommandString) {
		addParam(commandType.commandKey(), userCommandString);
	}
	
	public void addCommandArgument(String arg) {
		commandArgument = arg;
	}
	
	public void addParam(ParamEnum param, String args) {
		if (params.containsKey(param)) {
			params.get(param).add(args);
		} else {
			ArrayList<String> newArgsList = new ArrayList<String>();
			newArgsList.add(args);
			params.put(param, newArgsList);
		}
	}
	
	public Hashtable<ParamEnum, ArrayList<String>> getParam() {
		return params;
	}
	
	public String getCommandString() {
		return commandString;
	}
	
	public CommandEnum getCommand() {
		return commandType;
	}
	
	public String getCommandArgument() {
		return commandArgument;
	}
	
	/**
	 * Test
	 * @param args
	 */
	public static void main(String[] args) {
		Command test = new Command(CommandEnum.ADD);
		test.addCommandString("abc");
		test.addParam(ParamEnum.DATE, "hello");
		test.addParam(ParamEnum.DATE, "hey");
		System.out.println(test.getCommand());
		System.out.println(test.getParam().get(ParamEnum.DATE));
		System.out.println(test.getCommandString());	
	}
}
