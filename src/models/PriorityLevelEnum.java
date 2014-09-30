package models;

//TODO: Implement taking in input of colours
public enum PriorityLevelEnum {
	GREEN (0),
	ORANGE (1),
	RED (2);
	
	private final int level;
	//private static final HashMap<Integer, PriorityLevelEnum> lookup = new HashMap<Integer, PriorityLevelEnum>();
	
	PriorityLevelEnum(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}

	public static PriorityLevelEnum fromInteger(int level) {
		for (PriorityLevelEnum e : PriorityLevelEnum.values()) {
			if(level == e.getLevel()) {
				return e;
			}
			
		}
		return null;
	}

}
