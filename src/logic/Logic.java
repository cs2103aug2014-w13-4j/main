package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import com.rits.cloning.Cloner;

import storage.Storage;
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

public class Logic {
    private static final String ERROR_UNDO_MESSAGE = "Search and display actions cannot be undone.";
    private static final String INVALID_TASK_ID_MESSAGE = "Task ID: %1$s is invalid!";
    private static final String INVALID_DATE_ID_MESSAGE = "Date ID: %1$s is invalid!";
    private static final String ERROR_UPDATE_CONDITIONAL_TASK_MESSAGE = "Task %1$s is a conditional task, so it should contain multiple start and end dates";
    private static final String ERROR_COMPLETE_MESSAGE = "Only confirmed and uncompleted tasks without an end date before can be completed";
    private static final String ERROR_DATE_INPUT_MESSAGE = "The date parameters provided are invalid.";
    private static final String ERROR_CLEAR_MESSAGE = "The given parameters for clear are invalid.";
    private static final String ADD_MESSAGE = "%1$s is successfully added.";
    private static final String DELETE_MESSAGE = "%1$s is successfully deleted";
    private static final String EDIT_MESSAGE = "%1$s is successfully edited.";
    private static final String COMPLETE_MESSAGE = "%1$s is marked as completed.";
    private static final String SEARCH_MESSAGE = "%1$s results are found.";
    private static final String DISPLAY_MESSAGE = "All tasks are displayed.";
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
    Storage storage = null;
    private LogicUndo logicUndo = new LogicUndo();
    // public LogicUndo logicUndo = LogicUndo.getInstance();
    private Cloner cloner = new Cloner();
    private ArrayList<Task> suggestions = new ArrayList<Task>();

    private static Logic instance = null;

    private Logic() {
    }

    public static Logic getInstance() throws IOException,
    FileFormatNotSupportedException {
        if (instance == null) {
            instance = new Logic();
            ApplicationLogger.getApplicationLogger().log(Level.INFO,
                    "Initializing Logic.");
            instance.storage = Storage.getInstance();
        }
        return instance;
    }

