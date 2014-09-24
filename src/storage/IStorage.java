package storage;

import models.Task;
import models.exceptions.TaskNotFoundException;

/**
 *
 * @author chuyu 
 * This interface supports all the storage functionality
 */
public interface IStorage {
    // Get an instance of storage class
    // The storage class follows the singleton pattern
    // to ensure that there has exactly one instance the storage class
    Storage getInstance() throws IOException;

    // Add/Update a task to file
    void writeTaskToFile(Task task) throws IOException;

    // delete a task to file
    void deleteTaskFromFile(int taskID) throws TaskNotFoundException, IOException;

    // Get a task by task ID
    Task getTasks(int taskID) throws TaskNotFoundException;

    // Get a list of all the Tasks
    ArrayList<Task> getAllTasks();

    // Get a list of tasks that are done
    ArrayList<Task> getDoneTasks();

    // Get a list of tasks that are not completed
    ArrayList<Task> getActiveTasks();

    // Get a list of tags 
    ArrayList<String> getTags();

    // Search a list of tasks with certain tags
    ArrayList<Task> searchTask(ArrayList<String> tag);
}