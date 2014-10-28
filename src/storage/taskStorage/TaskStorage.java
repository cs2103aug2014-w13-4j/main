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

import org.apache.commons.lang3.StringUtils;

import command.ParamEnum;
import exceptions.EmptySearchResultException;
import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;
import models.DateParser;
import models.PriorityLevelEnum;
import models.Task;

/**
 *
 * @author Chuyu This class reads/writes task to file. It also supports power
 *         search.
 */
public class TaskStorage {
	private static final int MAX_DIFF_BETWEEN_WORDS = 3;
	private static final int MIN_INDEX = 0;
	private ArrayList<Task> taskBuffer;
	private int nextTaskIndex;
	private File dataFile;

	private static final int ID_FOR_NEW_TASK = -1;
	private static final int ID_FOR_FIRST_TASK = 0;

	private static final String COMPLETED = "completed";
	private static final String ACTIVE = "active";

	/**
	 * constructor``
	 * 
	 * @throws FileFormatNotSupportedException
	 *             , IOException
	 */
	public TaskStorage(String fileName) throws IOException,
			FileFormatNotSupportedException {
		Task task;
		dataFile = new File(fileName);

		if (!dataFile.exists()) {
			dataFile.createNewFile();
		}

		Scanner fileScanner = new Scanner(dataFile);
		taskBuffer = new ArrayList<Task>();
		nextTaskIndex = ID_FOR_FIRST_TASK;
		while (fileScanner.hasNextLine()) {
			task = TaskConverter.stringToTask(fileScanner.nextLine());
			taskBuffer.add(task);
			nextTaskIndex++;
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
	 */
	public void writeTaskToFile(Task task) throws TaskNotFoundException,
			IOException {
		int taskID = task.getId();
		if (taskID == ID_FOR_NEW_TASK) {
			// Add new task to task file
			task.setId(nextTaskIndex);
			nextTaskIndex++;
			addTask(task);
			// Add new task to task buffer
			taskBuffer.add(task);
		} else {
			if (isTaskExist(taskID)) {
				// Update task to task buffer
				taskBuffer.set(taskID, task);
				// Update task to task file
				updateTask();
			} else {
				throw new TaskNotFoundException(
						"Cannot update task since the current task doesn't exist");
			}
		}
	}

	// Check whether the current task exists or not
	private boolean isTaskExist(int taskID) {
		return taskID >= MIN_INDEX && taskID < nextTaskIndex;
	}

	// append task string to the end of the file
	private void addTask(Task task) throws IOException {
		BufferedWriter bufferedWriter = null;
		String taskString = TaskConverter.taskToString(task);
		bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true));
		bufferedWriter.write(taskString + "\r\n");
		bufferedWriter.close();
	}

