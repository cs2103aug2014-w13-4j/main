package models;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

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
		logger.addHandler(new FileHandler("application.log"));
	}

	public static ApplicationLogger getInstance() throws IOException {
		if (applicationLogger == null) {
			applicationLogger = new ApplicationLogger();
		}
		return applicationLogger;
	}
}
