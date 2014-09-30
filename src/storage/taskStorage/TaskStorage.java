package storage.taskStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

import models.PriorityLevelEnum;
import models.Task;
import models.exceptions.FileFormatNotSupportedException;
import models.exceptions.TaskNotFoundException;

/**
*
* @author Chuyu 
* This class reads/writes task to file.
* It also supports power search.
*/
public class TaskStorage {
    private ArrayList<Task> taskBuffer;
    private int nextTaskIndex;
    private File dataFile;

    private static final int ID_FOR_NEW_TASK = -1;
    private static final int ID_FOR_FIRST_TASK = 0;

    private static final String MESSAGE_SEPARATOR = "\tL@L";
    private static final String MESSAGE_ID = "Task ID: "  + MESSAGE_SEPARATOR;
    private static final String MESSAGE_NAME = "Name: " + MESSAGE_SEPARATOR;
    private static final String MESSAGE_DATE_DUE = "Due date: " + MESSAGE_SEPARATOR;
    private static final String MESSAGE_DATE_START = "Start date: " + MESSAGE_SEPARATOR;
    private static final String MESSAGE_DATE_END = "End date: " + MESSAGE_SEPARATOR;
    private static final String MESSAGE_PRIORITY_LEVEL = "Priority level: " + MESSAGE_SEPARATOR;
    private static final String MESSAGE_NOTE = "Note: " + MESSAGE_SEPARATOR;
    private static final String MESSAGE_TAGS = "Tags: ";
    private static final String MESSAGE_PARENT_TASKS = "Parent tasks: ";
    private static final String MESSAGE_CHILD_TASKS = "Child tasks: ";
    private static final String MESSAGE_CONDITIONAL_TASKS = "Conditional tasks: ";
    private static final String MESSAGE_IS_DELETED = "Is deleted :";
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

     /**
     * constructor``
     * @throws FileFormatNotSupportedException, IOException
     */
    public TaskStorage(String fileName) throws IOException, FileFormatNotSupportedException{
    	Task task;
        dataFile = new File(fileName);

        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }

