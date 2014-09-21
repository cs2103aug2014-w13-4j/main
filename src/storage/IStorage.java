package storage;

import models.Task;
import models.CommandBuffer;

/**
 *
 * @author chuyu 
 * This interface supports all the storage functionality
 */
public interface Storage {
    // Pass in a command object to storage to execute the storage functionalities
    void executeCommand(Command command);

    // Get a list of all the Tasks
    ArrayList<Task> getTaskList();

    // Get a task by task ID
    Task getTask(int taskID);
}