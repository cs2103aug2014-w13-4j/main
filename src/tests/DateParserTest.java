package tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import common.DateParser;
import common.exceptions.InvalidDateFormatException;

//@author A0111010R
public class DateParserTest {

    @Test
    public final void parseCalendarTest() throws InvalidDateFormatException {
        Calendar date;
        String dateString;
        String expectedDateString;

        dateString = DateParser.parseCalendar(null);
        expectedDateString = null;
        assertEquals("Null input should result in null output", expectedDateString, dateString);

        date = DateParser.parseString("20-10-1999 23:00");
        dateString = DateParser.parseCalendar(date);
        expectedDateString = "20 Oct 1999 23:00";
        assertEquals("Date seems to be parsed wrongly", expectedDateString, dateString);

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        date = DateParser.parseString("20-10-" + thisYear + " 23:00");
        dateString = DateParser.parseCalendar(date);
        expectedDateString = "20 Oct 23:00";
        assertEquals("Date this year seems to be parsed wrongly", expectedDateString, dateString);
    }

    @Test
    public final void parseStringTest() throws InvalidDateFormatException {
        String dateString = "31-12-2014";
        Calendar calendarDate = DateParser.parseString(dateString);
        Calendar expectedCalendarDate = new GregorianCalendar();
        expectedCalendarDate.set(2014, Calendar.DECEMBER, 31);
        assertEquals("Parsed date doesn't match user entered date", expectedCalendarDate.getTime(), calendarDate.getTime());
    }
}