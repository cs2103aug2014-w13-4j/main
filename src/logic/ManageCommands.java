package logic;

/**
 * 
 * @author pn This interface is supposed to account for all the commands. Each
 *         command is mapped to a function
 */
public interface ManageCommands {
	// Adds a task to the list of tasks
	void addTask();

	// Returns the list of tasks
	void showAllTasks();

	// Updates the task
	void updateTask();

	// Deletes the Task()
	void deleteTask();

	// Undo last action
	void undo();

	// Filters the list of task
	void filter();

	// Sorts the list of task
	void sort();

	// Adds a conditional task
	void addConditionalTask();
	
	// Confirms a conditional task
	void confirmConditionalTask();

	// Search the list of tasks
	void search();

	// Generates a random task
	void getRandomTask();

	// Completes a task
	void completeTask();
}
