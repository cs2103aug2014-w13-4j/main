package models;

import java.util.ArrayList;
import java.util.Calendar;
import models.PriorityLevelEnum;

public class Task {
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
	private ArrayList<StartDueDatePair> conditionalDates;
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
	
	public void appendConditionalDates(ArrayList<StartDueDatePair> conditionalDates) {
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
			return (dateStart != null || dateEnd != null);
		} else {
			return true;
		}
	}
	
	public void setStartDueDateFromConditional(int id) {
		//conditional dates must be present to set start and due date
		//assume id starts counting from 1
		assert (conditionalDates != null && id >= conditionalDates.size());
		dateStart = conditionalDates.get(id - 1).getStartDate();
		dateEnd = conditionalDates.get(id - 1).getDueDate();
	}
	
	public void addTags(ArrayList<String> newTags) {
		newTags.removeAll(this.tags);
		this.tags.addAll(newTags);
	}
	
}
