package main;

import command.Command;
import command.CommandParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import logic.Logic;
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
	public Label taskNameLabel;
	public Label dueDateLabel;
	public TextField userInputField;
	public TableView<Task> taskTableView;

	public void handleUserInput() {
		CommandParser commandParser = new CommandParser();
		String userInput = userInputField.getText();
		if (validateUserInput(userInput)){
			try {
				Command userCommand = commandParser.parseCommand(userInput);
				Logic logic = new Logic();
				logic.initialize();
				Feedback userCommandFeedback = logic.executeCommand(userCommand);
				String feedbackMessage = userCommandFeedback.getFeedbackMessage();
				//System.out.println(feedbackMessage);

				// Updating UI
				userInputField.clear();
				taskTableView.getItems().clear();
				ArrayList<Task> taskList = userCommandFeedback.getTaskList();
				ObservableList<Task> observableList = FXCollections.observableArrayList(taskList);
				System.out.println(observableList.size());
				taskTableView.getItems().addAll(observableList);
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("failed");
			}
		} else {
			// TODO: Set feedbackMessage to "unknown input!" and show it to the user
		}
	}

	public boolean validateUserInput(String userInput){
		return (userInput != null && !userInput.isEmpty());
	}
}
