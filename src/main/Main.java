package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main method of the program; program execution starts here.
 * @author szhlibrary
 */
public class Main extends Application{


	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
		primaryStage.setTitle("Awesome Task Manager");
		primaryStage.setScene(new Scene(root, 1024, 768));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
