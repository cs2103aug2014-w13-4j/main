package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CommandEnum {
	ADD ("add", ParamEnum.DATE, ParamEnum.START_DATE, ParamEnum.DUE_DATE,
			ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG),
	DELETE ("delete"),
	UPDATE ("update", ParamEnum.DATE, ParamEnum.NAME),
	UNDO ("undo"),
	SELECT ("select", ParamEnum.ORDER_BY, ParamEnum.TAG, ParamEnum.STATUS),
	SEARCH ("search", ParamEnum.ORDER_BY, ParamEnum.TAG, ParamEnum.STATUS),
	DISPLAY ("display"),
	DONE ("done"),
	TAG ("\\+"),
	LEVEL ("level");
	
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