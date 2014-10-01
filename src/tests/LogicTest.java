package tests;

import static org.junit.Assert.*;
import logic.Logic;
import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;

import org.junit.Test;

import command.Command;
import command.CommandEnum;
import command.ParamEnum;

public class LogicTest {

	@Test
	public final void testExecuteAddCommand() {
		Logic logic = new Logic();
		logic.initialize();
		assertNotNull(logic);
		Command addCommand = new Command(CommandEnum.ADD);
		addCommand.addCommandArgument("Eat my pet dog");
		String description = "I don't know why I want to do that?";
		addCommand.addParam(ParamEnum.NOTE, description);
		addCommand.addParam(ParamEnum.LEVEL, "2");
		addCommand.addParam(ParamEnum.START_DATE, "20-02-1999");
		Task test = logic.createTaskForAdd(addCommand);
		assertEquals(test.getName(), "Eat my pet dog");
		assertEquals(test.getNote(), "I don't know why I want to do that?");
		assertEquals(test.getPriorityLevel(), PriorityLevelEnum.RED);
		Feedback feedback = logic.executeCommand(addCommand);
		assertNotNull(feedback.getFeedbackMessage());
		assertNotNull(feedback.getTaskList());
	}

}
