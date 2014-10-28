package command;

public enum ParamEnum {
	KEYWORD ("", ""),
	DATE ("date", ""),
	NOTE ("note", ""),
	LEVEL ("level", ""),
	TAG ("\\+", "+"),
	DUE_DATE ("due", ""),
	START_DATE ("from", ""),
	END_DATE ("to ", ""),
	ORDER_BY ("order by", ""),
	OR_FROM ("or from", "from"),
	OR_END ("or to ", "to"),
	NAME ("name", ""),
	ID ("id", ""),
	STATUS ("status", "");
	
	private final String regex;
	private final String groupName;
	
	ParamEnum(String regex, String groupName) {
		this.regex = regex;
		this.groupName = groupName;
	}
	
	public String regex() { return regex; }
	public String groupName() { return groupName.isEmpty() ? regex : groupName; }
}
