package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeMap;

import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import models.PriorityLevelEnum;

public class Task {
	private static final String MESSAGE_SEPARATOR = "\tT@T";

	private int id = -1;
	private String name = "";
	private Calendar dateDue = null;
	private Calendar dateStart = null;
	private Calendar dateEnd = null;
	private PriorityLevelEnum priorityLevel = PriorityLevelEnum.DEFAULT;
	private String note = "";
	private ArrayList<String> tags = new ArrayList<String>();
	private ArrayList<Integer> parentTasks = new ArrayList<Integer>();
	private ArrayList<Integer> childTasks = new ArrayList<Integer>();
	public TreeMap<TaskAttributeEnum, String> taskAttributes;
	private ArrayList<StartDueDatePair> conditionalDates = new ArrayList<StartDueDatePair>();
	private boolean isDeleted = false;

	public Task() {
		taskAttributes = new TreeMap<TaskAttributeEnum, String>();
		for (TaskAttributeEnum taskAttribute : TaskAttributeEnum.values()) {
			taskAttributes.put(taskAttribute, "");
		}
		setDeleted(false);
	}

	public TreeMap<TaskAttributeEnum, String> getTaskAttributes() {
		return taskAttributes;
	}

	public void setTaskAttributes(
			TreeMap<TaskAttributeEnum, String> taskAttributes)
			throws ParseException, InvalidDateFormatException {
		// check length of tree map first
		setId(Integer.valueOf(taskAttributes.get(TaskAttributeEnum.ID)));
		setName(taskAttributes.get(TaskAttributeEnum.NAME));
		setDateDue(stringToTaskProperty(taskAttributes
				.get(TaskAttributeEnum.DATE_DUE)));
		setDateStart(stringToTaskProperty(taskAttributes
				.get(TaskAttributeEnum.DATE_START)));
		setDateEnd(stringToTaskProperty(taskAttributes
				.get(TaskAttributeEnum.DATE_END)));
		if (!taskAttributes.get(TaskAttributeEnum.PRIORITY_LEVEL).isEmpty()) {
			setPriorityLevel(PriorityLevelEnum.fromInteger(Integer
					.valueOf(taskAttributes
							.get(TaskAttributeEnum.PRIORITY_LEVEL))));
		}
		setNote(taskAttributes.get(TaskAttributeEnum.NOTE));
		setDeleted(Boolean.valueOf(taskAttributes
				.get(TaskAttributeEnum.IS_DELETED)));
		setTags(stringToStringArrayList(taskAttributes
				.get(TaskAttributeEnum.TAGS)));
		setParentTasks(stringToIntegerArrayList(taskAttributes
				.get(TaskAttributeEnum.PARENT_TASKS)));
		setChildTasks(stringToIntegerArrayList(taskAttributes
				.get(TaskAttributeEnum.CHILD_TASKS)));
		setConditionalDates(stringToDatePairArrayList(taskAttributes
				.get(TaskAttributeEnum.CONDITIONAL_DATES)));
	}

	public void setName(String name) {
		this.name = name;
		this.taskAttributes.put(TaskAttributeEnum.NAME, name);
	}

	public void setDateDue(Calendar dateDue) {
		this.dateDue = dateDue;
		this.taskAttributes.put(TaskAttributeEnum.DATE_DUE,
				taskAttributeToString(dateDue));
	}

	public void setDateStart(Calendar dateStart) {
		this.dateStart = dateStart;
		this.taskAttributes.put(TaskAttributeEnum.DATE_START,
				taskAttributeToString(dateStart));
	}

	public void setPriorityLevel(PriorityLevelEnum priorityLevel) {
		this.priorityLevel = priorityLevel;
		this.taskAttributes.put(TaskAttributeEnum.PRIORITY_LEVEL,
				taskAttributeToString(priorityLevel.getLevel()));
	}

	public void setDateEnd(Calendar dateEnd) {
		this.dateEnd = dateEnd;
		this.taskAttributes.put(TaskAttributeEnum.DATE_END,
				taskAttributeToString(dateEnd));
	}

	public void setNote(String note) {
		this.note = note;
		this.taskAttributes.put(TaskAttributeEnum.NOTE, note);
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
		this.taskAttributes.put(TaskAttributeEnum.TAGS,
				stringArrayListToString(tags));
	}

	public void setParentTasks(ArrayList<Integer> parentTasks) {
		this.parentTasks = parentTasks;
		this.taskAttributes.put(TaskAttributeEnum.PARENT_TASKS,
				integerArrayListToString(parentTasks));
	}

	public void setChildTasks(ArrayList<Integer> childTasks) {
		this.childTasks = childTasks;
		this.taskAttributes.put(TaskAttributeEnum.CHILD_TASKS,
				integerArrayListToString(childTasks));
	}

	public void setConditionalDates(ArrayList<StartDueDatePair> conditionalDates) {
		this.conditionalDates = conditionalDates;
		this.taskAttributes.put(TaskAttributeEnum.CONDITIONAL_DATES,
				datePairArrayListToString(conditionalDates));
	}

	public void appendConditionalDates(
			ArrayList<StartDueDatePair> conditionalDates) {
		this.conditionalDates.addAll(conditionalDates);
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
		this.taskAttributes.put(TaskAttributeEnum.IS_DELETED,
				taskAttributeToString(isDeleted));
	}

