package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import common.IntervalSearch;

//@author A0098722W
public class IntervalSearchTest {

    IntervalSearch tr;

    @Before
    public void initializeTest() {
        tr = new IntervalSearch();

        Calendar start = new GregorianCalendar(2014, 11, 12);
        Calendar end = new GregorianCalendar(2014, 11, 13);
        tr.add(start, end, 3);

        Calendar secondStart = new GregorianCalendar(2014, 11, 15);
        Calendar secondEnd = new GregorianCalendar(2014, 11, 18);
        tr.add(secondStart, secondEnd, 4);

        Calendar thirdStart = new GregorianCalendar(2014, 8, 10);
        Calendar thirdEnd = new GregorianCalendar(2014, 9, 12);
        tr.add(thirdStart, thirdEnd, 5);
    }

    @Test
    /**
     * This test the add date function of the interval search
     */
    public void testAddDate() {
        int currentSize = tr.size();

        Calendar newStart = new GregorianCalendar(2014, 10, 3);
        Calendar newEnd = new GregorianCalendar(2014, 10, 4);
        tr.add(newStart, newEnd, 5);
        int newSize = tr.size();

        assertEquals(1, (newSize - currentSize));
    }

    @Test
    /**
     * This test the get interval function of the interval search
     */
    public void testGetInterval() {
        Calendar searchStart = new GregorianCalendar(2014, 11, 1);
        Calendar searchEnd = new GregorianCalendar(2014, 11, 30);

        HashMap<Calendar, Calendar> results = tr.getOccupiedIntervals(
                searchStart, searchEnd);
        assertTrue(results.size() == 2);

        Calendar testStart = new GregorianCalendar(2014, 11, 15);
        Calendar testEnd = new GregorianCalendar(2014, 11, 18);

        assertEquals(testEnd.getTimeInMillis(), results.get(testStart)
                .getTimeInMillis());

        Calendar testStart2 = new GregorianCalendar(2014, 11, 12);
        Calendar testEnd2 = new GregorianCalendar(2014, 11, 13);

        assertEquals(testEnd2.getTimeInMillis(), results.get(testStart2)
                .getTimeInMillis());
    }

    @Test
    /**
     * This test the get intervalId function of the interval search
     */
    public void testGetIntervalId() {
        Calendar searchStart = new GregorianCalendar(2014, 11, 1);
        Calendar searchEnd = new GregorianCalendar(2014, 11, 30);

        ArrayList<Integer> results = tr.getTasksWithinInterval(searchStart,
                searchEnd);
        assertTrue(results.size() == 2);

        assertFalse(results.contains(5));
        assertTrue(results.contains(4));
        assertTrue(results.contains(3));
    }

    @Test
    /**
     * This test the get task id beyond a specific date function of interval search
     */
    public void testGetIdFromDate() {
        Calendar searchStart = new GregorianCalendar(2014, 11, 1);

        ArrayList<Integer> results = tr.getTasksFrom(searchStart);
        assertTrue(results.size() == 2);

        assertFalse(results.contains(5));
        assertTrue(results.contains(4));
        assertTrue(results.contains(3));
    }

    @Test
    /**
     * This test the update function of interval search
     */
    public void testUpdate() {
        Calendar oldStart = new GregorianCalendar(2014, 11, 12);
        Calendar oldEnd = new GregorianCalendar(2014, 11, 13);

        Calendar newStart = new GregorianCalendar(2014, 11, 13);
        Calendar newEnd = new GregorianCalendar(2014, 11, 14);

        tr.update(oldStart, oldEnd, newStart, newEnd, 3);
        assertEquals(-1, tr.get(oldStart, oldEnd));
        assertEquals(3, tr.get(newStart, newEnd));

    }

}
