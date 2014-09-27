package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateParser {

	private static final int MONTH_REPRESENTATION = Calendar.SHORT;
	private static final String[] VALID_DATE_FORMATS = { "dd-MM-yyyy HH:mm a",
			"dd-MM-yyyy", "dd.MM.yyyy" };

	
	/**
	 * Reads a date in the string format, and returns its corresponding calendar
	 * representation
	 * 
	 * @param dateString
	 *            : date in string format
	 * @return the calendar object representing the date
	 */
	public static Calendar parseDate(String dateString) {
		for (int i = 0; i < VALID_DATE_FORMATS.length; i++) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(
						VALID_DATE_FORMATS[i]);
				Date date = formatter.parse(dateString);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				return calendar;
			} catch (ParseException e) {

			}
		}
		return null;
	}

	private static void testDate(String dateString) {
		Calendar myCalendar;
		if ((myCalendar = parseDate(dateString)) == null) {
			System.out.println("Invalid Date Format!");
		} else {
			int day = myCalendar.get(Calendar.DAY_OF_MONTH);
			String month = myCalendar.getDisplayName(Calendar.MONTH,
					MONTH_REPRESENTATION, Locale.getDefault());
			int year = myCalendar.get(Calendar.YEAR);
			int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = myCalendar.get(Calendar.MINUTE);
			String time = String.format("%02d:%02d", hour, minute);
			System.out.println(day + " " + month + " " + year + " " + time);
		}
	}

	public static void main(String[] args) throws ParseException {
		testDate("02-10-1999 11:00 am");
		testDate("02-10-1999");
		testDate("2.10.1999");
		testDate("2-10-1999");
		testDate("10-1-2001");
		testDate("2-10-2010 23:59"); // Date Format for time not fully
										// implemented yet
	}

}
