package common;

import java.util.ArrayList;

public class Feedback {

    private String feedbackMessage;
    private ArrayList<Task> taskList;
    private Task taskDisplay;

    public Feedback(String message, ArrayList<Task> tasks, Task task) {
        setFeedbackMessage(message);
        setTaskList(tasks);
        setTaskDisplay(task);
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

    public Task getTaskDisplay() {
        return taskDisplay;
    }

    public void setTaskDisplay(Task taskDisplay) {
        this.taskDisplay = taskDisplay;
    }
}
