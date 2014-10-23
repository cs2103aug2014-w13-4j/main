package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;

import com.rits.cloning.Cloner;

import models.ApplicationLogger;
import storage.Storage;
import command.ParamEnum;
import models.Feedback;
import models.History;
import models.Task;
import exceptions.FileFormatNotSupportedException;
import exceptions.HistoryNotFoundException;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;

//TODO: Throw exceptions when mandatory fields are missing
public class Logic {
	private static final String COMPLETED = "completed";
	private static final String ADD_MESSAGE = "%1$s is successfully added.";
	private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
	private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
	private static final String COMPLETE_MESSAGE = "%1$s is marked as completed.";
	private static final String SEARCH_MESSAGE = "%1$s results are found.";
	private static final String ERROR_STORAGE_MESSAGE = "There is an error loading the storage.";
	private static final String DISPLAY_MESSAGE = "All tasks are displayed.";
	private static final String ERROR_ALREADY_DELETED_MESSAGE = "Task %1$s is already deleted.";
	private static final String CONFIRM_MESSAGE = "%1$s is marked as confirmed.";
	private static final String FILTER_MESSAGE = "%1$s tasks are filtered";
	private static final String UNDO_MESSAGE = "%1$s %2$s is undone";
	private static final String FILTER_KEYWORD_WRONG = "Filter keyword is wrong.";
	private static final Object ACTIVE = "active";
	Storage storage = null;
	private LogicUndo logicUndo = new LogicUndo();
	// public LogicUndo logicUndo = LogicUndo.getInstance();
	private Cloner cloner = new Cloner();

	Logic() {
	}

	Feedback initialize() {
		try {
			ApplicationLogger.getApplicationLogger().log(Level.INFO, "Initializing Logic Backend.");
			storage = new Storage();
			return displayAll();
		} catch (IOException | FileFormatNotSupportedException e) {
			ApplicationLogger.getApplicationLogger().log(Level.SEVERE, e.getMessage());
			return createTaskListFeedback(ERROR_STORAGE_MESSAGE, null);
		}
	}

	Feedback display(Hashtable<ParamEnum, ArrayList<String>> param)
			throws NumberFormatException, TaskNotFoundException {
		String idString = param.get(ParamEnum.KEYWORD).get(0);
		logicUndo.pushNullCommandToHistory();
		if (idString.isEmpty()) {
			return displayAll();
		} else {
			int id = Integer.parseInt(idString);
			return displayTask(id);
		}
	}

