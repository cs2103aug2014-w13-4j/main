package models;

import java.util.ArrayList;

public class Feedback {
	
	private String feedbackMessage;
	private ArrayList<Task> taskList;
	
	public Feedback(String message, ArrayList<Task> tasks) {
		setFeedbackMessage(message);
		setTaskList(tasks);
	}

	public ArrayList<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(ArrayList<Task> taskList) {
		this.taskList = taskList;
	}

	public String getFeedbackMessage() {
		return feedbackMessage;
	}

	public void setFeedbackMessage(String feedbackMessage) {
		this.feedbackMessage = feedbackMessage;
	}
	
	
}
