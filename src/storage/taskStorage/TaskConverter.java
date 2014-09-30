package storage.taskStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import models.DateParser;
import models.PriorityLevelEnum;
import models.Task;
import models.exceptions.FileFormatNotSupportedException;

/**
*
* @author Chuyu 
* This class converts the task to string 
* It can also convert the string to file
*/
class TaskConverter {
    private static final String MESSAGE_SEPARATOR = "\tL@L";
    private static final String MESSAGE_ID = "Task ID: ";
    private static final String MESSAGE_NAME = "Name: ";
    private static final String MESSAGE_DATE_DUE = "Due date: ";
    private static final String MESSAGE_DATE_START = "Start date: ";
    private static final String MESSAGE_DATE_END = "End date: ";
    private static final String MESSAGE_PRIORITY_LEVEL = "Priority level: ";
    private static final String MESSAGE_NOTE = "Note: ";
    private static final String MESSAGE_TAGS = "Tags: ";
    private static final String MESSAGE_PARENT_TASKS = "Parent tasks: ";
    private static final String MESSAGE_CHILD_TASKS = "Child tasks: ";
    private static final String MESSAGE_CONDITIONAL_TASKS = "Conditional tasks: ";
    private static final String MESSAGE_IS_DELETED = "Is deleted: ";
    private static final String MESSAGE_IS_COMFIRMED = "Is comfirmed: ";

    private static final int ID_ATTRIBUTE = 1;
    private static final int NAME_ATTRIBUTE = 3;
    private static final int DATE_DUE_ATTRIBUTE = 5;
    private static final int DATE_START_ATTRIBUTE = 7;
    private static final int DATE_END_ATTRIBUTE = 9;
    private static final int PRIORITY_LEVEL_ATTRIBUTE = 11;
    private static final int NOTE_ATTRIBUTE = 13;
    private static final int IS_DELETED_ATTRIBUTE = 15;
    private static final int IS_COMFIRMED_ATTRIBUTE = 17;
    private static final int TAGS_ATTRIBUTE = 19;

    private static final int RESULT_THRESHOLD = -1;

    // convert int task property to string
    private static String taskPropertyToString(Integer intProperty) {
        if (intProperty == null) {
            return "";
        } else {
            return intProperty.toString();
        }
    }

    // convert string task property to string
    private static String taskPropertyToString(String strProperty) {
        return strProperty;
    }

    // convert calendar task property to string
    private static String taskPropertyToString(Calendar calendarProperty) {
        if (calendarProperty == null) {
            return "";
        } else {
            return DateParser.parseCalendar(calendarProperty);
        }
    }

    // convert boolean task property to string
    private static String taskPropertyToString(Boolean booleanProperty) {
        if (booleanProperty == null) {
            return "";
        } else {
            return booleanProperty.toString();
        }
    }

    // convert string array list task property to string
    private static String taskPropertyToString(ArrayList<String> stringArrayListProperty) {
        if (stringArrayListProperty == null) {
            return "";
        } else {
            return arrayListToString(stringArrayListProperty);
        }
    }

    // convert integer array list task property to string
    private static String taskIntPropertyToString(ArrayList<Integer> intArrayListProperty) {
        if (intArrayListProperty == null) {
            return "";
        } else {
            ArrayList<String> stringArrayProperty = new ArrayList<String>();
            for (int intProperty : intArrayListProperty) {
                stringArrayProperty.add(taskPropertyToString(intProperty));
            }
            return taskPropertyToString(stringArrayProperty);
        }
    }

