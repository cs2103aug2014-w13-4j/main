package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import models.IntervalSearch;

import org.junit.Before;
import org.junit.Test;

public class IntervalSearchTest {

    IntervalSearch tr;

    @Before
    public void initializeTest() {
        tr = new IntervalSearch();
        
        Calendar start = new GregorianCalendar(2014,11,12);
        Calendar end = new GregorianCalendar(2014,11,13);
        tr.add(start, end, 3);
        
        Calendar secondStart = new GregorianCalendar(2014,11,15);
        Calendar secondEnd = new GregorianCalendar(2014,11,18);
        tr.add(secondStart, secondEnd, 4);
        
        Calendar thirdStart = new GregorianCalendar(2014,8,10);
        Calendar thirdEnd = new GregorianCalendar(2014,9,12);
        tr.add(thirdStart, thirdEnd, 5);
    }

    @Test
    public void testAddDate() {
        int currentSize = tr.size();

        Calendar newStart = new GregorianCalendar(2014,10,3);
        Calendar newEnd = new GregorianCalendar(2014,10,4);
        tr.add(newStart, newEnd, 5);
        int newSize = tr.size();

        assertTrue((newSize - currentSize) == 1);
    }

    @Test
    public void testGetInterval() {
        Calendar searchStart = new GregorianCalendar(2014,11,1);
        Calendar searchEnd = new GregorianCalendar(2014,11,30);

        HashMap<Calendar, Calendar> results = tr.getOccupiedIntervals(
                searchStart, searchEnd);
        assertTrue(results.size() == 2);

        Calendar testStart = new GregorianCalendar(2014,11,15);
        Calendar testEnd = new GregorianCalendar(2014,11,18);
        
        assertEquals(results.get(testStart).getTimeInMillis(),testEnd.getTimeInMillis());
        
        Calendar testStart2 = new GregorianCalendar(2014,11,12);
        Calendar testEnd2 = new GregorianCalendar(2014,11,13);
        
        assertEquals(results.get(testStart2).getTimeInMillis(),testEnd2.getTimeInMillis());
    }
    
    @Test
    public void testGetIntervalId() {
        Calendar searchStart = new GregorianCalendar(2014,11,1);
        Calendar searchEnd = new GregorianCalendar(2014,11,30);

        ArrayList<Integer> results = tr.getTasksWithinInterval(searchStart, searchEnd);
        assertTrue(results.size() == 2);
        
        assertFalse(results.contains(5));
        assertTrue(results.contains(4));
        assertTrue(results.contains(3));
    }

}
