package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import models.PriorityLevelEnum;
import models.Task;
import command.ParamEnum;

import org.junit.Before;
import org.junit.Test;

import exceptions.FileFormatNotSupportedException;
import storage.taskStorage.TaskStorage;

public class TaskStorageTest {
	private static final int ID_FOR_NEW_TASK = -1;

	private Task createTaskForTest(int id, String name, int priorityLevel,
			String note, boolean isDeleted, boolean isConfirmed) {
		Task task = new Task();
		task.setId(id);
		task.setName(name);
		task.setPriorityLevel(PriorityLevelEnum.fromInteger(priorityLevel));
		task.setNote(note);
		task.setDeleted(isDeleted);
		return task;
	}

	@Before
	public void clearData() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("taskStorage.data");
		writer.print("");
		writer.close();
	}

	@Test
	public void testCanAddAndUpdateTask() {
		try {
			// clear the file before testing
			/*

            */

			TaskStorage taskStorage = new TaskStorage("taskStorage.data");
			Task task = createTaskForTest(ID_FOR_NEW_TASK, "Write Report", 1,
					"Do eat apple when you are writing report.", false, false);
			taskStorage.writeTaskToFile(task);
			assertEquals(task,
					taskStorage.getTask(taskStorage.getAllTasks().size() - 1));
			task = createTaskForTest(0, "Read Report", 1,
					"Do eat apple when you are writing report.", false, false);
			taskStorage.writeTaskToFile(task);
			assertEquals(task, taskStorage.getTask(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCanSearchTask() {
		try {
			TaskStorage taskStorage = new TaskStorage("taskStorage.data");
			Hashtable<ParamEnum, ArrayList<String>> keyWordTable = new Hashtable<ParamEnum, ArrayList<String>>();
			ArrayList<String> taskName = new ArrayList<String>();
			taskName.add("Report");
			keyWordTable.put(ParamEnum.NAME, taskName);
			ArrayList<Task> searchResult = taskStorage.searchTask(keyWordTable,
					taskStorage.getAllTasks());
			assertEquals(searchResult.size(), taskStorage.getAllTasks().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
