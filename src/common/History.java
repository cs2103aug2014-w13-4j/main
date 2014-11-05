package common;

import java.util.ArrayList;

import command.CommandEnum;

public class History {
    private CommandEnum command;
    private ArrayList<Task> tasks;

    public History(CommandEnum command, ArrayList<Task> tasks) {
        setCommand(command);
        setTask(tasks);
    }

    public CommandEnum getCommand() {
        return command;
    }

    public void setCommand(CommandEnum command) {
        this.command = command;
    }

    public ArrayList<Task> getTask() {
        return tasks;
    }

    public void setTask(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
