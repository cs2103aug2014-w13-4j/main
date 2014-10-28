package main;

import javafx.application.Application;
import javafx.stage.Stage;
import logic.LogicApi;
import models.ApplicationLogger;
import models.Feedback;

import java.io.IOException;
import java.util.logging.Level;

/**
 * The main method of the program; program execution starts here.
 * @author szhlibrary
 */
public class Main extends Application{
	LogicApi logic;

	private Stage primaryStage;

	private RootLayoutController rootLayoutController;

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

	private void initLayouts(){
		assert(primaryStage != null);
		try {
			Feedback allActiveTasks = initLogicAndGetAllActiveTasks();
			rootLayoutController = new RootLayoutController();
			rootLayoutController.initialize(primaryStage, allActiveTasks, logic);
		} catch (IOException e) {
			ApplicationLogger.getApplicationLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	private Feedback initLogicAndGetAllActiveTasks() {
		ApplicationLogger.getApplicationLogger().log(Level.INFO, "Initializing Logic.");
		logic = new LogicApi();
		return logic.initialize();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
