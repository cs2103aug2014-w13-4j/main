package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import models.Command;
import models.DateParser;
import models.PriorityLevelEnum;
import models.StartDueDatePair;
import models.Task;
import command.ParamEnum;
import exceptions.InvalidDateFormatException;

public class TaskModifier {

	static void modifyTask(Hashtable<ParamEnum, ArrayList<String>> param,
			Task task) throws InvalidDateFormatException {
		setNameFromCommand(param, task);
		setTagsFromCommand(param, task);
		setLevelFromCommand(param, task);
		setNoteFromCommand(param, task);
		if (hasMultipleStartDates(param)) {
			assert hasSameNumberOfDueDates(param);
			setConditionalDatesFromCommand(param, task);
		} else {
			if (param.containsKey(ParamEnum.START_DATE)) {
				assert hasSingleStartDate(param);
				setStartDateFromCommand(param, task);
			}
			if (param.containsKey(ParamEnum.DUE_DATE)) {
				assert hasSingleDueDate(param);
				setDueDateFromCommand(param, task);
			}
		}
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
		ArrayList<String> startDates = param.get(ParamEnum.START_DATE);
		ArrayList<String> dueDates = param.get(ParamEnum.DUE_DATE);
		ArrayList<StartDueDatePair> conditionalDates = new ArrayList<StartDueDatePair>();
		for (int i = 0; i < startDates.size(); i++) {
			String startDate = startDates.get(i);
			String dueDate = dueDates.get(i);
			StartDueDatePair datePair = new StartDueDatePair(
					DateParser.parseString(startDate),
					DateParser.parseString(dueDate));
			conditionalDates.add(datePair);
		}
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
		Calendar dueDate = DateParser.parseString(param.get(ParamEnum.DUE_DATE)
				.get(0));
		task.setDateDue(dueDate);
	}

	private static void setStartDateFromCommand(
			Hashtable<ParamEnum, ArrayList<String>> param, Task task)
			throws InvalidDateFormatException {
		Calendar startDate = DateParser.parseString(param.get(
				ParamEnum.START_DATE).get(0));
		task.setDateStart(startDate);
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

	private static boolean hasSameNumberOfDueDates(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.containsKey(ParamEnum.DUE_DATE)
				&& param.get(ParamEnum.DUE_DATE).size() == param.get(
						ParamEnum.START_DATE).size();
	}

	private static boolean hasMultipleStartDates(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.containsKey(ParamEnum.START_DATE)
				&& param.get(ParamEnum.START_DATE).size() > 1;
	}

	private static boolean hasSingleDueDate(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.get(ParamEnum.DUE_DATE).size() == 1;
	}

	private static boolean hasSingleStartDate(
			Hashtable<ParamEnum, ArrayList<String>> param) {
		return param.get(ParamEnum.START_DATE).size() == 1;
	}

}
