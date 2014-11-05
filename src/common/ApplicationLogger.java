package common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author szhlibrary
 */
public class ApplicationLogger {

    private Logger logger;
    private static ApplicationLogger applicationLogger;

    /*
     * constructor This construtor follows the singleton pattern. It can only be
     * called via AppplicationLogger.getInstance(). This is to ensure that there
     * is only one instance of the ApplicationLogger object.
     * 
     * @throws IOException
     */
    private ApplicationLogger() {
        logger = Logger.getLogger(this.getClass().getName());
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler("./logs/application.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ApplicationLogger getInstance() {
        if (applicationLogger == null) {
            applicationLogger = new ApplicationLogger();
        }
        return applicationLogger;
    }

    public static Logger getApplicationLogger() {
        return getInstance().logger;
    }
}
