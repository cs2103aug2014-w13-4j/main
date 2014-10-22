package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The main method of the program; program execution starts here.
 * @author szhlibrary
 */
public class Main extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Scale window to display's DPI. Should maintain a consistent size
		// even on different displays. Method from:
		// http://news.kynosarges.org/2013/08/09/javafx-dpi-scaling/
		final double rem = Math.rint(new Text("").getLayoutBounds().getHeight());

		Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
		primaryStage.setTitle("Awesome Task Manager");
		primaryStage.setScene(new Scene(root, 60*rem, 45*rem));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
