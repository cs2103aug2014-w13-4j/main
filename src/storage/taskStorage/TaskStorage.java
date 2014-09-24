package storage.taskStorage;

public class TaskStorage {
    private ArrayList<Task> taskBuffer;
    private int nextTaskIndex;
    private File dataFile;
    private static Storage instance = null;

    private static final int ID_FOR_NEW_TASK = -1;
    private static final int ID_FOR_FIRST_TASK = 0;

     /**
     * constructor
     */
    public TaskStorage(String fileName) {
        dataFile = new File(fileName);

        if (!dataFile.exist()) {
            dataFile.createNewFile();
        }

        Scanner fileScanner = new Scanner(dataFile);
        taskBuffer =  new ArrayList<Task>();
        int nextTaskIndex = ID_FOR_FIRST_TASK;
        while (fileScanner.hasNextLine()) {
            task = stringToTask(fileScanner.nextLine());
            taskBuffer.add(task);
            nextTaskIndex ++;
        }   
    }

    // Add/Update a task to file
    public void writeTaskToFile(Task task) throws TaskNotFoundException, IOException {
        // Need to ask Peining
        if (task.id == ID_FOR_NEW_TASK) {
            // Add new task to task file
            task.id = nextTaskIndex;
            nextTaskIndex ++;
            addTask(task);
            // Add new task to task buffer
            taskBuffer.add(task);
        } else {
            if (isTaskExist(task.id)) {
                // Update task to task file
                updateTask();
                // Update task to task buffer
                taskBuffer.set(task.id, task);
            } else {
                throw new TaskNotFoundException("Cannot update task since the current task doesn't exist");
            }
        }
    }

    // Delete a task from file
    public void deleteTaskFromFile(int taskID) throws TaskNotFoundException, IOException {
        if (isTaskExist(task.id)){
            task = taskBuffer.get(taskID);
            task.isDeleted = true;
            updateTask();  
        } else {
            throw new TaskNotFoundException("Cannot delete task since the current task doesn't exist");
        }
    }

    // Check whether the current task exists or not
    private boolean isTaskExist(int taskID) {
        if (taskID >= nextTaskIndex) {
            return false;
        } else {
            return true;
        }
    } 

    // append task string to the end of the file
    private void addTask(Task task) throws IOException {     
        try {
            String taskString = TaskToString(task);
            BufferedWrite bufferedWriter = new BufferedWritter(new FileWriter(dataFile, true));
            bufferedWriter.write(taskString);
        } finally {
            bufferedWriter.close();
        }
    }

    private void updateTask() throws IOException {
        try {
            String taskString;
            BufferedWrite bufferedWriter = new BufferedWritter(new FileWriter(dataFile));
            for (Task task: taskBuffer) {
                taskString = TaskToString(task);
                bufferedWriter.write(taskString + "\r\n");
            }
        } finally {
            bufferedWriter.close();
        }
    }

    // Get a task by task ID
    Task getTask(int taskID) throws TaskNotFoundException {
        if (isTaskExist(taskID)) {
            return taskBuffer.get(taskID);
        } else {
            throw new TaskNotFoundException("Cannot return  task since the current task doesn't exist");
        }
    }

    // Get all tasks
    public ArrayList<Task> getAllTasks() {
        return taskBuffer;
    }

    // Get a list of tasks that are done
    ArrayList<Task> getCompletedTasks() {
        ArrayList<Task> completedTaskList = new ArrayList<Task>();
        for (Task task: taskBuffer) {
            if (task.dateEnd == null) {
                continue;
            } else {
                completedTaskList.add(task);
            }
        }
    }

    // Get a list of tasks that are not completed
    ArrayList<Task> getActiveTasks() {
        ArrayList<Task> activeTaskList = new ArrayList<Task>();
        for (Task task: taskBuffer) {
            if (task.dateEnd == null) {
                activeTaskList.add(task);
            } else {
                continue;
            }
        }
    }
}