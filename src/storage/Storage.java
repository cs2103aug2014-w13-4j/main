package storage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import command.ParamEnum;

import exceptions.FileFormatNotSupportedException;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;
import storage.tagStorage.TagStorage;
import storage.taskStorage.TaskStorage;
import models.Task;

/**
 *
 * @author Chuyu
 * This class reads/writes task to file.
 * It also supports power search.
 */
public class Storage {
	private TaskStorage taskFile;
	private TagStorage tagFile;

	/**
	 * constructor
	 * This constructor follows the singleton pattern
	 * It can only be called with in the current class (Storage.getInstance())
	 * This is to ensure that only there is exactly one instance of Storage class
	 * @throws FileFormatNotSupportedException, IOException
	 */
	public Storage() throws IOException, FileFormatNotSupportedException{
		taskFile = new TaskStorage("taskStorage.data");
		tagFile = new TagStorage("TagStorage.data");
	}

	// Add/Update a task to file
	public void writeTaskToFile(Task task) throws TaskNotFoundException, IOException {
		taskFile.writeTaskToFile(task);
		tagFile.updateTagToFile(task.getTags());
	}

	public void updateTagToFile(ArrayList<String> tags) throws IOException {
		tagFile.updateTagToFile(tags);
	}

	// Get a task by task ID
	public Task getTask(int taskID) throws TaskNotFoundException {
		return taskFile.getTask(taskID);
	}

	// Get a list of all the Tasks
	public ArrayList<Task> getAllTasks() {
		return taskFile.getAllTasks();
	}

	// Get a list of tags
	public ArrayList<String> getAllTags() {
		return tagFile.getAllTags();
	}

	// Search a list of tasks with certain key words
	// Assume keywords of name and note is only one string
	public ArrayList<Task> searchTask(Hashtable<ParamEnum, ArrayList<String>> keyWordTable) 
			throws InvalidDateFormatException, InvalidInputException {
		return taskFile.searchTask(keyWordTable, getAllTasks());
	}
}