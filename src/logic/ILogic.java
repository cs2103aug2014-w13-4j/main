package logic;

import command.Command;
import models.Feedback;

public interface ILogic {
	public Feedback executeCommand(Command command);

}
