package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import models.Feedback;
import models.Task;
import command.*;

public class Logic implements ILogic {
	private static final int INVALID_ID = -1;
	private static final String ADD_MESSAGE = "%1$s is successfully added.";
	private static final String ERROR_MESSAGE = "Invalid Number Input!";
	private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
	private static final int INVALID_LEVEL = -1;

	public Feedback executeCommand(Command command) {
		// TODO Auto-generated method stub
		CommandEnum commandType = command.getCommand();
		Hashtable<ParamEnum, ArrayList<String>> params = command.getParam();
		String name;
		Task task;
		switch (commandType) {
		case ADD:
			task = createTaskForAdd(command);
			writeTaskToFile(task);
			name = task.getName();
			return createFeedback(createMessage(ADD_MESSAGE, name));
		case DELETE:
			int id = getIdFromCommand(command);
			if (isValidId(id)) {
				name = getTaskName(id);
				deleteTaskFromFile(id);
				return createFeedback(createMessage(DELETE_MESSAGE, name));
			} else {
				return createFeedback(ERROR_MESSAGE);
			}
		case UPDATE:

		case UNDO:
			return null;
		case SELECT:
			return null;
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

	private Task createTaskForEdit(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String createMessage(String message, String variableText1) {
		return String.format(message, variableText1);
	}

	private Feedback createFeedback(String message) {
		ArrayList<Task> taskList = getAllTasks();
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
		Task task = getTask(id);
		return task.getName();
	}

	private void setStartDateFromCommand(Command command, Task task) {
		if (hasStartDate(command)) {
			// set task date;
		}
	}

	private void setEndDateFromCommand(Command command, Task task) {
		if (hasEndDate(command)) {
			// set task date;
		}

	}

	private void setDueDateFromCommand(Command command, Task task) {
		if (hasDueDate(command)) {
			// set task date;
		}

	}

	private void setIdFromCommand(Command command, Task task) {
		int id = getIdFromCommand(command);
		if (isValidId(id)) {
			task.setId(id);
		}
	}

	private Task createTaskForAdd(Command command) {
		Task task = new Task();
		setNameFromCommand(command, task);
		setStartDateFromCommand(command, task);
		setEndDateFromCommand(command, task);
		setDueDateFromCommand(command, task);
		setTagsFromCommand(command, task);
		setLevelFromCommand(command, task);
		setNoteFromCommand(command, task);
		return task;
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

	private boolean hasLevel(Command command) {
		return command.getParam().containsKey(ParamEnum.LEVEL);
	}

	private boolean hasNote(Command command) {
		return command.getParam().containsKey(ParamEnum.NOTE);
	}

	private boolean isValidId(int id) {
		return id > INVALID_ID;
	}

}
