package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import models.Feedback;
import models.Task;
import command.*;

//TODO: Throw exceptions when mandatory fields are missing
public class Logic implements ILogic {
	private static final int INVALID_ID = -1;
	private static final String ADD_MESSAGE = "%1$s is successfully added.";
	private static final String INVALID_INDEX_MESSAGE = "Invalid Number Input!";
	private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
	private static final int INVALID_LEVEL = -1;
	private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
	private static final String SELECT_MESSAGE = "%1$s is selected.";

	public Feedback executeCommand(Command command) {
		CommandEnum commandType = command.getCommand();
		Hashtable<ParamEnum, ArrayList<String>> params = command.getParam();
		String name;
		Task task;
		int id;
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
			return select(command);
		case DISPLAY:
			return null;
		case DONE:
			return null;
		case TAG:
			return null;
		case LEVEL:
			return null;
		default:
			return null;

		}

	}

	private Feedback add(Command command) {
		String name;
		Task task;
		task = createTaskForAdd(command);
		writeTaskToFile(task);
		name = task.getName();
		ArrayList<Task> taskList = getAllTasks();
		return createFeedback(taskList, createMessage(ADD_MESSAGE, name));
	}

	private Feedback delete(Command command) {
		String name;
		int id;
		id = getIdFromCommand(command);
		if (isValidId(id)) {
			name = getTaskName(id);
			deleteTaskFromFile(id);
			ArrayList<Task> taskList = getAllTasks();
			return createFeedback(taskList,createMessage(DELETE_MESSAGE, name));
		} else {
			return createFeedback(null, INVALID_INDEX_MESSAGE);
		}
	}

	private Feedback update(Command command) {
		String name;
		Task task;
		int id;
		id = getIdFromCommand(command);
		if (isValidId(id)) {
			task = getTasks(id);
			updateTask(command, task);
			writeTaskToFile(task);
			name = task.getName();
			ArrayList<Task> taskList = getAllTasks();
			return createFeedback(taskList, createMessage(EDIT_MESSAGE, name));
		} else {
			return createFeedback(null, INVALID_INDEX_MESSAGE);
		}
	}

	private Feedback select(Command command) {
		String name;
		int id;
		id = getIdFromCommand(command);
		if (isValidId(id)) {
			ArrayList<Task> taskList = new ArrayList<Task>();
			Task selectedTask = getTasks(id);
			taskList.add(selectedTask);
			name = selectedTask.getName();
			return createFeedback(taskList, createMessage(SELECT_MESSAGE, name));
		} else {
			return createFeedback(null, INVALID_INDEX_MESSAGE);
		}
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

	private String getTaskName(int id) {
		Task task = getTasks(id);
		return task.getName();
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
			//TODO: set task date;
		}
	}

	private void setEndDateFromCommand(Command command, Task task) {
		if (hasEndDate(command)) {
			//TODO: set task date;
		}

	}

	private void setDueDateFromCommand(Command command, Task task) {
		if (hasDueDate(command)) {
			//TODO: set task date;
		}

	}

	private void setIdFromCommand(Command command, Task task) {
		int id = getIdFromCommand(command);
		if (isValidId(id)) {
			task.setId(id);
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

	private boolean isValidId(int id) {
		//TODO: Store the largest current id in storage
		return id > INVALID_ID;
	}

}
