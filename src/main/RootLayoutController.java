package main;

import command.CommandParser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.LogicApi;
import models.ApplicationLogger;
import models.Command;
import models.Feedback;
import models.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author szhlibrary
 */
public class RootLayoutController {
	LogicApi logicApi;

	private BorderPane rootLayout;

	private TaskListViewController taskListViewController;
	private TaskDisplayViewController taskDisplayViewController;
	private UserInputViewController userInputViewController;

	public void initialize(Stage primaryStage, Feedback allActiveTasks, LogicApi logicApi) throws IOException {
		setLogic(logicApi);
		initRootLayout(primaryStage);
		initTaskListView(allActiveTasks);
		initTaskDisplayView();
		initUserInputView(allActiveTasks);
	}

	private void setLogic(LogicApi logicApi) {
		this.logicApi = logicApi;
	}

	private void initRootLayout(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/RootLayout.fxml"));
		rootLayout = loader.load();

		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initTaskListView(Feedback allActiveTasks) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/TaskListView.fxml"));
		AnchorPane taskList = loader.load();

		rootLayout.setCenter(taskList);

		taskListViewController = loader.getController();
		taskListViewController.initialize(allActiveTasks);
	}

	private void initTaskDisplayView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/TaskDisplayView.fxml"));
		AnchorPane taskDisplay = loader.load();

		rootLayout.setRight(taskDisplay);

		taskDisplayViewController = loader.getController();
		taskDisplayViewController.initialize();
	}

	private void initUserInputView(Feedback allActiveTasks) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/UserInputView.fxml"));
		AnchorPane userInput = loader.load();

		rootLayout.setBottom(userInput);

		userInputViewController = loader.getController();
		userInputViewController.initialize(allActiveTasks, this);
	}

	protected void executeCommand(String userInput){
		CommandParser commandParser = new CommandParser();
		if (validateUserInput(userInput)) {
			try {
				Command userCommand = commandParser.parseCommand(userInput);
				Feedback userCommandFeedback = logicApi.executeCommand(userCommand);
				String feedbackMessage = userCommandFeedback.getFeedbackMessage();

				ApplicationLogger.getApplicationLogger().log(Level.INFO, "Message shown: " + feedbackMessage);

				ArrayList<Task> taskList = userCommandFeedback.getTaskList();
				if (taskList != null){
					taskListViewController.updateTaskList(taskList);
				}

				Task taskToDisplay = userCommandFeedback.getTaskDisplay();
				if (taskToDisplay != null){
					taskDisplayViewController.updateTaskPanel(taskToDisplay);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean validateUserInput(String userInput){
		return (userInput != null && !userInput.isEmpty());
	}
}
