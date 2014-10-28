package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class IntervalSearch {
    
    private static long END_OF_DATE = Long.MAX_VALUE;
    private static long START_OF_DATE = Long.MIN_VALUE;

    public static class DateRange {
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

    public ArrayList<Integer> getTasksWithinInterval(Calendar start,
            Calendar end) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        ArrayList<DateRange> searchResults = searchInterval(start, end);

        for (DateRange r : searchResults) {
            results.add(map.get(r));
        }

        return results;
    }

    public HashMap<Calendar, Calendar> getOccupiedIntervals(Calendar start,
            Calendar end) {
        HashMap<Calendar, Calendar> results = new HashMap<Calendar, Calendar>();
        ArrayList<DateRange> searchResults = searchInterval(start, end);
        for (DateRange range : searchResults) {
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

    public void add(Calendar start, Calendar end, int id) {
        assert start.getTimeInMillis() >= end.getTimeInMillis();

        DateRange range = new DateRange(start, end);
        map.put(range, id);
    }

    public void remove(Calendar start, Calendar end) {
        DateRange range = new DateRange(start, end);

        if (map.containsKey(range)) {
            map.remove(range);
        }
    }

    public void update(Calendar oldStart, Calendar oldEnd, Calendar newStart,
            Calendar newEnd, int id) {
        remove(oldStart, oldEnd);
        add(newStart, newEnd, id);
    }

    public int get(Calendar start, Calendar end) {
        DateRange range = new DateRange(start, end);
       
        if (map.containsKey(range)) {
            return map.get(range);
        } else {
            return -1;
        }
    }
    
    public ArrayList<Integer> getTasksFrom(Calendar start) {
        Calendar end = GregorianCalendar.getInstance();
        end.setTimeInMillis(END_OF_DATE);
        return getTasksWithinInterval(start, end);
    }

    public ArrayList<Integer> getTasksBefore(Calendar end) {
        Calendar start = GregorianCalendar.getInstance();
        start.setTimeInMillis(START_OF_DATE);
        return getTasksWithinInterval(start, end);
    }

    public int size() {
        return map.size();
    }

    public Calendar getDateStart(int taskId) {
        Calendar dateStart = Calendar.getInstance();
        for (DateRange key : map.keySet()) {
            if (map.get(key) == taskId) {
                dateStart.setTimeInMillis(key.getStartDate());
                break;
            }
        }
        return dateStart;
    }

    public Calendar getDateDue(int taskId) {
        Calendar dateDue = Calendar.getInstance();
        for (DateRange key : map.keySet()) {
            if (map.get(key) == taskId) {
                dateDue.setTimeInMillis(key.getEndDate());
                break;
            }
        }
        return dateDue;
    }
}
