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
import logic.TaskModifier;
import models.DateParser;
import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;
import exceptions.TaskNotFoundException;

import org.junit.Before;
import org.junit.Test;

import storage.Storage;
import command.Command;
import command.CommandParser;
import command.ParamEnum;

public class LogicTest {
	Class<Logic> LogicClass = Logic.class;
	Class<TaskModifier> TaskModifierClass = TaskModifier.class;
	Field storage = LogicClass.getDeclaredField("storage");
	Method modifyTask = TaskModifierClass.getDeclaredMethod("modifyTask",
			Hashtable.class, Task.class);
	Method display = LogicClass.getDeclaredMethod("display", Hashtable.class);

	public LogicTest() throws NoSuchMethodException, NoSuchFieldException {

	}

	@Before
	public void setFunctionsAccessible() {
		modifyTask.setAccessible(true);
		display.setAccessible(true);
	}

	@Before
	public void setFieldsAccessible() {
		storage.setAccessible(true);
	}

	@Before
	public void clearData() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("taskStorage.data");
		writer.print("");
		writer.close();
	}

	@Test
	public final void testExecuteAddCommand() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser
				.parseCommand("add eat my pet dog from 20-02-1999 note I don't know why I want that? level 2");
		Task newTask = new Task();
		newTask.setId(-1);
		modifyTask.invoke(TaskModifierClass, addCommand.getParam(), newTask);
		assertEquals("eat my pet dog", newTask.getName());
		assertEquals("I don't know why I want that?", newTask.getNote());
		assertEquals(PriorityLevelEnum.RED, newTask.getPriorityLevel());
	}

	@Test
	public final void testCompleteTask() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser
				.parseCommand("add eat my pet dog from 20-02-1999 note I don't know why I want that? level 2");
		logic.executeCommand(addCommand);
		Task uncompletedTask = ((Storage) storage.get(logic)).getTask(0);
		assertTrue(uncompletedTask.getDateEnd() == null);
		Command completeCommand = parser.parseCommand("done 0");
		logic.executeCommand(completeCommand);
		Task completedTask = ((Storage) storage.get(logic)).getTask(0);
		assertTrue(completedTask.getDateEnd() != null);
	}

	@Test
	public final void testCompleteTaskWithDate() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser.parseCommand("add eat my pet dog");
		logic.executeCommand(addCommand);
		Task uncompletedTask = ((Storage) storage.get(logic)).getTask(0);
		assertTrue(uncompletedTask.getDateEnd() == null);
		Command completeCommand = parser.parseCommand("done 0 date 30-1-1992");
		logic.executeCommand(completeCommand);
		Task completedTask = ((Storage) storage.get(logic)).getTask(0);
		assertTrue(completedTask.getDateEnd().get(Calendar.DAY_OF_MONTH) == 30);
		assertTrue(completedTask.getDateEnd().get(Calendar.MONTH) + 1 == 1);
		assertTrue(completedTask.getDateEnd().get(Calendar.YEAR) == 1992);
	}

	@Test
	public final void testDisplayIndividualTask() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser.parseCommand("add eat my pet dog");
		logic.executeCommand(addCommand);
		Command displayCommand = parser.parseCommand("display 0");
		Feedback feedback = (Feedback) display.invoke(logic,
				displayCommand.getParam());
		assertEquals("ID is the same", 0, feedback.getTaskDisplay().getId());
		assertEquals("Name is correct", "eat my pet dog", feedback
				.getTaskDisplay().getName());
		assertNull("Task list is empty", feedback.getTaskList());
	}

	@Test(expected = TaskNotFoundException.class)
	public final void testDisplayException() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser.parseCommand("add eat my pet dog");
		logic.executeCommand(addCommand);
		Command displayCommand = parser.parseCommand("display -1");
		logic.executeCommand(displayCommand);
	}

	@Test
	public final void testDisplayAll() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser.parseCommand("add first");
		logic.executeCommand(addCommand);
		addCommand = parser.parseCommand("add second thing");
		logic.executeCommand(addCommand);
		Command displayCommand = parser.parseCommand("display");
		Feedback feedback = (Feedback) display.invoke(logic,
				displayCommand.getParam());
		assertEquals("Task length is correct", 2, feedback.getTaskList().size());
		assertEquals("Task 1 is correct", "first", feedback.getTaskList()
				.get(0).getName());
		assertEquals("Task 2 is correct", "second thing", feedback
				.getTaskList().get(1).getName());
		assertNull("Task Display is empty", feedback.getTaskDisplay());
	}

	@Test
	public final void testConditionalTasks() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser
				.parseCommand("Add CS2103T from 23.12.1992 due 23.12.2002 or due 8.10.2014");
		Feedback feedback = logic.executeCommand(addCommand);
		Task task = feedback.getTaskList().get(0);
		assertEquals("Task name is correct", "CS2103T", task.getName());
		assertTrue("Conditional dates are present", task.getConditionalDates()
				.size() == 2);
		assertEquals("First start date is correct", "23-12-1992 00:00", DateParser.parseCalendar(task.getConditionalDates().get(0).getStartDate()));
		assertEquals("Second start date is correct", null, task.getConditionalDates().get(1).getStartDate());
		assertEquals("First due date is correct", "23-12-2002 00:00", DateParser.parseCalendar(task.getConditionalDates().get(0).getDueDate()));
		assertEquals("Second due date is correct", "8-10-2014 00:00", DateParser.parseCalendar(task.getConditionalDates().get(1).getDueDate()));
	}

}
