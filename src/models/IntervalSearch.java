package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * This class helps to keep track of the dates of all the events from the user.
 * It helps to ensure no crash of event as well as finding empty slots for user.
 * 
 * @author xuanyi
 *
 */
public class IntervalSearch {

    private static long END_OF_DATE = Long.MAX_VALUE;
    private static long START_OF_DATE = Long.MIN_VALUE;
    private static int HASH_VALUE = 3;
    private static int HASH_CONSTANT = 7;

    /**
     * Helper class to represent an date interval. Supports check for overlap of
     * interval.
     * 
     * @author xuanyi
     *
     */
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
            int hash = HASH_VALUE;
            hash = (int) (HASH_CONSTANT * hash + endDate);
            hash = (int) (HASH_CONSTANT * hash + startDate);
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

    /**
     * This operation search in a given interval for all the DateRange object
     * that overlap with this given dates.
     * 
     * @param start
     *            of the date interval
     * @param end
     *            of the date interval
     * @return a list of daterange that contains all the overlapping objects.
     */
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

    /**
     * This operation check if a given date overlap with any other DateRange
     * 
     * @param start
     *            of the new date range to be added
     * @param end
     *            of the new date range to be added
     * @return true if no other date is found to be overlap with the given date.
     *         false otherwise.
     */
    public boolean isValid(Calendar start, Calendar end) {
        ArrayList<DateRange> results = searchInterval(start, end);
        return results.isEmpty();
    }

    /**
     * This operation returns all the task id found in a given date range
     * 
     * @param start
     *            of the given date range
     * @param end
     *            of the given date range
     * @return a list of task id that overlap with the given date range
     */
    public ArrayList<Integer> getTasksWithinInterval(Calendar start,
            Calendar end) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        ArrayList<DateRange> searchResults = searchInterval(start, end);

        for (DateRange r : searchResults) {
            results.add(map.get(r));
        }

        return results;
    }

    /**
     * This operation returns all the date range that is blocked within the
     * given date range
     * 
     * @param start
     *            of the given date range to be check
     * @param end
     *            of the given date range to be check
     * @return a dictionary of start date and end date calendar object
     */
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

    /**
     * This operaton adds a given start and end date to the hashmap to be check
     * against other date
     * 
     * @param start
     * @param end
     * @param id
     *            of the task
     */
    public void add(Calendar start, Calendar end, int id) {
        assert start.getTimeInMillis() <= end.getTimeInMillis();

        DateRange range = new DateRange(start, end);
        map.put(range, id);
    }

    /**
     * This operation remove the date range from the hashmap so that it will not
     * be check against other date
     * 
     * @param start
     * @param end
     */
    public void remove(Calendar start, Calendar end) {
        DateRange range = new DateRange(start, end);

        if (map.containsKey(range)) {
            map.remove(range);
        }
    }

    /**
     * This operation updates a given date range in the hashmap
     * 
     * @param oldStart
     * @param oldEnd
     * @param newStart
     * @param newEnd
     * @param id
     *            of the task
     */
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

    /**
     * This operation returns all the tasks starting from a given date
     * 
     * @param start
     * @return list of task that overlap with the given starting date
     */
    public ArrayList<Integer> getTasksFrom(Calendar start) {
        Calendar end = GregorianCalendar.getInstance();
        end.setTimeInMillis(END_OF_DATE);
        return getTasksWithinInterval(start, end);
    }

    /**
     * This operation returns all the tasks before a given date
     * 
     * @param end
     * @return list of task that overlap with the given ending date
     */
    public ArrayList<Integer> getTasksBefore(Calendar end) {
        Calendar start = GregorianCalendar.getInstance();
        start.setTimeInMillis(START_OF_DATE);
        return getTasksWithinInterval(start, end);
    }

    public int size() {
        return map.size();
    }

    public void remove(Task task) {
        int taskId = task.getId();
        Calendar dateStart, dateEnd;
        HashMap<DateRange, Integer> clonedMap = (HashMap<DateRange, Integer>) map.clone();
        
        for (DateRange key : clonedMap.keySet()) {
            if (map.get(key) == taskId) {
                dateStart = Calendar.getInstance();
                dateStart.setTimeInMillis(key.getStartDate());
                dateEnd = Calendar.getInstance();
                dateEnd.setTimeInMillis(key.getEndDate());
                remove(dateStart, dateEnd);
            }
        }
    }
}