	private void updateTask() throws IOException {
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

	// Get a list of tasks that are done
	private ArrayList<Task> getCompletedTasks(ArrayList<Task> searchRange) {
		ArrayList<Task> completedTaskList = new ArrayList<Task>();
		// check whether there are tasks in storage
		if (searchRange == null) {
			return null;
		}
		for (Task task : searchRange) {
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
		for (Task task : searchRange) {
			if (task.getDateEnd() == null && !task.isDeleted()) {
				activeTaskList.add(task);
			} else {
				continue;
			}
		}
		return activeTaskList;
	}

	/**
	 * Return search result by given tags
	 *
	 * @param tags
	 *            : A list of given tags as key words
	 * @param searchRange
	 *            : The range of tasks
	 * @return a list of tasks as search result
	 */
	private ArrayList<Task> searchTaskByTags(ArrayList<String> tags,
			ArrayList<Task> searchRange) {
		ArrayList<Task> taskList = new ArrayList<Task>();
		boolean hasTags;

		// exit if nothing to search
		if (searchRange == null) {
			return null;
		}

		// check whether the keyword is null
		if (tags == null) {
			return searchRange;
		}

		for (Task task : searchRange) {
			hasTags = true;
			for (String tag : tags) {
				if (task.getTags().contains(tag)) {
					continue;
				} else {
					hasTags = false;
					break;
				}
			}
			if (hasTags) {
				taskList.add(task);
			}
		}
		return taskList;
	}

	/**
	 * Return search result by given name string; The return tasks' name should
	 * include given name string
	 *
	 * @param name
	 *            : a string of given name
	 * @param searchRange
	 *            : The range of tasks
	 * @return a list of tasks as search result
	 * @throws EmptySearchResultException
	 */
	private ArrayList<Task> searchTaskByName(String name,
			ArrayList<Task> searchRange) throws EmptySearchResultException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		String wordSuggestion = "";
		int maxDistance = MAX_DIFF_BETWEEN_WORDS;
		// exit if nothing to search
		if (searchRange == null) {
			return null;
		}

		for (Task task : searchRange) {
			if (!task.isDeleted()) {
				if (task.getName().contains(name)) {
					taskList.add(task);
				} else {
					int distBetweenWords = StringUtils.getLevenshteinDistance(
							task.getName(), name);
					if (distBetweenWords < maxDistance) {
						wordSuggestion = task.getName();
						maxDistance = distBetweenWords;
					}
				}
			}
		}

		if (taskList.isEmpty() && !wordSuggestion.isEmpty()) {
			throw new EmptySearchResultException("Do you mean "
					+ wordSuggestion);
		}
		return taskList;
	}

	/**
	 * Return search result by given note string; The return tasks' note should
	 * include given note string
	 *
	 * @param note
	 *            : a string of given note
	 * @param searchRange
	 *            : The range of tasks
	 * @return a list of tasks as search result
	 * @throws EmptySearchResultException
	 */
	private ArrayList<Task> searchTaskByNote(String note,
			ArrayList<Task> searchRange) throws EmptySearchResultException {
		ArrayList<Task> taskList = new ArrayList<Task>();

		String wordSuggestion = "";
		int maxDistance = MAX_DIFF_BETWEEN_WORDS;
		// exit if nothing to search
		if (searchRange == null) {
			return null;
		}

		for (Task task : searchRange) {
			if (!task.isDeleted()) {
				if (task.getNote().contains(note)) {
					taskList.add(task);
				} else {
					int distBetweenWords = StringUtils.getLevenshteinDistance(
							task.getNote(), note);
					if (distBetweenWords <= maxDistance) {
						wordSuggestion = task.getNote();
						maxDistance = StringUtils.getLevenshteinDistance(
								task.getNote(), note);
					}
				}
			}
		}
		if (taskList.isEmpty() && !wordSuggestion.isEmpty()) {
			throw new EmptySearchResultException("Do you mean "
					+ wordSuggestion);
		}
		return taskList;
	}

	/**
	 * Return search result by given priority level
	 *
	 * @param priorityLevel
	 *            : given priorityLevel
	 * @param searchRange
	 *            : The range of tasks
	 * @return a list of tasks as search result
	 */
	private ArrayList<Task> searchTaskByPriorityLevel(Integer priorityLevel,
			ArrayList<Task> searchRange) {
		ArrayList<Task> taskList = new ArrayList<Task>();

		// exit if nothing to search
		if (searchRange == null) {
			return null;
		}

		for (Task task : searchRange) {
			if (!task.isDeleted()) {
				if (task.getPriorityLevelInteger().equals(priorityLevel)) {
					taskList.add(task);
				}
			}
		}
		return taskList;
	}

	private ArrayList<Task> searchTaskByDateStart(Calendar dateStart,
			ArrayList<Task> searchRange) {
		ArrayList<Task> taskList = new ArrayList<Task>();

		// exit if nothing to search
		if (searchRange == null) {
			return null;
		}

		for (Task task : searchRange) {
			if (!task.isDeleted()) {
				if (task.getDateStart().equals(dateStart)) {
					taskList.add(task);
				}
			}
		}
		return taskList;
	}

	private ArrayList<Task> searchTaskByDateDue(Calendar dateDue,
			ArrayList<Task> searchRange) {
		ArrayList<Task> taskList = new ArrayList<Task>();

		// exit if nothing to search
		if (searchRange == null) {
			return null;
		}

		for (Task task : searchRange) {
			if (!task.isDeleted()) {
				if (task.getDateDue().equals(dateDue)) {
					taskList.add(task);
				}
			}
		}
		return taskList;
	}

	// Search a list of tasks with certain key words
	// String operations
	public ArrayList<Task> searchTask(
			Hashtable<ParamEnum, ArrayList<String>> keyWordTable,
			ArrayList<Task> searchRange) throws InvalidDateFormatException,
			InvalidInputException, EmptySearchResultException {
		boolean isTarget;

		// exit if nothing to search
		if (searchRange == null) {
			return null;
		}

		// search tasks with the given name string
		String name = null;
		if (keyWordTable.get(ParamEnum.NAME) != null) {
			name = keyWordTable.get(ParamEnum.NAME).get(0);
			searchRange = searchTaskByName(name, searchRange);
		}

		// search tasks with the given note string
		String note = null;
		if (keyWordTable.get(ParamEnum.NOTE) != null) {
			note = keyWordTable.get(ParamEnum.NOTE).get(0);
			searchRange = searchTaskByNote(note, searchRange);
		}

		// search tasks with the given tags
		ArrayList<String> tags;
		if (keyWordTable.get(ParamEnum.TAG) != null) {
			tags = keyWordTable.get(ParamEnum.TAG);
			searchRange = searchTaskByTags(tags, searchRange);
		}

		// search tasks with the given priority level
		int priorityLevel;
		if (keyWordTable.get(ParamEnum.LEVEL) != null) {
			priorityLevel = Integer.valueOf(keyWordTable.get(ParamEnum.LEVEL)
					.get(0));
			searchRange = searchTaskByPriorityLevel(priorityLevel, searchRange);
		}

		// search tasks with the given start date
		Calendar dateStart;
		if (keyWordTable.get(ParamEnum.START_DATE) != null) {
			dateStart = DateParser.parseString(keyWordTable.get(
					ParamEnum.START_DATE).get(0));
			searchRange = searchTaskByDateStart(dateStart, searchRange);
		}

		// search tasks with the given due date
		Calendar dateDue;
		if (keyWordTable.get(ParamEnum.DUE_DATE) != null) {
			dateDue = DateParser.parseString(keyWordTable.get(
					ParamEnum.DUE_DATE).get(0));
			searchRange = searchTaskByDateDue(dateDue, searchRange);
		}

		// filter out tasks with different status
		String status;
		if (keyWordTable.get(ParamEnum.STATUS) != null) {
			status = keyWordTable.get(ParamEnum.STATUS).get(0).toLowerCase();
			if (status.equals(COMPLETED)) {
				searchRange = getCompletedTasks(searchRange);
			} else if (status.equals(ACTIVE)) {
				searchRange = getActiveTasks(searchRange);
			} else {
				throw new InvalidInputException("Filter keyword is wrong.");
			}
		}

		return searchRange;
	}
}