package common;

import java.util.ArrayList;

import command.CommandEnum;

//@author: A0114368E

/**
 * This class is used for undoing actions. It stores the command enum of the
 * previous command taken, and the tasks that needs to be written to storage to
 * undo it
 *
 * @author chocs
 *
 */
public class History {
    private CommandEnum command;
    private ArrayList<Task> tasks;

    public History(CommandEnum command, ArrayList<Task> tasks) {
        setCommand(command);
        setTasks(tasks);
    }

    public CommandEnum getCommand() {
        return command;
    }

    public void setCommand(CommandEnum command) {
        this.command = command;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
