package models;

//TODO: Implement taking in input of colours
public enum PriorityLevelEnum {
    DEFAULT(-1, "default"), GREEN(1, "green"), ORANGE(2, "orange"), RED(3,
            "red");

    private final int level;
    private final String name;

    PriorityLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the corresponding priority level enum given the integer level. 
     * @param level: the priority level
     * @return: priority level snum
     */
    public static PriorityLevelEnum fromInteger(int level) {
        if (level != PriorityLevelEnum.DEFAULT.getLevel()) {
            for (PriorityLevelEnum e : PriorityLevelEnum.values()) {
                if (level == e.getLevel()) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * Returns the corresponding priority level enum given the string name. 
     * @param level: the name of the priority level
     * @return: priority level snum
     */
    public static PriorityLevelEnum fromString(String name) {
        name.toLowerCase().trim();
        if (name != PriorityLevelEnum.DEFAULT.getName()) {
            for (PriorityLevelEnum e : PriorityLevelEnum.values()) {
                if (name.equals(e.getName())) {
                    return e;
                }
            }
        }
        return null;
    }

}
