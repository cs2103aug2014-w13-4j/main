package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.LogicApi;
import models.ApplicationLogger;
import models.Feedback;

import java.io.IOException;
import java.util.logging.Level;

/**
 * @author szhlibrary
 */
public class RootLayoutController {
	LogicApi logic;

	private BorderPane rootLayout;

	private TaskListViewController taskListViewController;
	private TaskDisplayViewController taskDisplayViewController;
	private UserInputViewController userInputViewController;

	public void initialize(Stage primaryStage, Feedback allActiveTasks) throws IOException {
		initRootLayout(primaryStage);
		initTaskListView(allActiveTasks);
		initTaskDisplayView();
		initUserInputView(allActiveTasks);
	}

	public void initRootLayout(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/RootLayout.fxml"));
		rootLayout = loader.load();

		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void initTaskListView(Feedback allActiveTasks) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/TaskListView.fxml"));
		AnchorPane taskList = loader.load();

		rootLayout.setCenter(taskList);

		taskListViewController = loader.getController();
		taskListViewController.initialize(allActiveTasks);
	}

	public void initTaskDisplayView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/TaskDisplayView.fxml"));
		AnchorPane taskDisplay = loader.load();

		rootLayout.setRight(taskDisplay);

		taskDisplayViewController = loader.getController();
		taskDisplayViewController.initialize();
	}

	public void initUserInputView(Feedback allActiveTasks) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/UserInputView.fxml"));
		AnchorPane userInput = loader.load();

		rootLayout.setBottom(userInput);

		userInputViewController = loader.getController();
		userInputViewController.initialize(allActiveTasks);
	}
}
