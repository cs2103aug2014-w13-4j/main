package models;
import gui.iGui;
import command.CommandParser;
import command.Command;
//import logic.Logic;

public class Gui implements iGui {

	public void main(String[] args){
		CommandParser commandParser = new CommandParser();
		Command userInput = commandParser.parseCommand(args[0]);
		Feedback executionResult = executeCommand(userInput);
		// TODO: Link result to GUI
	}
}
