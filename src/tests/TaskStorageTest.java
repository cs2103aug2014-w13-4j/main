package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import command.ParamEnum;
import common.DateParser;
import common.PriorityLevelEnum;
import common.StartEndDatePair;
import common.Task;
import common.exceptions.FileFormatNotSupportedException;
import common.exceptions.InvalidPriorityLevelException;
import common.exceptions.TimeIntervalOverlapException;

import org.junit.Before;
import org.junit.Test;

import storage.taskStorage.TaskStorage;

public class TaskStorageTest {
    private static final int ID_FOR_NEW_TASK = 0;
    TaskStorage taskStorage; 

    @Before
    public void clearData() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("taskStorage.data");
        writer.print("");
        writer.close();
        try {
			taskStorage = TaskStorage
			            .getNewInstance("taskStorage.data");
		} catch (IOException | FileFormatNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private Task createTaskForTest(int id, String name, Calendar dateDue, 
    		Calendar dateStart, Calendar dateEnd, int priorityLevel,
            String note, boolean isDeleted, ArrayList<String> tags, 
            ArrayList<StartEndDatePair> datePair)
            throws InvalidPriorityLevelException {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setDateDue(dateDue);
        task.setDateStart(dateStart);
        task.setDateEnd(dateEnd);
        task.setPriorityLevel(PriorityLevelEnum.fromInteger(priorityLevel));
        task.setNote(note);
        task.setTags(tags);
        task.setDeleted(isDeleted);
        task.setConditionalDates(datePair);
        return task;
    }

    /**
     * Tests that the task storage allows adding/updating tasks
     *
     * @throws Exception
     */
    @Test 
    public void testCanAddAndUpdateTask() {
    	try {
	    	Calendar dateStart = DateParser.parseString("21-10-2010");
	    	Calendar dateEnd = DateParser.parseString("22-10-2010");
	    	Calendar dateDue = DateParser.parseString("20-10-2010");
	    	ArrayList<String> tags = new ArrayList<String>();
	    	ArrayList<StartEndDatePair> datePairArrayList = new ArrayList<StartEndDatePair>();

	    	Task task = createTaskForTest(ID_FOR_NEW_TASK, "Eat chocalate",
	            		null, dateStart, dateEnd, 1,
	                    "Chocalate is good for your pet dog.", 
	                    false, tags, datePairArrayList);
	    	taskStorage.writeTaskToFile(task);
	    	assertEquals(task,
	                    taskStorage.getTask(task.getId()));

	    	task = createTaskForTest(task.getId(), "Eat chocalate",
	    		   dateDue, null, null, 1,
	               "Chocalate is bad for your pet dog.", 
	               false, tags, datePairArrayList);
	    	taskStorage.writeTaskToFile(task);
	    	assertEquals(task,
	                    taskStorage.getTask(task.getId()));
	    }  catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Tests that the task storage allows adding/updating conditional tasks
     *
     * @throws Exception
     */
    @Test
    public void testConditionalTask() {
        try {
            ArrayList<StartEndDatePair> datePairArrayList = new ArrayList<StartEndDatePair>();
            StartEndDatePair datePairA = new StartEndDatePair(
                    DateParser.parseString("23-10-2010"),
                    DateParser.parseString("24-10-2010"));
            StartEndDatePair datePairB = new StartEndDatePair(
                    DateParser.parseString("25-10-2010"),
                    DateParser.parseString("27-10-2010"));
            datePairArrayList.add(datePairA);
            datePairArrayList.add(datePairB);
            ArrayList<String> tags = new ArrayList<String>();

            Task task = createTaskForTest(ID_FOR_NEW_TASK, "Write Report",
            		null, null, null, 1,
                    "Do eat apple when you are writing report.", 
                    false, tags, datePairArrayList);
            taskStorage.writeTaskToFile(task);
            assertEquals(task,
                    taskStorage.getTask(task.getId()));

            task = createTaskForTest(task.getId(), "Read Report", 
            		null, null, null, 1,
                    "Do eat apple when you are writing report.", 
                    false, tags, datePairArrayList);
            taskStorage.writeTaskToFile(task);
            assertEquals(task, taskStorage.getTask(task.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    /**
     * Tests that the task storage allows searching tasks
     *
     * @throws Exception
     */
    @Test
    public void testCanSearchTask() {
        try {
            Hashtable<ParamEnum, ArrayList<String>> keyWordTable = new Hashtable<ParamEnum, ArrayList<String>>();
            ArrayList<String> taskName = new ArrayList<String>();
            taskName.add("Report");
            ArrayList<String> taskPriorityLevel = new ArrayList<String>();
            taskPriorityLevel.add("1");
            ArrayList<String> taskStatus = new ArrayList<String>();
            taskStatus.add("completed");            
            keyWordTable.put(ParamEnum.KEYWORD, taskStatus);
            keyWordTable.put(ParamEnum.NAME, taskName);
            keyWordTable.put(ParamEnum.LEVEL, taskPriorityLevel);
            ArrayList<Task> searchResult = taskStorage.searchTask(keyWordTable);
            assertEquals(0, searchResult.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that error will be thrown if task added
     * overlaping with existing time interval
     *
     * @throws Exception
     */
    @Test (expected = TimeIntervalOverlapException.class)
    public void testAddOverlapingTasks()
            throws Exception {
    	Calendar dateStart = DateParser.parseString("21-09-2010");
    	Calendar dateEnd = DateParser.parseString("25-09-2010");
    	ArrayList<String> tags = new ArrayList<String>();
    	ArrayList<StartEndDatePair> datePairArrayList = new ArrayList<StartEndDatePair>();

    	Task task = createTaskForTest(ID_FOR_NEW_TASK, "Eat banana",
            		null, dateStart, dateEnd, 1,
                    "Banana can help you keep fit", 
                    false, tags, datePairArrayList);
    	taskStorage.writeTaskToFile(task);

    	dateStart = DateParser.parseString("22-09-2010");
    	dateEnd = DateParser.parseString("26-09-2010");
    	task = createTaskForTest(ID_FOR_NEW_TASK, "Eat apple",
            		null, dateStart, dateEnd, 1,
                    "Banana cannot help you keep fit", 
                    false, tags, datePairArrayList);
    	taskStorage.writeTaskToFile(task);
    }
}
