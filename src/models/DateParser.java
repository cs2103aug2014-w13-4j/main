package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {

	private static final String[] VALID_DATE_FORMATS = { "dd.MM.yyyy",
			"dd-MM-yyyy hh:mm", "dd-MM-yyyy" };
	private static final String STORE_DATE_FORMAT = "%d-%d-%d %02d:%02d";

	/**
	 * Reads a date in the string format, and returns its corresponding calendar
	 * representation
	 * 
	 * @param dateString
	 *            : date in string format
	 * @return the calendar object representing the date
	 */
	public static Calendar parseString(String dateString) {
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

	public static String parseCalendar(Calendar date) {
		int day = date.get(Calendar.DAY_OF_MONTH);
		int month = date.get(Calendar.MONTH) + 1;
		int year = date.get(Calendar.YEAR);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int minute = date.get(Calendar.MINUTE);
		return createString(day, month, year, hour, minute);

	}

	private static String createString(int day, int month, int year, int hour,
			int minute) {
		return String.format(STORE_DATE_FORMAT, day, month, year, hour, minute);
	}
	/**
	 * private static void testDate(String dateString) { Calendar myCalendar; if
	 * ((myCalendar = parseString(dateString)) == null) {
	 * System.out.println("Invalid Date Format!"); } else { int day =
	 * myCalendar.get(Calendar.DAY_OF_MONTH); String month =
	 * myCalendar.getDisplayName(Calendar.MONTH, MONTH_REPRESENTATION,
	 * Locale.getDefault()); int year = myCalendar.get(Calendar.YEAR); int hour
	 * = myCalendar.get(Calendar.HOUR_OF_DAY); int minute =
	 * myCalendar.get(Calendar.MINUTE); String time = String.format("%02d:%02d",
	 * hour, minute); System.out.println(day + " " + month + " " + year + " " +
	 * time); System.out.println(parseCalendar(myCalendar)); } }
	 * 
	 * public static void main(String[] args) throws ParseException {
	 * testDate("02-10-1999 11:00 am"); testDate("02-10-1999");
	 * testDate("2.10.1999"); testDate("2-10-1999"); testDate("10-1-2001");
	 * testDate("2-10-2010 23:59"); // Date Format for time not fully //
	 * implemented yet }
	 **/
}
