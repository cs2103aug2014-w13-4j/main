package command;

import java.util.ArrayList;
import java.util.Hashtable;

public class Command {
	
	private String commandString;
	private CommandEnum commandType;
	private String commandArgument;
	private Hashtable<ParamEnum, ArrayList<String>> params;
	
	public Command(CommandEnum commandType) {
		this.commandType = commandType;
		this.params = new Hashtable<ParamEnum, ArrayList<String>>();
	}
	
	public void addCommandString(String userCommandString) {
		commandString = userCommandString;
	}
	
	public void addCommandArgument(String arg) {
		addParam(commandType.commandKey(), arg);
	}
	
	public void addParam(ParamEnum param, String args) {
		if (!params.containsKey(param)) {
			ArrayList<String> newArgsList = new ArrayList<String>();
			params.put(param,  newArgsList);
		}
		
		params.get(param).add(args);
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
