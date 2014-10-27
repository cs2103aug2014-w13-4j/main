package storage.taskStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Scanner;

import command.ParamEnum;

import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;
import models.DateParser;
import models.IntervalSearch;
import models.PriorityLevelEnum;
import models.Task;

/**
 *
 * @author Chuyu
 * This class reads/writes task to file.
 * It also supports power search.
 */
public class TaskStorage {

	private static final int MIN_INDEX = 0;
	private ArrayList<Task> taskBuffer;
	private int nextTaskIndex;
	private File dataFile;
	private IntervalSearch intervalTree;

	private static final int ID_FOR_NEW_TASK = -1;
	private static final int ID_FOR_FIRST_TASK = 0;

	private static final String COMPLETED = "completed";
	private static final String ACTIVE = "active";

	/**
	 * constructor``
	 * @throws FileFormatNotSupportedException, IOException
	 */
	public TaskStorage(String fileName) throws IOException, FileFormatNotSupportedException{
		Task task;
		Calendar dateStart;
		Calendar dateDue;
		dataFile = new File(fileName);

		if (!dataFile.exists()) {
			dataFile.createNewFile();
		}

		Scanner fileScanner = new Scanner(dataFile);
		taskBuffer =  new ArrayList<Task>();
		intervalTree = new IntervalSearch();
		nextTaskIndex = ID_FOR_FIRST_TASK;
		while (fileScanner.hasNextLine()) {
			task = TaskConverter.stringToTask(fileScanner.nextLine());
			taskBuffer.add(task);
			// add in interval tree
			if (task.isTimedTask()) {
				dateStart = task.getDateStart();
				dateDue = task.getDateDue();
				if (intervalTree.isValid(dateStart, dateDue)) {
					intervalTree.add(dateStart, dateDue, task.getId());
				} else {
					throw new FileFormatNotSupportedException("Events are overlapping");
				}
			}
			nextTaskIndex ++;
		}
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
	 * @param task: task to be added or updated
	 * @throws TaskNotFoundException: trying to update a not existing task
	 * @throws IOException: wrong IO operations
	 */
	public void writeTaskToFile(Task task) throws TaskNotFoundException, IOException {
		int taskID = task.getId();
		if (taskID == ID_FOR_NEW_TASK) {
			addTask(task);
		} else {
			if (isTaskExist(taskID)) {
				updateTask(task);
			} else {
				throw new TaskNotFoundException("Cannot update task since the current task doesn't exist");
			}
		}
	}

	/**
	 * Check whether a task is existing or not
	 *
	 * @param taskID: the task id to be checked
	 * @return boolean: whether a task is existing or not
	 */
	private boolean isTaskExist(int taskID) {
		return taskID >= MIN_INDEX && taskID < nextTaskIndex;
	}

	/**
	 * Add a task
	 *
	 * @param task: task to be added
	 * @throws IOException: wrong IO operations
	 */
	private void addTask(Task task) throws IOException {
		Calendar dateStart, dateDue;
		// Add new task to task file
		task.setId(nextTaskIndex);
		nextTaskIndex ++;
		addTaskToStorage(task);
		// Add new task to task buffer
		taskBuffer.add(task);
		// Add new task to Interval Tree
		if (task.isTimedTask()) {
			dateStart = task.getDateStart();
			dateDue = task.getDateDue();
			intervalTree.add(dateStart, dateDue, task.getId());
		}
	}

	/**
	 * Update a task
	 *
	 * @param task: task to be updated
	 * @throws IOException: wrong IO operations
	 */
	private void updateTask(Task task) throws IOException {	
		int taskID = task.getId();	
		Calendar dateStart, dateDue, oldStart, oldEnd;
		// Update task to task buffer
		taskBuffer.set(taskID, task);
		// Update task to task file
		updateTaskToStorage();
		// Update task to Interval Tree
		if (task.isTimedTask()) {
			dateStart = task.getDateStart();
			dateDue = task.getDateDue();
			oldStart = intervalTree.getDateStart(task.getId());
			oldEnd = intervalTree.getDateDue(task.getId());
			intervalTree.update(oldStart, oldEnd, dateStart, dateDue, taskID);
		}
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
		for (Task task: taskBuffer) {
			taskString = TaskConverter.taskToString(task);
			bufferedWriter.write(taskString + "\r\n");
		}
		bufferedWriter.close();
	}

	/**
	 * Get a task by its id
	 *
	 * @param taskID: the id of a task
	 * @return a task
	 * @throws TaskNotFoundException: trying to get a not existing task
	 */
	public Task getTask(int taskID) throws TaskNotFoundException {
		Task requiredTask = null;
		if (isTaskExist(taskID)) {
			for (Task task: taskBuffer) {
				if (task.getId() == taskID) {
					requiredTask = task;
				}
			}
		} else {
			throw new TaskNotFoundException("Cannot return  task since the current task doesn't exist");
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
		for (Task task: taskBuffer) {
			if (task.isDeleted()) {
				continue;
			} else {
				allTaskList.add(task);
			}
		}
		return allTaskList;
	}

	// Get a list of tasks that are done
	private ArrayList<Task> getCompletedTasks(ArrayList<Task> searchRange) {
		ArrayList<Task> completedTaskList = new ArrayList<Task>();
		// check whether there are tasks in storage
		if (searchRange == null) {
			return null;
		}
		for (Task task: searchRange) {
			if (task.getDateEnd() == null || task.isDeleted()) {
				continue;
			} else {
				completedTaskList.add(task);
			}
		}
		return completedTaskList;
	}

	// Get a list of tasks that are not completed
	private ArrayList<Task> getActiveTasks(ArrayList<Task> searchRange) {
		ArrayList<Task> activeTaskList = new ArrayList<Task>();
		// check whether there are tasks in storage
		if (searchRange == null) {
			return null;
		}
		for (Task task: searchRange) {
			if (task.getDateEnd() == null && !task.isDeleted()) {
				activeTaskList.add(task);
			} else {
				continue;
			}
		}
		return activeTaskList;
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
		for (String tag: tags) {
			if (task.getTags().contains(tag)) {
					continue;
				} else {
					return false;
				}
		}
		return true;
	}

	private boolean isSearchTargetByPriorityLevel(Task task, String priorityLevel) {
		int priorityLevelInteger = Integer.valueOf(priorityLevel);
		return task.getPriorityLevelInteger().equals(priorityLevelInteger);
	}

	private boolean isSearchTargetByPriorityStatus(Task task, String status) throws InvalidInputException {
		status = status.toLowerCase();
		if (status.equals(COMPLETED)) {
			return task.isCompleted() && !task.isDeleted();
		} else if (status.equals(ACTIVE)) {
			// need double check
			return !task.isDeleted();
		} else {
			throw new InvalidInputException("Filter keyword is wrong.");
		}
	}

	private boolean isSearchTargetByBefore(Task task, String dateString) throws InvalidDateFormatException {
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

	private boolean isSearchTargetByAfter(Task task, String dateString) throws InvalidDateFormatException {
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

	private boolean isSearchTargetByInterval(
			Task task, String dateStartString, String dateEndString) throws InvalidDateFormatException {
		Calendar dateStart = DateParser.parseString(dateStartString);
		Calendar dateEnd = DateParser.parseString(dateEndString);
		if (task.isTimedTask()) {
			return task.getDateStart().compareTo(dateStart) != -1 && 
					task.getDateEnd().compareTo(dateEnd) != 1;
		} else {
			return false;
		}
	}

	public ArrayList<Task> searchTask(
			Hashtable<ParamEnum, ArrayList<String>> keyWordTable, ArrayList<Task> searchRange) 
			throws InvalidDateFormatException, InvalidInputException {
		ArrayList<Task> taskList = (ArrayList<Task>) searchRange.clone();
		ArrayList<String> params;
		String firstParamElement, dateEnd;

		// exit if there is no keyword table
		if (keyWordTable == null) {
			return taskList;
		}

		for (ParamEnum key : keyWordTable.keySet()) {
			// exit if nothing to search
			if (searchRange == null) {
				return null;
			}
			params = keyWordTable.get(key);
			firstParamElement = params.get(0);
			for (Task task : searchRange) {
				switch (key) {
					case NAME:
						if (!isSearchTargetByName(task, firstParamElement)) {
							taskList.remove(task);
						}
						break;
					case NOTE:
						if (!isSearchTargetByNote(task, firstParamElement)) {
							taskList.remove(task);
						}
						break;
					case TAG:
						if (!isSearchTargetByTag(task, params)) {
							taskList.remove(task);
						}
						break;
					case LEVEL:
						if (!isSearchTargetByPriorityLevel(task, firstParamElement)) {
							taskList.remove(task);
						}
						break;
					case STATUS:
						if (!isSearchTargetByPriorityStatus(task, firstParamElement)) {
							taskList.remove(task);
						}
						break;
					case BEFORE:
						if (!isSearchTargetByBefore(task, firstParamElement)) {
							taskList.remove(task);
						}
						break;
					case AFTER:
						if (!isSearchTargetByAfter(task, firstParamElement)) {
							taskList.remove(task);
						}
						break;
					case START_DATE:
						if (keyWordTable.get(ParamEnum.END_DATE) == null) {
							throw new InvalidInputException("Search start date without end date");
						} else {
							dateEnd = keyWordTable.get(ParamEnum.END_DATE).get(0);
						}
						if (!isSearchTargetByInterval(task, firstParamElement, dateEnd)) {
							taskList.remove(task);
						}
						break;
					default:
						break;
				}
			}
			// update search range to reduce repeated work
			searchRange = (ArrayList<Task>) taskList.clone();
		}
		return searchRange;		
	}
}
