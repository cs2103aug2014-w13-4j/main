package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import models.Feedback;
import models.Task;
import command.*;

public class Logic implements ILogic {

	public Feedback executeCommand(Command command) {
		// TODO Auto-generated method stub
		CommandEnum commandType = command.getCommand();
		Hashtable<ParamEnum, ArrayList<String>> params = command.getParam();
		switch (commandType) {
		case ADD:
			Task task = new Task();
			String taskName = null;
			//String taskName = params.get(ParamEnum.TASK_NAME);
			Calendar dateEnd = Calendar.getInstance();
			//TODO: Set date
			dateEnd.set(2012, 10);
			task.setName(taskName);
			task.setDateEnd(dateEnd);
			return null;
		default:
			return null;
		
		}
			
	}

}
