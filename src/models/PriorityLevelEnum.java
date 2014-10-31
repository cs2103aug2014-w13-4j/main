package models;

import exceptions.InvalidPriorityLevelException;

public enum PriorityLevelEnum {
    DEFAULT(0, "default"), GREEN(1, "green"), ORANGE(2, "orange"), RED(3,
            "red");

    private static final int FIRST_INDEX = 0;

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

    public String getShortForm() {
        return String.valueOf(name.charAt(FIRST_INDEX));
    }

    /**
     * Returns the corresponding priority level enum given the integer level.
     *
     * @param level
     *            : the priority level
     * @return: priority level snum
     * @throws InvalidPriorityLevelException
     */
    public static PriorityLevelEnum fromInteger(int level)
            throws InvalidPriorityLevelException {
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
    public static PriorityLevelEnum fromString(String name)
            throws InvalidPriorityLevelException {
        name.toLowerCase().trim();
        return findPriorityLevelFromName(name);
    }

    private static PriorityLevelEnum findPriorityLevelFromLevel(int level)
            throws InvalidPriorityLevelException {
        for (PriorityLevelEnum e : PriorityLevelEnum.values()) {
            if (level == e.getLevel()) {
                return e;
            }
        }
        throw new InvalidPriorityLevelException();
    }

    private static PriorityLevelEnum findPriorityLevelFromName(String nameString)
            throws InvalidPriorityLevelException {
        String name = nameString.toLowerCase().trim();
        for (PriorityLevelEnum e : PriorityLevelEnum.values()) {
            if (name.equals(e.getName()) || name.equals(e.getShortForm())) {
                return e;
            }
        }
        throw new InvalidPriorityLevelException();
    }
}
