package storage.taskStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Scanner;

import common.*;
import org.apache.commons.lang3.StringUtils;

import com.rits.cloning.Cloner;

import command.ParamEnum;
import common.StartEndDatePair;
import common.exceptions.FileFormatNotSupportedException;
import common.exceptions.InvalidDateFormatException;
import common.exceptions.InvalidInputException;
import common.exceptions.TaskNotFoundException;
import common.exceptions.TimeIntervalOverlapException;

/**
 * This is the task storage class. It supports writing tasks to storage 
 * as well as reading tasks from storage. Moreover, it also includes search 
 * functionalities.
 *
 * @author Chuyu 
 */
public class TaskStorage {
	/**
     * Always creates a new instance of the TaskStorage class. 
     * This follows the singleton pattern.
     *
     * @param fileName 
     *				: Name of the File
     * @return An object instance of the TaskStorage class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static TaskStorage getInstance(String fileName) throws IOException,
            FileFormatNotSupportedException {
        if (taskStorageInstance == null) {
            taskStorageInstance = new TaskStorage(fileName);
        }
        return taskStorageInstance;
    }

    /**
     * Always creates a new instance of the TaskStorage class. 
     * For debugging purposes.
     *
     * @param fileName 
     *				: Name of the File
     * @return an object instance of the TaskStorage class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static TaskStorage getNewInstance(String fileName)
            throws IOException, FileFormatNotSupportedException {
        taskStorageInstance = new TaskStorage(fileName);
        return new TaskStorage(fileName);
    }

    private static final String INVALID_SEARCH_KEYWORD = "The search keyword: %1$s is invalid";
    private static TaskStorage taskStorageInstance = null;
    private static final int MAX_DIFF_BETWEEN_WORDS = 3;
    private static final int MIN_INDEX = 1;
    private ArrayList<Task> taskBuffer;
    private int nextTaskIndex;

    private File dataFile;
    private IntervalSearch intervalTree;

    private static final int ID_FOR_FIRST_TASK = 1;

    private static final int MIN_LENGTH_FOR_KEY_WORD_IN_SEARCH = 2;

    private static final String COMPLETED = "completed";

    private static final String ACTIVE = "active";

    private static final String ALL = "all";

    private Scanner fileScanner;

    /**
     * This constructor follows the singleton pattern.
     * It can only be called within the current class (TaskStorage.getInstance()) 
     * This is to ensure that only there is exactly one instance of TaskStorage class
     *
     * @param fileName 
     *				: Name of the File
     * @return an object instance of the TaskStorage class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    protected TaskStorage(String fileName) throws IOException,
            FileFormatNotSupportedException {
        Task task;
        dataFile = new File(fileName);

        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }

        fileScanner = new Scanner(dataFile);
        taskBuffer = new ArrayList<Task>();
        intervalTree = new IntervalSearch();
        nextTaskIndex = ID_FOR_FIRST_TASK;
        while (fileScanner.hasNextLine()) {
            task = TaskConverter.stringToTask(fileScanner.nextLine());
            taskBuffer.add(task);
            // add the new task into the interval tree
            if (isTaskTimeValid(task) && !task.isDeleted()) {
                addTimeIntervalToIntervalTree(task);
            } else if (!isTaskTimeValid(task) && !task.isDeleted()) {
                throw new FileFormatNotSupportedException(
                        "Events are overlapping!");
            }
            nextTaskIndex ++;
        }
        fileScanner.close();
    }

    /**
     * Get all tasks that are active
     *
     * @return all tasks that are active
     */
    public ArrayList<Task> getAllActiveTasks() {
        ArrayList<Task> activeList = new ArrayList<Task>();
        if (taskBuffer == null) {
            return null;
        }
        for (Task task : taskBuffer) {
            if (!task.isDeleted() && !task.isCompleted()) {
                activeList.add(task);
            }
        }
        return activeList;
    }

