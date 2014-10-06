package tests;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

import logic.Logic;
import logic.TaskModifier;
import models.PriorityLevelEnum;
import models.Task;

import org.junit.Before;
import org.junit.Test;

import storage.Storage;
import command.Command;
import command.CommandParser;

public class LogicTest {
	Class<Logic> LogicClass = Logic.class;
	Class<TaskModifier> TaskModifierClass = TaskModifier.class;
	Field storage = LogicClass.getDeclaredField("storage");
	Method modifyTask = TaskModifierClass.getDeclaredMethod("modifyTask", Command.class, Task.class);

	public LogicTest() throws NoSuchMethodException, NoSuchFieldException {

	}

	@Before
	public void setFunctionsAccessible() {
		modifyTask.setAccessible(true);
	}

	@Before
	public void setFieldsAccessible() {
		storage.setAccessible(true);
	}

	@Test
	public final void testExecuteAddCommand() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser.parseCommand("add eat my pet dog from 20-02-1999 note I don't know why I want that? level 2");
		Task newTask = new Task();
		newTask.setId(-1);
		modifyTask.invoke(TaskModifierClass, addCommand, newTask);
		assertEquals("eat my pet dog", newTask.getName());
		assertEquals("I don't know why I want that?", newTask.getNote());
		assertEquals(PriorityLevelEnum.RED, newTask.getPriorityLevel());
	}

	@Test
	public final void testCompleteTask() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser();
		Command addCommand = parser.parseCommand("add eat my pet dog from 20-02-1999 note I don't know why I want that? level 2");
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

}
