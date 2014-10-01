package tests;

import static org.junit.Assert.*;
import logic.Logic;
import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;

import org.junit.Test;

import command.Command;
import command.CommandEnum;
import command.CommandParser;
import command.ParamEnum;

public class LogicTest {

	@Test
	public final void testExecuteAddCommand() throws Exception {
		Logic logic = new Logic();
		logic.initialize();
		CommandParser parser = new CommandParser(); 
		Command addCommand = parser.parseCommand("add eat my pet dog from 20-02-1999 note I don't know why I want that? level 2");
		Task test = logic.createTaskForAdd(addCommand);
		assertEquals("eat my pet dog", test.getName());
		assertEquals("I don't know why I want that?", test.getNote());
		assertEquals(PriorityLevelEnum.RED, test.getPriorityLevel());
		Feedback feedback = logic.executeCommand(addCommand);
		assertNotNull(feedback.getFeedbackMessage());
		assertNotNull(feedback.getTaskList());
	}

}
