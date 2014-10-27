package command;

public enum ParamEnum {
	KEYWORD ("", ""),
	DATE ("date", ""),
	NOTE ("note", ""),
	LEVEL ("level", ""),
	TAG ("\\+", ""),
	DUE_DATE ("due", ""),
	START_DATE ("from", "(?<from>\\S+)( to)?(?<to>.*)?$", "from", "to"),
	END_DATE ("to", "(?<to>.*)(?<from>.*?)?$", "from", "to"),
	ORDER_BY ("order by", ""),
	OR_FROM ("or from", "(?<from>\\S+)( to)?(?<to>.*)?$", "from", "to"),
	OR_END ("or to", "(?<to>.*)(?<from>.*?)?$", "from", "to"),
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
