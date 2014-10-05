package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import models.PriorityLevelEnum;

public class Task {
	private static final String MESSAGE_SEPARATOR = "\tL@L";
	
	private int id;
	private String name;
	private Calendar dateDue;
	private Calendar dateStart;
	private Calendar dateEnd;
	private PriorityLevelEnum priorityLevel;
	private String note;
	private ArrayList<String> tags;
	private ArrayList<Integer> parentTasks;
	private ArrayList<Integer> childTasks;
	private ArrayList<StartEndDatePair> conditionalDates;
	private boolean isDeleted;
	private boolean isConfirmed;
	public TreeMap<TaskAttributeEnum, String> taskAttributes;
	
	public Task() {
		taskAttributes = new TreeMap<TaskAttributeEnum, String>();
		for (TaskAttributeEnum taskAttribute : TaskAttributeEnum.values()) {
			taskAttributes.put(taskAttribute, null);
		}
		setDeleted(false);
		setConfirmed(false);
	}

	public TreeMap<TaskAttributeEnum, String> getTaskAttributes() {
		return taskAttributes;
	}
	
	public void setName(String name) {
		this.name = name;
		this.taskAttributes.put(TaskAttributeEnum.NAME, name);
	}
	
	public void setDateDue(Calendar dateDue) {
		this.dateDue = dateDue;
		this.taskAttributes.put(TaskAttributeEnum.DATE_DUE, taskAttributeToString(dateDue));
	}
	
	public void setDateStart(Calendar dateStart) {
		this.dateStart = dateStart;
		this.taskAttributes.put(TaskAttributeEnum.DATE_START, taskAttributeToString(dateStart));
	}

	public void setPriorityLevel(PriorityLevelEnum priorityLevel) {
		this.priorityLevel = priorityLevel;
		this.taskAttributes.put(TaskAttributeEnum.PRIORITY_LEVEL, taskAttributeToString(priorityLevel.getLevel()));
	}

	public void setDateEnd(Calendar dateEnd) {
		this.dateEnd = dateEnd;
		this.taskAttributes.put(TaskAttributeEnum.DATE_END, taskAttributeToString(dateEnd));
	}

	public void setNote(String note) {
		this.note = note;
		this.taskAttributes.put(TaskAttributeEnum.NOTE, note);
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
		this.taskAttributes.put(TaskAttributeEnum.TAGS, stringArrayListToString(tags));
	}

	public void setParentTasks(ArrayList<Integer> parentTasks) {
		this.parentTasks = parentTasks;
		this.taskAttributes.put(TaskAttributeEnum.PARENT_TASKS, integerArrayListToString(parentTasks));
	}

	public void setChildTasks(ArrayList<Integer> childTasks) {
		this.childTasks = childTasks;
		this.taskAttributes.put(TaskAttributeEnum.CHILD_TASKS, integerArrayListToString(childTasks));
	}

	public void setConditionalTasks(ArrayList<StartEndDatePair> conditionalDates) {
		this.conditionalDates = conditionalDates;
		this.taskAttributes.put(TaskAttributeEnum.CONDITIONAL_DATES, datePairArrayListToString(conditionalDates));
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
		this.taskAttributes.put(TaskAttributeEnum.IS_DELETED, taskAttributeToString(isDeleted));
	}

	public void setConfirmed(boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
		this.taskAttributes.put(TaskAttributeEnum.IS_CONFIRMED, taskAttributeToString(isConfirmed));
	}

	public void setId(int id) {
		this.id = id;
		this.taskAttributes.put(TaskAttributeEnum.ID, taskAttributeToString(id));
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

	public ArrayList<StartEndDatePair> getConditionalDates() {
		return conditionalDates;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean isConfirmed() {
		return isConfirmed;
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
    private static String stringArrayListToString(ArrayList<String> stringArrayListAttribute) {
        if (stringArrayListAttribute == null) {
            return "";
        } else {
            return arrayListToString(stringArrayListAttribute);
        }
    }

    // convert integer array list task attribute to string
    private static String integerArrayListToString(ArrayList<Integer> intArrayListAttribute) {
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
    private static String datePairArrayListToString(ArrayList<StartEndDatePair> datePairAttribute) {
        if (datePairAttribute == null) {
            return "";
        } else {
            ArrayList<String> stringArrayAttribute = new ArrayList<String>();
            for (StartEndDatePair datePair : datePairAttribute) {
                stringArrayAttribute.add(taskAttributeToString(datePair.getStartDate()));
                stringArrayAttribute.add(taskAttributeToString(datePair.getDueDate()));
            }
            return stringArrayListToString(stringArrayAttribute);
        }
    }

    private static String arrayListToString(ArrayList<String> stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringArray) {
            // System.out.println(str + "LOL");
            stringBuilder.append(str + MESSAGE_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    
}
