package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import storage.Storage;
import models.DateParser;
import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;
import models.exceptions.FileFormatNotSupportedException;
import models.exceptions.TaskNotFoundException;
import command.*;

//TODO: Throw exceptions when mandatory fields are missing
public class Logic implements ILogic {
	private static final int INVALID_ID = -1;
	private static final String ADD_MESSAGE = "%1$s is successfully added.";
	private static final String INVALID_INDEX_MESSAGE = "Invalid Number Input!";
	private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
	private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
	private static final String COMPLETE_MESSAGE = "%l$s is marked as completed.";
	private static final String ERROR_STORAGE_MESSAGE = "There is an error loading the storage.";
	private static final String DISPLAY_MESSAGE = "All tasks are displayed.";
	private static final String ERROR_IO_MESSAGE = "There is an error in loading the file.";
	private static final String ERROR_INPUT_MESSAGE = "The input is invalid.";
	private Storage storage = null;

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
			return display();
		} catch (IOException | FileFormatNotSupportedException e) {
			System.out.println(e);
			return createFeedback(null, ERROR_STORAGE_MESSAGE);
		}
	}

	/**
	 * Main function to call to execute command
	 * 
	 * @param the
	 *            command created by the commandParser
	 * @return the feedback (tasklist and message) corresponding to the
	 *         particular command
	 */
	public Feedback executeCommand(Command command) {
		if (storage == null) {
			return createFeedback(null, ERROR_STORAGE_MESSAGE);
		} else {
			try {
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
				case SELECT:
					return null;
				case DISPLAY:
					return display();
				case DONE:
					return markAsDone(command);
				case LEVEL:
					return null;
					/**
					 * case SEARCH: return null;
					 **/
					// storage search -> (HashMap CommandParam)
				default:
					return createFeedback(storage.getActiveTasks(),
							ERROR_INPUT_MESSAGE);
				}
			} catch (TaskNotFoundException e) {
				ArrayList<Task> taskList = storage.getActiveTasks();
				return createFeedback(taskList, INVALID_INDEX_MESSAGE);
			} catch (IOException e) {
				return createFeedback(null, ERROR_IO_MESSAGE);
			}
		}
	}

	/**
	 * Displays all the tasks in the file
	 * 
	 * @return feedback containing all the tasks in the file, and the message.
	 */
	private Feedback display() {
		ArrayList<Task> taskList = storage.getActiveTasks();
		return createFeedback(taskList, createMessage(DISPLAY_MESSAGE, null));
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
	 */
	private Feedback markAsDone(Command command) throws TaskNotFoundException,
			IOException {
		int id = getIdFromCommand(command);
		Task task = storage.getTask(id);
		String name = task.getName();
		// to allow them to type in their own date
		task.setDateEnd(Calendar.getInstance());
		storage.writeTaskToFile(task);
		ArrayList<Task> taskList = storage.getActiveTasks();
		return createFeedback(taskList, createMessage(COMPLETE_MESSAGE, name));
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
	 */
	private Feedback add(Command command) throws TaskNotFoundException,
			IOException {
		Task task = createTaskForAdd(command);
		storage.writeTaskToFile(task);
		String name = task.getName();
		ArrayList<Task> taskList = storage.getActiveTasks();
		return createFeedback(taskList, createMessage(ADD_MESSAGE, name));
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
		int id = getIdFromCommand(command);
		Task task = storage.getTask(id);
		String name = task.getName();
		storage.deleteTaskFromFile(id);
		ArrayList<Task> taskList = storage.getActiveTasks();
		return createFeedback(taskList, createMessage(DELETE_MESSAGE, name));
	}

	/**
	 * Updates the task in the file
	 * 
	 * @param command
	 *            : the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and
	 *         the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 */
	private Feedback update(Command command) throws TaskNotFoundException,
			IOException {
		int id = getIdFromCommand(command);
		Task task = storage.getTask(id);
		updateTask(command, task);
		storage.writeTaskToFile(task);
		String name = task.getName();
		ArrayList<Task> taskList = storage.getActiveTasks();
		return createFeedback(taskList, createMessage(EDIT_MESSAGE, name));
	}

	public Task createTaskForAdd(Command command) {
		Task task = new Task();
		task.setId(-1);
		setNameFromCommand(command, task);
		setStartDateFromCommand(command, task);
		setDueDateFromCommand(command, task);
		setTagsFromCommand(command, task);
		setLevelFromCommand(command, task);
		setNoteFromCommand(command, task);
		return task;
	}

	private void updateTask(Command command, Task task) {
		updateName(command, task);
		setDueDateFromCommand(command, task);
	}

	private static String createMessage(String message, String variableText1) {
		return String.format(message, variableText1);
	}

	private Feedback createFeedback(ArrayList<Task> taskList, String message) {
		return new Feedback(message, taskList);
	}

	private int getIdFromCommand(Command command) {
		try {
			return Integer.parseInt(command.getCommandArgument());
		} catch (NumberFormatException e) {
			return INVALID_ID;
		}
	}

	private void updateName(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.NAME)) {
			String taskName = command.getParam().get(ParamEnum.NAME).get(0);
			task.setName(taskName);
		}
	}

	void setDueDateFromCommand(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.END_DATE)) {
			Calendar dueDate = DateParser.parseString(command.getParam()
					.get(ParamEnum.END_DATE).get(0));
			task.setDateDue(dueDate);
		}
	}

	private void setStartDateFromCommand(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.START_DATE)) {
			Calendar startDate = DateParser.parseString(command.getParam()
					.get(ParamEnum.START_DATE).get(0));
			task.setDateDue(startDate);
		}
	}

	private void setLevelFromCommand(Command command, Task task) {
		PriorityLevelEnum priorityEnum = null;
		if (command.getParam().containsKey(ParamEnum.LEVEL)) {
			try {
				int level = Integer.parseInt(command.getParam()
						.get(ParamEnum.LEVEL).get(0));
				priorityEnum = PriorityLevelEnum.fromInteger(level);
			} catch (NumberFormatException | NullPointerException e) {
			}
			// TODO: Indicate error when invalid priority level is added
			task.setPriorityLevel(priorityEnum);
		}
	}

	private void setNameFromCommand(Command command, Task task) {
		String taskName = command.getCommandArgument();
		task.setName(taskName);
	}

	private void setNoteFromCommand(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.NOTE)) {
			String note = command.getParam().get(ParamEnum.NOTE).get(0);
			task.setNote(note);
		}

	}

	private void setTagsFromCommand(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.TAG)) {
			ArrayList<String> tags = command.getParam().get(ParamEnum.TAG);
			task.setTags(tags);
		}
	}

}
