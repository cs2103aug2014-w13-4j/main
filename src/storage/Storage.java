package storage;

import java.io.IOException;
import java.util.ArrayList;

import storage.tagStorage.TagStorage;
import storage.taskStorage.TaskStorage;
import models.Task;
import models.exceptions.FileFormatNotSupportedException;
import models.exceptions.TaskNotFoundException;

/**
 *
 * @author Chuyu 
 * This class reads/writes task to file.
 * It also supports power search.
 */
public class Storage implements IStorage {
    private TaskStorage taskFile;
    private TagStorage tagFile;    

    /**
     * constructor
     * This constructor follows the singleton pattern
     * It can only be called with in the current class (Storage.getInstance())
     * This is to ensure that only there is exactly one instance of Storage class
     * @throws FileFormatNotSupportedException, IOException
     */
    public Storage() throws IOException, FileFormatNotSupportedException{
        taskFile = new TaskStorage("taskStorage.data");
        tagFile = new TagStorage("TagStorage.data");
    }

    // Add/Update a task to file
    public void writeTaskToFile(Task task) throws TaskNotFoundException, IOException {
        taskFile.writeTaskToFile(task);
        tagFile.updateTagToFile(task.getTags());
    }

    // Delete a task from file
    public void deleteTaskFromFile(int taskID) throws TaskNotFoundException, IOException {
        taskFile.deleteTaskFromFile(taskID); 
    }

    // Get a task by task ID
    public Task getTask(int taskID) throws TaskNotFoundException {
        return taskFile.getTask(taskID);
    }

    // Get a list of all the Tasks
    public ArrayList<Task> getAllTasks() {
        return taskFile.getAllTasks();
    }

    // Get a list of tasks that are done
    public ArrayList<Task> getCompletedTasks() {
        return taskFile.getCompletedTasks();
    }

    // Get a list of tasks that are not completed
    public ArrayList<Task> getActiveTasks() {
        return taskFile.getActiveTasks();
    }

    // Get a list of tags 
    public ArrayList<String> getAllTags() {
    	return tagFile.getAllTags();
    }

    // Search a list of tasks with certain tags
    public ArrayList<Task> searchTask(ArrayList<String> tags) {
        return taskFile.searchTask(tags);
    }
}