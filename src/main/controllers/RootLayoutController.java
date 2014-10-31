package main.controllers;

import command.CommandParser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.LogicApi;
import main.Main;
import models.ApplicationLogger;
import models.Command;
import models.Feedback;
import models.Task;
import org.controlsfx.control.NotificationPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author szhlibrary
 */
public class RootLayoutController {
	protected LogicApi logicApi;

	private BorderPane rootLayout;
	private NotificationPane notificationPane;

	private TaskListViewController taskListViewController;
	private TaskDisplayViewController taskDisplayViewController;
	private UserInputViewController userInputViewController;
	
	private Scene scene;

	public void initialize(Stage primaryStage, Feedback allActiveTasks, LogicApi logicApi) throws IOException {
		setLogic(logicApi);
		initRootLayout(primaryStage);
		initScene();
		initNotificationPane();
		initTaskListView(allActiveTasks);
		initTaskDisplayView();
		initUserInputView(allActiveTasks);
		showStage(primaryStage);
	}

	private void setLogic(LogicApi logicApi) {
		this.logicApi = logicApi;
	}

	private void initRootLayout(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/RootLayout.fxml"));
		rootLayout = loader.load();
	}

	private void initNotificationPane() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/NotificationPaneWrapper.fxml"));
		notificationPane = loader.load();
		notificationPane.setShowFromTop(false);
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

		notificationPane.setContent(taskDisplay);
		rootLayout.setRight(notificationPane);

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

	private void showStage(Stage primaryStage) {
		assert (scene != null);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initScene() {
		scene = new Scene(rootLayout);
	}
	
	protected void executeCommand(String userInput) {
		CommandParser commandParser = new CommandParser();
		if (validateUserInput(userInput)) {
			try {
				Command userCommand = commandParser.parseCommand(userInput);
				Feedback userCommandFeedback = logicApi.executeCommand(userCommand);
				String feedbackMessage = userCommandFeedback.getFeedbackMessage();

				showNotification(feedbackMessage);

				ApplicationLogger.getApplicationLogger().log(Level.INFO, "Message shown: " + feedbackMessage);

				ArrayList<Task> taskList = userCommandFeedback.getTaskList();
				if (taskList != null) {
					taskListViewController.updateTaskList(taskList);
				}

				Task taskToDisplay = userCommandFeedback.getTaskDisplay();
				if (taskToDisplay != null) {
					taskDisplayViewController.updateTaskPanel(taskToDisplay);
				}
			} catch (Exception e) {
				showNotification(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void showNotification(String feedbackMessage) {
		notificationPane.setText(feedbackMessage);
		notificationPane.show();
	}

	private boolean validateUserInput(String userInput) {
		return (userInput != null && !userInput.isEmpty());
	}
}
