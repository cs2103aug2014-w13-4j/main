package storage.taskStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import models.DateParser;
import models.PriorityLevelEnum;
import models.Task;
import models.TaskAttributeEnum;

/**
*
* @author Chuyu 
* This class converts the task to string 
* It can also convert the string to file
*/
class TaskConverter {
    private static final String MESSAGE_SEPARATOR = "\tL@L";
    private static final String MESSAGE_SEPARATOR_FOR_ATTRIBUTE = "\tT@T";

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

    static String taskToString(Task task) {
        StringBuilder taskString = new StringBuilder();
        TreeMap<TaskAttributeEnum, String> taskAttributes = task.getTaskAttributes();
        for (Map.Entry<TaskAttributeEnum, String> entry: taskAttributes.entrySet()) {
            String attributeType = entry.getKey().toString();
            String attributeValue = entry.getValue();
            taskString.append(attributeType + MESSAGE_SEPARATOR + attributeValue + MESSAGE_SEPARATOR);
        }
        return taskString.toString();
    }

    static Task stringToTask(String taskString) throws FileFormatNotSupportedException {
        try{
            String[] taskStringArray = taskString.split(MESSAGE_SEPARATOR);
            TaskAttributeEnum attributeType = null;
            String attributeValue;
            Task task = new Task();
            TreeMap<TaskAttributeEnum, String> taskAttributes = new TreeMap<TaskAttributeEnum, String>();
            for (String taskStringElement : taskStringArray) {
                if (attributeType == null) {
                    attributeType = TaskAttributeEnum.valueOf(taskStringElement);
                } else {
                    attributeValue = taskStringElement;
                    taskAttributes.put(attributeType, attributeValue);
                    attributeType = null;
                }
            }
            task.setTaskAttributes(taskAttributes);
            return task;
        } catch (Exception e) {
            throw new FileFormatNotSupportedException("File format is not supported.");
        }
    }
/*



    

    protected static Task stringToTask(String taskString) throws FileFormatNotSupportedException {
        int arrayIndex;
        String tagStored;
        int taskStored;
        Task task = new Task();

        try{
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
            //task.setConditionalTasks(taskConditionalTasks);
            return task;
        } catch (Exception e) {
            throw new FileFormatNotSupportedException("File format is not supported.");
        }
    }*/
}