package models;
import gui.iGui;
import command.CommandParser;
import logic.Logic;

public class Gui implements iGui {

	public void main(String[] args){
		Command userInput = parseCommand(args);
		Feedback executionResult = executeCommand(userInput);
		// TODO: Link result to GUI
	}
}
