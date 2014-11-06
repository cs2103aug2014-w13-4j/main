package common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import common.exceptions.InvalidDateFormatException;

public class DateParser {
    private static final String DISPLAY_DATE_FORMAT = "%d %s %02d:%02d";
    private static final String FULL_DISPLAY_DATE_FORMAT = "%d %s %d %02d:%02d";
    private static final String CONVERSION_DATE_FORMAT = "\\d\\d[\\\\\\-\\.]\\d\\d[\\\\\\-\\.]\\d{2}(?:\\d{2})?";
    private static final String CORRECT_DATE_FORMAT = "%1$s-%2$s-%3$s";
    private static final String DATE_SPLIT_FORMAT = "[\\\\\\-\\.]";
    private static final int MONTH = 0;
    private static final int DAY = 1;
    private static final int YEAR = 2;
    private static final int INVALID_FORMAT = 1;

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
        return nattyDateParser(formatDate(dateString));
    }

    /**
     * Parse a given date given in given natural language into its corresponding
     * calendar representation
     *
     * @param dateString
     *            : A string in any natural format representing a correct date
     * @return the calendar object representing the date
     * @throws InvalidDateFormatException
     */
    private static Calendar nattyDateParser(String dateString)
            throws InvalidDateFormatException {
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(dateString);
        Calendar inputDate = null;
        for (DateGroup group : groups) {
            List<Date> dates = group.getDates();
            if (dates.size() > INVALID_FORMAT) {
                throw new InvalidDateFormatException("'" + dateString + "'"
                        + " is invalid!");
            } else {
                inputDate = new GregorianCalendar();
                inputDate.setTime(dates.get(0));
            }
        }

        return inputDate;
    }

    /**
     * A function to parse a given calendar object into its date string format
     *
     * @param date
     *            : calendar object representing the intended date to parse
     * @return Date in its string format
     */
    public static String parseCalendar(Calendar date) {
        if (date != null) {
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            int day = date.get(Calendar.DAY_OF_MONTH);
            //int month = date.get(Calendar.MONTH) + 1;
            String month = new SimpleDateFormat("MMM").format(new Date(date.getTimeInMillis()));
            int year = date.get(Calendar.YEAR);
            int hour = date.get(Calendar.HOUR_OF_DAY);
            int minute = date.get(Calendar.MINUTE);

            if (thisYear == year) {
                return createStringWithoutDate(day, month, hour, minute);
            } else {
                return createString(day, month, year, hour, minute);
            }
        } else {
            return null;
        }
    }

    private static String createString(int day, String month, int year, int hour,
            int minute) {
        return String.format(FULL_DISPLAY_DATE_FORMAT, day, month, year, hour, minute);
    }

    private static String createStringWithoutDate(int day, String month, int hour,
                                                  int minute) {

        return String.format(DISPLAY_DATE_FORMAT, day, month, hour, minute);
    }

    /**
     * Format a date from dd-mm-yyyy format into natty acceptable date format
     *
     * @param date
     *            : string representation of a date
     * @return corrected date format
     */
    private static String formatDate(String date) {
        String correctedDate = "";
        Pattern pattern = Pattern.compile(CONVERSION_DATE_FORMAT);
        Matcher m = pattern.matcher(date);
        if (m.find()) {
            String[] dateComponents = date.split(DATE_SPLIT_FORMAT);
            correctedDate = String.format(CORRECT_DATE_FORMAT,
                    dateComponents[DAY], dateComponents[MONTH],
                    dateComponents[YEAR]);
        } else {
            correctedDate = date;
        }

        return correctedDate;
    }
}