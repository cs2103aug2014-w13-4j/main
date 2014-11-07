package main;

import javafx.application.Application;
import javafx.stage.Stage;
import logic.LogicApi;
import main.controllers.RootLayoutController;

import java.io.IOException;
import java.util.logging.Level;

import common.ApplicationLogger;
import common.Feedback;
import common.exceptions.FileFormatNotSupportedException;

/**
 * The main method of the program; program execution starts here.
 *
 * @author szhlibrary
 */
public class Main extends Application {
    private LogicApi logicApi;

    private Stage primaryStage;

    private RootLayoutController rootLayoutController;

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
            Feedback allActiveTasks = initLogicAndGetAllActiveTasks();
            rootLayoutController = new RootLayoutController();
            rootLayoutController.initialize(primaryStage, allActiveTasks,
                    logicApi);
        } catch (IOException e) {
            ApplicationLogger.getLogger().log(Level.SEVERE,
                    e.getMessage());
        }
    }

    private Feedback initLogicAndGetAllActiveTasks() throws IOException,
            FileFormatNotSupportedException {
        ApplicationLogger.getLogger().log(Level.INFO,
                "Initializing Logic.");
        logicApi = LogicApi.getInstance();
        return logicApi.displayAllActive();
    }
}
