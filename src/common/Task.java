package common;

import java.util.ArrayList;
import java.util.Calendar;

//@author A0114368E
/**
 * This class stores all the attributes of a task. Currently, 4 types of tasks
 * are supported: Deadline Task, Floating Task, Timed Task and Conditional Task
 *
 */
public class Task implements Comparable<Task> {
    public static final int ID_FOR_NEW_TASK = 0;
    private int id = 0;
    private String name = "";
    private Calendar dateDue = null;
    private Calendar dateStart = null;
    private Calendar dateEnd = null;
    private PriorityLevelEnum priorityLevel = PriorityLevelEnum.DEFAULT;
    private String note = "";
    private ArrayList<String> tags = new ArrayList<String>();
    private ArrayList<StartEndDatePair> conditionalDates = new ArrayList<StartEndDatePair>();
    private boolean isDeleted = false;

    public Task() {
    }

    public void addTags(ArrayList<String> newTags) {
        newTags.removeAll(tags);
        tags.addAll(newTags);
    }

    public void appendConditionalDates(
            ArrayList<StartEndDatePair> conditionalDates) {
        this.conditionalDates.addAll(conditionalDates);
    }

    public ArrayList<StartEndDatePair> getConditionalDates() {
        return conditionalDates;
    }

    public Calendar getDateDue() {
        return dateDue;
    }

    public Calendar getDateEnd() {
        return dateEnd;
    }

    public Calendar getDateStart() {
        return dateStart;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public PriorityLevelEnum getPriorityLevel() {
        return priorityLevel;
    }

    public Integer getPriorityLevelInteger() {
        if (priorityLevel == null) {
            return null;
        } else {
            return priorityLevel.getLevel();
        }
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public boolean isCompleted() {
        Boolean isTimedTaskCompleted = isTimedTask() && isDateEndOver();
        Boolean isDeadlineTaskCompleted = isDeadlineTask() && dateEnd != null;
        Boolean isFloatingTaskCompleted = isFloatingTask() && dateEnd != null;
        return isTimedTaskCompleted || isDeadlineTaskCompleted
                || isFloatingTaskCompleted;
    }

    public boolean isConditionalTask() {
        return dateDue == null && dateStart == null && dateEnd == null
                && !conditionalDates.isEmpty();
    }

    public boolean isConfirmed() {
        if (conditionalDates != null && !conditionalDates.isEmpty()) {
            return (dateStart != null || dateEnd != null);
        } else {
            return true;
        }
    }

    private boolean isDateEndOver() {
        return dateEnd.compareTo(Calendar.getInstance()) <= 0;
    }

    public boolean isDeadlineTask() {
        return dateDue != null && dateStart == null
                && conditionalDates.isEmpty();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isFloatingTask() {
        return dateDue == null && dateStart == null
                && conditionalDates.isEmpty();
    }

    public boolean isTimedTask() {
        return dateDue == null && dateStart != null && dateEnd != null;
    }

    public void setConditionalDates(ArrayList<StartEndDatePair> conditionalDates) {
        this.conditionalDates = conditionalDates;
    }

    public void setDateDue(Calendar dateDue) {
        this.dateDue = dateDue;
    }

    public void setDateEnd(Calendar dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setDateStart(Calendar dateStart) {
        this.dateStart = dateStart;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPriorityLevel(PriorityLevelEnum priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public void setStartDueDateFromConditional(int id) {
        assert (conditionalDates != null && id >= conditionalDates.size());
        dateStart = conditionalDates.get(id - 1).getStartDate();
        dateEnd = conditionalDates.get(id - 1).getEndDate();
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public int compareTo(Task otherTask) {
        Calendar firstDate = this.getDateForComparison();
        Calendar secondDate = otherTask.getDateForComparison();
        if (firstDate == null && secondDate == null) {
            return 0;
        } else if (firstDate == null) {
            return 1;
        } else if (secondDate == null) {
            return -1;
        } else {
            return (firstDate.compareTo(secondDate));
        }
    }

    public Calendar getDateForComparison() {
        if (isDeadlineTask()) {
            return getDateDue();
        } else if (isTimedTask()) {
            return getDateStart();
        } else {
            return null;
        }
    }
}
