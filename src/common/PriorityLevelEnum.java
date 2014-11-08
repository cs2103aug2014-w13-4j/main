package common;

import common.exceptions.InvalidPriorityLevelException;

//@author A0114368E

/**
 * This is the enum class for the priority level. There are currently 4
 * different levels (default, green, orange and red). The user can indicate the
 * priority level by number, name, or the first letter of the name of the level.
 *
 */
public enum PriorityLevelEnum {
    DEFAULT(0, "default"), GREEN(1, "green"), ORANGE(2, "orange"), RED(3, "red");

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
     * Returns the corresponding priority level Enum given the integer level.
     *
     * @param level
     *            : the priority level
     * @return: priority level Enum
     * @throws InvalidPriorityLevelException
     */
    public static PriorityLevelEnum fromInteger(int level)
            throws InvalidPriorityLevelException {
        return findPriorityLevelFromLevel(level);
    }

    /**
     * Returns the corresponding priority level Enum given the string name.
     *
     * @param name
     *            : the name of the priority level
     * @return: priority level Enum
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
