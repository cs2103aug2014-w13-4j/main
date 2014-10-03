package tests;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import logic.Logic;
import models.PriorityLevelEnum;
import models.Task;

import org.junit.Before;
import org.junit.Test;

import storage.Storage;
import command.Command;
import command.CommandParser;

public class LogicTest {
	Class<Logic> LogicClass = Logic.class;
	Field storage = LogicClass.getDeclaredField("storage");
	Method createTaskForAdd = LogicClass.getDeclaredMethod("createTaskForAdd",
			Command.class);
	
	public LogicTest() throws NoSuchMethodException, NoSuchFieldException {
		
	}

	@Before
	public void setFunctionsAccessible() {
		createTaskForAdd.setAccessible(true);
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
		Task test = (Task) createTaskForAdd.invoke(logic, addCommand);
		assertEquals("eat my pet dog", test.getName());
		assertEquals("I don't know why I want that?", test.getNote());
		assertEquals(PriorityLevelEnum.RED, test.getPriorityLevel());
	}
	
	@Test
	public final void testExecuteMarkAsDoneCommand() throws Exception {
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

}
