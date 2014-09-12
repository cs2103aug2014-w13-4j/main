package logic;

/**
 * 
 * @author pn interface for the shared 
 * task-specific functionalities that each command will
 *         use.
 */
public interface ManageTasks {
	// Adds a task to the list of tasks
	void createTask();

	// Return a single task
	void returnTask();

	// Updates the task
	void updateTask();

	// Deletes the Task()
	void deleteTask();

}
