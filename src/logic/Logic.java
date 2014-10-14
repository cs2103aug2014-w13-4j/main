package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import storage.Storage;
import command.CommandParser;
import command.ParamEnum;
import models.Command;
import models.Feedback;
import models.StartDueDatePair;
import models.Task;
import command.*;
import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;
import models.Feedback;
import models.Task;


//TODO: Throw exceptions when mandatory fields are missing
public class Logic {
	private static final String ADD_MESSAGE = "%1$s is successfully added.";
	private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
	private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
	private static final String COMPLETE_MESSAGE = "%1$s is marked as completed.";
	private static final String SEARCH_MESSAGE = "%1$s results are found.";
	private static final String ERROR_STORAGE_MESSAGE = "There is an error loading the storage.";
	private static final String DISPLAY_MESSAGE = "All tasks are displayed.";
	private static final String INVALID_INDEX_MESSAGE = "The index is invalid.";
	private static final String ERROR_ALREADY_DELETED_MESSAGE = "Task %1$s is already deleted.";
	private static final String CONFIRM_MESSAGE = "%1$s is marked as confirmed.";
	private static final String INVALID_COMMAND_MESSAGE = "The command is invalid.";
	Storage storage = null;

	Logic() {
	}

	Feedback initialize() {
		try {
			storage = new Storage();
			return displayAll();
		} catch (IOException | FileFormatNotSupportedException e) {
			System.out.println(e);
			return createTaskListFeedback(ERROR_STORAGE_MESSAGE, null);
		}
	}
	Feedback display(Hashtable<ParamEnum, ArrayList<String>> param)
			throws NumberFormatException, TaskNotFoundException {
		String idString = param.get(ParamEnum.KEYWORD).get(0);
		if (idString.isEmpty()) {
			return displayAll();
		} else {
			int id = Integer.parseInt(idString);
			return displayTask(id);
		}
	}

	Feedback confirm(Hashtable<ParamEnum, ArrayList<String>> param)
			throws InvalidInputException, TaskNotFoundException, IOException {
		if (!param.containsKey(ParamEnum.KEYWORD)
				|| !param.containsKey(ParamEnum.ID)) {
			throw new InvalidInputException(INVALID_COMMAND_MESSAGE);
		} else {
			int taskId = getTaskId(param);
			String dateIdString = param.get(ParamEnum.ID).get(0);
			int dateId = Integer.parseInt(dateIdString);
			Task task = storage.getTask(taskId);
			TaskModifier.confirmTask(dateId, task);
			storage.writeTaskToFile(task);
			String taskName = task.getName();
			return createTaskAndTaskListFeedback(
					createMessage(CONFIRM_MESSAGE, taskName),
					storage.getAllTasks(), task);
		}
	}

	/**
	 * Display all tasks in the list
	 * @return feedback containing all the tasks in the file, and the message.
	 */
	private Feedback displayAll() {
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(DISPLAY_MESSAGE, null),
				taskList);
	}

	/**
	 * Search for tasks that contain the keyword in the name, description or
	 * tags
	 * 
	 * @param command
	 *            : the command created by CommandParser
	 * @return feedback containing all the tasks in the file, and the message
	 */

	Feedback search(Hashtable<ParamEnum, ArrayList<String>> param) {
		ArrayList<Task> taskList = storage.searchTask(param);
		return createTaskListFeedback(
				createMessage(SEARCH_MESSAGE, String.valueOf(taskList.size())),
				taskList);
	}

	/**
	 * Marks a particular task as done
	 * 
	 * @param param
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 * @throws InvalidDateFormatException
	 */
	Feedback complete(Hashtable<ParamEnum, ArrayList<String>> param)
			throws TaskNotFoundException, IOException,
			InvalidDateFormatException {
		int id = getTaskId(param);
		Task task = storage.getTask(id);
		TaskModifier.completeTask(param, task);
		String name = task.getName();
		storage.writeTaskToFile(task);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(COMPLETE_MESSAGE, name),
				taskList);
	}

	/**
	 * Adds a new task to the file
	 * 
	 * @param param
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 * @throws InvalidDateFormatException
	 */
	Feedback add(Hashtable<ParamEnum, ArrayList<String>> param)
			throws TaskNotFoundException, IOException,
			InvalidDateFormatException {
		Task newTask = new Task();
		TaskModifier.modifyTask(param, newTask);
		storage.writeTaskToFile(newTask);
		String name = newTask.getName();
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(ADD_MESSAGE, name),
				taskList);
	}

	/**
	 * Deletes a task from the file
	 * 
	 * @param param
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 */
	Feedback delete(Hashtable<ParamEnum, ArrayList<String>> param)
			throws TaskNotFoundException, IOException {
		int id = getTaskId(param);
		Task task = storage.getTask(id);
		String name = task.getName();
		TaskModifier.deleteTask(task);
		storage.writeTaskToFile(task);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(DELETE_MESSAGE, name),
				taskList);
	}

	/**
	 * Updates the task in the file. It can currently only update due date and
	 * name.
	 * 
	 * @param param
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 * @throws InvalidDateFormatException
	 */
	Feedback update(Hashtable<ParamEnum, ArrayList<String>> param)
			throws TaskNotFoundException, IOException,
			InvalidDateFormatException {
		int id = getTaskId(param);
		Task task = storage.getTask(id);
		TaskModifier.modifyTask(param, task);
		storage.writeTaskToFile(task);
		String name = task.getName();
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(EDIT_MESSAGE, name),
				taskList);
	}

	private Feedback displayTask(int id) throws TaskNotFoundException {
		Task task = storage.getTask(id);
		if (task.isDeleted()) {
			throw new TaskNotFoundException(createMessage(
					ERROR_ALREADY_DELETED_MESSAGE, Integer.toString(id)));
		}
		return createTaskFeedback(createMessage(DISPLAY_MESSAGE, null), task);
	}

	private String createMessage(String message, String variableText1) {
		return String.format(message, variableText1);
	}

	private Feedback createTaskListFeedback(String message,
			ArrayList<Task> taskList) {
		return new Feedback(message, taskList, null);
	}

	private Feedback createTaskFeedback(String message, Task task) {
		return new Feedback(message, null, task);
	}

	private Feedback createTaskAndTaskListFeedback(String message,
			ArrayList<Task> taskList, Task task) {
		return new Feedback(message, taskList, task);
	}

	private int getTaskId(Hashtable<ParamEnum, ArrayList<String>> param) {
		if (param.containsKey(ParamEnum.KEYWORD)) {
			return Integer.parseInt(param.get(ParamEnum.KEYWORD).get(0));
		} else {
			throw new NumberFormatException(INVALID_INDEX_MESSAGE);
		}
	}

}
