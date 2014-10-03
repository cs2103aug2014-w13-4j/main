package interfaces;

import java.io.IOException;
import java.util.ArrayList;

import exceptions.TaskNotFoundException;
import models.Task;

/**
 *
 * @author Chuyu 
 * This interface supports all the storage functionality
 */
public interface IStorage {
    // Add/Update a task to file
    void writeTaskToFile(Task task) throws TaskNotFoundException, IOException;

    // delete a task to file
    void deleteTaskFromFile(int taskID) throws TaskNotFoundException, IOException;

    // Get a task by task ID
    Task getTask(int taskID) throws TaskNotFoundException;

    // Get a list of all the Tasks
    ArrayList<Task> getAllTasks();

    // Get a list of tasks that are done
    ArrayList<Task> getCompletedTasks();

    // Get a list of tasks that are not completed
    ArrayList<Task> getActiveTasks();

    // Get a list of tags 
    ArrayList<String> getAllTags();

    // Search a list of tasks with certain tags
    ArrayList<Task> searchTask(ArrayList<String> tag);
}