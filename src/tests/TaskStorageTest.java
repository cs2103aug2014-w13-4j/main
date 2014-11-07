package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import command.ParamEnum;
import common.DateParser;
import common.PriorityLevelEnum;
import common.StartEndDatePair;
import common.Task;
import common.exceptions.InvalidPriorityLevelException;

import org.junit.Before;
import org.junit.Test;

import storage.taskStorage.TaskStorage;

public class TaskStorageTest {
    private static final int ID_FOR_NEW_TASK = 0;

    @Before
    public void clearData() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("taskStorage.data");
        writer.print("");
        writer.close();
    }

    private Task createTaskForTest(int id, String name, int priorityLevel,
            String note, boolean isDeleted, ArrayList<StartEndDatePair> datePair)
                    throws InvalidPriorityLevelException {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setPriorityLevel(PriorityLevelEnum.fromInteger(priorityLevel));
        task.setNote(note);
        task.setDeleted(isDeleted);
        task.setConditionalDates(datePair);
        return task;
    }

    @Test
    public void testCanAddAndUpdateTask() {
        try {
            TaskStorage taskStorage = TaskStorage
                    .getNewInstance("taskStorage.data");
            ArrayList<StartEndDatePair> datePairArrayList = new ArrayList<StartEndDatePair>();
            StartEndDatePair datePairA = new StartEndDatePair(
                    DateParser.parseString("23.10.2010"),
                    DateParser.parseString("24.10.2010"));
            StartEndDatePair datePairB = new StartEndDatePair(
                    DateParser.parseString("25.10.2010"),
                    DateParser.parseString("27.10.2010"));
            datePairArrayList.add(datePairA);
            datePairArrayList.add(datePairB);

            Task task = createTaskForTest(ID_FOR_NEW_TASK, "Write Report", 1,
                    "Do eat apple when you are writing report.", false,
                    datePairArrayList);
            taskStorage.writeTaskToFile(task);
            assertEquals(task,
                    taskStorage.getTask(taskStorage.getAllTasks().size()));
            task = createTaskForTest(1, "Read Report", 1,
                    "Do eat apple when you are writing report.", false,
                    datePairArrayList);
            taskStorage.writeTaskToFile(task);
            assertEquals(task, taskStorage.getTask(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCanGetCompletedTask() {
        try {
            TaskStorage taskStorage = TaskStorage
                    .getNewInstance("taskStorage.data");
            ArrayList<Task> completedTaskList = taskStorage
                    .getAllCompletedTasks();
            assertEquals(0, completedTaskList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCanSearchTask() {
        try {
            TaskStorage taskStorage = TaskStorage
                    .getNewInstance("taskStorage.data");
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable = new Hashtable<ParamEnum, ArrayList<String>>();
            ArrayList<String> taskName = new ArrayList<String>();
            taskName.add("Report");
            ArrayList<String> taskPriorityLevel = new ArrayList<String>();
            taskPriorityLevel.add("1");
            ArrayList<String> taskStatus = new ArrayList<String>();
            taskStatus.add("completed");
            keyWordTable.put(ParamEnum.NAME, taskName);
            keyWordTable.put(ParamEnum.LEVEL, taskPriorityLevel);
            ArrayList<Task> searchResult = taskStorage.searchTask(keyWordTable);
            assertEquals(taskStorage.getAllTasks().size(), searchResult.size());

            keyWordTable.put(ParamEnum.KEYWORD, taskStatus);
            searchResult = taskStorage.searchTask(keyWordTable);
            assertEquals(0, searchResult.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
