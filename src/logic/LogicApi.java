package logic;

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
			assert param.containsKey(ParamEnum.KEYWORD);
			switch (commandType) {
			case ADD:
				assert hasNameParam(param);
				if (!isNameParamEmpty(param)) {
					return logic.add(param);
				}
				break;
			case DELETE:
				if (!isKeywordParamEmpty(param)) {
				return logic.delete(param);
				}
				break;
			case UPDATE:
				if (!isKeywordParamEmpty(param)) {
				return logic.update(param);
				}
				break;
			case UNDO:
				return null;
			case FILTER:
				if (hasStatusParam(param)) {
					return logic.filter(param);
				}
				break;
			case DISPLAY:
				return logic.display(param);
			case DONE:
				if (!isKeywordParamEmpty(param)) {
					return logic.complete(param);
				}
				break;
			case LEVEL:
				return null;
			case SEARCH:
				if (!isKeywordParamEmpty(param) || hasNameParam(param) || hasNoteParam(param) || hasTagParam(param)) {
					return logic.search(param);
				}
				break;
			case CONFIRM:
				if (!isKeywordParamEmpty(param) && hasIdParam(param)) {
					return logic.confirm(param);
				}
				break;
			case TAG:
				break;
			default:
				break;
			}
			throw new InvalidInputException(INVALID_COMMAND_MESSAGE);
		}
	}

	private boolean hasStatusParam(Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.containsKey(ParamEnum.STATUS);
	}

	private boolean hasTagParam(Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.containsKey(ParamEnum.TAG);
	}

	private boolean hasNoteParam(Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.containsKey(ParamEnum.NOTE);
	}

	private boolean hasNameParam(Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.containsKey(ParamEnum.NAME);
	}

	private boolean isNameParamEmpty(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.get(ParamEnum.NAME).get(0).isEmpty();
	}

	private boolean hasIdParam(Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.containsKey(ParamEnum.ID);
	}

	private boolean isKeywordParamEmpty(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.get(ParamEnum.KEYWORD).get(0).isEmpty();
	}

}