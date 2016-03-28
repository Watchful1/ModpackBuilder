package gr.watchful.packbuilder.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class LogUtils {
	public static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void logException(String message, Exception e) {
		logger.warning(message);
		logger.fine(exceptionToString(e));
	}

	public static String exceptionToString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
