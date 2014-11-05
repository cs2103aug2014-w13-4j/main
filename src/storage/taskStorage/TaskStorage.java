package storage.taskStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.rits.cloning.Cloner;

import command.ParamEnum;
import common.DateParser;
import common.IntervalSearch;
import common.StartDueDatePair;
import common.Task;
import common.exceptions.FileFormatNotSupportedException;
import common.exceptions.InvalidDateFormatException;
import common.exceptions.InvalidInputException;
import common.exceptions.TaskNotFoundException;
import common.exceptions.TimeIntervalOverlapException;

/**
 *
 * @author Chuyu This class reads/writes task to file. It also supports power
 *         search.
 */
public class TaskStorage {
    private static TaskStorage taskStorageInstance = null;

    private static final int MAX_DIFF_BETWEEN_WORDS = 3;
    private static final int MIN_INDEX = 1;
    private ArrayList<Task> taskBuffer;
    private int nextTaskIndex;
    private File dataFile;
    private IntervalSearch intervalTree;

    private static final int ID_FOR_NEW_TASK = 0;
    private static final int ID_FOR_FIRST_TASK = 1;

    private static final String COMPLETED = "completed";
    private static final String ACTIVE = "active";

    private Scanner fileScanner;

    /**
     * constructor``
     *
     * @throws FileFormatNotSupportedException
     *             , IOException
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
            // add in interval tree
            if (isTaskTimeValid(task) && !task.isDeleted()) {
                addTimeIntervalToIntervalTree(task);
            } else if (!isTaskTimeValid(task) && !task.isDeleted()) {
                throw new FileFormatNotSupportedException(
                        "Events are overlapping");
            }
            nextTaskIndex++;
        }
        fileScanner.close();
    }

    public static TaskStorage getInstance(String fileName) throws IOException,
    FileFormatNotSupportedException {
        if (taskStorageInstance == null) {
            taskStorageInstance = new TaskStorage(fileName);
        }
        return taskStorageInstance;
    }

    public static TaskStorage getNewInstance(String fileName)
            throws IOException, FileFormatNotSupportedException {
        taskStorageInstance = new TaskStorage(fileName);
        return new TaskStorage(fileName);
    }

    /**
     * Return an interval tree for the whole list of tasks
     *
     * @return IntervalSearch: the interval tree for the whole list of tasks
     */
    public IntervalSearch getIntervalTree() {
        return intervalTree;
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
        if (taskID == ID_FOR_NEW_TASK) {
            addTask(task);
        } else if (isTaskExist(taskID)) {
            if (task.isDeleted()) {
                deleteTask(task);
            } else {
                // check if it is an undo (task in task storage was deleted)
                Task oldTask = getTask(task.getId());
                if (oldTask.isDeleted()) {
                    restoreTask(task);
                } else {
                    updateTask(task);
                }
            }
        } else {
            throw new TaskNotFoundException(
                    "Cannot update task since the current task doesn't exist");
        }
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
     * Check whether a time interval overlaps with the exising time interval
     *
     * @param dateStart
     *            : start time
     * @param dateEnd
     *            : end date
     * @return boolean: whether a task overlaps with the exising time interval
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
     * Check whether a task overlaps with the exising time interval
     *
     * @param taskID
     *            : the task id to be checked
     * @return boolean: whether a task overlaps with the exising time interval
     */
    private boolean isTaskTimeValid(Task task) {
        int taskId = task.getId();
        Calendar dateStart, dateEnd;
        boolean isValid = true;
        ArrayList<StartDueDatePair> conditionalDates;

        if (task.isTimedTask()) {
            dateStart = task.getDateStart();
            dateEnd = task.getDateEnd();
            isValid = isTimeIntervalValid(taskId, dateStart, dateEnd);
        } else if (task.isConditionalTask()) {
            conditionalDates = task.getConditionalDates();
            for (StartDueDatePair datePair : conditionalDates) {
                dateStart = datePair.getStartDate();
                dateEnd = datePair.getDueDate();
                if (!isTimeIntervalValid(taskId, dateStart, dateEnd)) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
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
        ArrayList<StartDueDatePair> conditionalDates;
        if (task.isTimedTask()) {
            dateStart = task.getDateStart();
            dateEnd = task.getDateEnd();
            intervalTree.add(dateStart, dateEnd, taskId);
        } else if (task.isConditionalTask()) {
            conditionalDates = task.getConditionalDates();
            for (StartDueDatePair datePair : conditionalDates) {
                dateStart = datePair.getStartDate();
                dateEnd = datePair.getDueDate();
                intervalTree.add(dateStart, dateEnd, taskId);
            }
        }
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
            // Add new task to task file
            task.setId(nextTaskIndex);
            nextTaskIndex++;
            addTaskToStorage(task);
            // Add new task to task buffer
            taskBuffer.add(task);
            // Add new task to Interval Tree
            addTimeIntervalToIntervalTree(task);
        } else {
            throw new TimeIntervalOverlapException(
                    "New timed task overlaps with exisitng time interval.");
        }
    }

    /**
     * Update a task
     *
     * @param task
     *            : task to be updated
     * @throws IOException
     *             : wrong IO operations
     */
    private void updateTask(Task task) throws IOException,
    TimeIntervalOverlapException {
        int taskID = task.getId();
        if (isTaskTimeValid(task)) {
            // Update task to task buffer
            taskBuffer.set(taskID - 1, task);
            // Update task to task file
            updateTaskToStorage();
            removeTimeIntervalFromIntervalTree(task);
            addTimeIntervalToIntervalTree(task);
        } else {
            throw new TimeIntervalOverlapException(
                    "Updated task overlaps with exisitng time interval.");
        }
    }

    private void restoreTask(Task task) throws IOException,
    TimeIntervalOverlapException {
        int taskID = task.getId();
        if (isTaskTimeValid(task)) {
            // Update task to task buffer
            taskBuffer.set(taskID - 1, task);
            // Update task to task file
            updateTaskToStorage();
            addTimeIntervalToIntervalTree(task);
        } else {
            throw new TimeIntervalOverlapException(
                    "Updated task overlaps with exisitng time interval.");
        }

    }

    private void deleteTask(Task task) throws IOException {
        int taskID = task.getId();
        // Update task to task buffer
        taskBuffer.set(taskID - 1, task);
        // Update task to task file
        updateTaskToStorage();
        removeTimeIntervalFromIntervalTree(task);
    }

    // append task string to the end of the file
    private void addTaskToStorage(Task task) throws IOException {
        BufferedWriter bufferedWriter = null;
        String taskString = TaskConverter.taskToString(task);
        bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true));
        bufferedWriter.write(taskString + "\r\n");
        bufferedWriter.close();
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
                    "Cannot return  task since the current task doesn't exist");
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
                    "Cannot return  task since the current task doesn't exist");
        }
        return requiredTask;
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
            if (task.isDeleted()) {
                continue;
            } else {
                allTaskList.add(task);
            }
        }
        return allTaskList;
    }

    /**
     * Get all tasks that are completed but not deleted
     *
     * @return all tasks that are not deleted
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

    private boolean isSearchTargetByName(Task task, String name) {
        return task.getName().contains(name);
    }

    private boolean isSearchTargetByNote(Task task, String note) {
        return task.getNote().contains(note);
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

    private boolean isSearchTargetByPriorityLevel(Task task,
            String priorityLevel) {
        int priorityLevelInteger = Integer.valueOf(priorityLevel);
        return task.getPriorityLevelInteger().equals(priorityLevelInteger);
    }

    private boolean isSearchTargetByPriorityStatus(Task task, String status)
            throws InvalidInputException {
        status = status.toLowerCase();
        if (status.equals(COMPLETED)) {
            return task.isCompleted() && !task.isDeleted();
        } else if (status.equals(ACTIVE)) {
            // need double check
            return !task.isCompleted() && !task.isDeleted();
        } else {
            throw new InvalidInputException("Filter keyword is wrong.");
        }
    }

    private boolean isSearchTargetByBefore(Task task, String dateString)
            throws InvalidDateFormatException {
        Calendar date = DateParser.parseString(dateString);
        if (task.isConditionalTask() || task.isFloatingTask()) {
            return false;
        } else if (task.isDeadlineTask()) {
            // Is this considered as magic number?
            return task.getDateDue().compareTo(date) != 1;
        } else {
            return intervalTree.getTasksBefore(date).contains(task.getId());
        }
    }

    private boolean isLastIndex(ArrayList<String> arrayList, int i) {
        return i == arrayList.size() - 1;
    }

    private boolean isNearMatch(String stringToMatch, String stringInTask) {
        return StringUtils.getLevenshteinDistance(
                stringInTask.substring(
                        0,
                        Integer.min(stringInTask.length(),
                                stringToMatch.length())), stringToMatch) <= MAX_DIFF_BETWEEN_WORDS;
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
            // Is this considered as magic number?
            return task.getDateDue().compareTo(date) != -1;
        } else {
            return intervalTree.getTasksFrom(date).contains(task.getId());
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

    public ArrayList<Task> searchTask(
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable,
            ArrayList<Task> searchRange) throws InvalidDateFormatException,
            InvalidInputException {
        ArrayList<Task> taskList = (ArrayList<Task>) searchRange.clone();
        ArrayList<Task> parallelTaskList = (ArrayList<Task>) searchRange
                .clone();
        ArrayList<String> params;
        String firstParamElement, dateEnd;

        // exit if there is no keyword table
        if (keyWordTable == null) {
            return taskList;
        }

        for (ParamEnum key : keyWordTable.keySet()) {
            params = keyWordTable.get(key);
            firstParamElement = params.get(0);
            for (Task task : searchRange) {
                switch (key) {
                case NAME:
                    if (!isSearchTargetByName(task, firstParamElement)) {
                        taskList.remove(task);
                    }
                    if (!isNearMatchSearchTargetByName(task, firstParamElement)) {
                        parallelTaskList.remove(task);
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
                case STATUS:
                    if (!isSearchTargetByPriorityStatus(task, firstParamElement)) {
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
                    dateEnd = keyWordTable.get(ParamEnum.END_DATE).get(0);
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
        if (taskList.isEmpty()) {
            return parallelTaskList;
        } else {
            return taskList;
        }
    }

    private boolean isNearMatchSearchTargetByTag(Task task,
            ArrayList<String> params) {
        return isNearMatchTag(task.getTags(), params);
    }

    private boolean isNearMatchSearchTargetByNote(Task task,
            String firstParamElement) {
        return isNearMatch(firstParamElement, task.getName());
    }

    private boolean isNearMatchSearchTargetByName(Task task,
            String firstParamElement) {
        return isNearMatch(firstParamElement, task.getName());
    }
}
