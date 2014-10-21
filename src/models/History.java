package models;

import command.CommandEnum;

public class History {
	private CommandEnum command;
	private Task task;

	public History(CommandEnum command, Task task) {
		this.setCommand(command);
		this.setTask(task);
	}

	public CommandEnum getCommand() {
		return command;
	}

	public void setCommand(CommandEnum command) {
		this.command = command;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}
