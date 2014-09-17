package logic;

import models.Task;

/**
 * 
 * @author pn interface for the shared 
 * task-specific functionalities that each command will
 *         use.
 */
public interface ManageTasks {
	/**
	 * Creates a new task
	 * @return the task
	 * to add params
	 */
	Task createTask();

	/**
	 * returns the task with the id
	 * @param id: id of the task to be returned
	 * @return the corresponding task object with that ID
	 */
	Task returnTask(int id);

	/**
	 * updates the task with the id
	 * @param id: id of the task to be updated
	 * @return the updated task object with that ID
	 */
	Task updateTask(int id);

	/**
	 * Deletes the task with the id 
	 * @param id: id of the task to be deleted
	 */
	void deleteTask(int id);

}
