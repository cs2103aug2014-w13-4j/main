package logic;

import java.util.ArrayList;
import java.util.Calendar;

import models.Command;
import models.DateParser;
import models.PriorityLevelEnum;
import models.Task;
import command.ParamEnum;
import exceptions.InvalidDateFormatException;

public class TaskModifier {
	Task task;
	
	public TaskModifier(Task task){
		this.task = task;
	}
	
	static void modifyTask(Command command, Task task)
			throws InvalidDateFormatException {
		setNameFromCommand(command, task);
		setStartDateFromCommand(command, task);
		setDueDateFromCommand(command, task);
		setTagsFromCommand(command, task);
		setLevelFromCommand(command, task);
		setNoteFromCommand(command, task);
	}
	
	static void deleteTask(Task task) {
		task.setDeleted(true);
	}
	
	static void completeTask(Command command, Task task) throws InvalidDateFormatException {
		if (command.getParam().containsKey(ParamEnum.DATE)) {
			Calendar completedDate = DateParser.parseString(command.getParam()
					.get(ParamEnum.DATE).get(0));
			task.setDateEnd(completedDate);
		} else {
			task.setDateEnd(Calendar.getInstance());
	}
		
	}

	private static void setNameFromCommand(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.NAME)) {
			String taskName = command.getParam().get(ParamEnum.NAME).get(0);
			task.setName(taskName);
		}
	}

	private static void setDueDateFromCommand(Command command, Task task)
			throws InvalidDateFormatException {
		if (command.getParam().containsKey(ParamEnum.DUE_DATE)) {
			Calendar dueDate = DateParser.parseString(command.getParam()
					.get(ParamEnum.DUE_DATE).get(0));
			task.setDateDue(dueDate);
		}
	}

	private static void setStartDateFromCommand(Command command, Task task)
			throws InvalidDateFormatException {
		if (command.getParam().containsKey(ParamEnum.START_DATE)) {
			Calendar startDate = DateParser.parseString(command.getParam()
					.get(ParamEnum.START_DATE).get(0));
			task.setDateStart(startDate);
		}
	}

	private static void setLevelFromCommand(Command command, Task task) {
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

	private static void setNoteFromCommand(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.NOTE)) {
			String note = command.getParam().get(ParamEnum.NOTE).get(0);
			task.setNote(note);
		}
	}

	private static void setTagsFromCommand(Command command, Task task) {
		if (command.getParam().containsKey(ParamEnum.TAG)) {
			ArrayList<String> tags = command.getParam().get(ParamEnum.TAG);
			task.setTags(tags);
		}
	}

}
