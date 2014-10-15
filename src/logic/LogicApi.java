package logic;

import interfaces.ILogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import models.Command;
import models.Feedback;
import command.*;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;

//TODO: Throw exceptions when mandatory fields are missing
public class LogicApi {
	Logic logic;
	private static final String INVALID_COMMAND_MESSAGE = "The command is invalid.";

	public LogicApi() {

	}

	/**
	 * constructor This constructor follows the singleton pattern It can only be
	 * called with in the current class (Logic.getInstance()) This is to ensure
	 * that only there is exactly one instance of Logic class
	 * 
	 * @throws FileFormatNotSupportedException
	 *             , IOException
	 * @return Logic object
	 * 
	 *         To be implemented in the future
	 */
	/**
	 * private static Logic instance = null;
	 * 
	 * private Logic() {
	 * 
	 * }
	 * 
	 * public static Logic getInstance() { if (instance == null) { instance =
	 * new Logic(); } return instance; }
	 **/

	/**
	 * Initialises the logic object by creating its corresponding storage object
	 * It also catches the exceptions that can be thrown
	 * 
	 * @return the feedback indicating whether the storage has been successfully
	 *         loaded.
	 */
	public Feedback initialize() {
		logic = new Logic();
		return logic.initialize();
	}

	/**
	 * Main function to call to execute command
	 * 
	 * @param the
	 *            command created by the commandParser
	 * @return the feedback (tasklist and message) corresponding to the
	 *         particular command
	 * @throws InvalidDateFormatException
	 * @throws IOException
	 * @throws TaskNotFoundException
	 * @throws InvalidInputException
	 */
	public Feedback executeCommand(Command command)
			throws TaskNotFoundException, IOException,
			InvalidDateFormatException, InvalidInputException {
		if (logic.storage == null) {
			throw new IOException();
		} else {
			CommandEnum commandType = command.getCommand();
			Hashtable<ParamEnum, ArrayList<String>> param = command.getParam();
			switch (commandType) {
			case ADD:
				return logic.add(param);
			case DELETE:
				return logic.delete(param);
			case UPDATE:
				return logic.update(param);
			case UNDO:
				return null;
			case FILTER:
				return null;
			case DISPLAY:
				return logic.display(param);
			case DONE:
				return logic.complete(param);
			case LEVEL:
				return null;
			case SEARCH:
				return logic.search(param);
			case CONFIRM:
				return logic.confirm(param);
			default:
				throw new InvalidInputException(INVALID_COMMAND_MESSAGE);
			}
		}
	}

}
