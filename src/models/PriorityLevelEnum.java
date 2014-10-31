package models;

import exceptions.InvalidPriorityLevelException;

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
     *
     * @param level
     *            : the priority level
     * @return: priority level snum
     * @throws InvalidPriorityLevelException 
     */
    public static PriorityLevelEnum fromInteger(int level) throws InvalidPriorityLevelException {
        return findPriorityLevelFromLevel(level);
    }

    /**
     * Returns the corresponding priority level enum given the string name.
     *
     * @param level
     *            : the name of the priority level
     * @return: priority level enum
     * @throws InvalidPriorityLevelException 
     */
    public static PriorityLevelEnum fromString(String name) throws InvalidPriorityLevelException {
        name.toLowerCase().trim();
        return findPriorityLevelFromName(name);
    }

    private static PriorityLevelEnum findPriorityLevelFromLevel(int level) throws InvalidPriorityLevelException {
        if (!isDefaultLevel(level)) {
            for (PriorityLevelEnum e : PriorityLevelEnum.values()) {
                if (level == e.getLevel()) {
                    return e;
                }
            }
        }
        throw new InvalidPriorityLevelException();
    }

    private static PriorityLevelEnum findPriorityLevelFromName(String nameString) throws InvalidPriorityLevelException {
        String name = nameString.toLowerCase().trim();
        if (!isDefaultName(name)) {
            for (PriorityLevelEnum e : PriorityLevelEnum.values()) {
                if (name.equals(e.getName())) {
                    return e;
                }
            }
        }
        throw new InvalidPriorityLevelException();
    }

    private static boolean isDefaultLevel(int level) {
        return level == PriorityLevelEnum.DEFAULT.getLevel();
    }

    private static boolean isDefaultName(String name) {
        return name == PriorityLevelEnum.DEFAULT.getName();
    }
}
