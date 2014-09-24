package storage.taskStorage;

public class TaskStorage {
    private ArrayList<Task> taskBuffer;
    private int nextTaskIndex;
    private File dataFile;
    private static Storage instance = null;

    private static final int ID_FOR_NEW_TASK = -1;
    private static final int ID_FOR_FIRST_TASK = 0;

    private static final String MESSAGE_SEPARATOR = "\tL@L";
    private static final String MESSAGE_ID = "Task ID: ";
    private static final String MESSAGE_NAME = "Name: ";
    private static final String MESSAGE_DATE_DUE = "Due date: ";
    private static final String MESSAGE_DATE_START = "Start date: ";
    private static final String MESSAGE_DATE_END = "End date: ";
    private static final String MESSAGE_PRIORITY_LEVEL = "Priority level: ";
    private static final String MESSAGE_NOTE = "Note: ";
    private static final String MESSAGE_TAGS = "Tags: ";
    private static final String MESSAGE_PARENT_TASKS = "Parent tasks: ";
    private static final String MESSAGE_CHILD_TASKS = "Child tasks: ";
    private static final String MESSAGE_CONDITIONAL_TASKS = "Conditional tasks: ";
    private static final String MESSAGE_IS_DELETED = "Is deleted :";
    private static final String MESSAGE_IS_COMFIRMED = "Is comfirmed: ";


     /**
     * constructor``
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

    private String TaskToString(Task task) {
        String[] taskStringArray = new String[]{MESSAGE_ID, task.getID(), MESSAGE_NAME, task.getName(), MESSAGE_DATE_DUE, task.getDateDue(),
            MESSAGE_DATE_START, task.getDateStart(), MESSAGE_DATE_END, task.getDateEnd(), MESSAGE_PRIORITY_LEVEL, task.getPriorityLevel(),
            MESSAGE_NOTE, task.getNote(), MESSAGE_PARENT_TASKS};
        String taskString = taskStringArray.join(MESSAGE_SEPARATOR);
        for (int parentID : task.getParentTasks()) {
            taskString = taskString + MESSAGE_SEPARATOR + parentID;
        }
        taskString = taskString + MESSAGE_SEPARATOR + MESSAGE_CHILD_TASKS;
        for (int childID : task.getChildTasks()) {
            taskString = taskString + MESSAGE_SEPARATOR + childID;
        }
        taskString = taskString + MESSAGE_SEPARATOR + MESSAGE_CONDITIONAL_TASKS;
        for (int conditionalID : task.getConditionalTasks()) {
            taskString = taskString + MESSAGE_SEPARATOR + conditionalID;
        }
        taskString = taskString + MESSAGE_SEPARATOR + MESSAGE_IS_DELETED;
        taskString = taskString + MESSAGE_SEPARATOR + task.isDeleted();
        taskString = taskString + MESSAGE_SEPARATOR + MESSAGE_IS_COMFIRMED;
        taskString = taskString + MESSAGE_SEPARATOR + task.isComfirmed();
        return taskString;
    }

    private Task stringToTask(String taskString) {
        
    }

    // Add/Update a task to file
    public void writeTaskToFile(Task task) throws TaskNotFoundException, IOException {
        int taskID = task.getID();
        if (taskID == ID_FOR_NEW_TASK) {
            // Add new task to task file
            task.setID(nextTaskIndex);
            nextTaskIndex ++;
            addTask(task);
            // Add new task to task buffer
            taskBuffer.add(task);
        } else {
            if (isTaskExist(taskID)) {
                // Update task to task file
                updateTask();
                // Update task to task buffer
                taskBuffer.set(taskID, task);
            } else {
                throw new TaskNotFoundException("Cannot update task since the current task doesn't exist");
            }
        }
    }

    // Delete a task from file
    public void deleteTaskFromFile(int taskID) throws TaskNotFoundException, IOException {
        if (isTaskExist(taskID)){
            for (Task task: taskBuffer) {
                if (task.getID() == taskID) {
                    task.setDeleted(true);
                    break;
                }
            }
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
            for (Task task: taskBuffer) {
                if (task.getID() == taskID) {
                    return task;
                }
            }
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
            if (task.getDateEnd() == null) {
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
            if (task.getDateEnd() == null) {
                activeTaskList.add(task);
            } else {
                continue;
            }
        }
    }

    // Search a list of tasks with certain tags
    ArrayList<Task> searchTask(ArrayList<String> tags) {
        ArrayList<Task> taskList = new ArrayList<Task>();
        boolean hasTags;
        for (Task task: taskBuffer) {
            hasTags = true;
            for (String tag: tags) {
                if (task.getTags.contains(tag)) {
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
    }
}