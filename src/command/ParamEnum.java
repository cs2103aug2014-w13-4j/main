package command;

public enum ParamEnum {
	KEYWORD ("", ""),
	DATE ("date", ""),
	NOTE ("note", ""),
	LEVEL ("level", ""),
	TAG ("\\+", ""),
	START_DATE ("from", "(?<from>\\S+)( due)?(?<due>.*)?$", "from", "due"),
	DUE_DATE ("due", "(?<due>.*)(?<from>.*?)?$", "from", "due"),
	ORDER_BY ("order by", ""),
	OR_FROM ("or from", "(?<from>\\S+)( due)?(?<due>.*)?$", "from", "due"),
	OR_DUE ("or due", "(?<due>.*)(?<from>.*?)?$", "from", "due"),
	NAME ("name", ""),
	ID ("id", ""),
	STATUS ("status", "");
	
	private final String regex;
	private final String deepRegex;
	private final String[] groupNames;
	
	ParamEnum(String regex, String deepRegex, String... groupNames) {
		this.regex = regex;
		this.deepRegex = deepRegex;
		this.groupNames = groupNames;
	}
	
	public String regex() { return regex; }
	public String deepRegex() { return deepRegex; }
	public String[] groupNames() { return groupNames; }
}
