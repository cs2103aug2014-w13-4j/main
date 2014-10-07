package main;

import command.Command;
import command.CommandParser;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import logic.Logic;
import models.DateParser;
import models.Feedback;
import models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Controller for the main GUI window.
 * @author szhlibrary
 */
public class MainController {
	Logic logic;

	public TextField userInputField;
	public TableView<Task> taskTableView;

	public Label idLabel;
	public Label taskNameLabel;
	public Label dueDateLabel;
	public Label startDateLabel;
	public Label endDateLabel;
	public Label priorityLevelLabel;
	public Label noteLabel;

	final StringProperty idLabelValue = new SimpleStringProperty("-");
	final StringProperty taskNameLabelValue = new SimpleStringProperty("-");
	final StringProperty dueDateLabelValue = new SimpleStringProperty("-");
	final StringProperty startDateLabelValue = new SimpleStringProperty("-");
	final StringProperty endDateLabelValue = new SimpleStringProperty("-");
	final StringProperty priorityLevelLabelValue = new SimpleStringProperty("-");
	final StringProperty noteLabelValue = new SimpleStringProperty("-");


	public void initialize(){
		System.out.println("Initializing...");
		CommandParser commandParser = new CommandParser();
		try {
			logic = new Logic();
			Feedback displayAllActiveTasks = logic.initialize();

			ArrayList<Task> taskList = displayAllActiveTasks.getTaskList();
			ObservableList<Task> observableList = FXCollections.observableArrayList(taskList);
			taskTableView.getItems().addAll(observableList);

			idLabel.textProperty().bind(idLabelValue);
			taskNameLabel.textProperty().bind(taskNameLabelValue);
			dueDateLabel.textProperty().bind(dueDateLabelValue);
			startDateLabel.textProperty().bind(startDateLabelValue);
			endDateLabel.textProperty().bind(endDateLabelValue);
			priorityLevelLabel.textProperty().bind(priorityLevelLabelValue);
			noteLabel.textProperty().bind(noteLabelValue);

			setFocusToUserInputField();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
					taskTableView.getItems().clear();
					ObservableList<Task> observableList = FXCollections.observableArrayList(taskList);
					taskTableView.getItems().addAll(observableList);
				}

				Task taskToDisplay = userCommandFeedback.getTaskDisplay();
				if (taskToDisplay != null){
					setLabelValueInGui(idLabelValue, Integer.toString(taskToDisplay.getId()));
					setLabelValueInGui(taskNameLabelValue, taskToDisplay.getName());
					setLabelValueInGui(dueDateLabelValue, DateParser.parseCalendar(taskToDisplay.getDateDue()));
					setLabelValueInGui(startDateLabelValue, DateParser.parseCalendar(taskToDisplay.getDateStart()));
					setLabelValueInGui(endDateLabelValue, DateParser.parseCalendar(taskToDisplay.getDateEnd()));
					setLabelValueInGui(priorityLevelLabelValue, (taskToDisplay.getPriorityLevel() == null ? null : taskToDisplay.getPriorityLevel().name()));
					setLabelValueInGui(noteLabelValue, taskToDisplay.getNote());
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

	private void setFocusToUserInputField(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				userInputField.requestFocus();
			}
		});
	}

	private void setLabelValueInGui(StringProperty labelValue, String value){
		labelValue.setValue(value != null && !value.isEmpty() ? value : "-");
	}
}
