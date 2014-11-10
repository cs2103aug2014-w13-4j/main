package main;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import common.ApplicationLogger;
import common.exceptions.FileFormatNotSupportedException;
import main.controllers.RootController;

//@author A0111010R
/**
 * The main method of the program; program execution starts here. In charge of
 * initializing the primary Stage, and also initializes the layouts via RootController.
 *
 * @author szhlibrary
 */
public class Main extends Application {
    private Stage primaryStage;

    private RootController rootController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationLogger.getLogger().log(Level.INFO,
                "Initializing JavaFX UI.");

        initPrimaryStage(primaryStage);
        initLayouts();
    }

    private void initPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Awesome Task Manager");
    }

    private void initLayouts() throws FileFormatNotSupportedException {
        assert (primaryStage != null);
        try {
            rootController = new RootController();
            rootController.initialize(primaryStage);
        } catch (IOException e) {
            ApplicationLogger.getLogger().log(Level.SEVERE,
                    e.getMessage());
        }
    }
}
