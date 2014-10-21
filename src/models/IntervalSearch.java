package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class IntervalSearch {

    public static class DateRange implements Comparable<DateRange> {
        public long startDate;
        public long endDate;

        public DateRange(Calendar start, Calendar end) {
            startDate = start.getTimeInMillis();
            endDate = end.getTimeInMillis();
        }

        public long getStartDate() {
            return startDate;
        }

        public long getEndDate() {
            return endDate;
        }

        public boolean overlaps(DateRange range) {
            return startDate <= range.getEndDate()
                    && endDate >= range.getStartDate();
        }

        public int hashCode() {
            int hash = 3;
            hash = (int) (7 * hash + endDate);
            hash = (int) (7 * hash + startDate);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DateRange))
                return false;
            if (obj == this)
                return true;

            DateRange range = (DateRange) obj;
            return startDate == range.getStartDate()
                    && endDate == range.getEndDate();
        }
    }

    HashMap<DateRange, Integer> map = new HashMap<DateRange, Integer>();

    public ArrayList<DateRange> searchInterval(Calendar start, Calendar end) {
        DateRange target = new DateRange(start, end);
        ArrayList<DateRange> results = new ArrayList<DateRange>();
        for (DateRange key : map.keySet()) {

            if (target.overlaps(key)) {
                results.add(key);
            }
        }
        return results;
    }
    
    public boolean isValid(Calendar start, Calendar end) {
        ArrayList<DateRange> results = searchInterval(start, end);
        return results.isEmpty();
    }
    
    public ArrayList<Integer> getTasksWithinInterval(Calendar start, Calendar end) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        ArrayList<DateRange> searchResults = searchInterval(start, end);
        
        for (DateRange r: searchResults) {
            results.add(map.get(r));
        }
        
        return results;
    }
    
    public HashMap<Calendar, Calendar> getOccupiedIntervals(Calendar start, Calendar end) {
        HashMap<Calendar, Calendar> results = new HashMap<Calendar, Calendar>();
        ArrayList<DateRange> searchResults = searchInterval(start, end);
        for (DateRange range: searchResults) {
            long startTime = range.getStartDate();
            long endTime = range.getEndDate();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(startTime);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(endTime);
            results.put(startCalendar, endCalendar);
        }
        return results;
    }

    public boolean add(Calendar start, Calendar end, int id) {
        assert start.getTimeInMillis() >= end.getTimeInMillis();
        
        if (isValid(start, end)) {
            DateRange range = new DateRange(start, end);
            map.put(range, id);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean remove(Calendar start, Calendar end, int id) {
        DateRange range = new DateRange(start, end);
        
        if(map.containsKey(range)) {
            map.remove(range);
            return true;
        } else {
            return false;
        }
    }

}
