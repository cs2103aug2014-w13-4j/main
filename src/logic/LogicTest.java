package logic;

import static org.junit.Assert.*;

import java.util.ArrayList;

import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;

import org.junit.Test;

import command.Command;
import command.CommandEnum;
import command.ParamEnum;

public class LogicTest {

	@Test
	public final void testExecuteEmptyAddCommand() {
		Logic logic = new Logic();
		logic.initialize();
		assertNotNull(logic);
		
		Command addCommand = new Command(CommandEnum.ADD);
		Task test = logic.createTaskForAdd(addCommand);
		//Feedback feedback = logic.executeCommand(addCommand);
		//assertNotNull(test.getId());
	}
	
	@Test
	public final void testExecuteAddCommand() {
		Logic logic = new Logic();
		logic.initialize();
		assertNotNull(logic);
		//note and description seems misleading
		Command addCommand = new Command(CommandEnum.ADD);
		addCommand.addCommandArgument("Eat my pet dog");
		String description = "I don't know why I want to do that?";
		addCommand.addParam(ParamEnum.NOTE, description);
		addCommand.addParam(ParamEnum.LEVEL, "2");
		Task test = logic.createTaskForAdd(addCommand);
		assertEquals(test.getName(), "Eat my pet dog");
		assertEquals(test.getNote(), "I don't know why I want to do that?");
		assertEquals(test.getPriorityLevel(), PriorityLevelEnum.RED);
		//Feedback feedback = logic.executeCommand(addCommand);
		//assertNotNull(test.getId());
	}

}
