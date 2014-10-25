package models;

import java.util.ArrayList;
import java.util.Calendar;
import models.PriorityLevelEnum;

public class Task {
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
	private ArrayList<StartDueDatePair> conditionalDates = new ArrayList<StartDueDatePair>();
	private boolean isDeleted = false;

	public Task() {
	}

	public void addTags(ArrayList<String> newTags) {
		newTags.removeAll(this.tags);
		this.tags.addAll(newTags);
	}

	public void appendConditionalDates(
			ArrayList<StartDueDatePair> conditionalDates) {
		this.conditionalDates.addAll(conditionalDates);
	}

	public ArrayList<Integer> getChildTasks() {
		return childTasks;
	}

	public ArrayList<StartDueDatePair> getConditionalDates() {
		return conditionalDates;
	}

	public Calendar getDateDue() {
		return dateDue;
	}

	public Calendar getDateEnd() {
		return dateEnd;
	}

	public Calendar getDateStart() {
		return dateStart;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getNote() {
		return note;
	}

	public ArrayList<Integer> getParentTasks() {
		return parentTasks;
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

	public ArrayList<String> getTags() {
		return tags;
	}

	public boolean isCompleted() {
		Boolean isEventCompleted = isNull(dateDue) && isDateEndOver();
		Boolean isTaskCompleted = !isNull(dateDue) && !isNull(dateEnd);
		return isEventCompleted || isTaskCompleted;
	}

	private boolean isNull(Calendar date) {
		return date == null;
	}

	private boolean isDateEndOver() {
		return dateEnd.compareTo(Calendar.getInstance()) <= 0;
	}

	public boolean isConfirmed() {
		if (conditionalDates != null && !conditionalDates.isEmpty()) {
			return (dateStart != null || dateEnd != null);
		} else {
			return true;
		}
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setChildTasks(ArrayList<Integer> childTasks) {
		this.childTasks = childTasks;
	}

	public void setConditionalDates(ArrayList<StartDueDatePair> conditionalDates) {
		this.conditionalDates = conditionalDates;
	}

	public void setDateDue(Calendar dateDue) {
		this.dateDue = dateDue;
	}

	public void setDateEnd(Calendar dateEnd) {
		this.dateEnd = dateEnd;
	}

	public void setDateStart(Calendar dateStart) {
		this.dateStart = dateStart;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setParentTasks(ArrayList<Integer> parentTasks) {
		this.parentTasks = parentTasks;
	}

	public void setPriorityLevel(PriorityLevelEnum priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public void setStartDueDateFromConditional(int id) {
		// conditional dates must be present to set start and due date
		// assume id starts counting from 1
		assert (conditionalDates != null && id >= conditionalDates.size());
		dateStart = conditionalDates.get(id - 1).getStartDate();
		dateEnd = conditionalDates.get(id - 1).getDueDate();
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
}
