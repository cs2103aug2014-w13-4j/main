package storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.logging.Level;

import command.ParamEnum;
import common.ApplicationLogger;
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
public class StorageApi {
    private static StorageApi storageInstance;
    private TaskStorage taskFile;

    private static final String FILE_NAME_TASK_STORAGE = "taskStorage.data";

    /**
     * This constructor follows the singleton pattern.
     * It can only be called within the current class (StorageApi.getInstance()) 
     * This is to ensure that only there is exactly one instance of StorageApi class
     *
     * @throws FileFormatNotSupportedException
     * @throws IOException
     */
    protected StorageApi() throws IOException, FileFormatNotSupportedException {
        ApplicationLogger.getLogger().log(Level.INFO,
                "Initializing Storage.");
    }

    /**
     * Always creates a new instance of the StorageApi class. 
     * This follows the singleton pattern.
     *
     * @return An object instance of the StorageApi class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static StorageApi getInstance() throws IOException,
            FileFormatNotSupportedException {
        if (storageInstance == null) {
            storageInstance = new StorageApi();
            storageInstance.taskFile = TaskStorage
                    .getInstance(FILE_NAME_TASK_STORAGE);
        }
        return storageInstance;
    }

    /**
     * Always creates a new instance of the StorageApi class. 
     * For debugging purposes.
     *
     * @return An object instance of the StorageApi class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static StorageApi getNewInstance() throws IOException,
            FileFormatNotSupportedException {
        storageInstance = new StorageApi();
        storageInstance.taskFile = TaskStorage
                .getNewInstance(FILE_NAME_TASK_STORAGE);
        return storageInstance;
    }

    /**
     * Add or update a task back to storage
     *
     * @param task
     *            : task to be added or updated
     * @throws TaskNotFoundException
     *             : trying to update a not existing task
     * @throws IOException
     *             : wrong IO operations
     * @throws TimeIntervalOverlapException
     */
    public void writeTaskToFile(Task task) throws TaskNotFoundException,
            IOException, TimeIntervalOverlapException {
        ApplicationLogger.getLogger().log(Level.INFO,
                "Writing Task to file.");
        taskFile.writeTaskToFile(task);
    }

    /**
     * Get a task by its id
     *
     * @param taskID
     *            : the id of a task
     * @return a task
     * @throws TaskNotFoundException
     *             : trying to get a not existing task
     */
    public Task getTask(int taskID) throws TaskNotFoundException {
        return taskFile.getTask(taskID);
    }

    /**
     * Get a copy of an exiting task by its id
     *
     * @param taskID
     *            : the id of a task
     * @return a copy of an exiting task
     * @throws TaskNotFoundException
     *             : trying to get a not existing task
     */
    public Task getTaskCopy(int taskID) throws TaskNotFoundException {
        return taskFile.getTaskCopy(taskID);
    }

    /**
     * Get all tasks that are not deleted
     *
     * @return all tasks that are not deleted
     */
    public ArrayList<Task> getAllTasks() {
        return taskFile.getAllTasks();
    }

    /**
     * Get all tasks that are active
     *
     * @return all tasks that are active
     */
    public ArrayList<Task> getAllActiveTasks() {
        return taskFile.getAllActiveTasks();
    }

    /**
     * Get all tasks that are completed but not deleted
     *
     * @return all tasks that completed but not deleted
     */
    public ArrayList<Task> getAllCompletedTasks() {
        return taskFile.getAllCompletedTasks();
    }

    /**
     * Search tasks by the given keyword table
     *
     * @param keyWordTable
     *            : the key word table to be searched
     * @return the search result
     * @throws InvalidDateFormatException
     * @throws InvalidInputException 
     */
    public ArrayList<Task> searchTask(
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable)
            throws InvalidDateFormatException, InvalidInputException {
        return taskFile.searchTask(keyWordTable);
    }

    /**
     * Search suggested tasks by the given keyword table
     *
     * @param keyWordTable
     *            : the key word table to be searched
     * @return the search result
     * @throws InvalidDateFormatException
     * @throws InvalidInputException 
     */
    public ArrayList<Task> suggestedSearchTask(
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable) 
            throws InvalidDateFormatException, InvalidInputException {
        Hashtable<ParamEnum, ArrayList<String>> searchKeyWordTable =
            (Hashtable<ParamEnum, ArrayList<String>>) keyWordTable.clone();
        searchKeyWordTable.remove(ParamEnum.NAME);
        ArrayList<Task> taskList = searchTask(searchKeyWordTable);
        Collections.sort(taskList);
        return taskList;
    }
}