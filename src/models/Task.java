package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeMap;

import org.hamcrest.internal.ArrayIterator;

import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import models.PriorityLevelEnum;

public class Task {
	private static final String MESSAGE_SEPARATOR = "\tT@T";
	private static final String MESSAGE_NULL = "";

	private int id = -1;
	private String name = null;
	private Calendar dateDue = null;
	private Calendar dateStart = null;
	private Calendar dateEnd = null;
	private PriorityLevelEnum priorityLevel = null;
	private String note = null;
	private ArrayList<String> tags = null;
	private ArrayList<Integer> parentTasks = null;
	private ArrayList<Integer> childTasks = null;
	public TreeMap<TaskAttributeEnum, String> taskAttributes = null;
	private ArrayList<StartDueDatePair> conditionalDates = null;

	private boolean isDeleted = false;

	public Task() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDateDue(Calendar dateDue) {
		this.dateDue = dateDue;
		
	}

	public void setDateStart(Calendar dateStart) {
		this.dateStart = dateStart;
		
	}

	public void setPriorityLevel(PriorityLevelEnum priorityLevel) {
		this.priorityLevel = priorityLevel;
		
	}

	public void setDateEnd(Calendar dateEnd) {
		this.dateEnd = dateEnd;
		
	}

	public void setNote(String note) {
		this.note = note;
		
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
		
	}

	public void setParentTasks(ArrayList<Integer> parentTasks) {
		this.parentTasks = parentTasks;
		
	}

	public void setChildTasks(ArrayList<Integer> childTasks) {
		this.childTasks = childTasks;
		
	}

	public void setConditionalDates(ArrayList<StartDueDatePair> conditionalDates) {
		this.conditionalDates = conditionalDates;
		
	}

	public void appendConditionalDates(
			ArrayList<StartDueDatePair> conditionalDates) {
		this.conditionalDates.addAll(conditionalDates);
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
		
	}

	public void setId(int id) {
		this.id = id;
		
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

	public TreeMap<TaskAttributeEnum, String> getTaskAttributes() {
		taskAttributes = new TreeMap<TaskAttributeEnum, String>();
		for (TaskAttributeEnum taskAttribute : TaskAttributeEnum.values()) {
			taskAttributes.put(taskAttribute, "");
		}

		taskAttributes.put(TaskAttributeEnum.NAME, taskAttributeToString(name));
		taskAttributes.put(TaskAttributeEnum.DATE_DUE,
				taskAttributeToString(dateDue));
		taskAttributes.put(TaskAttributeEnum.DATE_START,
				taskAttributeToString(dateStart));
		taskAttributes.put(TaskAttributeEnum.PRIORITY_LEVEL,
				taskAttributeToString(priorityLevel.getLevel()));
		taskAttributes.put(TaskAttributeEnum.DATE_END,
				taskAttributeToString(dateEnd));
		taskAttributes.put(TaskAttributeEnum.NOTE, taskAttributeToString(note));
		taskAttributes.put(TaskAttributeEnum.TAGS,
				stringArrayListToString(tags));
		taskAttributes.put(TaskAttributeEnum.PARENT_TASKS,
				integerArrayListToString(parentTasks));
		taskAttributes.put(TaskAttributeEnum.CHILD_TASKS,
				integerArrayListToString(childTasks));
		taskAttributes.put(TaskAttributeEnum.CONDITIONAL_DATES,
				datePairArrayListToString(conditionalDates));
		taskAttributes.put(TaskAttributeEnum.IS_DELETED,
				taskAttributeToString(isDeleted));
		taskAttributes
				.put(TaskAttributeEnum.ID, taskAttributeToString(id));
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

	private static String taskAttributeToString(String stringAttribute) {
		if (stringAttribute == null) {
			return MESSAGE_NULL;
		} else {
			return stringAttribute.toString();
		}
	}

	// convert int task attribute to string
	private static String taskAttributeToString(Integer intAttribute) {
		if (intAttribute == null) {
			return MESSAGE_NULL;
		} else {
			return intAttribute.toString();
		}
	}

	// convert boolean task attribute to string
	private static String taskAttributeToString(Boolean booleanAttribute) {
		if (booleanAttribute == null) {
			return MESSAGE_NULL;
		} else {
			return booleanAttribute.toString();
		}
	}

	// convert calendar task attribute to string
	private static String taskAttributeToString(Calendar calendarAttribute) {
		if (calendarAttribute == null) {
			return MESSAGE_NULL;
		} else {
			return DateParser.parseCalendar(calendarAttribute);
		}
	}

	// convert string array list task attribute to string
	private static String stringArrayListToString(
			ArrayList<String> stringArrayListAttribute) {
		if (stringArrayListAttribute == null) {
			return MESSAGE_NULL;
		} else {
			return arrayListToString(stringArrayListAttribute);
		}
	}

	// convert integer array list task attribute to string
	private static String integerArrayListToString(
			ArrayList<Integer> intArrayListAttribute) {
		if (intArrayListAttribute == null) {
			return MESSAGE_NULL;
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
			return MESSAGE_NULL;
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
		if (propertyString.equals(MESSAGE_NULL)) {
			return null;
		} else {
			return DateParser.parseString(propertyString);
		}
	}

	private ArrayList<String> stringToStringArrayList(String stringProperty) {
		if (stringProperty.equals(MESSAGE_NULL)) {
			return null;
		}
		String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
		return (ArrayList<String>) Arrays.asList(stringArray);
	}

	private ArrayList<Integer> stringToIntegerArrayList(String stringProperty) {
		if (stringProperty.equals(MESSAGE_NULL)) {
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
		if (stringProperty.equals(MESSAGE_NULL)) {
			return null;
		}
		String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
		ArrayList<StartDueDatePair> datePairArrayList = new ArrayList<StartDueDatePair>();
		Calendar startDate = null;
		Calendar endDate;
		for (int i=0; i<stringArray.length; i++) {
			if (isEven(i)) {
				startDate = stringToTaskProperty(stringArray[i]);
			} else {
				endDate = stringToTaskProperty(stringArray[i]);
				StartDueDatePair datePair = new StartDueDatePair(startDate,endDate);
            	datePairArrayList.add(datePair);
			}
		}
		return datePairArrayList;
	}

	private boolean isEven(int number) {
		if (number % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}
}
