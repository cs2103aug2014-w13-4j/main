package models;

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
	* constructor
	* This construtor follows the singleton pattern. It can only be called
	* via AppplicationLogger.getInstance(). This is to ensure that there is
	* only one instance of the ApplicationLogger object.
	*
	* @throws IOException
	*/
	private ApplicationLogger() throws IOException {
		logger = Logger.getLogger(this.getClass().getName());
		FileHandler fileHandler = new FileHandler("./logs/application.log");
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
	}

	private static ApplicationLogger getInstance() throws IOException {
		if (applicationLogger == null) {
			applicationLogger = new ApplicationLogger();
		}
		return applicationLogger;
	}

	public static Logger getApplicationLogger() throws IOException {
		return getInstance().logger;
	}
}
