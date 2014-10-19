package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import logic.Logic;
import logic.LogicApi;
import logic.TaskModifier;
import models.DateParser;
import models.Command;
import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;
import exceptions.TaskNotFoundException;

import org.junit.Before;
import org.junit.Test;

import storage.Storage;
import command.CommandParser;

public class LogicTest {
	Class<LogicApi> logicApiClass = LogicApi.class;
	Class<Logic> logicClass = Logic.class;
	Class<TaskModifier> taskModifierClass = TaskModifier.class;
	Field logic = logicApiClass.getDeclaredField("logic");
	Field storage = logicClass.getDeclaredField("storage");
	Method modifyTask = taskModifierClass.getDeclaredMethod("modifyTask",
			Hashtable.class, Task.class);
	Method display = logicClass.getDeclaredMethod("display", Hashtable.class);
	LogicApi logicApiObject;
	Logic logicObject;
	Storage storageObject;
	CommandParser parser;

	public LogicTest() throws NoSuchMethodException, NoSuchFieldException {
		parser = new CommandParser();
	}

	@Before
	public void setFunctionsAccessible() {
		modifyTask.setAccessible(true);
		display.setAccessible(true);
	}

	@Before
	public void setFieldsAccessible() {
		storage.setAccessible(true);
		logic.setAccessible(true);
	}

