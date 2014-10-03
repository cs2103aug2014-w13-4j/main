package interfaces;

import command.Command;

public interface ICommandParser {
	public Command parseCommand(String commandString) throws Exception;
}
