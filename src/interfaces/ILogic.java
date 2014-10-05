package interfaces;

import java.io.IOException;

import command.Command;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;
import models.Feedback;

public interface ILogic {
	public Feedback executeCommand(Command command) throws TaskNotFoundException, IOException, InvalidDateFormatException, InvalidInputException;

}
