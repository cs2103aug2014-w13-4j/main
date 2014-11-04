package command;

public enum CommandEnum {
	ADD ("add", ParamEnum.NAME, ParamEnum.DUE_DATE, ParamEnum.OR_END, ParamEnum.OR_FROM, ParamEnum.DATE, ParamEnum.START_DATE, ParamEnum.END_DATE,
			ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG),
	DELETE ("delete", ParamEnum.KEYWORD),
	UPDATE ("update", ParamEnum.KEYWORD, ParamEnum.DUE_DATE, ParamEnum.OR_END, ParamEnum.OR_FROM, ParamEnum.DATE, ParamEnum.START_DATE, ParamEnum.END_DATE,
            ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG, ParamEnum.NAME),
	UNDO ("undo", ParamEnum.KEYWORD),
	SEARCH ("search", ParamEnum.KEYWORD, ParamEnum.NAME, ParamEnum.NOTE, ParamEnum.TAG, ParamEnum.STATUS, ParamEnum.AFTER, ParamEnum.BEFORE, ParamEnum.START_DATE, ParamEnum.END_DATE, ParamEnum.ON),
	DISPLAY ("display", ParamEnum.KEYWORD),
	DONE ("done", ParamEnum.KEYWORD, ParamEnum.DATE),
	COMPLETE ("complete", ParamEnum.KEYWORD, ParamEnum.DATE),
	TAG ("\\+", ParamEnum.KEYWORD),
	LEVEL ("level", ParamEnum.KEYWORD),
	CONFIRM ("confirm", ParamEnum.KEYWORD, ParamEnum.ID),
	CLEAR ("clear", ParamEnum.KEYWORD);
	
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
}