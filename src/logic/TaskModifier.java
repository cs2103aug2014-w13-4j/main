package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import models.DateParser;
import models.PriorityLevelEnum;
import models.StartDueDatePair;
import models.Task;
import command.Command;
import command.ParamEnum;
import exceptions.InvalidDateFormatException;

public class TaskModifier {

	static void modifyTask(Hashtable<ParamEnum, ArrayList<String>> param,
			Task task) throws InvalidDateFormatException {
		setNameFromCommand(param, task);
		setStartDateFromCommand(param, task);
		setDueDateFromCommand(param, task);
		setConditionalDatesFromCommand(param, task);
		setTagsFromCommand(param, task);
		setLevelFromCommand(param, task);
		setNoteFromCommand(param, task);
	}

	static void deleteTask(Task task) {
		task.setDeleted(true);
	}

	static void completeTask(Hashtable<ParamEnum, ArrayList<String>> param,
			Task task) throws InvalidDateFormatException {
		if (param.containsKey(ParamEnum.DATE)) {
			Calendar completedDate = DateParser.parseString(param.get(
					ParamEnum.DATE).get(0));
			task.setDateEnd(completedDate);
		} else {
			task.setDateEnd(Calendar.getInstance());
		}

	}

	private static void setConditionalDatesFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task)
			throws InvalidDateFormatException {
		/**
		 * if (command.getParam().containsKey(ParamEnum.ConditionalDates) {
		 * command.getParam().get(ParamEnum.ConditionalDates); }
		 **/
		String startDateFirst = "23.01.2014";
		String dueDateFirst = "23.10.2014";
		String startDateSecond = "23.02.2014";
		String dueDateSecond = "23.09.2014";

		StartDueDatePair firstPair = new StartDueDatePair(
				DateParser.parseString(startDateFirst),
				DateParser.parseString(dueDateFirst));
		StartDueDatePair secondPair = new StartDueDatePair(
				DateParser.parseString(startDateSecond),
				DateParser.parseString(dueDateSecond));

		ArrayList<StartDueDatePair> conditionalDates = new ArrayList<StartDueDatePair>();
		conditionalDates.add(firstPair);
		conditionalDates.add(secondPair);
		task.setConditionalDates(conditionalDates);
	}

	private static void setNameFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task) {
		if (param.containsKey(ParamEnum.NAME)) {
			String taskName = param.get(ParamEnum.NAME).get(0);
			task.setName(taskName);
		}
	}

	private static void setDueDateFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task)
			throws InvalidDateFormatException {
		if (param.containsKey(ParamEnum.DUE_DATE)) {
			Calendar dueDate = DateParser.parseString(param.get(
					ParamEnum.DUE_DATE).get(0));
			task.setDateDue(dueDate);
		}
	}

	private static void setStartDateFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task)
			throws InvalidDateFormatException {
		if (param.containsKey(ParamEnum.START_DATE)) {
			Calendar startDate = DateParser.parseString(param.get(
					ParamEnum.START_DATE).get(0));
			task.setDateStart(startDate);
		}
	}

	private static void setLevelFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task) {
		PriorityLevelEnum priorityEnum = null;
		if (param.containsKey(ParamEnum.LEVEL)) {
			try {
				int level = Integer.parseInt(param.get(ParamEnum.LEVEL).get(0));
				priorityEnum = PriorityLevelEnum.fromInteger(level);
			} catch (NumberFormatException | NullPointerException e) {
			}
			// TODO: Indicate error when invalid priority level is added
			task.setPriorityLevel(priorityEnum);
		}
	}

	private static void setNoteFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task) {
		if (param.containsKey(ParamEnum.NOTE)) {
			String note = param.get(ParamEnum.NOTE).get(0);
			task.setNote(note);
		}
	}

	private static void setTagsFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task) {
		if (param.containsKey(ParamEnum.TAG)) {
			ArrayList<String> tags = param.get(ParamEnum.TAG);
			task.setTags(tags);
		}
	}

}
