package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Task {
	private int id = null;
	private String name;
	private Calendar dateDue;
	private Calendar dateStart;
	private Calendar dateEnd;
	private int priorityLevel;
	private String note;
	private ArrayList<String> tags;
	private ArrayList<Integer> parentTasks;
	private ArrayList<Integer> childTasks;
	private ArrayList<Integer> conditionalTasks;
	private boolean isDeleted = false;
	private boolean isConfirmed = false;
	
	public Task(String name, Calendar dateStart, Calendar dateDue, int priorityLevel) {
		this.name = name;
		this.setDateStart(dateStart);
		this.dateDue = dateDue;
		this.setPriorityLevel(priorityLevel);
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

	public void setPriorityLevel(int priorityLevel) {
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

	public void setConditionalTasks(ArrayList<Integer> conditionalTasks) {
		this.conditionalTasks = conditionalTasks;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setConfirmed(boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
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

	public int getPriorityLevel() {
		return priorityLevel;
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

	public ArrayList<Integer> getConditionalTasks() {
		return conditionalTasks;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean isConfirmed() {
		return isConfirmed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
