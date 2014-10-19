package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import exceptions.InvalidDateFormatException;

public class DateParser {

	private static final String[] VALID_DATE_FORMATS = { "dd.MM.yyyy",
			"dd-MM-yyyy HH:mm", "dd-MM-yyyy" };
	private static final String STORE_DATE_FORMAT = "%d-%d-%d %02d:%02d";

	/**
	 * Reads a date in the string format, and returns its corresponding calendar
	 * representation
	 * 
	 * @param dateString
	 *            : date in string format
	 * @return the calendar object representing the date
	 * @throws InvalidDateFormatException
	 */
	public static Calendar parseString(String dateString)
			throws InvalidDateFormatException {
		// TODO: Change this!
		if (dateString.isEmpty()) {
			return null;
		}
		for (int i = 0; i < VALID_DATE_FORMATS.length; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					VALID_DATE_FORMATS[i]);
			try {
				Date date = formatter.parse(dateString);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				return calendar;
			} catch (ParseException e) {
			}
		}
		throw new InvalidDateFormatException("'" + dateString + "'"
				+ " is invalid!");
	}

	public static String parseCalendar(Calendar date) {
		if (date != null) {
			int day = date.get(Calendar.DAY_OF_MONTH);
			int month = date.get(Calendar.MONTH) + 1;
			int year = date.get(Calendar.YEAR);
			int hour = date.get(Calendar.HOUR_OF_DAY);
			int minute = date.get(Calendar.MINUTE);
			return createString(day, month, year, hour, minute);
		} else {
			return null;
		}
	}

	private static String createString(int day, int month, int year, int hour,
			int minute) {
		return String.format(STORE_DATE_FORMAT, day, month, year, hour, minute);
	}
}