	@Before
	public void clearData() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("taskStorage.data");
		writer.print("");
		writer.close();
	}

	@Before
	public void getLogicAndStorage() throws IllegalArgumentException,
			IllegalAccessException {
		logicApiObject = new LogicApi();
		logicApiObject.initialize();
		logicObject = (Logic) logic.get(logicApiObject);
		storageObject = (Storage) storage.get(logicObject);
	}

	@Test
	public final void testModifyTask() throws Exception {
		Command addCommand = parser
				.parseCommand("add eat my pet dog from 20-02-1999 note I don't know why I want that? level 2");
		Task newTask = new Task();
		newTask.setId(-1);
		modifyTask.invoke(taskModifierClass, addCommand.getParam(), newTask);
		assertEquals("eat my pet dog", newTask.getName());
		assertEquals("I don't know why I want that?", newTask.getNote());
		assertEquals(PriorityLevelEnum.RED, newTask.getPriorityLevel());
		assertEquals(20, newTask.getDateStart().get(Calendar.DAY_OF_MONTH));
		assertEquals(2, newTask.getDateStart().get(Calendar.MONTH) + 1);
		assertEquals(1999, newTask.getDateStart().get(Calendar.YEAR));
	}

	@Test
	public final void testCompleteTask() throws Exception {
		Command addCommand = parser
				.parseCommand("add eat my pet dog from 20-02-1999 note I don't know why I want that? level 2");
		logicApiObject.executeCommand(addCommand);
		Task uncompletedTask = storageObject.getTask(0);
		assertTrue(uncompletedTask.getDateEnd() == null);
		Command completeCommand = parser.parseCommand("done 0");
		logicApiObject.executeCommand(completeCommand);
		Task completedTask = storageObject.getTask(0);
		assertTrue(completedTask.getDateEnd() != null);
	}

	@Test
	public final void testCompleteTaskWithDate() throws Exception {
		Command addCommand = parser.parseCommand("add eat my pet dog");
		logicApiObject.executeCommand(addCommand);
		Task uncompletedTask = storageObject.getTask(0);
		assertNull(uncompletedTask.getDateEnd());
		Command completeCommand = parser.parseCommand("done 0 date 30-1-1992");
		logicApiObject.executeCommand(completeCommand);
		Task completedTask = storageObject.getTask(0);
		assertEquals(30, completedTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
		assertEquals(1, completedTask.getDateEnd().get(Calendar.MONTH) + 1);
		assertEquals(1992, completedTask.getDateEnd().get(Calendar.YEAR));
	}

	@Test
	public final void testDisplayIndividualTask() throws Exception {
		Command addCommand = parser.parseCommand("add eat my pet dog");
		logicApiObject.executeCommand(addCommand);
		Command displayCommand = parser.parseCommand("display 0");
		Feedback feedback = (Feedback) display.invoke(logicObject,
				displayCommand.getParam());
		assertEquals("ID is the same", 0, feedback.getTaskDisplay().getId());
		assertEquals("Name is correct", "eat my pet dog", feedback
				.getTaskDisplay().getName());
		assertNull("Task list is empty", feedback.getTaskList());
	}

	@Test(expected = TaskNotFoundException.class)
	public final void testDisplayException() throws Exception {
		Command addCommand = parser.parseCommand("add eat my pet dog");
		logicApiObject.executeCommand(addCommand);
		Command displayCommand = parser.parseCommand("display -1");
		logicApiObject.executeCommand(displayCommand);
	}

	@Test
	public final void testDisplayAll() throws Exception {
		Command addCommand = parser.parseCommand("add first");
		logicApiObject.executeCommand(addCommand);
		addCommand = parser.parseCommand("add second thing");
		logicApiObject.executeCommand(addCommand);
		Command displayCommand = parser.parseCommand("display");
		Feedback feedback = (Feedback) display.invoke(logicObject,
				displayCommand.getParam());
		assertEquals("Task length is correct", 2, feedback.getTaskList().size());
		assertEquals("Task 1 is correct", "first", storageObject.getTask(0).getName());
		assertEquals("Task 2 is correct", "second thing", storageObject.getTask(1).getName());
		assertNull("Task Display is empty", feedback.getTaskDisplay());
	}

	@Test
	public final void testConditionalTasks() throws Exception {
		Command addCommand = parser
				.parseCommand("Add CS2103T from 23.12.1992 due 23.12.2002 or due 8.10.2014");
		 logicApiObject.executeCommand(addCommand);
		Task task = storageObject.getTask(0);
		assertEquals("Task name is correct", "CS2103T", task.getName());
		assertTrue("Conditional dates are present", task.getConditionalDates()
				.size() == 2);
		assertEquals(
				"First start date is correct",
				"23-12-1992 00:00",
				DateParser.parseCalendar(task.getConditionalDates().get(0)
						.getStartDate()));
		assertEquals("Second start date is correct", null, task
				.getConditionalDates().get(1).getStartDate());
		assertEquals(
				"First due date is correct",
				"23-12-2002 00:00",
				DateParser.parseCalendar(task.getConditionalDates().get(0)
						.getDueDate()));
		assertEquals(
				"Second due date is correct",
				"8-10-2014 00:00",
				DateParser.parseCalendar(task.getConditionalDates().get(1)
						.getDueDate()));
	}
	
	@Test
	public final void testConfirmConditionalTasks() throws Exception {
		CommandParser parser = new CommandParser();
		Command addCommand = parser
				.parseCommand("Add CS2103T from 23.12.1992 due 23.12.2002 or due 8.10.2014");
		logicApiObject.executeCommand(addCommand);
		Command confirmCommand = parser.parseCommand("confirm 0 id 1");
		logicApiObject.executeCommand(confirmCommand);
		Task task = storageObject.getTask(0);
		assertEquals("Task name is correct", "CS2103T", task.getName());
		assertTrue("Task is confirmed", task.isConfirmed());
		assertEquals("Confirmed start date is correct", task.getConditionalDates().get(1).getStartDate(), task.getDateStart());
		assertEquals("Confirmed due date is correct", task.getConditionalDates().get(1).getDueDate(), task.getDateDue());
		assertEquals("Second due date is correct", "8-10-2014 00:00", DateParser.parseCalendar(task.getConditionalDates().get(1).getDueDate()));
	}
	
	@Test
	public final void testFilterTask() throws Exception {
		CommandParser parser = new CommandParser();
		Command addCommand = parser
				.parseCommand("Add completed task from 23.12.1992 due 23.12.2002");
		logicApiObject.executeCommand(addCommand);
		Command completeCommand = parser.parseCommand("done 0");
		logicApiObject.executeCommand(completeCommand);
		addCommand = parser
				.parseCommand("Add nocompleted task");
		logicApiObject.executeCommand(addCommand);
		Command filterCommand = parser.parseCommand("filter status completed");
		Feedback feedback = logicApiObject.executeCommand(filterCommand);
		ArrayList<Task> taskList = feedback.getTaskList();
		assertEquals("Only 1 task is shown", 1, taskList.size());
		assertEquals("Completed task is shown", "completed task", taskList.get(0).getName());
		
	}

}