	Feedback confirm(Hashtable<ParamEnum, ArrayList<String>> param)
			throws InvalidInputException, TaskNotFoundException, IOException {
		int taskId = getTaskId(param);
		String dateIdString = param.get(ParamEnum.ID).get(0);
		int dateId = Integer.parseInt(dateIdString);
		Task task = getTaskFromStorage(taskId);
		TaskModifier.confirmTask(dateId, task);
		storage.writeTaskToFile(task);
		Task clonedTask = cloner.deepClone(task);
		logicUndo.pushConfirmCommandToHistory(clonedTask);
		String taskName = task.getName();
		return createTaskAndTaskListFeedback(
				createMessage(CONFIRM_MESSAGE, taskName, null),
				storage.getAllTasks(), task);
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
		logicUndo.pushNullCommandToHistory();
		return createTaskListFeedback(
				createMessage(SEARCH_MESSAGE, String.valueOf(taskList.size()),
						null), taskList);
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
		int taskId = getTaskId(param);
		Task task = getTaskFromStorage(taskId);
		TaskModifier.completeTask(param, task);
		String name = task.getName();
		storage.writeTaskToFile(task);
		Task clonedTask = cloner.deepClone(task);
		logicUndo.pushCompleteCommandToHistory(clonedTask);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(
				createMessage(COMPLETE_MESSAGE, name, null), taskList);
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
		Task task = new Task();
		TaskModifier.modifyTask(param, task);
		storage.writeTaskToFile(task);
		String name = task.getName();
		Task clonedTask = cloner.deepClone(task);
		logicUndo.pushAddCommandToHistory(clonedTask);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(createMessage(ADD_MESSAGE, name, null),
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
		int taskId = getTaskId(param);
		Task task = getTaskFromStorage(taskId);
		String name = task.getName();
		TaskModifier.deleteTask(task);
		storage.writeTaskToFile(task);
		Task clonedTask = cloner.deepClone(task);
		logicUndo.pushDeleteCommandToHistory(clonedTask);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(
				createMessage(DELETE_MESSAGE, name, null), taskList);
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
		int taskId = getTaskId(param);
		Task oldTask = getTaskFromStorage(taskId);
		Task task = cloner.deepClone(oldTask);
		TaskModifier.modifyTask(param, task);
		storage.writeTaskToFile(task);
		String name = task.getName();
		ArrayList<Task> taskList = storage.getAllTasks();
		logicUndo.pushUpdateCommandToHistory(oldTask);
		return createTaskListFeedback(createMessage(EDIT_MESSAGE, name, null),
				taskList);
	}

	// set assert to ensure that value is an arraylist
	Feedback filter(Hashtable<ParamEnum, ArrayList<String>> param)
			throws InvalidInputException {
		logicUndo.pushNullCommandToHistory();
		if (isFilterStatusCompleted(param)) {
			ArrayList<Task> taskList = storage.getCompletedTasks(storage
					.getAllTasks());
			return createTaskListFeedback(
					createMessage(FILTER_MESSAGE, COMPLETED, null), taskList);
		} else if (isFilterStatusActive(param)) {
			ArrayList<Task> taskList = storage.getActiveTasks(storage
					.getAllTasks());
			return createTaskListFeedback(
					createMessage(FILTER_MESSAGE, COMPLETED, null), taskList);
		} else {
			throw new InvalidInputException(createMessage(FILTER_KEYWORD_WRONG,
					null, null));
		}

	}

	// Hiccup: undo add will not update the task (make it disappear) if it is
	// displayed
	Feedback undo() throws HistoryNotFoundException, TaskNotFoundException,
			IOException {
		History lastAction = logicUndo.getLastAction();
		if (lastAction == null) {
			throw new HistoryNotFoundException("Not supported yet. :( ");
		} else {
			Task task = lastAction.getTask();
			storage.writeTaskToFile(task);
			// TODO: Find better way. Is there a way to generalise such that the
			// task detail is not shown if it is deleted?
			if (task.isDeleted()) {
				return createTaskAndTaskListFeedback(
						createMessage(UNDO_MESSAGE, lastAction.getCommand()
								.regex(), task.getName()),
						storage.getAllTasks(), null);
			} else {
				return createTaskAndTaskListFeedback(
						createMessage(UNDO_MESSAGE, lastAction.getCommand()
								.regex(), task.getName()),
						storage.getAllTasks(), lastAction.getTask());
			}
		}
	}

	/**
	 * Display all tasks in the list
	 *
	 * @return feedback containing all the tasks in the file, and the message.
	 */
	private Feedback displayAll() {
		ArrayList<Task> taskList = storage.getAllTasks();
		return createTaskListFeedback(
				createMessage(DISPLAY_MESSAGE, null, null), taskList);
	}

	/**
	 * Displays the individual task
	 * 
	 * @param id
	 *            : task id
	 * @return feedback containing the task and the message
	 * @throws TaskNotFoundException
	 *             : if the id is invalid or if it is deleted
	 */

	private Feedback displayTask(int id) throws TaskNotFoundException {
		Task task = getTaskFromStorage(id);
		return createTaskFeedback(createMessage(DISPLAY_MESSAGE, null, null),
				task);
	}

	/**
	 * Gets the task from storage
	 * 
	 * @param id
	 *            : id of task
	 * @return task corresponding to the id
	 * @throws TaskNotFoundException
	 *             : if task is already deleted, or if id is invalid
	 */
	private Task getTaskFromStorage(int id) throws TaskNotFoundException {
		Task task = storage.getTask(id);
		if (task.isDeleted()) {
			throw new TaskNotFoundException(createMessage(
					ERROR_ALREADY_DELETED_MESSAGE, Integer.toString(id), null));
		}
		return task;
	}

	private String createMessage(String message, String variableText1,
			String variableText2) {
		return String.format(message, variableText1, variableText2);
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
		return Integer.parseInt(param.get(ParamEnum.KEYWORD).get(0));
	}

	private boolean isFilterStatusActive(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.get(ParamEnum.STATUS).get(0).toLowerCase().equals(ACTIVE);
	}

	private boolean isFilterStatusCompleted(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.get(ParamEnum.STATUS).get(0).toLowerCase()
				.equals(COMPLETED);
	}
}
