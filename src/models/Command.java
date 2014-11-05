package models;

import java.util.ArrayList;
import java.util.Hashtable;

import command.CommandEnum;
import command.ParamEnum;

/**
 * This class acts a a common object for different components of the project to interpret
 * the given command of the user
 * @author xuanyi
 *
 */
public class Command {

    private String commandString;
    private CommandEnum commandType;
    private String commandArgument;
    private Hashtable<ParamEnum, ArrayList<String>> params;

    public Command(CommandEnum commandType) {
        this.commandType = commandType;
        this.params = new Hashtable<ParamEnum, ArrayList<String>>();
    }

    public void addCommandString(String userCommandString) {
        commandString = userCommandString;
    }

    public void addCommandArgument(String arg) {
        addParam(commandType.commandKey(), arg);
    }

    public void addParam(ParamEnum param, String args) {
        if (!params.containsKey(param)) {
            ArrayList<String> newArgsList = new ArrayList<String>();
            params.put(param, newArgsList);
        }

        params.get(param).add(args);
    }

    public Hashtable<ParamEnum, ArrayList<String>> getParam() {
        return params;
    }

    public String getCommandString() {
        return commandString;
    }

    public CommandEnum getCommand() {
        return commandType;
    }

    public String getCommandArgument() {
        return commandArgument;
    }
}