    /**
     * Get all tasks that are completed but not deleted
     *
     * @return all tasks that completed but not deleted
     */
    public ArrayList<Task> getAllCompletedTasks() {
        ArrayList<Task> completedList = new ArrayList<Task>();
        if (taskBuffer == null) {
            return null;
        }
        for (Task task : taskBuffer) {
            if (!task.isDeleted() && task.isCompleted()) {
                completedList.add(task);
            }
        }
        return completedList;
    }

    /**
     * Get all tasks that are not deleted
     *
     * @return all tasks that are not deleted
     */
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTaskList = new ArrayList<Task>();
        if (taskBuffer == null) {
            return null;
        }
        for (Task task : taskBuffer) {
            if (!task.isDeleted()) {                
                allTaskList.add(task);
            } 
        }
        return allTaskList;
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
        Task requiredTask = null;
        if (isTaskExist(taskID)) {
            for (Task task : taskBuffer) {
                if (task.getId() == taskID) {
                    requiredTask = task;
                    break;
                }
            }
        } else {
            throw new TaskNotFoundException(
                    "The task doesn't exist!");
        }
        return requiredTask;
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
        Task requiredTask = null;
        if (isTaskExist(taskID)) {
            for (Task task : taskBuffer) {
                if (task.getId() == taskID) {
                    Cloner cloner = new Cloner();
                    requiredTask = cloner.deepClone(task);
                    break;
                }
            }
        } else {
            throw new TaskNotFoundException(
                    "The task doesn't exist!");
        }
        return requiredTask;
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
        ArrayList<Task> searchRange = getSearchRange(keyWordTable);
        ArrayList<Task> taskList = (ArrayList<Task>) searchRange.clone();
        ArrayList<Task> parallelTaskList = (ArrayList<Task>) searchRange
                .clone();

        // exit if there is no keyword table
        if (keyWordTable == null) {
            return taskList;
        }

