package main;

import command.CommandEnum;
import command.CommandParser;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import models.ApplicationLogger;
import models.Command;
import models.Feedback;
import models.Task;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author szhlibrary
 */
public class UserInputViewController {

	public TextField userInputField;
	private RootLayoutController rootLayoutController;

	private AutoCompletionBinding<String> autoCompletionBinding;
	private boolean autoCompleteCommandInitialized = false;
	private boolean autoCompleteSearchInitialized = false;

	public void initialize(Feedback initialTasks, RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
		initializeAutoComplete(initialTasks);
		setFocusToUserInputField();
	}

	private void initializeAutoCompleteForSearch(Feedback displayAllActiveTasks){
		if (!autoCompleteSearchInitialized){
			ArrayList<String> autoCompleteStringList = new ArrayList<String>();
			ArrayList<Task> taskList = displayAllActiveTasks.getTaskList();

			for (Task task : taskList){
				if (!task.isDeleted()){
					autoCompleteStringList.add("search name "+ task.getName());
				}
			}
			if (autoCompletionBinding != null){
				autoCompletionBinding.dispose();
			}
			autoCompletionBinding = TextFields.bindAutoCompletion(userInputField, autoCompleteStringList);
			autoCompleteSearchInitialized = true;
		}
	}

	private void initializeAutoComplete(Feedback displayAllActiveTasks){
		initializeAutoCompleteForCommands();
	}

	private void initializeAutoCompleteForCommands(){
		if (!autoCompleteCommandInitialized) {
			ArrayList<String> autoCompleteStringList = new ArrayList<String>();

			for (CommandEnum command : CommandEnum.values()) {
				autoCompleteStringList.add(String.valueOf(command).toLowerCase() + " ");
			}
			if (autoCompletionBinding != null) {
				autoCompletionBinding.dispose();
			}
			autoCompletionBinding = TextFields.bindAutoCompletion(userInputField, autoCompleteStringList);
			autoCompleteCommandInitialized = true;
		}
	}

	public void handleUserIncrementalInput(){
		String userInput = userInputField.getText();
		if (userInput.split(" ")[0].equalsIgnoreCase(String.valueOf(CommandEnum.SEARCH))){
			try{
				ApplicationLogger.getApplicationLogger().log(Level.INFO, "Sent to Command Parser: "+ userInput);
				CommandParser commandParser = new CommandParser();
				Command displayCommand = commandParser.parseCommand(String.valueOf(CommandEnum.DISPLAY));

				ApplicationLogger.getApplicationLogger().log(Level.INFO, "Sent to Logic: "+ userInput);
				Feedback displayCommandFeedback = rootLayoutController.logicApi.executeCommand(displayCommand);
				initializeAutoCompleteForSearch(displayCommandFeedback);
				autoCompleteCommandInitialized = false;
				rootLayoutController.executeCommand(userInputField.getText());
			} catch (Exception e) {
				ApplicationLogger.getApplicationLogger().log(Level.WARNING, e.getMessage());
			}
		} else {
			autoCompleteSearchInitialized = false;
			initializeAutoCompleteForCommands();
		}
		System.out.println(userInput);
	}

	private void setFocusToUserInputField(){
		userInputField.requestFocus();
	}

	public void handleUserInput() {
		rootLayoutController.executeCommand(userInputField.getText());
		userInputField.clear();
	}
}
