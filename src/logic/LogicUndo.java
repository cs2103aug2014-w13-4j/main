package logic;

import java.util.EmptyStackException;
import java.util.Stack;

import command.CommandEnum;
import exceptions.HistoryNotFoundException;
import models.History;
import models.Task;

public class LogicUndo {
	/**
	 * constructor This constructor follows the singleton pattern It can only be
	 * called with in the current class (Logic.getInstance()) This is to ensure
	 * that only there is exactly one instance of Logic class
	 *
	 * @throws FileFormatNotSupportedException
	 *             , IOException
	 * @return Logic object
	 *
	 *         To be implemented in the future
	 */
	private static LogicUndo instance = null;
	private Stack<History> undoStack;

	private LogicUndo() {
		undoStack = new Stack<History>();

	}

	public static LogicUndo getInstance() {
		if (instance == null) {
			instance = new LogicUndo();
		}
		return instance;
	}

	/**
	 * Creates a history object containing the newly added task that is set as
	 * deleted, and stores it in the stack
	 * 
	 * @param task
	 *            : the new task that is added, it is not set as deleted yet
	 */
	void pushAddCommandToHistory(Task task) {
		TaskModifier.deleteTask(task);
		History historyCommand = new History(CommandEnum.ADD, task);
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
		History historyCommand = new History(CommandEnum.UPDATE, task);
		undoStack.push(historyCommand);
	}

	/**
	 * Creates a history object containing the undeleted version of the task, and
	 * stores it in the stack
	 * 
	 * @param task
	 *            : the task after it was deleted
	 */
	void pushDeleteCommandToHistory(Task task) {
		TaskModifier.undeleteTask(task);
		History historyCommand = new History(CommandEnum.DELETE, task);
		undoStack.push(historyCommand);
	}

	/**
	 * Creates a history object containing the uncompleted version of the task, and
	 * stores it in the stack
	 * 
	 * @param task : the task after it was completed. 
	 */
	void pushCompleteCommandToHistory(Task task) {
		TaskModifier.uncompleteTask(task);
		History historyCommand = new History(CommandEnum.DONE, task);
		undoStack.push(historyCommand);
	}

	/**
	 * For commands that are not supported yet. Pushes a null history into the
	 * stack
	 */
	void pushNullCommandToHistory() {
		History historyCommand = null;
		undoStack.push(historyCommand);
	}

	void pushConfirmCommandToHistory(Task task) {
		TaskModifier.unconfirmTask(task);
		History historyCommand = new History(CommandEnum.CONFIRM, task);
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
