package common;

import java.util.ArrayList;
import java.util.Calendar;

//@author A0114368E
/**
 * This class stores all the attributes of a task. Currently, 4 types of tasks
 * are supported: Deadline Task, Floating Task, Timed Task and Conditional Task
 *
 */
public class Task {
    public static final int ID_FOR_NEW_TASK = 0;
    private int id = 0;
    private String name = "";
    private Calendar dateDue = null;
    private Calendar dateStart = null;
    private Calendar dateEnd = null;
    private PriorityLevelEnum priorityLevel = PriorityLevelEnum.DEFAULT;
    private String note = "";
    private ArrayList<String> tags = new ArrayList<String>();
    private ArrayList<Integer> parentTasks = new ArrayList<Integer>();
    private ArrayList<Integer> childTasks = new ArrayList<Integer>();
    private ArrayList<StartDueDatePair> conditionalDates = new ArrayList<StartDueDatePair>();
    private boolean isDeleted = false;

    public Task() {
    }

    public void addTags(ArrayList<String> newTags) {
        newTags.removeAll(tags);
        tags.addAll(newTags);
    }

    public void appendConditionalDates(
            ArrayList<StartDueDatePair> conditionalDates) {
        this.conditionalDates.addAll(conditionalDates);
    }

    public ArrayList<Integer> getChildTasks() {
        return childTasks;
    }

    public ArrayList<StartDueDatePair> getConditionalDates() {
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

    public ArrayList<Integer> getParentTasks() {
        return parentTasks;
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

    public void setChildTasks(ArrayList<Integer> childTasks) {
        this.childTasks = childTasks;
    }

    public void setConditionalDates(ArrayList<StartDueDatePair> conditionalDates) {
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

    public void setParentTasks(ArrayList<Integer> parentTasks) {
        this.parentTasks = parentTasks;
    }

    public void setPriorityLevel(PriorityLevelEnum priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public void setStartDueDateFromConditional(int id) {
        assert (conditionalDates != null && id >= conditionalDates.size());
        dateStart = conditionalDates.get(id - 1).getStartDate();
        dateEnd = conditionalDates.get(id - 1).getDueDate();
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
