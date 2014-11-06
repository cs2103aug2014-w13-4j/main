package command;

/**
 * This enum class describe the regex keyword that for each of the param command
 *
 * @author xuanyi
 *
 */
public enum ParamEnum {
    KEYWORD("", ""),
    DATE(" date", ""),
    NOTE(" note", ""),
    LEVEL(" level", ""),
    TAG(" \\+", " +"),
    DUE_DATE(" due", ""),
    START_DATE(" from", ""),
    END_DATE(" to ", ""),
    ORDER_BY(" order by", ""),
    OR_FROM(" or from", " from"),
    OR_END(" or to ", " to"),
    NAME(" name", ""),
    ID(" id", ""),
    BEFORE(" before", ""),
    AFTER(" after", ""),
    ON(" on", ""),
    DURATION (" duration", "");

    private final String regex;
    private final String groupName;

    ParamEnum(String regex, String groupName) {
        this.regex = regex;
        this.groupName = groupName;
    }

    public String regex() {
        return this.regex;
    }

    public String groupName() {
        return this.groupName.isEmpty() ? this.regex : this.groupName;
    }
}
