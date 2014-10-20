package storage.taskStorage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import models.DateParser;
import models.PriorityLevelEnum;
import models.StartDueDatePair;
import models.Task;
import models.TaskAttributeEnum;

/**
*
* @author Chuyu 
* This class converts a task to a string 
* It also converts a formatted string to task
*/
class TaskConverter {
    private static final String MESSAGE_SEPARATOR = "\tL@L";
    private static final String MESSAGE_SEPARATOR_FOR_ATTRIBUTE = "\tT@T";    
    private static final String MESSAGE_NULL = "";


    /**
     * Converts a task to a string
     *
     * @param task : the task to be converted to a string
     * @return a string that represents a task
     */
    static String taskToString(Task task) {
        StringBuilder taskString = new StringBuilder();
        TreeMap<TaskAttributeEnum, String> taskAttributes = getTaskAttributes(task);
        for (Map.Entry<TaskAttributeEnum, String> entry: taskAttributes.entrySet()) {
            String attributeType = entry.getKey().toString();
            String attributeValue = entry.getValue();
            taskString.append(attributeType + MESSAGE_SEPARATOR + attributeValue + MESSAGE_SEPARATOR);
        }
        return taskString.toString();
    }

    /**
     * Converts a formatted string to a task
     *
     * @param taskString: a formatted string containing a task
     * @return a task
     * @throws FileFormatNotSupportedException
     */
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
            setTaskAttributes(task, taskAttributes);
            return task;
        } catch (Exception e) {
            throw new FileFormatNotSupportedException("File format is not supported.");
        }
    }

    private static TreeMap<TaskAttributeEnum, String> getTaskAttributes(Task task) {
        TreeMap<TaskAttributeEnum, String> taskAttributes = new TreeMap<TaskAttributeEnum, String>();

        taskAttributes.put(TaskAttributeEnum.NAME, task.getName());
        taskAttributes.put(TaskAttributeEnum.DATE_DUE,
            taskAttributeToString(task.getDateDue()));
        taskAttributes.put(TaskAttributeEnum.DATE_START,
            taskAttributeToString(task.getDateStart()));
        taskAttributes.put(TaskAttributeEnum.PRIORITY_LEVEL,
            taskAttributeToString(task.getPriorityLevelInteger()));
        taskAttributes.put(TaskAttributeEnum.DATE_END,
            taskAttributeToString(task.getDateEnd()));
        taskAttributes.put(TaskAttributeEnum.NOTE, task.getNote());
        taskAttributes.put(TaskAttributeEnum.TAGS,
            stringArrayListToString(task.getTags()));
        taskAttributes.put(TaskAttributeEnum.PARENT_TASKS,
            integerArrayListToString(task.getParentTasks()));
        taskAttributes.put(TaskAttributeEnum.CHILD_TASKS,
            integerArrayListToString(task.getChildTasks()));
        taskAttributes.put(TaskAttributeEnum.CONDITIONAL_DATES,
            datePairArrayListToString(task.getConditionalDates()));
        taskAttributes.put(TaskAttributeEnum.IS_DELETED,
            taskAttributeToString(task.isDeleted()));
        taskAttributes.put(TaskAttributeEnum.ID, 
            taskAttributeToString(task.getId()));        
        return taskAttributes;
    }  

    private static void setTaskAttributes(
            Task task, TreeMap<TaskAttributeEnum, String> taskAttributes)
            throws ParseException, InvalidDateFormatException {
        // check length of tree map first
        task.setId(Integer.valueOf(taskAttributes.get(TaskAttributeEnum.ID)));
        task.setName(taskAttributes.get(TaskAttributeEnum.NAME));
        task.setDateDue(stringToTaskProperty(taskAttributes
                .get(TaskAttributeEnum.DATE_DUE)));
        task.setDateStart(stringToTaskProperty(taskAttributes
                .get(TaskAttributeEnum.DATE_START)));
        task.setDateEnd(stringToTaskProperty(taskAttributes
                .get(TaskAttributeEnum.DATE_END)));
        if (!taskAttributes.get(TaskAttributeEnum.PRIORITY_LEVEL).isEmpty()) {
            task.setPriorityLevel(PriorityLevelEnum.fromInteger(Integer
                    .valueOf(taskAttributes
                            .get(TaskAttributeEnum.PRIORITY_LEVEL))));
        }
        task.setNote(taskAttributes.get(TaskAttributeEnum.NOTE));
        task.setDeleted(Boolean.valueOf(taskAttributes
                .get(TaskAttributeEnum.IS_DELETED)));
        task.setTags(stringToStringArrayList(taskAttributes
                .get(TaskAttributeEnum.TAGS)));
        task.setParentTasks(stringToIntegerArrayList(taskAttributes
                .get(TaskAttributeEnum.PARENT_TASKS)));
        task.setChildTasks(stringToIntegerArrayList(taskAttributes
                .get(TaskAttributeEnum.CHILD_TASKS)));
        task.setConditionalDates(stringToDatePairArrayList(taskAttributes
                .get(TaskAttributeEnum.CONDITIONAL_DATES)));
    }



    // convert int task attribute to string
    private static String taskAttributeToString(Integer intAttribute) {
        if (intAttribute == null) {
            return MESSAGE_NULL;
        } else {
            return intAttribute.toString();
        }
    }

    // convert boolean task attribute to string
    private static String taskAttributeToString(Boolean booleanAttribute) {
        if (booleanAttribute == null) {
            return MESSAGE_NULL;
        } else {
            return booleanAttribute.toString();
        }   
    }

    // convert calendar task attribute to string
    private static String taskAttributeToString(Calendar calendarAttribute) {
        if (calendarAttribute == null) {
            return MESSAGE_NULL;
        } else {
            return DateParser.parseCalendar(calendarAttribute);
        }
    }

    // convert string array list task attribute to string
    private static String stringArrayListToString(
            ArrayList<String> stringArrayListAttribute) {
        if (stringArrayListAttribute == null) {
            return MESSAGE_NULL;
        } else {
            return arrayListToString(stringArrayListAttribute);
        }
    }

    // convert integer array list task attribute to string
    private static String integerArrayListToString(
            ArrayList<Integer> intArrayListAttribute) {
        if (intArrayListAttribute == null) {
            return MESSAGE_NULL;
        } else {
            ArrayList<String> stringArrayAttribute = new ArrayList<String>();
            for (int intAttribute : intArrayListAttribute) {
                stringArrayAttribute.add(taskAttributeToString(intAttribute));
            }
            return stringArrayListToString(stringArrayAttribute);
        }
    }

    // convert start end date pair array list task attribute to string
    private static String datePairArrayListToString(
            ArrayList<StartDueDatePair> datePairAttribute) {
        if (datePairAttribute == null) {
            return MESSAGE_NULL;
        } else {
            ArrayList<String> stringArrayAttribute = new ArrayList<String>();
            for (StartDueDatePair datePair : datePairAttribute) {
                stringArrayAttribute.add(taskAttributeToString(datePair
                        .getStartDate()));
                stringArrayAttribute.add(taskAttributeToString(datePair
                        .getDueDate()));
            }
            return stringArrayListToString(stringArrayAttribute);
        }
    }

    private static String arrayListToString(ArrayList<String> stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringArray) {
            stringBuilder.append(str + MESSAGE_SEPARATOR_FOR_ATTRIBUTE);
        }
        return stringBuilder.toString();
    }

    private static Calendar stringToTaskProperty(String propertyString)
            throws ParseException, InvalidDateFormatException {
        if (propertyString.equals(MESSAGE_NULL)) {
            return null;
        } else {
            return DateParser.parseString(propertyString);
        }
    }

    private static ArrayList<String> stringToStringArrayList(String stringProperty) {
        if (stringProperty.equals(MESSAGE_NULL)) {
            return new ArrayList<String>();
        }
        String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
        return (ArrayList<String>) Arrays.asList(stringArray);
    }

    private static ArrayList<Integer> stringToIntegerArrayList(String stringProperty) {
        if (stringProperty.equals(MESSAGE_NULL)) {
            return new ArrayList<Integer>();
        }
        String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
        ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
        for (String stringElement : stringArray) {
            integerArrayList.add(Integer.valueOf(stringElement));
        }
        return integerArrayList;
    }

    private static ArrayList<StartDueDatePair> stringToDatePairArrayList(
            String stringProperty) throws ParseException,
            InvalidDateFormatException {
        if (stringProperty.equals(MESSAGE_NULL)) {
            return new ArrayList<StartDueDatePair>();
        }
        String[] stringArray = stringProperty.split(MESSAGE_SEPARATOR);
        ArrayList<StartDueDatePair> datePairArrayList = new ArrayList<StartDueDatePair>();
        Calendar startDate = null;
        Calendar endDate;
        for (String stringElement : stringArray) {
            if (startDate == null) {
                startDate = stringToTaskProperty(stringElement);
            } else {
                endDate = stringToTaskProperty(stringElement);
                StartDueDatePair datePair = new StartDueDatePair(startDate,
                        endDate);
                datePairArrayList.add(datePair);
                startDate = null;
            }
        }
        return datePairArrayList;
    }
}