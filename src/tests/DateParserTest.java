package tests;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import common.DateParser;
import common.exceptions.InvalidDateFormatException;

public class DateParserTest {

    @Test
    public final void test() throws InvalidDateFormatException {
        Calendar date = DateParser.parseString("20-10-1999 23:00");
        // System.out.println(date.get(Calendar.HOUR));
        // Calendar expectedDate = Calendar.getInstance();
        // month value is 0 based
        // expectedDate.set(1999, 9, 20, 23, 00);
        // assertEquals("Dates are equal", expectedDate, date);
        String dateString = DateParser.parseCalendar(date);
        String expectedDateString = "20-10-1999 23:00";
        assertEquals("Strings are equal", expectedDateString, dateString);
        Calendar newDate = DateParser.parseString(dateString);
        assertEquals("Dates are still equal", newDate, date);
    }

}
