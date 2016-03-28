package gr.watchful.packbuilder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder bldr = new StringBuilder();
		bldr.append(new SimpleDateFormat("[MM/dd hh:mm:ss] ").format(new Date(record.getMillis())));
		bldr.append(record.getLevel());
		bldr.append(": ");
		bldr.append(record.getMessage());
		bldr.append("\n");
		return bldr.toString();
	}
}