    private static String arrayListToString(ArrayList<String> stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringArray) {
            // System.out.println(str + "LOL");
            stringBuilder.append(str + MESSAGE_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    static String taskToString(Task task) {
        ArrayList<String> taskStringArrayList = new ArrayList<String>();
        taskStringArrayList.add(MESSAGE_ID);
        taskStringArrayList.add(taskPropertyToString(task.getId()));
        taskStringArrayList.add(MESSAGE_NAME);
        taskStringArrayList.add(taskPropertyToString(task.getName()));
        taskStringArrayList.add(MESSAGE_DATE_DUE);
        taskStringArrayList.add(taskPropertyToString(task.getDateDue()));
        taskStringArrayList.add(MESSAGE_DATE_START);
        taskStringArrayList.add(taskPropertyToString(task.getDateStart()));
        taskStringArrayList.add(MESSAGE_DATE_END);
        taskStringArrayList.add(taskPropertyToString(task.getDateEnd()));
        taskStringArrayList.add(MESSAGE_PRIORITY_LEVEL);
        taskStringArrayList.add(taskPropertyToString(task.getPriorityLevelInteger()));
        taskStringArrayList.add(MESSAGE_NOTE);
        taskStringArrayList.add(taskPropertyToString(task.getNote()));
        taskStringArrayList.add(MESSAGE_IS_DELETED);
        taskStringArrayList.add(taskPropertyToString(task.isDeleted()));
        taskStringArrayList.add(MESSAGE_IS_COMFIRMED);
        taskStringArrayList.add(taskPropertyToString(task.isConfirmed()));
        taskStringArrayList.add(MESSAGE_TAGS);
        taskStringArrayList.add(taskPropertyToString(task.getTags()));
        taskStringArrayList.add(MESSAGE_PARENT_TASKS);
        taskStringArrayList.add(taskIntPropertyToString(task.getParentTasks()));
        taskStringArrayList.add(MESSAGE_CHILD_TASKS);
        taskStringArrayList.add(taskIntPropertyToString(task.getChildTasks()));
        taskStringArrayList.add(MESSAGE_CONDITIONAL_TASKS);
        taskStringArrayList.add(taskIntPropertyToString(task.getConditionalTasks()));

        String taskString = arrayListToString(taskStringArrayList);
        return taskString;


        /*
        String[taskStringArray] = getPropertyArray(task);
        String taskID = Integer.toString(task.getId());
        String taskName = task.getName();
        String taskDateDue = task.getDateDue().toString();
        String taskDateStart = task.getDateStart().toString();
        String taskDateEnd = task.getDateEnd().toString();
        String taskPriorityLevel = Integer.toString(task.getPriorityLevelInteger());
        String taskNote = task.getNote();
        String taskIsDeleted = Boolean.toString(task.isDeleted());
        String taskIsConfirmed = Boolean.toString(task.isConfirmed());
        String[] taskStringArray = new String[]{MESSAGE_ID, taskID, MESSAGE_NAME, taskName,
            MESSAGE_DATE_DUE, taskDateDue, MESSAGE_DATE_START, taskDateStart, MESSAGE_DATE_END,
            taskDateEnd, MESSAGE_PRIORITY_LEVEL, taskPriorityLevel, MESSAGE_NOTE, taskNote, 
            MESSAGE_IS_DELETED, taskIsDeleted, MESSAGE_IS_COMFIRMED, taskIsConfirmed, MESSAGE_TAGS};
        // String taskString = StringUtils.join(taskStringArray, MESSAGE_SEPARATOR);
        String taskString = Arrays.toString(taskStringArray);
        for (String tag : task.getTags()) {
            taskString = taskString + MESSAGE_SEPARATOR + tag;
        }
        taskString = taskString + MESSAGE_SEPARATOR + MESSAGE_PARENT_TASKS;
        for (int parentID : task.getParentTasks()) {
            taskString = taskString + MESSAGE_SEPARATOR + parentID;
        }
        taskString = taskString + MESSAGE_SEPARATOR + MESSAGE_CHILD_TASKS;
        for (int childID : task.getChildTasks()) {
            taskString = taskString + MESSAGE_SEPARATOR + childID;
        }
        taskString = taskString + MESSAGE_SEPARATOR + MESSAGE_CONDITIONAL_TASKS;
        for (int conditionalID : task.getConditionalTasks()) {
            taskString = taskString + MESSAGE_SEPARATOR + conditionalID;
        }
        return taskString;
        */
    }

    protected static Calendar stringtoTaskProperty(String propertyString) throws ParseException {
        if (propertyString.equals("")) {
            // System.out.println("null property");
            return null;            
        } else {
            return DateParser.parseString(propertyString);
        }
    }

    protected static Task stringToTask(String taskString) throws FileFormatNotSupportedException {
        int arrayIndex;
        String tagStored;
        int taskStored;
        Task task = new Task();

        try{
            // taskString  = taskString.replaceAll("[\n\r\\s]", "");
            String[] taskStringArray = taskString.split(MESSAGE_SEPARATOR, RESULT_THRESHOLD);
            int taskID = Integer.valueOf(taskStringArray[ID_ATTRIBUTE]);
            String taskName = taskStringArray[NAME_ATTRIBUTE];
            Calendar taskDateDue = stringtoTaskProperty(taskStringArray[DATE_DUE_ATTRIBUTE]);
            Calendar taskDateStart = stringtoTaskProperty(taskStringArray[DATE_START_ATTRIBUTE]);
            Calendar taskDateEnd = stringtoTaskProperty(taskStringArray[DATE_END_ATTRIBUTE]);
            PriorityLevelEnum taskPriorityLevel = null;
            if (!taskStringArray[PRIORITY_LEVEL_ATTRIBUTE].isEmpty()) {
                taskPriorityLevel = PriorityLevelEnum.fromInteger(Integer.valueOf(taskStringArray[PRIORITY_LEVEL_ATTRIBUTE]));
            }
            String taskNote = taskStringArray[NOTE_ATTRIBUTE];
            boolean taskIsDeleted = Boolean.valueOf(taskStringArray[IS_DELETED_ATTRIBUTE]);
            boolean taskIsConfirmed = Boolean.valueOf(taskStringArray[IS_COMFIRMED_ATTRIBUTE]);
            ArrayList<String> taskTags = new ArrayList<String>();
            arrayIndex = TAGS_ATTRIBUTE;
            // need to refactor later
            if (taskStringArray[arrayIndex].equals("")) {
                taskTags = null;
                arrayIndex ++;
            }
            while (!taskStringArray[arrayIndex].equals(MESSAGE_PARENT_TASKS)) {
                tagStored = taskStringArray[arrayIndex];
                taskTags.add(tagStored);
                arrayIndex ++;
            }
            ArrayList<Integer> taskParentTasks = new ArrayList<Integer>();
            arrayIndex ++;
            // need to refactor later
            if (taskStringArray[arrayIndex].equals("")) {
                taskParentTasks = null;
                arrayIndex ++;
            }
            while (!taskStringArray[arrayIndex].equals(MESSAGE_CHILD_TASKS)) {
                taskStored = Integer.valueOf(taskStringArray[arrayIndex]);
                taskParentTasks.add(taskStored);
                arrayIndex ++;
            }
            ArrayList<Integer> taskChildTasks = new ArrayList<Integer>();
            arrayIndex ++;
            // need to refactor later
            if (taskStringArray[arrayIndex].equals("")) {
                taskChildTasks = null;
                arrayIndex ++;
            }
            while (!taskStringArray[arrayIndex].equals(MESSAGE_CONDITIONAL_TASKS)) {
                taskStored = Integer.valueOf(taskStringArray[arrayIndex]);
                taskChildTasks.add(taskStored);
                arrayIndex ++;
            }
            ArrayList<Integer> taskConditionalTasks = new ArrayList<Integer>();
            arrayIndex ++;
            // need to refactor later
            if (taskStringArray[arrayIndex].equals("")) {
                taskConditionalTasks = null;
                arrayIndex ++;
            }
            while (arrayIndex <= (taskStringArray.length - 2)) {
                taskStored = Integer.valueOf(taskStringArray[arrayIndex]);
                taskConditionalTasks.add(taskStored);
                arrayIndex ++;
            }
            
            task.setId(taskID);
            task.setName(taskName);
            task.setDateDue(taskDateDue);
            task.setDateStart(taskDateStart);
            task.setDateEnd(taskDateEnd);
            task.setPriorityLevel(taskPriorityLevel);
            task.setNote(taskNote);
            task.setTags(taskTags);
            task.setParentTasks(taskParentTasks);
            task.setChildTasks(taskChildTasks);
            task.setConditionalTasks(taskConditionalTasks);
            return task;
        } catch (Exception e) {
            throw new FileFormatNotSupportedException("File format is not supported.");
        }
    }
}