	public void setId(int id) {
		this.id = id;
		this.taskAttributes
				.put(TaskAttributeEnum.ID, taskAttributeToString(id));
	}

	public String getName() {
		return name;
	}

	public Calendar getDateDue() {
		return dateDue;
	}

	public Calendar getDateStart() {
		return dateStart;
	}

	public Calendar getDateEnd() {
		return dateEnd;
	}

	public int getId() {
		return id;
	}

	public PriorityLevelEnum getPriorityLevel() {
		return priorityLevel;
	}

	public Integer getPriorityLevelInteger() {
		if (priorityLevel == null) {
			return null;
		} else {
			return priorityLevel.getLevel();
		}
	}

	public String getNote() {
		return note;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public ArrayList<Integer> getParentTasks() {
		return parentTasks;
	}

	public ArrayList<Integer> getChildTasks() {
		return childTasks;
	}

	public ArrayList<StartDueDatePair> getConditionalDates() {
		return conditionalDates;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean isConfirmed() {
		if (conditionalDates != null) {
			return (dateStart != null || dateDue != null);
		} else {
			return true;
		}
	}

	public void setStartDueDateFromConditional(int id) {
		// conditional dates must be present to set start and due date
		// assume id starts counting from 1
		assert (conditionalDates != null && id >= conditionalDates.size());
		dateStart = conditionalDates.get(id - 1).getStartDate();
		dateEnd = conditionalDates.get(id - 1).getDueDate();
	}

	public void addTags(ArrayList<String> newTags) {
		newTags.removeAll(this.tags);
		this.tags.addAll(newTags);
	}

	// convert int task attribute to string
	private static String taskAttributeToString(Integer intAttribute) {
		return intAttribute.toString();
	}

	// convert boolean task attribute to string
	private static String taskAttributeToString(Boolean booleanAttribute) {
		return booleanAttribute.toString();
	}

	// convert calendar task attribute to string
	private static String taskAttributeToString(Calendar calendarAttribute) {
		if (calendarAttribute == null) {
			return "";
		} else {
			return DateParser.parseCalendar(calendarAttribute);
		}
	}

	// convert string array list task attribute to string
	private static String stringArrayListToString(
			ArrayList<String> stringArrayListAttribute) {
		if (stringArrayListAttribute == null) {
			return "";
		} else {
			return arrayListToString(stringArrayListAttribute);
		}
	}

	// convert integer array list task attribute to string
	private static String integerArrayListToString(
			ArrayList<Integer> intArrayListAttribute) {
		if (intArrayListAttribute == null) {
			return "";
		} else {
			ArrayList<String> stringArrayAttribute = new ArrayList<String>();
			for (int intAttribute : intArrayListAttribute) {
				stringArrayAttribute.add(taskAttributeToString(intAttribute));
			}
			return stringArrayListToString(stringArrayAttribute);
		}
	}

	// convert start end date pair array list task attribute to string
	private static String datePairArrayListToString(
			ArrayList<StartDueDatePair> datePairAttribute) {
		if (datePairAttribute == null) {
			return "";
		} else {
			ArrayList<String> stringArrayAttribute = new ArrayList<String>();
			for (StartDueDatePair datePair : datePairAttribute) {
				stringArrayAttribute.add(taskAttributeToString(datePair
						.getStartDate()));
				stringArrayAttribute.add(taskAttributeToString(datePair
						.getDueDate()));
			}
			return stringArrayListToString(stringArrayAttribute);
		}
	}

	private static String arrayListToString(ArrayList<String> stringArray) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String str : stringArray) {
			stringBuilder.append(str + MESSAGE_SEPARATOR);
		}
		return stringBuilder.toString();
	}

	private static Calendar stringToTaskProperty(String propertyString)
			throws ParseException, InvalidDateFormatException {
		if (propertyString.equals("")) {
			return null;
		} else {
			return DateParser.parseString(propertyString);
		}
	}

	private ArrayList<String> stringToStringArrayList(String stringProperty) {
		if (stringProperty.equals("")) {
			return null;
		}
		String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
		return (ArrayList<String>) Arrays.asList(stringArray);
	}

	private ArrayList<Integer> stringToIntegerArrayList(String stringProperty) {
		if (stringProperty.equals("")) {
			return null;
		}
		String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
		ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
		for (String stringElement : stringArray) {
			integerArrayList.add(Integer.valueOf(stringElement));
		}
		return integerArrayList;
	}

	private ArrayList<StartDueDatePair> stringToDatePairArrayList(
			String stringProperty) throws ParseException,
			InvalidDateFormatException {
		if (stringProperty.equals("")) {
			return null;
		}
		String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
		ArrayList<StartDueDatePair> datePairArrayList = new ArrayList<StartDueDatePair>();
		Calendar startDate = null;
		Calendar endDate;
		for (String stringElement : stringArray) {
			if (startDate == null) {
				startDate = stringToTaskProperty(stringElement);
			} else {
				endDate = stringToTaskProperty(stringElement);
				StartDueDatePair datePair = new StartDueDatePair(startDate,
						endDate);
				datePairArrayList.add(datePair);
				startDate = null;
			}
		}
		return datePairArrayList;
	}
}
