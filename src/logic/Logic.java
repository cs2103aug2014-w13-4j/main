package logic;

import java.io.IOException;
import java.util.ArrayList;

import storage.Storage;
import models.Feedback;
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
	private static final int INVALID_LEVEL = -1;
	private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
	private static final String COMPLETE_MESSAGE = "%l$s is marked as completed.";
	private static final String ERROR_STORAGE_MESSAGE = "There is an error loading the storage.";
	private static final String DISPLAY_MESSAGE = "All tasks are displayed.";
	private static final String ERROR_IO_MESSAGE = "There is an error in loading the file.";
	private Storage storage = null;
	

	public Logic() {

	}

	/**
     * constructor
     * This constructor follows the singleton pattern
     * It can only be called with in the current class (Logic.getInstance())
     * This is to ensure that only there is exactly one instance of Logic class
     * @throws FileFormatNotSupportedException, IOException
	 * @return Logic object
	 * 
	 * To be implemented in the future
	 */
	/**
	private static Logic instance = null;
	
	private Logic() {

	}
	
	public static Logic getInstance() {
		if (instance == null) {
			instance = new Logic();
		}
		return instance;
	}
	**/

	/**
	 * Initialises the logic object by creating its corresponding storage object
	 * It also catches the exceptions that can be thrown
	 * @return the feedback indicating whether the storage has been successfully loaded. 
	 */
	public Feedback initialize() {
		try {
			storage = new Storage();
			return display();
		} catch (IOException | FileFormatNotSupportedException e) {
			return createFeedback(null, ERROR_STORAGE_MESSAGE);
		}
	}

	/**
	 * Main function to call to execute command
	 * 
	 * @param the command created by the commandParser
	 * @return the feedback (tasklist and message) corresponding to the particular command
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
				case TAG:
					return null;
				case LEVEL:
					return null;
				default:
					return null;
				}
			} catch (TaskNotFoundException e) {
				ArrayList<Task> taskList = storage.getAllTasks();
				return createFeedback(taskList, INVALID_INDEX_MESSAGE);
			} catch (IOException e) {
				return createFeedback(null, ERROR_IO_MESSAGE);
			}
		}
	}

	/**
	 * Displays all the tasks in the file
	 * @return feedback containing all the tasks in the file, and the message.
	 */
	private Feedback display() {
		ArrayList<Task> taskList = storage.getAllTasks();
		return createFeedback(taskList, createMessage(DISPLAY_MESSAGE, null));
	}

	/**
	 * Marks a particular task as done
	 * @param command: the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 */
	private Feedback markAsDone(Command command) throws TaskNotFoundException,
			IOException {
		int id = getIdFromCommand(command);
		Task task = storage.getTask(id);
		String name = task.getName();
		task.setCompleted(true);
		storage.writeTaskToFile(task);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createFeedback(taskList, createMessage(COMPLETE_MESSAGE, name));
	}

	/**
	 * Adds a new task to the file
	 * @param command: the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 */
	private Feedback add(Command command) throws TaskNotFoundException,
			IOException {
		Task task = createTaskForAdd(command);
		storage.writeTaskToFile(task);
		String name = task.getName();
		ArrayList<Task> taskList = storage.getAllTasks();
		return createFeedback(taskList, createMessage(ADD_MESSAGE, name));
	}

	/**
	 * Deletes a task from the file
	 * @param command: the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and the message.
	 * @throws TaskNotFoundException
	 * @throws IOException
	 */
	private Feedback delete(Command command) throws TaskNotFoundException,
			IOException {
		int id = getIdFromCommand(command);
		Task task = storage.getTask(id);
		String name = task.getName();
		storage.deleteTaskFromFile(id);
		ArrayList<Task> taskList = storage.getAllTasks();
		return createFeedback(taskList, createMessage(DELETE_MESSAGE, name));
	}

	/**
	 * Updates the task in the file
	 * @param command: the command created by commandParser
	 * @return feedback containing the updated list of tasks in the file, and the message.
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
		ArrayList<Task> taskList = storage.getAllTasks();
		return createFeedback(taskList, createMessage(EDIT_MESSAGE, name));
	}

	private Task createTaskForAdd(Command command) {
		Task task = new Task();
		task.setId(-1);
		setNameFromCommand(command, task);
		setStartDateFromCommand(command, task);
		setEndDateFromCommand(command, task);
		setDueDateFromCommand(command, task);
		setTagsFromCommand(command, task);
		setLevelFromCommand(command, task);
		setNoteFromCommand(command, task);
		return task;
	}

	private Task updateTask(Command command, Task task) {
		updateName(command, task);
		updateDueDate(command, task);
		return task;
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

	private void updateName(Command command, Task oldTask) {
		if (hasNewName(command)) {
			String taskName = command.getParam().get(ParamEnum.NAME).get(0);
			oldTask.setName(taskName);
		}

	}

	private void updateDueDate(Command command, Task oldTask) {
		// TODO Auto-generated method stub

	}

	private void setStartDateFromCommand(Command command, Task task) {
		if (hasStartDate(command)) {
			// TODO: set task date;
		}
	}

	private void setEndDateFromCommand(Command command, Task task) {
		if (hasEndDate(command)) {
			// TODO: set task date;
		}

	}

	private void setDueDateFromCommand(Command command, Task task) {
		if (hasDueDate(command)) {
			// TODO: set task date;
		}

	}

	private void setLevelFromCommand(Command command, Task task) {
		if (hasLevel(command)) {
			int level;
			try {
				level = Integer.parseInt(command.getParam()
						.get(ParamEnum.LEVEL).get(0));
			} catch (NumberFormatException e) {
				level = INVALID_LEVEL;
			}
			// TODO: Decide on range of priority levels
			// TODO: Should an error message be thrown if an invalid level is
			// given?
			// Should the task be saved in that case?
			if (level > INVALID_LEVEL) {
				task.setPriorityLevel(level);
			}
		}
	}

	private void setNameFromCommand(Command command, Task task) {
		String taskName = command.getCommandArgument();
		task.setName(taskName);
	}

	private void setNoteFromCommand(Command command, Task task) {
		if (hasNote(command)) {
			String note = command.getParam().get(ParamEnum.NOTE).get(0);
			task.setNote(note);
		}

	}

	private void setTagsFromCommand(Command command, Task task) {
		ArrayList<String> tags = command.getParam().get(ParamEnum.TAG);
		task.setTags(tags);
	}

	private boolean hasStartDate(Command command) {
		return command.getParam().containsKey(ParamEnum.START_DATE);
	}

	private boolean hasEndDate(Command command) {
		return command.getParam().containsKey(ParamEnum.START_DATE);
	}

	private boolean hasDueDate(Command command) {
		return command.getParam().containsKey(ParamEnum.DATE);
	}

	private boolean hasNewName(Command command) {
		return command.getParam().containsKey(ParamEnum.NAME);
	}

	private boolean hasLevel(Command command) {
		return command.getParam().containsKey(ParamEnum.LEVEL);
	}

	private boolean hasNote(Command command) {
		return command.getParam().containsKey(ParamEnum.NOTE);
	}

}
