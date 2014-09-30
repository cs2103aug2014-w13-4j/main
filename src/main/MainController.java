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
		// TODO: handle user command
		CommandParser commandParser = new CommandParser();
		String userInput = userInputField.getText();
		if (validateUserInput(userInput)){
			Command userCommand = commandParser.parseCommand(userInput);
			userInputField.clear();
		}
	}

	private boolean validateUserInput(String userInput){
		return (userInput != null && !userInput.isEmpty());
	}
}
