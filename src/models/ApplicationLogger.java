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
