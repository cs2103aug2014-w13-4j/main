package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateParser {
	
	private static String[] validDateFormats = {"dd-MM-yyyy HH:mm a", "dd-MM-yyyy", "dd.MM.yyyy"};
	
	public static Calendar parseDate(String dateString) {
		for (int i = 0; i < validDateFormats.length; i++) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(validDateFormats[i]);
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
			String month = myCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			int year = myCalendar.get(Calendar.YEAR);
			int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = myCalendar.get(Calendar.MINUTE);
			System.out.println(day + " " + month + " " + year +" " + hour + " "+  minute);
		}
	}

	public static void main (String[] args) throws ParseException {
		testDate("02-10-1999 11:00 am");
		testDate("02-10-1999");
		testDate("2.10.1999");
		testDate("2-10-1999");
		testDate("10-1-2001");
		testDate("2-10-2010 23:59");
	}

}
