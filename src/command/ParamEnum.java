package command;

public enum ParamEnum {
	KEYWORD (""),
	DATE ("date"),
	NOTE ("note"),
	LEVEL ("level"),
	TAG ("\\+"),
	START_DATE ("from"),
	DUE_DATE ("due"),
	ORDER_BY ("order by"),
	EITHER ("either"),
	OR ("or"),
	NAME ("name"),
	STATUS ("status");
	
	private final String regex;
	
	ParamEnum(String regex) {
		this.regex = regex;
	}
	
	public String regex() { return regex; }
}
