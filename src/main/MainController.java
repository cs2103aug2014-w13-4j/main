package main;

import command.CommandParser;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import logic.LogicApi;
import models.Command;
import models.DateParser;
import models.Feedback;
import models.StartDueDatePair;
import models.Task;

import java.util.ArrayList;

/**
 * Controller for the main GUI window.
 * @author szhlibrary
 */
public class MainController {
	LogicApi logic;

	public TextField userInputField;
	public TableView<Task> taskTableView;

	public Label idLabel;
	public Label taskNameLabel;
	public Label dueDateLabel;
	public Label startDateLabel;
	public Label endDateLabel;
	public Label priorityLevelLabel;
	public Label noteLabel;
	public Label conditionalDateLabel;

	final StringProperty idLabelValue = new SimpleStringProperty("-");
	final StringProperty taskNameLabelValue = new SimpleStringProperty("-");
	final StringProperty dueDateLabelValue = new SimpleStringProperty("-");
	final StringProperty startDateLabelValue = new SimpleStringProperty("-");
	final StringProperty endDateLabelValue = new SimpleStringProperty("-");
	final StringProperty priorityLevelLabelValue = new SimpleStringProperty("-");
	final StringProperty noteLabelValue = new SimpleStringProperty("-");
	final StringProperty conditionalDateLabelValue = new SimpleStringProperty("-");

	public void initialize(){
		System.out.println("Initializing...");
		CommandParser commandParser = new CommandParser();
		try {
			Feedback displayAllActiveTasks = initializeLogic();
			initializeGuiTaskList(displayAllActiveTasks);
			initializeGuiLabelBindings();
			setFocusToUserInputField();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Feedback initializeLogic() {
		logic = new LogicApi();
		return logic.initialize();
	}

	private void initializeGuiTaskList(Feedback displayAllActiveTasks) {
		ArrayList<Task> taskList = displayAllActiveTasks.getTaskList();
		ObservableList<Task> observableList = FXCollections.observableArrayList(taskList);
		taskTableView.getItems().addAll(observableList);
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

	private void setFocusToUserInputField(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				userInputField.requestFocus();
			}
		});
	}

	public void handleUserInput() {
		CommandParser commandParser = new CommandParser();
		String userInput = userInputField.getText();
		if (validateUserInput(userInput)){
			try {
				Command userCommand = commandParser.parseCommand(userInput);
				Feedback userCommandFeedback = logic.executeCommand(userCommand);
				String feedbackMessage = userCommandFeedback.getFeedbackMessage();
				System.out.println(feedbackMessage);

				ArrayList<Task> taskList = userCommandFeedback.getTaskList();
				if (taskList != null){
					updateTaskList(taskList);
				}

				Task taskToDisplay = userCommandFeedback.getTaskDisplay();
				if (taskToDisplay != null){
					updateTaskPanel(taskToDisplay);
				}
				userInputField.clear();
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("failed");
			}
		} else {
			// TODO: Set feedbackMessage to "unknown input!" and show it to the user
		}
	}

	private boolean validateUserInput(String userInput){
		return (userInput != null && !userInput.isEmpty());
	}

	private void updateTaskList(ArrayList<Task> taskList) {
		taskTableView.getItems().clear();
		ObservableList<Task> observableList = FXCollections.observableArrayList(taskList);
		taskTableView.getItems().addAll(observableList);
	}

	private void updateTaskPanel(Task taskToDisplay) {
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
