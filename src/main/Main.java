package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.LogicApi;
import models.ApplicationLogger;
import models.Feedback;
import models.Task;

import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;

/**
 * The main method of the program; program execution starts here.
 * @author szhlibrary
 */
public class Main extends Application{
	LogicApi logic;

	private Stage primaryStage;
	private BorderPane rootLayout;

	private TaskListViewController taskListViewController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		ApplicationLogger.getApplicationLogger().log(Level.INFO, "Initializing JavaFX UI.");

		initPrimaryStage(primaryStage);
		initLayouts();
	}

	private void initPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Awesome Task Manager");
	}

	public void initLayouts(){
		try {
			initRootLayout();
			initTaskListView();
			initTaskDisplayView();
			initUserInputView();
		} catch (IOException e) {
			ApplicationLogger.getApplicationLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	public void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/RootLayout.fxml"));
		rootLayout = loader.load();

		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void initTaskListView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		//loader.setLocation(Main.class.getResource("main.fxml"));
		loader.setLocation(Main.class.getResource("views/TaskListView.fxml"));
		AnchorPane taskList = loader.load();

		rootLayout.setCenter(taskList);

		taskListViewController = loader.getController();
		taskListViewController.initialize(initLogicAndGetAllActiveTasks());
	}

	private Feedback initLogicAndGetAllActiveTasks() {
		ApplicationLogger.getApplicationLogger().log(Level.INFO, "Initializing Logic.");
		logic = new LogicApi();
		return logic.initialize();
	}

	public void initTaskDisplayView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/TaskDisplayView.fxml"));
		AnchorPane taskDisplay = loader.load();

		rootLayout.setRight(taskDisplay);

		TaskDisplayViewController taskDisplayViewController = loader.getController();
		taskDisplayViewController.initialize();
	}

	public void initUserInputView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("views/UserInputView.fxml"));
		AnchorPane userInput = loader.load();

		rootLayout.setBottom(userInput);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
