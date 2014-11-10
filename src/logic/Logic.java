package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Calendar;

import com.rits.cloning.Cloner;

import storage.StorageApi;
import command.CommandEnum;
import command.ParamEnum;
import common.ApplicationLogger;
import common.DateParser;
import common.Feedback;
import common.History;
import common.MessageCreator;
import common.Task;
import common.exceptions.FileFormatNotSupportedException;
import common.exceptions.HistoryNotFoundException;
import common.exceptions.InvalidCommandUseException;
import common.exceptions.InvalidDateFormatException;
import common.exceptions.InvalidInputException;
import common.exceptions.TaskNotFoundException;
import common.exceptions.TimeIntervalOverlapException;

//@author A0114368E

/**
 * This class provides the respective methods to execute the different commands
 * as specified by the user. It also interacts with storage to retrieve the
 * task, or to write the task back to storage
 *
 */
public class Logic {

    private static final int NEXT = 1;
    private static final int HOURS_TO_SECONDS = 3600;
    private static final String ERROR_SUGGEST_MESSAGE = "Start and end date and duration are required";
    private static final int MAX_INVALID_DURATION = 0;

    /**
     * This constructor follows the singleton pattern It can only be called with
     * in the current class (Logic.getInstance()) This is to ensure that only
     * there is exactly one instance of Logic class
     *
     * @return an object instance of the Logic class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static Logic getInstance() throws IOException,
            FileFormatNotSupportedException {
        if (instance == null) {
            instance = new Logic();
            ApplicationLogger.getLogger()
                    .log(Level.INFO, "Initializing Logic.");
            instance.storage = StorageApi.getInstance();
        }
        return instance;
    }

    /**
     * Always creates a new instance of the Logic class. For debugging purposes.
     *
     * @return an object instance of the Logic class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static Logic getNewInstance() throws IOException,
            FileFormatNotSupportedException {
        instance = new Logic();
        ApplicationLogger.getLogger().log(Level.INFO, "Initializing Logic.");
        instance.storage = StorageApi.getNewInstance();
        return instance;
    }

    private static final String LOG_TIMED_TASK = "Adding timed task.";
    private static final String LOG_DEADLINE_TASK = "Adding deadline task.";
    private static final String LOG_CONDITIONAL_TASK = "Adding conditional task.";
    private static final String LOG_FLOATING_TASK = "Adding floating task.";
    private static final String COMPLETED_KEYWORD = "completed";
    private static final String ALL_KEYWORD = "all";
    private static final String ACTIVE_KEYWORD = "active";
    private static final String ERROR_UNDO_MESSAGE = "Search and display actions cannot be undone.";
    private static final String INVALID_TASK_ID_MESSAGE = "Task ID: %1$s is invalid!";
    private static final String INVALID_DATE_ID_MESSAGE = "Date ID: %1$s is invalid!";
    private static final String ERROR_UPDATE_CONDITIONAL_TASK_MESSAGE = "Task %1$s is a conditional task, so it should contain multiple start and end dates";
    private static final String ERROR_COMPLETE_MESSAGE = "Only uncompleted deadline tasks can be completed";
    private static final String ERROR_DATE_INPUT_MESSAGE = "The date parameters provided are invalid.";
    private static final String ERROR_CLEAR_MESSAGE = "The given parameters for clear are invalid.";
    private static final String ERROR_DISPLAY_MESSAGE = "The display keyword: %1$s is invalid.";
    private static final String ADD_MESSAGE = "%1$s is successfully added.";
    private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
    private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
    private static final String COMPLETE_MESSAGE = "%1$s is marked as completed.";
    private static final String SEARCH_MESSAGE = "%1$s results are found.";
    private static final String DISPLAY_MESSAGE = "All %1$s tasks are displayed.";
    private static final String DISPLAY_TASK_MESSAGE = "Task %1$s: %2$s is displayed.";
    private static final String ERROR_ALREADY_DELETED_MESSAGE = "Task %1$s is already deleted.";
    private static final String CONFIRM_MESSAGE = "%1$s is marked as confirmed.";
    private static final String UNDO_MESSAGE = "%1$s %2$s is undone";
    private static final String UNDO_CLEAR_MESSAGE = "Clear is undone";
    private static final String CLEAR_MESSAGE = "All completed task are cleared from the list";
    private static final String SUGGESTION_MESSAGE = "We have some suggestions for you";
    private static final String NO_SUGGESTION_MESSAGE = "Sorry, we couldn't find a good slot for you";
    // Represent a thirty minutes block in milliseconds
    private static long TIME_BLOCK = 1800000;
    private static long HOUR_TO_MILLIS = 3600000;
    private static int MAX_RESULT = 3;
    private static int START_VALUE = 0;
    StorageApi storage = null;

    private LogicUndo logicUndo = new LogicUndo();

    private Cloner cloner = new Cloner();
    private ArrayList<Task> suggestions = new ArrayList<Task>();

    private static Logic instance = null;

    private Logic() {
    }

    /**
     * Accept a suggested date
     *
     * @return Feedback containing the updated list of tasks
     * @throws InvalidInputException
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws InvalidCommandUseException
     * @throws TimeIntervalOverlapException
     */
    Feedback accept(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidInputException, TaskNotFoundException, IOException,
            InvalidCommandUseException, TimeIntervalOverlapException {
        int taskId = getTaskId(param);
        Task task = getTaskFromSuggestion(taskId-1);
        String name = task.getName();
        TaskModifier.resetId(task);
        storage.writeTaskToFile(task);
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushAcceptCommandToHistory(clonedTask);
        ArrayList<Task> taskList = storage.getAllActiveTasks();
        suggestions.clear();
        return createTaskListFeedback(
                MessageCreator.createMessage(ADD_MESSAGE, name, null), taskList);
    }

    /**
     * Adds a new task to the file
     *
     * @param param
     *            : the command created by commandParser
     * @return feedback containing the updated list of tasks in the file, and
     *         the message.
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     * @throws TimeIntervalOverlapException
     */
    Feedback add(Hashtable<ParamEnum, ArrayList<String>> param)
            throws TaskNotFoundException, IOException,
            InvalidDateFormatException, InvalidInputException,
            TimeIntervalOverlapException {
        Task task = new Task();
        if (hasFloatingTaskParams(param)) {
            ApplicationLogger.getLogger().log(Level.INFO, LOG_FLOATING_TASK);
            TaskModifier.modifyFloatingTask(param, task);
        } else if (hasConditionalTaskParams(param)) {
            ApplicationLogger.getLogger().log(Level.INFO, LOG_CONDITIONAL_TASK);
            TaskModifier.modifyConditionalTask(param, task);
        } else if (hasTimedTaskParams(param)) {
            ApplicationLogger.getLogger().log(Level.INFO, LOG_TIMED_TASK);
            TaskModifier.modifyTimedTask(param, task);
        } else if (hasDeadlineTaskParams(param)) {
            ApplicationLogger.getLogger().log(Level.INFO, LOG_DEADLINE_TASK);
            TaskModifier.modifyDeadlineTask(param, task);
        } else {
            throw new InvalidInputException(ERROR_DATE_INPUT_MESSAGE);
        }
        storage.writeTaskToFile(task);
        String name = task.getName();
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushAddCommandToHistory(clonedTask);
        ArrayList<Task> taskList = storage.getAllActiveTasks();
        return createTaskAndTaskListFeedback(
                MessageCreator.createMessage(ADD_MESSAGE, name, null),
                taskList, task);
    }

    /**
     * Clear all the completed task in the task list
     *
     * @param param
     *            : the command created by commandParser
     * @return feedback containing the list of tasks that are not completed in
     *         the file, and the message.
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws InvalidInputException
     * @throws TimeIntervalOverlapException
     */
    Feedback clear(Hashtable<ParamEnum, ArrayList<String>> param)
            throws TaskNotFoundException, IOException, InvalidInputException,
            TimeIntervalOverlapException {
        String keyword = param.get(ParamEnum.KEYWORD).get(0).toLowerCase();
        switch (keyword) {
        case COMPLETED_KEYWORD:
            // get all completed task from storage
            ArrayList<Task> completedTasks = storage.getAllCompletedTasks();
            ArrayList<Task> cloneCompletedTasks = cloner
                    .deepClone(completedTasks);
            for (int i = 0; i < completedTasks.size(); i++) {
                Task task = completedTasks.get(i);
                TaskModifier.deleteTask(task);
                storage.writeTaskToFile(task);
            }
            logicUndo.pushClearCommandToHistory(cloneCompletedTasks);
            return createTaskListFeedback(
                    MessageCreator.createMessage(CLEAR_MESSAGE, null, null),
                    storage.getAllActiveTasks());
        }
        throw new InvalidInputException(MessageCreator.createMessage(
                ERROR_CLEAR_MESSAGE, null, null));
    }

    /**
     * Marks a particular task as done
     *
     * @param param
     *            : the command created by commandParser
     * @return feedback containing the updated list of tasks in the file, and
     *         the message.
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws InvalidInputException
     * @throws InvalidDateFormatException
     * @throws InvalidCommandUseException
     * @throws InvalidInputException
     * @throws TimeIntervalOverlapException
     */
    Feedback complete(Hashtable<ParamEnum, ArrayList<String>> param)
            throws TaskNotFoundException, IOException,
            InvalidDateFormatException, InvalidCommandUseException,
            InvalidInputException, TimeIntervalOverlapException {
        int taskId = getTaskId(param);
        Task task = getTaskFromStorage(taskId);
        if (task.isConfirmed() && task.getDateEnd() == null) {
            TaskModifier.completeTask(param, task);
            String name = task.getName();
            storage.writeTaskToFile(task);
            Task clonedTask = cloner.deepClone(task);
            logicUndo.pushCompleteCommandToHistory(clonedTask);
            ArrayList<Task> taskList = storage.getAllActiveTasks();
            return createTaskListFeedback(
                    MessageCreator.createMessage(COMPLETE_MESSAGE, name, null),
                    taskList);
        } else {
            throw new InvalidCommandUseException(ERROR_COMPLETE_MESSAGE);
        }
    }

    /**
     * Confirms a particular conditional date pair in the conditional task to be
     * used as the start and end date
     *
     * @param param
     *            : the command created by commandParser
     * @return feedback containing the updated list of tasks in the file, and
     *         the message.
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws InvalidInputException
     * @throws TimeIntervalOverlapException
     */

    Feedback confirm(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidInputException, TaskNotFoundException, IOException,
            TimeIntervalOverlapException {
        int taskId = getTaskId(param);
        String dateIdString = param.get(ParamEnum.ID).get(0);
        int dateId;
        try {
            dateId = Integer.parseInt(dateIdString);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(MessageCreator.createMessage(
                    INVALID_DATE_ID_MESSAGE, dateIdString, null));
        }
        Task task = getTaskFromStorage(taskId);
        TaskModifier.confirmTask(dateId, task);
        storage.writeTaskToFile(task);
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushConfirmCommandToHistory(clonedTask);
        String taskName = task.getName();
        return createTaskAndTaskListFeedback(
                MessageCreator.createMessage(CONFIRM_MESSAGE, taskName, null),
                storage.getAllActiveTasks(), task);
    }

    /**
     * Deletes a task from the storage
     *
     * @param param
     *            : the command created by commandParser
     * @return feedback containing the updated list of tasks in the file, and
     *         the message.
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws InvalidInputException
     */
    Feedback delete(Hashtable<ParamEnum, ArrayList<String>> param)
            throws TaskNotFoundException, IOException, InvalidInputException {
        int taskId = getTaskId(param);
        Task task = getTaskFromStorage(taskId);
        String name = task.getName();
        TaskModifier.deleteTask(task);
        try {
            storage.writeTaskToFile(task);
        } catch (TimeIntervalOverlapException e) {
            // This exception should not be thrown.
            assert false;
        }
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushDeleteCommandToHistory(clonedTask);
        ArrayList<Task> taskList = storage.getAllActiveTasks();
        return createTaskListFeedback(
                MessageCreator.createMessage(DELETE_MESSAGE, name, null),
                taskList);
    }

    /**
     * Displays all active tasks if no keyword is provided. Otherwise, it will
     * display either active tasks, completed tasks or all tasks depending on
     * the keyword, or the details of a single task if the task id is provided.
     *
     * @param param
     *            : the requirements of the user
     * @return feedback containing the list of all tasks in the file/the task to
     *         be displayed, and the message.
     * @throws TaskNotFoundException
     * @throws InvalidInputException
     */

    Feedback display(Hashtable<ParamEnum, ArrayList<String>> param)
            throws TaskNotFoundException, InvalidInputException {
        String displayString = param.get(ParamEnum.KEYWORD).get(0)
                .toLowerCase();
        logicUndo.pushNullCommandToHistory();
        if (displayString.isEmpty() || displayString.equals(ACTIVE_KEYWORD)) {
            return displayAllActive();
        } else if (displayString.equals(ALL_KEYWORD)) {
            return displayAll();
        } else if (displayString.equals(COMPLETED_KEYWORD)) {
            return displayAllCompleted();
        } else {
            return displayTaskById(displayString);
        }
    }

    /**
     * Display all active tasks in the list
     *
     * @return feedback containing all the tasks in the file, and the message.
     */
    Feedback displayAllActive() {
        ArrayList<Task> taskList = storage.getAllActiveTasks();
        return createTaskListFeedback(MessageCreator.createMessage(
                DISPLAY_MESSAGE, ACTIVE_KEYWORD, null), taskList);
    }

    /**
     * Search for tasks that contain the keyword in the different attributes of
     * the task, such as name, description, tags and dates
     *
     * @param param
     *            : the command created by CommandParser
     * @return feedback containing all the tasks in the file, and the message
     * @throws InvalidInputException
     * @throws InvalidDateFormatException
     */

    Feedback search(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidDateFormatException, InvalidInputException {
        ArrayList<Task> taskList = storage.searchTask(param);
        logicUndo.pushNullCommandToHistory();
        return createTaskListFeedback(
                MessageCreator.createMessage(SEARCH_MESSAGE,
                        String.valueOf(taskList.size()), null), taskList);
    }
    
  //@author A0098722W
    /**
     * Suggest a list of date that fulfill the user requirements
     *
     * @param param
     *            : the requirements specified by the user
     * @return feedback containing the list of free slots ======= Suggest a list
     *         of dates that fulfill the user requirements
     *
     * @param param
     * @return Feedback
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    Feedback suggest(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidDateFormatException, InvalidInputException {
        if (!hasTimedTaskParams(param) || hasEmptyDurationParam(param)) {
            throw new InvalidInputException(ERROR_SUGGEST_MESSAGE);
        }
        suggestions.clear();
        
        ArrayList<Task> taskList = storage.suggestedSearchTask(param);
        // Round off the given time interval to the nearest 30min block
        // and add into tasklist to facilitate search
        Calendar startTime = processDateString(param
                .get(ParamEnum.START_DATE).get(START_VALUE));
        Calendar endTime = processDateString(param
                .get(ParamEnum.END_DATE).get(START_VALUE));
        addStartTaskToTaskList(taskList, startTime);
        addEndTaskToTaskList(taskList, endTime);
        
        float duration = getDuration(param);
        assert duration > MAX_INVALID_DURATION;

        generateSuggestions(param, taskList, startTime, endTime, duration);
        String message = getSuggestionMessage();

        return createTaskAndTaskListFeedback(
                MessageCreator.createMessage(message, "", null), suggestions,
                null);
    }

    /**
     * Process the date to ensure that it starts after current time and is round off to the nearest block
     * @param dateString
     * @return
     * @throws InvalidDateFormatException
     */
    private Calendar processDateString(String dateString) throws InvalidDateFormatException {
        Calendar userInputTime = DateParser.parseString(dateString);
        if (userInputTime.before(Calendar.getInstance())) {
            return roundToNearestBlock(Calendar.getInstance());
        } else {
            return roundToNearestBlock(userInputTime);
        }
    }

    private String getSuggestionMessage() {
        String message;
        if (suggestions.isEmpty()) {
            message = NO_SUGGESTION_MESSAGE;
        } else {
            message = SUGGESTION_MESSAGE;
        }
        return message;
    }

    private float getDuration(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidInputException {
        float duration;
        String durationString = param.get(ParamEnum.DURATION).get(START_VALUE);
        try {
            duration = Float.parseFloat(durationString);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(MessageCreator.createMessage(
                    "Duration %1$s is invalid.", durationString, null));
        }
        if (duration <= MAX_INVALID_DURATION) {
            throw new InvalidInputException(MessageCreator.createMessage(
                    "Duration %1$s is invalid.", durationString, null));
        }
        return duration;
    }

    /**
     * Look for empty slot within the given interval which satisfy the user duration
     * @param param
     *           : the requirements specified by the user
     * @param taskList of all the task in the interval
     * @param startTime of the interval to find the empty slot
     * @param endTime of the interval to find the empty slot
     * @param duration of the empty slot to look for
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    private void generateSuggestions(
            Hashtable<ParamEnum, ArrayList<String>> param,
            ArrayList<Task> taskList, Calendar startTime, Calendar endTime,
            float duration) throws InvalidDateFormatException,
            InvalidInputException {
        int suggestionCounter = 1;
        while (suggestions.size() < MAX_RESULT) {
            int i;
            Calendar curr;
            Calendar next;
            for (i = 0; i < taskList.size() - 1; i++) {
                curr = roundToNearestBlock(taskList.get(i).getDateEnd());
                next = roundToNearestBlock(taskList.get(i + NEXT).getDateStart());
                if (isValidTimeSlot(startTime, endTime, duration, curr, next)) {
                    addTimeSlotToSuggestion(param, taskList, duration, i,
                            suggestionCounter, curr);
                    suggestionCounter++;
                    System.out.println(suggestionCounter);
                    break;
                }
            }
            // Exit from the loop if no results is found
            if (i == taskList.size() - 1) {
                break;
            }
        }
    }

  //@author A0114368E
    private void addEndTaskToTaskList(ArrayList<Task> taskList, Calendar endTime) {
        Task endTask = new Task();
        endTask.setDateStart(endTime);
        taskList.add(endTask);
    }

    private void addStartTaskToTaskList(ArrayList<Task> taskList,
            Calendar startTime) {
        Task startTask = new Task();
        startTask.setDateEnd(startTime);
        taskList.add(START_VALUE, startTask);
    }

    private boolean isValidTimeSlot(Calendar startTime, Calendar endTime,
            float duration, Calendar curr, Calendar next) {
        return curr.getTimeInMillis() >= startTime.getTimeInMillis()
                && next.getTimeInMillis() <= endTime.getTimeInMillis()
                && (next.getTimeInMillis() - curr.getTimeInMillis()) >= (duration * HOUR_TO_MILLIS);
    }

    /**
     * Add the given interval to the current task and suggestion list
     * @param param
     *           : the requirements specified by the user
     * @param taskList of task within the interval
     * @param duration given by the user for the empty slot
     * @param i the current counter
     * @param suggestionCounter index of the suggestion
     * @param curr
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    private void addTimeSlotToSuggestion(
            Hashtable<ParamEnum, ArrayList<String>> param,
            ArrayList<Task> taskList, float duration, int i,
            int suggestionCounter, Calendar curr)
            throws InvalidDateFormatException, InvalidInputException {
        Task newTask = new Task();
        newTask.setId(suggestionCounter);
        TaskModifier.modifyTimedTask(param, newTask);
        newTask.setDateStart(curr);
        Calendar temp = (Calendar) curr.clone();
        temp.add(Calendar.SECOND, (int) (duration * HOURS_TO_SECONDS));
        newTask.setDateEnd(temp);
        taskList.add(i + 1, newTask);
        suggestions.add(newTask);
    }

    private boolean hasEmptyDurationParam(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.get(ParamEnum.DURATION).get(0).isEmpty();
    }

    /**
     * Undo the last action taken
     *
     * @return feedback containing the list of updated tasks in the file, and
     *         the message.
     * @throws HistoryNotFoundException
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws TimeIntervalOverlapException
     */
    Feedback undo() throws HistoryNotFoundException, TaskNotFoundException,
            IOException, TimeIntervalOverlapException {
        History lastAction = logicUndo.getLastAction();
        if (lastAction == null) {
            throw new HistoryNotFoundException(ERROR_UNDO_MESSAGE);
        } else {
            ArrayList<Task> tasks = lastAction.getTasks();
            // Add all history task back to current task
            for (Task task : tasks) {
                storage.writeTaskToFile(task);
            }
            if (lastAction.getCommand() == CommandEnum.CLEAR) {
                return createTaskAndTaskListFeedback(
                        MessageCreator.createMessage(UNDO_CLEAR_MESSAGE,
                                lastAction.getCommand().action(), null),
                        storage.getAllActiveTasks(), null);
            } else {
                Task task = tasks.get(0);
                Task displayTask = getTaskDisplayForUndo(task);
                return createTaskAndTaskListFeedback(
                        MessageCreator.createMessage(UNDO_MESSAGE, lastAction
                                .getCommand().action(), task.getName()),
                        storage.getAllActiveTasks(), displayTask);
            }
        }
    }

    /**
     * Updates the task in the file.
     *
     * @param param
     *            : the command created by commandParser
     * @return feedback containing the updated list of tasks in the file, and
     *         the message.
     * @throws TaskNotFoundException
     * @throws IOException
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     * @throws TimeIntervalOverlapException
     */
    Feedback update(Hashtable<ParamEnum, ArrayList<String>> param)
            throws TaskNotFoundException, IOException,
            InvalidDateFormatException, InvalidInputException,
            TimeIntervalOverlapException {
        int taskId = getTaskId(param);
        Task task = cloner.deepClone(getTaskFromStorage(taskId));
        Task clonedTask = cloner.deepClone(task);
        if (task.isConditionalTask()) {
            updateConditionalTask(param, taskId, task);
        } else if (task.isTimedTask()) {
            updateTimedTask(param, taskId, task);
        } else if (task.isDeadlineTask()) {
            updateDeadlineTask(param, taskId, task);
        } else {
            assert task.isFloatingTask();
            updateFloatingTask(param, task);
        }
        storage.writeTaskToFile(task);
        String name = task.getName();
        ArrayList<Task> taskList = storage.getAllActiveTasks();
        logicUndo.pushUpdateCommandToHistory(clonedTask);
        return createTaskAndTaskListFeedback(
                MessageCreator.createMessage(EDIT_MESSAGE, name, null),
                taskList, task);
    }

    private Feedback createTaskAndTaskListFeedback(String message,
            ArrayList<Task> taskList, Task task) {
        return new Feedback(message, taskList, task);
    }

    private Feedback createTaskFeedback(String message, Task task) {
        return new Feedback(message, null, task);
    }

    private Feedback createTaskListFeedback(String message,
            ArrayList<Task> taskList) {
        return new Feedback(message, taskList, null);
    }

    /**
     * Display all tasks in the list
     *
     * @return feedback containing all the tasks in the file, and the message.
     */
    private Feedback displayAll() {
        ArrayList<Task> taskList = storage.getAllTasks();
        return createTaskListFeedback(
                MessageCreator.createMessage(DISPLAY_MESSAGE, "", null),
                taskList);
    }

    private Feedback displayAllCompleted() {
        ArrayList<Task> taskList = storage.getAllCompletedTasks();
        return createTaskListFeedback(MessageCreator.createMessage(
                DISPLAY_MESSAGE, COMPLETED_KEYWORD, null), taskList);
    }

    /**
     * Displays the individual task
     *
     * @param id
     *            : task id
     * @return feedback containing the task and the message
     * @throws TaskNotFoundException
     *             : if the id is invalid or if it is deleted
     */

    private Feedback displayTask(int id) throws TaskNotFoundException {
        Task task = getTaskFromStorage(id);
        return createTaskFeedback(
                MessageCreator.createMessage(DISPLAY_TASK_MESSAGE,
                        String.valueOf(id), task.getName()), task);
    }

    private Feedback displayTaskById(String displayString)
            throws TaskNotFoundException, InvalidInputException {
        try {
            int id = Integer.parseInt(displayString);
            return displayTask(id);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(MessageCreator.createMessage(
                    ERROR_DISPLAY_MESSAGE, displayString, null));
        }
    }

    private Task getTaskDisplayForUndo(Task task) {
        if (task.isDeleted()) {
            return null;
        } else {
            return task;
        }
    }

    /**
     * Gets the task from storage
     *
     * @param id
     *            : id of task
     * @return task corresponding to the id
     * @throws TaskNotFoundException
     *             : if task is already deleted, or if id is invalid
     */
    private Task getTaskFromStorage(int id) throws TaskNotFoundException {
        Task task = storage.getTask(id);
        if (task.isDeleted()) {
            throw new TaskNotFoundException(MessageCreator.createMessage(
                    ERROR_ALREADY_DELETED_MESSAGE, Integer.toString(id), null));
        }
        return task;
    }

    private Task getTaskFromSuggestion(int taskId) throws TaskNotFoundException, InvalidCommandUseException {
        if (suggestions.isEmpty()) {
            throw new InvalidCommandUseException(MessageCreator.createMessage("There is no suggestion to accept"
                    ,"", null));
        }
        try {
            return suggestions.get(taskId);
        } catch (IndexOutOfBoundsException e) {
            throw new TaskNotFoundException(MessageCreator.createMessage(
                    INVALID_TASK_ID_MESSAGE, Integer.toString(taskId), null));
        }
    }

    private int getTaskId(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidInputException {
        String taskIdString = param.get(ParamEnum.KEYWORD).get(0);
        try {
            return Integer.parseInt(taskIdString);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(MessageCreator.createMessage(
                    INVALID_TASK_ID_MESSAGE, taskIdString, null));
        }
    }

    private boolean hasConditionalTaskParams(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return hasMultipleEntries(param, ParamEnum.START_DATE)
                && hasMultipleEntries(param, ParamEnum.END_DATE)
                && hasEqualStartAndEndDates(param)
                && !param.containsKey(ParamEnum.DUE_DATE);
    }

    private boolean hasDateParam(Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.containsKey(ParamEnum.START_DATE)
                || param.containsKey(ParamEnum.DUE_DATE)
                || param.containsKey(ParamEnum.END_DATE);

    }

    private boolean hasDeadlineTaskParams(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return !param.containsKey(ParamEnum.START_DATE)
                && param.containsKey(ParamEnum.DUE_DATE)
                && param.get(ParamEnum.DUE_DATE).size() == 1
                && !param.containsKey(ParamEnum.END_DATE);
    }

    private boolean hasEmptyElements(ArrayList<String> arrayList) {
        for (String s : arrayList) {
            if (s.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEqualStartAndEndDates(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.get(ParamEnum.START_DATE).size() == param.get(
                ParamEnum.END_DATE).size();
    }

    private boolean hasFloatingTaskParams(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return !param.containsKey(ParamEnum.START_DATE)
                && !param.containsKey(ParamEnum.DUE_DATE)
                && !param.containsKey(ParamEnum.END_DATE);
    }

    private boolean hasMultipleEntries(
            Hashtable<ParamEnum, ArrayList<String>> param, ParamEnum type) {
        return param.containsKey(type) && param.get(type).size() > 1
                && !hasEmptyElements(param.get(type));
    }

    private boolean hasTimedTaskParams(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        if (param.containsKey(ParamEnum.START_DATE)
                && param.containsKey(ParamEnum.END_DATE)
                && !param.containsKey(ParamEnum.DUE_DATE)) {
            assert (param.get(ParamEnum.START_DATE) != null);
            assert (param.get(ParamEnum.END_DATE) != null);
            return param.get(ParamEnum.START_DATE).size() == 1
                    && !param.get(ParamEnum.START_DATE).get(0).isEmpty()
                    && param.get(ParamEnum.END_DATE).size() == 1
                    && !param.get(ParamEnum.END_DATE).get(0).isEmpty();
        } else {
            return false;
        }
    }

    private boolean hasUpdateTimedTaskParams(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return !param.containsKey(ParamEnum.DUE_DATE)
                && !(hasMultipleEntries(param, ParamEnum.START_DATE) || hasMultipleEntries(
                        param, ParamEnum.END_DATE));
    }

  //@author A0098722W
    /**
     * Return a calendar to the nearest thirty minutes block
     *
     * @param date
     * @return a calendar object a with the nearest block
     */
    private Calendar roundToNearestBlock(Calendar date) {
        long dateTime = date.getTimeInMillis();
        if (dateTime % TIME_BLOCK == 0) {
            return date;
        } else {
            dateTime = ((dateTime / TIME_BLOCK) + NEXT) * TIME_BLOCK;
            Calendar nearestBlock = Calendar.getInstance();
            nearestBlock.setTimeInMillis(dateTime);
            return nearestBlock;
        }
    }

  //@author A0114368E
    private void updateConditionalTask(
            Hashtable<ParamEnum, ArrayList<String>> param, int taskId, Task task)
            throws InvalidInputException, InvalidDateFormatException {
        if (!hasDateParam(param) || hasConditionalTaskParams(param)) {
            TaskModifier.modifyConditionalTask(param, task);
        } else {
            throw new InvalidInputException(MessageCreator.createMessage(
                    ERROR_UPDATE_CONDITIONAL_TASK_MESSAGE,
                    Integer.toString(taskId), null));
        }
    }

    private void updateDeadlineTask(
            Hashtable<ParamEnum, ArrayList<String>> param, int taskId, Task task)
            throws InvalidDateFormatException, InvalidInputException {
        if (hasDeadlineTaskParams(param) || !hasDateParam(param)) {
            TaskModifier.modifyDeadlineTask(param, task);
        } else if (hasTimedTaskParams(param)) {
            TaskModifier.modifyTimedTask(param, task);
        } else {
            throw new InvalidInputException(MessageCreator.createMessage(
                    ERROR_DATE_INPUT_MESSAGE, Integer.toString(taskId), null));
        }
    }

    private void updateFloatingTask(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException, InvalidInputException {
        if (hasConditionalTaskParams(param)) {
            TaskModifier.modifyConditionalTask(param, task);
        } else if (hasTimedTaskParams(param)) {
            TaskModifier.modifyTimedTask(param, task);
        } else if (hasDeadlineTaskParams(param)) {
            TaskModifier.modifyDeadlineTask(param, task);
        } else if (hasFloatingTaskParams(param)) {
            TaskModifier.modifyFloatingTask(param, task);
        } else {
            throw new InvalidInputException(ERROR_DATE_INPUT_MESSAGE);
        }
    }

    private void updateTimedTask(Hashtable<ParamEnum, ArrayList<String>> param,
            int taskId, Task task) throws InvalidDateFormatException,
            InvalidInputException {
        if (hasUpdateTimedTaskParams(param) || !hasDateParam(param)) {
            TaskModifier.modifyTimedTask(param, task);
        } else if (hasDeadlineTaskParams(param)) {
            TaskModifier.modifyDeadlineTask(param, task);
        } else {
            throw new InvalidInputException(MessageCreator.createMessage(
                    ERROR_DATE_INPUT_MESSAGE, Integer.toString(taskId), null));
        }
    }
}