    // for debugging purposes. Always create a new instance
    public static Logic getNewInstance() throws IOException,
    FileFormatNotSupportedException {
        instance = new Logic();
        ApplicationLogger.getApplicationLogger().log(Level.INFO,
                "Initializing Logic.");
        instance.storage = Storage.getNewInstance();
        return instance;
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
            ApplicationLogger.getApplicationLogger().log(Level.INFO,
                    "Adding floating task.");
            TaskModifier.modifyFloatingTask(param, task);
        } else if (hasConditionalTaskParams(param)) {
            ApplicationLogger.getApplicationLogger().log(Level.INFO,
                    "Adding conditional task.");
            TaskModifier.modifyConditionalTask(param, task);
        } else if (hasTimedTaskParams(param)) {
            ApplicationLogger.getApplicationLogger().log(Level.INFO,
                    "Adding timed task.");
            TaskModifier.modifyTimedTask(param, task);
        } else if (hasDeadlineTaskParams(param)) {
            ApplicationLogger.getApplicationLogger().log(Level.INFO,
                    "Adding deadline task.");
            TaskModifier.modifyDeadlineTask(param, task);
        } else {
            throw new InvalidInputException(ERROR_DATE_INPUT_MESSAGE);
        }
        storage.writeTaskToFile(task);
        String name = task.getName();
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushAddCommandToHistory(clonedTask);
        ArrayList<Task> taskList = storage.getAllTasks();
        return createTaskListFeedback(
                MessageCreator.createMessage(ADD_MESSAGE, name, null), taskList);
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
            ArrayList<Task> taskList = storage.getAllTasks();
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
     * @throws InvalidInputException
     * @throws TaskNotFoundException
     * @throws IOException
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
        TaskModifier.confirmEvent(dateId, task);
        storage.writeTaskToFile(task);
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushConfirmCommandToHistory(clonedTask);
        String taskName = task.getName();
        return createTaskAndTaskListFeedback(
                MessageCreator.createMessage(CONFIRM_MESSAGE, taskName, null),
                storage.getAllTasks(), task);
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
        case "completed":
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
                    storage.getAllTasks());
        }
        throw new InvalidInputException(MessageCreator.createMessage(
                ERROR_CLEAR_MESSAGE, null, null));
    }

    /**
     * Deletes a task from the file
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
    Feedback delete(Hashtable<ParamEnum, ArrayList<String>> param)
            throws TaskNotFoundException, IOException, InvalidInputException,
            TimeIntervalOverlapException {
        int taskId = getTaskId(param);
        Task task = getTaskFromStorage(taskId);
        String name = task.getName();
        TaskModifier.deleteTask(task);
        storage.writeTaskToFile(task);
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushDeleteCommandToHistory(clonedTask);
        ArrayList<Task> taskList = storage.getAllTasks();
        return createTaskListFeedback(
                MessageCreator.createMessage(DELETE_MESSAGE, name, null),
                taskList);
    }

    /**
     * Displays the task if the id is provided, or all the tasks otherwise
     *
     * @param param
     *            : the command created by commandParser
     * @return feedback containing the list of all tasks in the file/the task to
     *         be displayed, and the message.
     * @throws NumberFormatException
     * @throws TaskNotFoundException
     */

    Feedback display(Hashtable<ParamEnum, ArrayList<String>> param)
            throws NumberFormatException, TaskNotFoundException {
        String idString = param.get(ParamEnum.KEYWORD).get(0);
        logicUndo.pushNullCommandToHistory();
        if (idString.isEmpty()) {
            return displayAll();
        } else {
            int id = Integer.parseInt(idString);
            return displayTask(id);
        }
    }

    /**
     * Search for tasks that contain the keyword in the name, description or
     * tags
     *
     * @param command
     *            : the command created by CommandParser
     * @return feedback containing all the tasks in the file, and the message
     * @throws InvalidInputException
     * @throws InvalidDateFormatException
     * @throws EmptySearchResultException
     */

    Feedback search(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidDateFormatException, InvalidInputException {
        ArrayList<Task> taskList = storage.searchTask(param);
        logicUndo.pushNullCommandToHistory();
        return createTaskListFeedback(
                MessageCreator.createMessage(SEARCH_MESSAGE,
                        String.valueOf(taskList.size()), null), taskList);
    }
    
    /**
     * Return a calendar to the nearest thirty minutes block
     * @param date
     * @return a calendar object a with the nearest block
     */
    private Calendar roundToNearestBlock(Calendar date) {
        long dateTime = date.getTimeInMillis();
        if (dateTime % TIME_BLOCK == 0) {
            return date;
        } else {
            dateTime = ((dateTime / TIME_BLOCK) + 1) * TIME_BLOCK;
            Calendar nearestBlock = GregorianCalendar.getInstance();
            nearestBlock.setTimeInMillis(dateTime);
            return nearestBlock;
        }
    }
    
    /**
     * Suggest a list of date that fulfill the user requirements
     * 
     * @param param
     * @return
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    Feedback suggest(Hashtable<ParamEnum, ArrayList<String>> param)
            throws InvalidDateFormatException, InvalidInputException {
        ArrayList<Task> taskList = storage.suggestedSearchTask(param);
        
        suggestions.clear();
        Calendar startTime = roundToNearestBlock(DateParser.parseString(param
                .get(ParamEnum.START_DATE).get(START_VALUE)));
        Task startTask = new Task();
        startTask.setDateEnd(startTime);
        taskList.add(START_VALUE, startTask);
        Calendar endTime = roundToNearestBlock(DateParser.parseString(param
                .get(ParamEnum.END_DATE).get(START_VALUE)));
        Task endTask = new Task();
        endTask.setDateStart(endTime);
        taskList.add(endTask);

        int duration = Integer.parseInt(param.get(ParamEnum.DURATION).get(
                START_VALUE));

        while (suggestions.size() < MAX_RESULT) {
            int i;
            Calendar curr;
            Calendar next;
            for (i = 0; i < taskList.size() - 1; i++) {
                curr = roundToNearestBlock(taskList.get(i).getDateEnd());
                next = roundToNearestBlock(taskList.get(i + 1).getDateStart());

                if (curr.getTimeInMillis() >= startTime.getTimeInMillis()
                        && next.getTimeInMillis() <= endTime.getTimeInMillis()
                        && (next.getTimeInMillis() - curr.getTimeInMillis()) >= (duration * HOUR_TO_MILLIS)) {
                    Task newTask = new Task();
                    TaskModifier.modifyTimedTask(param, newTask);
                    newTask.setDateStart(curr);
                    Calendar temp = (Calendar) curr.clone();
                    temp.add(Calendar.HOUR_OF_DAY, duration);
                    newTask.setDateEnd(temp);
                    taskList.add(i + 1, newTask);
                    suggestions.add(newTask);
                    break;
                }
            }
            // Exit from the loop if no results is found
            if (i == taskList.size() - 1) {
                break;
            }
        }

        String message;
        if (suggestions.isEmpty()) {
            message = NO_SUGGESTION_MESSAGE;
        } else {
            message = SUGGESTION_MESSAGE;
        }

        return createTaskAndTaskListFeedback(
                MessageCreator.createMessage(message, "", null), suggestions,
                null);
    }
    
    /**
     * Accept a suggested date
     * @return
     * @throws InvalidInputException 
     * @throws TaskNotFoundException 
     * @throws IOException 
     * @throws InvalidCommandUseException 
     * @throws TimeIntervalOverlapException 
     */
    Feedback accept(Hashtable<ParamEnum, ArrayList<String>> param) throws InvalidInputException, TaskNotFoundException, IOException, InvalidCommandUseException, TimeIntervalOverlapException {
        int taskId = getTaskId(param);
        Task task = getTaskFromSuggestion(taskId);
        String name = task.getName();
        storage.writeTaskToFile(task);
        Task clonedTask = cloner.deepClone(task);
        logicUndo.pushAcceptCommandToHistory(clonedTask);
        ArrayList<Task> taskList = storage.getAllTasks();
        suggestions.clear();
        return createTaskListFeedback(
                MessageCreator.createMessage(ADD_MESSAGE, name, null),
                taskList);
    }

    private Task getTaskFromSuggestion(int taskId) throws TaskNotFoundException {
        try {
            return suggestions.get(taskId);
        } catch (IndexOutOfBoundsException e) {
            throw new TaskNotFoundException(MessageCreator.createMessage(
                    INVALID_TASK_ID_MESSAGE, Integer.toString(taskId), null));
        }
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
            ArrayList<Task> tasks = lastAction.getTask();
            // Add all history task back to current task
            for (Task task : tasks) {
                storage.writeTaskToFile(task);
            }
            if (lastAction.getCommand() == CommandEnum.CLEAR) {
                return createTaskAndTaskListFeedback(
                        MessageCreator.createMessage(UNDO_CLEAR_MESSAGE,
                                lastAction.getCommand().action(), null),
                                storage.getAllTasks(), null);
            } else {
                Task task = tasks.get(0);
                Task displayTask = getTaskDisplayForUndo(task);
                return createTaskAndTaskListFeedback(
                        MessageCreator.createMessage(UNDO_MESSAGE, lastAction
                                .getCommand().action(), task.getName()),
                                storage.getAllTasks(), displayTask);
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
        ArrayList<Task> taskList = storage.getAllTasks();
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
    Feedback displayAll() {
        ArrayList<Task> taskList = storage.getAllTasks();
        return createTaskListFeedback(
                MessageCreator.createMessage(DISPLAY_MESSAGE, null, null),
                taskList);
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

    private boolean hasDateParam(Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.containsKey(ParamEnum.START_DATE)
                || param.containsKey(ParamEnum.DUE_DATE)
                || param.containsKey(ParamEnum.END_DATE);

    }

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

    private boolean hasUpdateTimedTaskParams(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return !param.containsKey(ParamEnum.DUE_DATE)
                && !(hasMultipleEntries(param, ParamEnum.START_DATE) || hasMultipleEntries(
                        param, ParamEnum.END_DATE));
    }
}
