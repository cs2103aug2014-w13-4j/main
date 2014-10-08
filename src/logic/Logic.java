package logic;

import interfaces.ILogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import storage.Storage;
import models.Command;
import models.Feedback;
import models.Task;
import command.*;
import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;

//TODO: Throw exceptions when mandatory fields are missing
public class Logic implements ILogic {
	private static final int NEW_ID = -1;
	private static final String ADD_MESSAGE = "%1$s is successfully added.";
	private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
	private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
	private static final String COMPLETE_MESSAGE = "%1$s is marked as completed.";
	private static final String SEARCH_MESSAGE = "%1$s results are found.";
	private static final String ERROR_STORAGE_MESSAGE = "There is an error loading the storage.";
	private static final String DISPLAY_MESSAGE = "All tasks are displayed.";
	private static final String INVALID_COMMAND_MESSAGE = "The command is invalid.";
	private static final String INVALID_INDEX_MESSAGE = "The index is invalid.";
	Storage storage = null;

	public Logic() {

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
		try {
			storage = new Storage();
			return displayAll();
		} catch (IOException | FileFormatNotSupportedException e) {
			System.out.println(e);
			return createTaskListFeedback(ERROR_STORAGE_MESSAGE, null);
		}
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
		if (storage == null) {
			return createTaskListFeedback(ERROR_STORAGE_MESSAGE, null);
		} else {
			CommandEnum commandType = command.getCommand();
			switch (commandType) {
			case ADD:
				return add(command);
			case DELETE:
				return delete(command);
			case UPDATE:
				return update(command);
			case UNDO:
				return null;
			case FILTER:
				return null;
			case DISPLAY:
				return display(command);
			case DONE:
				return complete(command);
			case LEVEL:
				return null;
			case SEARCH:
				return search(command);
			default:
				throw new InvalidInputException(INVALID_COMMAND_MESSAGE);
			}
		}
	}
	
	
	private Feedback display(Command command) throws NumberFormatException, TaskNotFoundException {
		String idString = command.getParam().get(ParamEnum.KEYWORD).get(0);
		if (idString.isEmpty()) {
			return displayAll();
		} else {
			int id = Integer.parseInt(idString);
			return displayTask(id);
		}
	}

	private Feedback displayTask(int id) throws TaskNotFoundException {
		Task task = storage.getTask(id);
		return createTaskFeedback(createMessage(DISPLAY_MESSAGE, null), task);
	}

	/**
	 * Displays all the tasks in the file
	 * 
	 * @return feedback containing all the tasks in the file, and the message.
	 */
	private Feedback displayAll() {
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(DISPLAY_MESSAGE, null), taskList);
	}

	/**
	 * Search for tasks that contain the keyword in the name, description or tags
	 * 
	 * @param command: the command created by CommandParser
	 * @return feedback containing all the tasks in the file, and the message
	 */

	private Feedback search(Command command) {
		Hashtable<ParamEnum, ArrayList<String>> params = command.getParam();
		ArrayList<Task> taskList = storage.searchTask(params);
		return createTaskListFeedback(createMessage(SEARCH_MESSAGE, String.valueOf(taskList.size())),
				taskList);
	}

	/**
	 * Marks a particular task as done
	 * 
	 * @param command
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 * @throws InvalidDateFormatException
	 */
	private Feedback complete(Command command) throws TaskNotFoundException,
			IOException, InvalidDateFormatException {
		int id = getTaskId(command);
		Task task = storage.getTask(id);
		TaskModifier.completeTask(command, task);
		String name = task.getName();
		storage.writeTaskToFile(task);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(COMPLETE_MESSAGE, name), taskList);
	}

	/**
	 * Adds a new task to the file
	 * 
	 * @param command
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 * @throws InvalidDateFormatException
	 */
	private Feedback add(Command command) throws TaskNotFoundException,
			IOException, InvalidDateFormatException {
		Task newTask = new Task();
		newTask.setId(NEW_ID);
		TaskModifier.modifyTask(command, newTask);
		storage.writeTaskToFile(newTask);
		String name = newTask.getName();
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(ADD_MESSAGE, name), taskList);
	}

	/**
	 * Deletes a task from the file
	 * 
	 * @param command
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 */
	private Feedback delete(Command command) throws TaskNotFoundException,
			IOException {
		int id = getTaskId(command);
		Task task = storage.getTask(id);
		String name = task.getName();
		TaskModifier.deleteTask(task);
		storage.writeTaskToFile(task);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(DELETE_MESSAGE, name), taskList);
	}

	/**
	 * Updates the task in the file. It can currently only update due date and
	 * name.
	 * 
	 * @param command
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 * @throws InvalidDateFormatException
	 */
	private Feedback update(Command command) throws TaskNotFoundException,
			IOException, InvalidDateFormatException {
		int id = getTaskId(command);
		Task task = storage.getTask(id);
		TaskModifier.modifyTask(command, task);
		storage.writeTaskToFile(task);
		String name = task.getName();
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(EDIT_MESSAGE, name), taskList);
	}

	private static String createMessage(String message, String variableText1) {
		return String.format(message, variableText1);
	}

	private Feedback createTaskListFeedback(String message, ArrayList<Task> taskList) {
		return new Feedback(message, taskList, null);
	}
	
	private Feedback createTaskFeedback(String message, Task task) {
		return new Feedback(message, null, task);
	}

	private int getTaskId(Command command) {
		if (command.getParam().containsKey(ParamEnum.KEYWORD)) {
			return Integer.parseInt(command.getParam().get(ParamEnum.KEYWORD)
					.get(0));
		} else {
			throw new NumberFormatException(INVALID_INDEX_MESSAGE);
		}
	}

}
