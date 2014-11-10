package command;

//@author A0098722W
/**
 * This enum describe the different kind of params enum that are allowed for
 * each of the command
 *
 *
 *
 */
public enum CommandEnum {
    ADD("add", ParamEnum.NAME, ParamEnum.DUE_DATE, ParamEnum.OR_END,
        ParamEnum.OR_FROM, ParamEnum.DATE, ParamEnum.START_DATE,
        ParamEnum.END_DATE, ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG),
    DELETE("delete", ParamEnum.KEYWORD),
    UPDATE("update", ParamEnum.KEYWORD, ParamEnum.DUE_DATE, ParamEnum.OR_END,
        ParamEnum.OR_FROM, ParamEnum.DATE, ParamEnum.START_DATE,
        ParamEnum.END_DATE,ParamEnum.LEVEL, ParamEnum.NOTE, ParamEnum.TAG, ParamEnum.NAME),
    UNDO("undo", ParamEnum.KEYWORD),
    SEARCH("search", ParamEnum.KEYWORD, ParamEnum.NAME, ParamEnum.NOTE,
        ParamEnum.TAG, ParamEnum.AFTER, ParamEnum.BEFORE,
        ParamEnum.START_DATE, ParamEnum.END_DATE, ParamEnum.ON),
    DISPLAY("display", ParamEnum.KEYWORD),
    DONE("done", ParamEnum.KEYWORD, ParamEnum.DATE),
    COMPLETE("complete", ParamEnum.KEYWORD, ParamEnum.DATE),
    CONFIRM("confirm", ParamEnum.KEYWORD, ParamEnum.ID),
    CLEAR("clear", ParamEnum.KEYWORD),
    TAB("tab", ParamEnum.KEYWORD),
    SUGGEST ("suggest", ParamEnum.NAME, ParamEnum.START_DATE, ParamEnum.END_DATE, ParamEnum.DURATION),
    ACCEPT ("accept", ParamEnum.KEYWORD);

    private final String action;
    private final ParamEnum commandKey;
    private final ParamEnum[] params;

    /**
     *
     * @param action
     *            The action of the command
     * @param associatedParams
     *            Additional params associated with the command
     */
    CommandEnum(String action, ParamEnum commandKey,
            ParamEnum... associatedParams) {
        this.action = action;
        this.commandKey = commandKey;
        params = associatedParams;
    }

    public String action() {
        return action;
    }

    public ParamEnum[] params() {
        return params;
    }

    public ParamEnum commandKey() {
        return commandKey;
    }

}