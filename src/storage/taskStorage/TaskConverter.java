package storage.taskStorage;

import com.google.gson.Gson;

import common.Task;
import common.exceptions.FileFormatNotSupportedException;

/**
 *
 * @author Chuyu This class converts a task to a string It also converts a
 *         formatted string to task
 */
class TaskConverter {
    /**
     * Converts a task to a string
     *
     * @param task
     *            : the task to be converted to a string
     * @return a string that represents a task
     */
    static String taskToString(Task task) {
        Gson gson = new Gson();
        String taskString = gson.toJson(task);
        return taskString;
    }

    /**
     * Converts a formatted string to a task
     *
     * @param taskString
     *            : a formatted string containing a task
     * @return a task
     * @throws FileFormatNotSupportedException
     */
    static Task stringToTask(String taskString)
            throws FileFormatNotSupportedException {
        try {            
            Gson gson = new Gson();
            Task task = gson.fromJson(taskString, Task.class);
            return task;
        } catch (Exception e) {
        	throw new FileFormatNotSupportedException("Storage File Corrupted.");
        }
    }
}