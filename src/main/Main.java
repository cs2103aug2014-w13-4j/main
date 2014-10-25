package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.ApplicationLogger;

import java.io.IOException;
import java.util.logging.Level;

/**
 * The main method of the program; program execution starts here.
 * @author szhlibrary
 */
public class Main extends Application{

	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) throws Exception {
		ApplicationLogger.getApplicationLogger().log(Level.INFO, "Initializing JavaFX UI.");

		initPrimaryStage(primaryStage);
		initRootLayout();
		initTaskList();

//		Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
//		primaryStage.setTitle("Awesome Task Manager");
//		primaryStage.setScene(new Scene(root));
//		primaryStage.show();
	}

	private void initPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Awesome Task Manager");
	}

	public void initRootLayout(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("views/RootLayout.fxml"));
			rootLayout = loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initTaskList(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("main.fxml"));
			AnchorPane taskList = loader.load();

			rootLayout.setCenter(taskList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
