package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CommandEnum {
	ADD ("add",ParamEnum.NAME, ParamEnum.DATE, ParamEnum.START_DATE, ParamEnum.DUE_DATE,
			ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG),
	DELETE ("delete", ParamEnum.KEYWORD),
	UPDATE ("update", ParamEnum.KEYWORD, ParamEnum.DATE, ParamEnum.NAME, ParamEnum.NOTE, ParamEnum.DUE_DATE,
			ParamEnum.START_DATE, ParamEnum.DATE),
	UNDO ("undo", ParamEnum.KEYWORD),
	FILTER ("filter", ParamEnum.KEYWORD, ParamEnum.STATUS),
	SEARCH ("search", ParamEnum.KEYWORD, ParamEnum.NAME, ParamEnum.NOTE, ParamEnum.TAG),
	DISPLAY ("display", ParamEnum.KEYWORD),
	DONE ("done", ParamEnum.KEYWORD, ParamEnum.DATE),
	TAG ("\\+", ParamEnum.KEYWORD),
	LEVEL ("level", ParamEnum.KEYWORD);
	
	private final String regex;
	private final ParamEnum commandKey;
	private final ParamEnum[] params;
	
	/**
	 * 
	 * @param regex The regex pattern of the command
	 * @param associatedParams Additional params associated with the command
	 */
	CommandEnum(String regex,ParamEnum commandKey, ParamEnum... associatedParams)
	{
		this.regex = regex;
		this.commandKey = commandKey;
		this.params = associatedParams;
	}
	
	public String regex() { return regex; }
	
	public ParamEnum[] params() { return params; } 
	
	public ParamEnum commandKey() { return commandKey; }

	// Test
	public static void main(String[] args) {
		
		System.out.println(CommandEnum.values());
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