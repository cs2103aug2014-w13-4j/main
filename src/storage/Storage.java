package storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;

import command.ParamEnum;
import common.ApplicationLogger;
import common.IntervalSearch;
import common.Task;
import common.exceptions.FileFormatNotSupportedException;
import common.exceptions.InvalidDateFormatException;
import common.exceptions.InvalidInputException;
import common.exceptions.TaskNotFoundException;
import common.exceptions.TimeIntervalOverlapException;
import storage.taskStorage.TaskStorage;

/**
 *
 * @author Chuyu This class reads/writes task to file. It also supports power
 *         search.
 */
public class Storage {
    private static Storage storageInstance;
    private TaskStorage taskFile;

    private static final String FILE_NAME_TASK_STORAGE = "taskStorage.data";

    /**
     * constructor This constructor follows the singleton pattern It can only be
     * called with in the current class (Storage.getInstance()) This is to
     * ensure that only there is exactly one instance of Storage class
     *
     * @throws FileFormatNotSupportedException
     *             , IOException
     */
    protected Storage() throws IOException, FileFormatNotSupportedException {
        ApplicationLogger.getLogger().log(Level.INFO,
                "Initializing Storage.");
    }

    public static Storage getInstance() throws IOException,
            FileFormatNotSupportedException {
        if (storageInstance == null) {
            storageInstance = new Storage();
            storageInstance.taskFile = TaskStorage
                    .getInstance(FILE_NAME_TASK_STORAGE);
        }
        return storageInstance;
    }

    public static Storage getNewInstance() throws IOException,
            FileFormatNotSupportedException {
        storageInstance = new Storage();
        storageInstance.taskFile = TaskStorage
                .getNewInstance(FILE_NAME_TASK_STORAGE);
        return storageInstance;
    }

    // Add/Update a task to file
    public void writeTaskToFile(Task task) throws TaskNotFoundException,
            IOException, TimeIntervalOverlapException {
        ApplicationLogger.getLogger().log(Level.INFO,
                "Writing Task to file.");
        taskFile.writeTaskToFile(task);
    }

    // Get a task by task ID
    public Task getTask(int taskID) throws TaskNotFoundException {
        return taskFile.getTask(taskID);
    }

    // Return a copy of existing task for update
    public Task getTaskCopy(int taskID) throws TaskNotFoundException {
        return taskFile.getTaskCopy(taskID);
    }

    // Get a list of all the Tasks
    public ArrayList<Task> getAllTasks() {
        return taskFile.getAllTasks();
    }

    // Get a list of all active tasks
    // To be the default display.
    public ArrayList<Task> getAllActiveTasks() {
        return taskFile.getAllActiveTasks();
    }

    // Get a list of all the completed task
    // This method is for clearing all the completed tasks
    public ArrayList<Task> getAllCompletedTasks() {
        return taskFile.getAllCompletedTasks();
    }

    // Search a list of tasks with certain key words
    // Assume keywords of name and note is only one string
    public ArrayList<Task> searchTask(
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable)
            throws InvalidDateFormatException, InvalidInputException {
        return taskFile.searchTask(keyWordTable);
    }

    // Search a list of tasks within certain interval only
    public ArrayList<Task> suggestedSearchTask(Hashtable<ParamEnum, ArrayList<String>> keyWordTable) throws InvalidDateFormatException, InvalidInputException {
        Hashtable<ParamEnum, ArrayList<String>> searchKeyWordTable = (Hashtable<ParamEnum, ArrayList<String>>) keyWordTable.clone();
        searchKeyWordTable.remove(ParamEnum.NAME);
        return searchTask(searchKeyWordTable);
    }

    public IntervalSearch getIntervalTree() {
        return taskFile.getIntervalTree();
    }
}