        getSearchResults(keyWordTable, searchRange, taskList, parallelTaskList);
        if (taskList.isEmpty()) {
            return parallelTaskList;
        } else {
            return taskList;
        }
    }

    private void getSearchResults(
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable,
            ArrayList<Task> searchRange, ArrayList<Task> taskList,
            ArrayList<Task> parallelTaskList) throws InvalidDateFormatException {
        for (ParamEnum key : keyWordTable.keySet()) {
            ArrayList<String> params = keyWordTable.get(key);
            String firstParamElement = params.get(0);
            for (Task task : searchRange) {
                switch (key) {
                case NAME:
                    if (!isNearMatchSearchTargetByName(task, firstParamElement)) {
                        parallelTaskList.remove(task);
                        taskList.remove(task);
                    } else if (!isSearchTargetByName(task, firstParamElement)) {
                        taskList.remove(task);
                    }
                    break;
                case NOTE:
                    if (!isNearMatchSearchTargetByNote(task, firstParamElement)) {
                        parallelTaskList.remove(task);
                        taskList.remove(task);
                    } else if (!isSearchTargetByNote(task, firstParamElement)) {
                        taskList.remove(task);
                    }
                    break;
                case TAG:
                    if (!isNearMatchSearchTargetByTag(task, params)) {
                        parallelTaskList.remove(task);
                        taskList.remove(task);
                    } else if (!isSearchTargetByTag(task, params)) {
                        taskList.remove(task);
                    }
                    break;
                case LEVEL:
                    if (!isSearchTargetByPriorityLevel(task, firstParamElement)) {
                        taskList.remove(task);
                        parallelTaskList.remove(task);
                    }
                    break;
                case BEFORE:
                    if (!isSearchTargetByBefore(task, firstParamElement)) {
                        taskList.remove(task);
                        parallelTaskList.remove(task);
                    }
                    break;
                case AFTER:
                    if (!isSearchTargetByAfter(task, firstParamElement)) {
                        taskList.remove(task);
                        parallelTaskList.remove(task);
                    }
                    break;
                case START_DATE:
                    assert keyWordTable.get(ParamEnum.END_DATE) != null;
                    String dateEnd = keyWordTable.get(ParamEnum.END_DATE)
                            .get(0);
                    if (!isSearchTargetByInterval(task, firstParamElement,
                            dateEnd)) {
                        taskList.remove(task);
                        parallelTaskList.remove(task);
                    }
                    break;
                case ON:
                    if (!isSearchTargetByOn(task, firstParamElement)) {
                        taskList.remove(task);
                        parallelTaskList.remove(task);
                    }
                default:
                    break;
                }
            }
            searchRange = (ArrayList<Task>) parallelTaskList.clone();
        }

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
        int taskID = task.getId();
        if (taskID == Task.ID_FOR_NEW_TASK) {
            addTask(task);
        } else if (isTaskExist(taskID)) {
        	updateTask(task);
        } else {
            throw new TaskNotFoundException(
                    "Cannot update task since the current task doesn't exist");
        }
    }

    /**
     * Add a task
     *
     * @param task
     *            : task to be added
     * @throws IOException
     *             : wrong IO operations
     */
    private void addTask(Task task) throws IOException,
            TimeIntervalOverlapException {
        if (isTaskTimeValid(task)) {
            task.setId(nextTaskIndex);
            nextTaskIndex++;            
            // Add new task to task file
            addTaskToStorage(task);
            // Add new task to task buffer
            taskBuffer.add(task);
            // Add new task to Interval Tree
            addTimeIntervalToIntervalTree(task);
        } else {
            throw new TimeIntervalOverlapException(
                    "New timed task overlaps with existing time interval.");
        }
    }

    private void addTaskToStorage(Task task) throws IOException {
        BufferedWriter bufferedWriter = null;
        String taskString = TaskConverter.taskToString(task);
        bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true));
        bufferedWriter.write(taskString + "\r\n");
        bufferedWriter.close();
    }

    /**
     * Add time intervals to interval tree
     *
     * @param task
     *            : task that contains the time interval to be added
     */
    private void addTimeIntervalToIntervalTree(Task task) {
        int taskId = task.getId();
        Calendar dateStart, dateEnd;
        if (task.isTimedTask()) {
            dateStart = task.getDateStart();
            dateEnd = task.getDateEnd();
            intervalTree.add(dateStart, dateEnd, taskId);
        } else if (task.isConditionalTask()) {
            ArrayList<StartEndDatePair> conditionalDates = task.getConditionalDates();
            for (StartEndDatePair datePair : conditionalDates) {
                dateStart = datePair.getStartDate();
                dateEnd = datePair.getEndDate();
                intervalTree.add(dateStart, dateEnd, taskId);
            }
        }
    }

    private ArrayList<Task> getSearchRange(
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable)
            throws InvalidInputException {
        ArrayList<Task> searchRange = null;
        String keyWordString = keyWordTable.get(ParamEnum.KEYWORD).get(0)
                .toLowerCase();
        if (keyWordString.equals(ALL)) {
            searchRange = getAllTasks();
        } else if (keyWordString.equals(COMPLETED)) {
            searchRange = getAllCompletedTasks();
        } else if (keyWordString.equals(ACTIVE) || keyWordString.isEmpty()) {
            searchRange = getAllActiveTasks();
        } else {
            throw new InvalidInputException(MessageCreator.createMessage(
                    INVALID_SEARCH_KEYWORD, keyWordString, null));
        }
        return searchRange;
    }

    private boolean isLastIndex(ArrayList<String> arrayList, int i) {
        return i == arrayList.size() - 1;
    }

    private boolean isNearMatch(String stringToMatch, String stringInTask) {
        if (!stringInTask.isEmpty()) {
            if (stringToMatch.length() > stringInTask.length()) {
                return StringUtils.getLevenshteinDistance(stringInTask,
                        stringToMatch) <= MAX_DIFF_BETWEEN_WORDS;
            } else {
                //compare index by index to find the best substring to compare with
                for (int i = 0; i <= stringInTask.length()
                        - stringToMatch.length(); i++) {
                    String subString = stringInTask.substring(i, stringToMatch.length()
                            + i);
                    Boolean result = StringUtils.getLevenshteinDistance(
                            subString, stringToMatch) <= MAX_DIFF_BETWEEN_WORDS;
                    if (result) {
                        return result;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNearMatchSearchTargetByName(Task task,
            String firstParamElement) {
        return isNearMatch(firstParamElement, task.getName());
    }

    private boolean isNearMatchSearchTargetByNote(Task task,
            String firstParamElement) {
        return isNearMatch(firstParamElement, task.getNote());
    }

    private boolean isNearMatchSearchTargetByTag(Task task,
            ArrayList<String> params) {
        return isNearMatchTag(task.getTags(), params);
    }

    private boolean isNearMatchTag(ArrayList<String> tagsInTask,
            ArrayList<String> tagsToMatch) {
        if (tagsInTask.size() == 0) {
            return false;
        } else {
            for (String tagToMatch : tagsToMatch) {
                for (int i = 0; i < tagsInTask.size(); i++) {
                    String tagInTask = tagsInTask.get(i);
                    if (isNearMatch(tagToMatch, tagInTask)) {
                        break;
                    } else if (isLastIndex(tagsInTask, i)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isSearchTargetByAfter(Task task, String dateString)
            throws InvalidDateFormatException {
        Calendar date = DateParser.parseString(dateString);
        if (task.isConditionalTask() || task.isFloatingTask()) {
            return false;
        } else if (task.isDeadlineTask()) {
            return task.getDateDue().compareTo(date) != -1;
        } else {
            return intervalTree.getTasksFrom(date).contains(task.getId());
        }
    }

    private boolean isSearchTargetByBefore(Task task, String dateString)
            throws InvalidDateFormatException {
        Calendar date = DateParser.parseString(dateString);
        if (task.isConditionalTask() || task.isFloatingTask()) {
            return false;
        } else if (task.isDeadlineTask()) {
            return task.getDateDue().compareTo(date) != 1;
        } else {
            return intervalTree.getTasksBefore(date).contains(task.getId());
        }
    }

    private boolean isSearchTargetByInterval(Task task, String dateStartString,
            String dateEndString) throws InvalidDateFormatException {
        Calendar dateStart = DateParser.parseString(dateStartString);
        Calendar dateEnd = DateParser.parseString(dateEndString);
        if (task.isTimedTask()) {
            return task.getDateStart().compareTo(dateStart) != -1
                    && task.getDateEnd().compareTo(dateEnd) != 1;
        } else {
            return false;
        }
    }

    private boolean isSearchTargetByName(Task task, String name) {
        return isSearchTargetByContent(task, name, true, false);
    }

    private boolean isSearchTargetByNote(Task task, String note) {
    	return isSearchTargetByContent(task, note, false, true);
    }

    private boolean isSearchTargetByContent(Task task, String text, boolean isSearchingName,
    										boolean isSearchingNote) {
    	assert task != null;
    	assert text != null;

    	boolean result = true;
    	if (isSearchingName) {
    		result &= containsKeywordsInText(task.getName(), text);
    	}
    	if (isSearchingNote) {
    		result &= containsKeywordsInText(task.getNote(), text);
    	}
    	return result;
    }

    private boolean containsKeywordsInText(String text, String keywords) {
    	assert keywords != null;
    	assert text != null;
    	boolean result;
    	String[] keywordsArray = keywords.split(" ");
    	for (String keyword : keywordsArray) {
    		if (keyword.length() >= MIN_LENGTH_FOR_KEY_WORD_IN_SEARCH) {
    			result = text.toLowerCase().contains(keyword.toLowerCase());
    			if (!result) {
    				return false;
    			}
    		}
    	}
    	return true;
    }

    private boolean isSearchTargetByOn(Task task, String dateString)
            throws InvalidDateFormatException {
        String dateStartString, dateEndString;
        Calendar date = DateParser.parseString(dateString);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        dateStartString = DateParser.parseCalendar(date);
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        dateEndString = DateParser.parseCalendar(date);
        return isSearchTargetByBefore(task, dateEndString)
                && isSearchTargetByAfter(task, dateStartString);
    }

    private boolean isSearchTargetByPriorityLevel(Task task,
            String priorityLevel) {
        int priorityLevelInteger = Integer.valueOf(priorityLevel);
        return task.getPriorityLevelInteger().equals(priorityLevelInteger);
    }

    private boolean isSearchTargetByTag(Task task, ArrayList<String> tags) {
        // check whether the keyword is null
        if (tags == null) {
            return true;
        }
        for (String tag : tags) {
            if (task.getTags().contains(tag)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether a task is existing or not
     *
     * @param taskID
     *            : the task id to be checked
     * @return boolean: whether a task is existing or not
     */
    private boolean isTaskExist(int taskID) {
        return taskID >= MIN_INDEX && taskID < nextTaskIndex;
    }

    /**
     * Check whether a task overlaps with the existing time interval
     *
     * @param task
     *            : the task id to be checked
     * @return boolean: whether a task overlaps with the existing time interval
     */
    private boolean isTaskTimeValid(Task task) {
        int taskId = task.getId();
        Calendar dateStart, dateEnd;
        boolean isValid = true;
        ArrayList<StartEndDatePair> conditionalDates;

        if (task.isTimedTask()) {
            dateStart = task.getDateStart();
            dateEnd = task.getDateEnd();
            isValid = isTimeIntervalValid(taskId, dateStart, dateEnd);
        } else if (task.isConditionalTask()) {
            conditionalDates = task.getConditionalDates();
            for (StartEndDatePair datePair : conditionalDates) {
                dateStart = datePair.getStartDate();
                dateEnd = datePair.getEndDate();
                if (!isTimeIntervalValid(taskId, dateStart, dateEnd)) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    /**
     * Check whether a time interval overlaps with the existing time interval
     *
     * @param dateStart
     *            : start time
     * @param dateEnd
     *            : end date
     * @return boolean: whether a task overlaps with the existing time interval
     */
    private boolean isTimeIntervalValid(int taskId, Calendar dateStart,
            Calendar dateEnd) {
        ArrayList<Integer> overlapTask = intervalTree.getTasksWithinInterval(
                dateStart, dateEnd);
        boolean isValid = true;
        if (!overlapTask.isEmpty()) {
            for (Integer overlapTaskId : overlapTask) {
                if (overlapTaskId != taskId) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    /**
     * Remove time intervals to interval tree
     *
     * @param task
     *            : task that contains the time interval to be added
     */
    private void removeTimeIntervalFromIntervalTree(Task task) {
        intervalTree.remove(task);
    }

    /**
     * Update a task
     *
     * @param task
     *            : task to be updated
     * @throws IOException
     *             : wrong IO operations
     * @throws TaskNotFoundException 
     */
    private void updateTask(Task task) throws IOException,
            TimeIntervalOverlapException, TaskNotFoundException {
        int taskID = task.getId();
        Task oldTask = getTask(task.getId());
        if (isTaskTimeValid(task)) {
            // Update task to task buffer
            taskBuffer.set(taskID - 1, task);
            // Update task to task file
            updateTaskToStorage();
            // Update task to Interval tree
            if (task.isDeleted()) {
            	// check if it is a delete 
                removeTimeIntervalFromIntervalTree(task);
            } else if (oldTask.isDeleted()) {
                // check if it is an undo (task in task storage was deleted)
                addTimeIntervalToIntervalTree(task);
            } else {          
            	// normal update operation      
	            removeTimeIntervalFromIntervalTree(task);
	            addTimeIntervalToIntervalTree(task);
            }
        } else {
            throw new TimeIntervalOverlapException(
                    "Updated task overlaps with existing time interval.");
        }
    }

    private void updateTaskToStorage() throws IOException {
        BufferedWriter bufferedWriter = null;
        String taskString;
        bufferedWriter = new BufferedWriter(new FileWriter(dataFile));
        for (Task task : taskBuffer) {
            taskString = TaskConverter.taskToString(task);
            bufferedWriter.write(taskString + "\r\n");
        }
        bufferedWriter.close();
    }
}
