package logic;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

import command.CommandEnum;
import common.History;
import common.Task;
import common.exceptions.HistoryNotFoundException;

//@author A0114368E
/**
 *
 * Provides the methods to add the executed commands to the undo stack, and is
 * responsible for undoing the commands that were executed.
 *
 */
public class LogicUndo {
    private Stack<History> undoStack;

    LogicUndo() {
        undoStack = new Stack<History>();
    }

    /**
     * Creates a version of the new task that is set as deleted, and stores it
     * in the stack
     *
     * @param task
     *            : the new task that is added, it is not set as deleted yet
     */
    void pushAddCommandToHistory(Task task) {
        TaskModifier.deleteTask(task);
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.add(task);
        History historyCommand = new History(CommandEnum.ADD, tasks);
        undoStack.push(historyCommand);
    }

    /**
     * Creates a version of the accepted task that is set as deleted, and stores
     * it in the stack
     *
     * @param task
     *            : the new task that is accepted, it is not set as deleted yet
     */
    void pushAcceptCommandToHistory(Task task) {
        TaskModifier.deleteTask(task);
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.add(task);
        History historyCommand = new History(CommandEnum.ACCEPT, tasks);
        undoStack.push(historyCommand);
    }

    /**
     * Creates a history object containing the task before it was edited, and
     * stores it in the stack
     *
     * @param task
     *            : the task before it was edited
     */
    void pushUpdateCommandToHistory(Task task) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.add(task);
        History historyCommand = new History(CommandEnum.UPDATE, tasks);
        undoStack.push(historyCommand);
    }

    /**
     * Creates a history object containing the undeleted version of the task,
     * and stores it in the stack
     *
     * @param task
     *            : the task after it was deleted
     */
    void pushDeleteCommandToHistory(Task task) {
        TaskModifier.undeleteTask(task);
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.add(task);
        History historyCommand = new History(CommandEnum.DELETE, tasks);
        undoStack.push(historyCommand);
    }

    /**
     * Creates a history object containing the uncompleted version of the task,
     * and stores it in the stack
     *
     * @param task
     *            : the task after it was completed.
     */
    void pushCompleteCommandToHistory(Task task) {
        TaskModifier.uncompleteTask(task);
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.add(task);
        History historyCommand = new History(CommandEnum.DONE, tasks);
        undoStack.push(historyCommand);
    }

    /**
     * Stores a null object in the undo stack. Used for display and search as
     * there is no action to be done to undo it
     */
    void pushNullCommandToHistory() {
        History historyCommand = null;
        undoStack.push(historyCommand);
    }

    /**
     * Stores the unconfirmed version of the task in the undo stack
     *
     * @param task
     *            : the task after it is confirmed
     */
    void pushConfirmCommandToHistory(Task task) {
        TaskModifier.unconfirmTask(task);
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.add(task);
        History historyCommand = new History(CommandEnum.CONFIRM, tasks);
        undoStack.push(historyCommand);
    }

    /**
     * Stores the undeleted version of the completed tasks in the undo stack
     *
     * @param task
     *            : the completed tasks
     */
    void pushClearCommandToHistory(ArrayList<Task> tasks) {
        History historyCommand = new History(CommandEnum.CLEAR, tasks);
        undoStack.push(historyCommand);
    }

    /**
     * Returns the inverse action needed to undo the last action taken
     *
     * @return the history object indicating the last action and the reversal
     *         needed to undo it
     * @throws HistoryNotFoundException
     *             when the stack is empty
     */
    History getLastAction() throws HistoryNotFoundException {
        try {
            return undoStack.pop();
        } catch (EmptyStackException e) {
            throw new HistoryNotFoundException(
                    "There are not past actions taken");
        }
    }

}
