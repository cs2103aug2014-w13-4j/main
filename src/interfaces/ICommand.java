package interfaces;

import java.util.ArrayList;
import java.util.Hashtable;

import command.CommandEnum;
import command.ParamEnum;

public interface ICommand {
	/**
	 * This operation returns the command type
	 * 
	 * @return type of command
	 */
	public CommandEnum getCommand();
	
	/**
	 * This operation return a table of Param type and its associated arguments
	 * 
	 * @return Hashtable of param type to its associated arguments
	 */
	public Hashtable<ParamEnum, ArrayList<String>> getParam();
}