        Scanner fileScanner = new Scanner(dataFile);
        taskBuffer =  new ArrayList<Task>();
        int nextTaskIndex = ID_FOR_FIRST_TASK;
        while (fileScanner.hasNextLine()) {
            task = stringToTask(fileScanner.nextLine());
            taskBuffer.add(task);
            nextTaskIndex ++;
        }   
    }

    private String TaskToString(Task task) {
    	String taskID = Integer.toString(task.getId()) + MESSAGE_SEPARATOR;
        String taskName = task.getName() + MESSAGE_SEPARATOR;
        String taskDateDue = task.getDateDue().toString() + MESSAGE_SEPARATOR;
        String taskDateStart = task.getDateStart().toString() + MESSAGE_SEPARATOR;
        String taskDateEnd = task.getDateEnd().toString() + MESSAGE_SEPARATOR;
        String taskPriorityLevel = Integer.toString(task.getPriorityLevelInteger()) + MESSAGE_SEPARATOR;
        String taskNote = task.getNote() + MESSAGE_SEPARATOR;
        String taskIsDeleted = Boolean.toString(task.isDeleted()) + MESSAGE_SEPARATOR;
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
    }

    private Task stringToTask(String taskString) throws FileFormatNotSupportedException {
        int arrayIndex;
        String tagStored;
        int taskStored;
        Task task = new Task();
        // ??need to confirm date format
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar taskDateDue = Calendar.getInstance();
        Calendar taskDateStart = Calendar.getInstance();
        Calendar taskDateEnd = Calendar.getInstance();

        try{
            String[] taskStringArray = taskString.split(MESSAGE_SEPARATOR);
            int taskID = Integer.valueOf(taskStringArray[ID_ATTRIBUTE]);
            String taskName = taskStringArray[NAME_ATTRIBUTE];       
            taskDateDue.setTime(dateFormat.parse(taskStringArray[DATE_DUE_ATTRIBUTE]));
            taskDateStart.setTime(dateFormat.parse(taskStringArray[DATE_START_ATTRIBUTE]));
            taskDateEnd.setTime(dateFormat.parse(taskStringArray[DATE_END_ATTRIBUTE]));
            PriorityLevelEnum taskPriorityLevel = PriorityLevelEnum.fromInteger(Integer.valueOf(taskStringArray[PRIORITY_LEVEL_ATTRIBUTE]));
            String taskNote = taskStringArray[NOTE_ATTRIBUTE];
            boolean taskIsDeleted = Boolean.valueOf(taskStringArray[IS_DELETED_ATTRIBUTE]);
            boolean taskIsConfirmed = Boolean.valueOf(taskStringArray[IS_COMFIRMED_ATTRIBUTE]);
            ArrayList<String> taskTags = new ArrayList<String>();
            arrayIndex = TAGS_ATTRIBUTE;
            while (!taskStringArray[arrayIndex].equals(MESSAGE_PARENT_TASKS)) {
                tagStored = taskStringArray[arrayIndex];
                taskTags.add(tagStored);
                arrayIndex ++;
            }
            ArrayList<Integer> taskParentTasks = new ArrayList<Integer>();
            arrayIndex ++;
            while (!taskStringArray[arrayIndex].equals(MESSAGE_CHILD_TASKS)) {
                taskStored = Integer.valueOf(taskStringArray[arrayIndex]);
                taskParentTasks.add(taskStored);
                arrayIndex ++;
            }
            ArrayList<Integer> taskChildTasks = new ArrayList<Integer>();
            arrayIndex ++;
            while (!taskStringArray[arrayIndex].equals(MESSAGE_CONDITIONAL_TASKS)) {
                taskStored = Integer.valueOf(taskStringArray[arrayIndex]);
                taskChildTasks.add(taskStored);
                arrayIndex ++;
            }
            ArrayList<Integer> taskConditionalTasks = new ArrayList<Integer>();
            arrayIndex ++;
            while (arrayIndex <= (taskStringArray.length - 1)) {
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

    // Add/Update a task to file
    public void writeTaskToFile(Task task) throws TaskNotFoundException, IOException {
        int taskID = task.getId();
        if (taskID == ID_FOR_NEW_TASK) {
            // Add new task to task file
            task.setId(nextTaskIndex);
            nextTaskIndex ++;
            addTask(task);
            // Add new task to task buffer
            taskBuffer.add(task);
        } else {
            if (isTaskExist(taskID)) {
                // Update task to task file
                updateTask();
                // Update task to task buffer
                taskBuffer.set(taskID, task);
            } else {
                throw new TaskNotFoundException("Cannot update task since the current task doesn't exist");
            }
        }
    }

    // Delete a task from file
    public void deleteTaskFromFile(int taskID) throws TaskNotFoundException, IOException {
        if (isTaskExist(taskID)){
            for (Task task: taskBuffer) {
                if (task.getId() == taskID) {
                    task.setDeleted(true);
                    break;
                }
            }
            updateTask();  
        } else {
            throw new TaskNotFoundException("Cannot delete task since the current task doesn't exist");
        }
    }

    // Check whether the current task exists or not
    private boolean isTaskExist(int taskID) {
        if (taskID >= nextTaskIndex) {
            return false;
        } else {
            return true;
        }
    } 

    // append task string to the end of the file
    private void addTask(Task task) throws IOException { 
    	BufferedWriter bufferedWriter = null;
        try {
            String taskString = TaskToString(task);
            bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true));
            bufferedWriter.write(taskString);
            bufferedWriter.close();
        } finally {
            
        }
    }

    private void updateTask() throws IOException {
    	BufferedWriter bufferedWriter = null;
        try {
            String taskString;
            bufferedWriter = new BufferedWriter(new FileWriter(dataFile));
            for (Task task: taskBuffer) {
                taskString = TaskToString(task);
                bufferedWriter.write(taskString + "\r\n");
            }
            bufferedWriter.close();
        } finally {        	
        }
    }

    // Get a task by task ID
    public Task getTask(int taskID) throws TaskNotFoundException {
    	Task requiredTask = null;
        if (isTaskExist(taskID)) {
            for (Task task: taskBuffer) {
                if (task.getId() == taskID) {
                    requiredTask = task;
                }
            }
        } else {
            throw new TaskNotFoundException("Cannot return  task since the current task doesn't exist");
        }
        return requiredTask;
    }

    // Get all tasks
    public ArrayList<Task> getAllTasks() {
        return taskBuffer;
    }

    // Get a list of tasks that are done
    public ArrayList<Task> getCompletedTasks() {
        ArrayList<Task> completedTaskList = new ArrayList<Task>();
        for (Task task: taskBuffer) {
            if (task.getDateEnd() == null) {
                continue;
            } else {
                completedTaskList.add(task);
            }
        }
        return completedTaskList;
    }

    // Get a list of tasks that are not completed
    public ArrayList<Task> getActiveTasks() {
        ArrayList<Task> activeTaskList = new ArrayList<Task>();
        for (Task task: taskBuffer) {
            if (task.getDateEnd() == null) {
                activeTaskList.add(task);
            } else {
                continue;
            }
        }
        return activeTaskList;
    }

    // Search a list of tasks with certain tags
    public ArrayList<Task> searchTask(ArrayList<String> tags) {
        ArrayList<Task> taskList = new ArrayList<Task>();
        boolean hasTags;
        for (Task task: taskBuffer) {
            hasTags = true;
            for (String tag: tags) {
                if (task.getTags().contains(tag)) {
                    continue;
                } else {
                    hasTags = false;
                    break;
                }
            }
            if (hasTags) {
                taskList.add(task);
            }
        }
        return taskList;
    }
}