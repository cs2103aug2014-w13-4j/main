package common;

import java.util.ArrayList;

//@author A0114368E

/**
 * This class is used to update the display in GUI following the action by the
 * user. It contains the feedback message that will be shown in the notification
 * pane, the task list that is shown in the table view, and the individual task
 * that is shown in the task display.
 */
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
