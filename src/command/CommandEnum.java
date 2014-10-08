package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CommandEnum {
	ADD ("add", ParamEnum.NAME, ParamEnum.DUE_DATE, ParamEnum.OR_DUE, ParamEnum.OR_FROM, ParamEnum.DATE, ParamEnum.START_DATE,
			ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG),
	DELETE ("delete", ParamEnum.KEYWORD, null),
	UPDATE ("update", ParamEnum.KEYWORD, ParamEnum.DUE_DATE, ParamEnum.DATE, ParamEnum.START_DATE,
			ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG),
	UNDO ("undo", ParamEnum.KEYWORD, null),
	FILTER ("filter", ParamEnum.KEYWORD, null, ParamEnum.STATUS),
	SEARCH ("search", ParamEnum.KEYWORD, null, ParamEnum.NAME, ParamEnum.NOTE, ParamEnum.TAG),
	DISPLAY ("display", ParamEnum.KEYWORD, null),
	DONE ("done", ParamEnum.KEYWORD, null, ParamEnum.DATE),
	TAG ("\\+", ParamEnum.KEYWORD, null),
	LEVEL ("level", ParamEnum.KEYWORD, null);
	
	private final String regex;
	private final ParamEnum commandKey;
	private final ParamEnum startParam;
	private final ParamEnum[] params;
	
	/**
	 * 
	 * @param regex The regex pattern of the command
	 * @param associatedParams Additional params associated with the command
	 */
	CommandEnum(String regex,ParamEnum commandKey, ParamEnum startParam, ParamEnum... associatedParams)
	{
		this.regex = regex;
		this.commandKey = commandKey;
		this.startParam = startParam;
		this.params = associatedParams;
	}
	
	public String regex() { return regex; }
	
	public ParamEnum[] params() { return params; } 
	
	public ParamEnum commandKey() { return commandKey; }
	
	public ParamEnum startParam() { return startParam; }
}