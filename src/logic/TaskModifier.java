package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import models.DateParser;
import models.PriorityLevelEnum;
import models.StartDueDatePair;
import models.Task;
import command.ParamEnum;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;

public class TaskModifier {
	private static final String INVALID_CONDITIONAL_DATE_ID_MESSAGE = "The conditional date id is invalid.";
	private static final int MIN_ID = 0;
	private static final String INVALID_CONFIRMED_TASK_MESSAGE = "The task is already confirmed.";
	
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

	static void confirmTask(int dateId, Task task) throws InvalidInputException {
		if (isLessThanMinId(dateId) || hasNullConditionalDates(task)
				|| isIdOutsideConditionalDatesRange(dateId, task)) {
			throw new InvalidInputException(INVALID_CONDITIONAL_DATE_ID_MESSAGE);
		} else if (task.isConfirmed()) {
			throw new InvalidInputException(INVALID_CONFIRMED_TASK_MESSAGE);
		} else {
			StartDueDatePair conditionalDatesToConfirm = task
					.getConditionalDates().get(dateId);
			Calendar startDate = conditionalDatesToConfirm.getStartDate();
			task.setDateStart(startDate);
			System.out.println ("Test:" + task.getDateStart() == null);
			Calendar dueDate = conditionalDatesToConfirm.getDueDate();
			task.setDateDue(dueDate);
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

	private static boolean isIdOutsideConditionalDatesRange(int dateId, Task task) {
		return task.getConditionalDates().size() <= dateId;
	}

	private static boolean hasNullConditionalDates(Task task) {
		return task.getConditionalDates() == null;
	}

	private static boolean isLessThanMinId(int dateId) {
		return dateId < MIN_ID;
	}

}
