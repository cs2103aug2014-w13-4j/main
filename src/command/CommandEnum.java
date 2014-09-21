package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CommandEnum {
	ADD ("Add", ParamEnum.DATE, ParamEnum.START_DATE, ParamEnum.END_DATE,
			ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG),
	DELETE ("B", ParamEnum.TASK_NAME, ParamEnum.TASK_NUMBER),
	UPDATE ("I", ParamEnum.TASK_NAME, ParamEnum.TASK_NUMBER, ParamEnum.DATE),
	UNDO ("I"),
	SELECT ("I", ParamEnum.ORDER_BY, ParamEnum.TAG),
	DISPLAY ("I"),
	DONE ("I", ParamEnum.TASK_NAME, ParamEnum.TASK_NUMBER),
	TAG ("I"),
	// Is this a param or a command? It seems to be a command in our Proposal
	LEVEL ("I", ParamEnum.LEVEL);
	
	private final String regex;
	private final ParamEnum[] params;
	
	/**
	 * 
	 * @param regex The regex pattern of the command
	 * @param associatedParams Additional params associated with the command
	 */
	CommandEnum(String regex, ParamEnum... associatedParams)
	{
		this.regex = regex;
		this.params = associatedParams;
	}
	
	public String regex() { return regex; }
	
	public ParamEnum[] params() { return params; } 
	
	// Test
	public static void main(String[] args) {
		String text = "Add how are you Note noted Date yesterday";
		String patternString = "(Add|Date|Note)(.*?)(?=Note|Date|$)";
		Pattern pattern = Pattern.compile(patternString);
		
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()) {
		    System.out.println("found: " + matcher.group(1) +
		                       " "       + matcher.group(2).trim());
		}
	}
}