package interfaces;

import java.io.IOException;

import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;
import models.Command;
import models.Feedback;

public interface ILogic {
	public Feedback executeCommand(Command command) throws TaskNotFoundException, IOException, InvalidDateFormatException, InvalidInputException;

}
