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

	private ApplicationLogger() {
		try {
			logger = Logger.getLogger(this.getClass().getName());
			logger.addHandler(new FileHandler("application.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ApplicationLogger getInstance() {
		if (applicationLogger == null) {
			applicationLogger = new ApplicationLogger();
		}
		return applicationLogger;
	}
}
