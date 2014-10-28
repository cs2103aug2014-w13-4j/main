package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import models.DateParser;
import models.StartDueDatePair;
import models.Task;

import java.util.ArrayList;

/**
 * @author szhlibrary
 */
public class TaskDisplayViewController {

	public Label idLabel;
	public Label taskNameLabel;
	public Label dueDateLabel;
	public Label startDateLabel;
	public Label endDateLabel;
	public Label priorityLevelLabel;
	public Label noteLabel;
	public Label conditionalDateLabel;

	private final StringProperty idLabelValue = new SimpleStringProperty("-");
	private final StringProperty taskNameLabelValue = new SimpleStringProperty("-");
	private final StringProperty dueDateLabelValue = new SimpleStringProperty("-");
	private final StringProperty startDateLabelValue = new SimpleStringProperty("-");
	private final StringProperty endDateLabelValue = new SimpleStringProperty("-");
	private final StringProperty priorityLevelLabelValue = new SimpleStringProperty("-");
	private final StringProperty noteLabelValue = new SimpleStringProperty("-");
	private final StringProperty conditionalDateLabelValue = new SimpleStringProperty("-");

	public void initialize() {
		initializeGuiLabelBindings();
	}

	private void initializeGuiLabelBindings() {
		idLabel.textProperty().bind(idLabelValue);
		taskNameLabel.textProperty().bind(taskNameLabelValue);
		dueDateLabel.textProperty().bind(dueDateLabelValue);
		startDateLabel.textProperty().bind(startDateLabelValue);
		endDateLabel.textProperty().bind(endDateLabelValue);
		priorityLevelLabel.textProperty().bind(priorityLevelLabelValue);
		noteLabel.textProperty().bind(noteLabelValue);
		conditionalDateLabel.textProperty().bind(conditionalDateLabelValue);
	}

	protected void updateTaskPanel(Task taskToDisplay) {
		setLabelValueInGui(idLabelValue, Integer.toString(taskToDisplay.getId()));
		setLabelValueInGui(taskNameLabelValue, taskToDisplay.getName());
		setLabelValueInGui(dueDateLabelValue, DateParser.parseCalendar(taskToDisplay.getDateDue()));
		setLabelValueInGui(startDateLabelValue, DateParser.parseCalendar(taskToDisplay.getDateStart()));
		setLabelValueInGui(endDateLabelValue, DateParser.parseCalendar(taskToDisplay.getDateEnd()));
		setLabelValueInGui(priorityLevelLabelValue, (taskToDisplay.getPriorityLevel() == null ? null : taskToDisplay.getPriorityLevel().name()));
		setLabelValueInGui(noteLabelValue, taskToDisplay.getNote());
		updateTaskPanelForConditionalDates(taskToDisplay);
	}

	private void updateTaskPanelForConditionalDates(Task taskToDisplay){
		ArrayList<StartDueDatePair> conditionalDateList = taskToDisplay.getConditionalDates();
		String conditionalDates = "";
		if (conditionalDateList != null) {
			int dateId = 0;
			for (StartDueDatePair conditionalDatePair : conditionalDateList) {
				conditionalDates += dateId + ": " + DateParser.parseCalendar(conditionalDatePair.getStartDate())
						+ " - " + DateParser.parseCalendar(conditionalDatePair.getDueDate())
						+ "\n";
				dateId++;
			}
			setLabelValueInGui(conditionalDateLabelValue, conditionalDates);
		}
	}

	private void setLabelValueInGui(StringProperty labelValue, String value){
		labelValue.setValue(value != null && !value.isEmpty() ? value : "-");
	}
}

