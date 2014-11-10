package command;

//@author A0098722W
public enum CommandAlias {
    ADD(CommandEnum.ADD, "add"),
    DELETE(CommandEnum.DELETE, "delete", "remove"),
    UPDATE(CommandEnum.UPDATE, "modify", "update"),
    UNDO(CommandEnum.UNDO, "undo"),
    SEARCH(CommandEnum.SEARCH, "search", "find"),
    DISPLAY(CommandEnum.DISPLAY, "display", "show"),
    DONE(CommandEnum.DONE, "done", "complete"),
    CONFIRM(CommandEnum.CONFIRM, "confirm"),
    CLEAR(CommandEnum.CLEAR, "clear"),
    TAB(CommandEnum.TAB, "tab"),
    SUGGEST(CommandEnum.SUGGEST, "suggest"),
    ACCEPT(CommandEnum.ACCEPT, "accept");

    private final CommandEnum command;
    private final String[] alias;

    CommandAlias(CommandEnum command, String... aliasList) {
        this.command = command;
        this.alias = aliasList;
    }

    public CommandEnum command() {
        return this.command;
    }

    public String[] alias() {
        return this.alias;
    }

}
