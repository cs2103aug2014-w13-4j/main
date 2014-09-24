package storage;

import models.Task;
import models.exceptions.TaskNotFoundException;

/**
 *
 * @author chuyu 
 * This class reads/writes task to file.
 * It also supports power search.
 */
public interface Storage implements IStorage {
    private ArrayList<String> tagBuffer;
    private TaskStorage taskFile;
    private TagStorage tagFile;    

    /**
     * constructor
     * This constructor follows the singleton pattern
     * It can only be called with in the current class (Storage.getInstance())
     * This is to ensure that only there is exactly one instance of Storage class
     */
    public Storage() throws IOException{
        taskFile = new TaskStorage("taskStorage.data");
        tagFile = new TagStorage("TagStorage.data");
        taskBuffer = taskFile.getAllTasks();
        tagBuffer = tagFile.getAllTags();
        nextTaskIndex = taskFile.getNestTaskIndex();
    }

    // Add/Update a task to file
    public void writeTaskToFile(Task task) throws TaskNotFoundException, IOException {
        taskFile.writeTaskToFile(task);
        tagFile.updateTagToFile(task.tags);
    }

    // Delete a task from file
    public void deleteTaskFromFile(int taskID) throws TaskNotFoundException, IOException {
        taskFile.deleteTaskFromFile(taskID); 
    }

    private void updateTags(Task task) {
        for (String tag: task.tags) {
            if (tagBuffer.contains(tag)) {
                continue;
            } else {
                tagFile.addToFile(tag);
                tagBuffer.add(tag);
            }
        }        
    }

    // Get a task by task ID
    Task getTask(int taskID) throws TaskNotFoundException {
        taskFile.getTask(taskID);
    }

    // Get a list of all the Tasks
    ArrayList<Task> getAllTasks() {
        taskFile.getAllTasks();
    }

    // Get a list of tasks that are done
    ArrayList<Task> getCompletedTasks() {
        taskFile.getCompletedTasks();
    }

    // Get a list of tasks that are not completed
    ArrayList<Task> getActiveTasks() {
        tagFile.getActiveTasks();
    }

    // Get a list of tags 
    ArrayList<String> getTags();

    // Search a list of tasks with certain tags
    ArrayList<Task> searchTask(ArrayList<String> tag);

}