package interfaces;

import models.Command;

public interface ICommandParser {
	public Command parseCommand(String commandString) throws Exception;
}
