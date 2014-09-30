package storage.taskStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
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
            task = TaskConverter.stringToTask(fileScanner.nextLine());
            taskBuffer.add(task);
            nextTaskIndex ++;
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
                // Update task to task buffer
                taskBuffer.set(taskID, task);
                // Update task to task file
                updateTask();
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
            String taskString = TaskConverter.taskToString(task);
            bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true));
            bufferedWriter.write(taskString + "\r\n");
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
                taskString = TaskConverter.taskToString(task);
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
        // check whether there are tasks in storage
        if (taskBuffer == null) {
            return null;
        }
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
        // check whether there are tasks in storage
        if (taskBuffer == null) {
            return null;
        }
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
        // check whether there are tasks in storage
        if (taskBuffer == null) {
            return null;
        }
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