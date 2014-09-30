package main;

import command.Command;
import command.CommandParser;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the main GUI window.
 * @author szhlibrary
 */
public class MainController {
	public Label taskNameLabel;
	public Label dueDateLabel;
	public TextField userInputField;

	public void handleUserInput() {
		CommandParser commandParser = new CommandParser();
		String userInput = userInputField.getText();
		if (validateUserInput(userInput)){
			Command userCommand = commandParser.parseCommand(userInput);
			// Logic logic = new Logic();
			// Feedback userCommandFeedback = logic.executeCommand(userCommand);
			// TODO: Parse Feedback
			
			userInputField.clear();
		}
	}

	private boolean validateUserInput(String userInput){
		return (userInput != null && !userInput.isEmpty());
	}
}
