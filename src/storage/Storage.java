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
    private ArrayList<Task> taskBuffer;
    private ArrayList<String> tagBuffer;
    private int nextTaskIndex;
    private TaskStorage taskFile;
    private TagStorage tagFile;
    private static Storage instance = null;

    /**
     * constructor
     * This constructor follows the singleton pattern
     * It can only be called with in the current class (Storage.getInstance())
     * This is to ensure that only there is exactly one instance of Storage class
     */
    private Storage(){
        taskFile = new TaskStorage("taskStorage.data");
        tagFile = new TagStorage("TagStorage.data");
        taskBuffer = taskFile.getAllTasks();
        tagBuffer = tagFile.getAllTags();
        nextTaskIndex = taskFile.getNestTaskIndex();
    }

    public static Storage getInstance() throws IOException{
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    // Add/Update a task to file
    void writeTaskToFile(Task task);

    // delete a task to file
    void deleteTaskFromFile(int taskID) throws TaskNotFoundException;

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