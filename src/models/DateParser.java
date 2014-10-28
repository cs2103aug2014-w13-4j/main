package models;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import exceptions.InvalidDateFormatException;

public class DateParser {
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
        return nattyDateParser(dateString);
    }

    private static Calendar nattyDateParser(String dateString)
            throws InvalidDateFormatException {
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(dateString);
        Calendar inputDate = null;
        for (DateGroup group : groups) {
            List<Date> dates = group.getDates();
            if (dates.size() > 1) {
                throw new InvalidDateFormatException("'" + dateString + "'"
                        + " is invalid!");
            } else {
                inputDate = new GregorianCalendar();
                inputDate.setTime(dates.get(0));
            }
        }

        return inputDate;
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