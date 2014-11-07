package command;

import java.util.ArrayList;

public enum CommandAlias {
    ADD(CommandEnum.ADD, "add"),
    DELETE(CommandEnum.DELETE, "delete", "remove"),
    UPDATE(CommandEnum.UPDATE, "modify", "update"),
    UNDO(CommandEnum.UNDO, "undo"),
    SEARCH(CommandEnum.SEARCH, "search", "find"),
    DISPLAY(CommandEnum.DISPLAY, "display", "show"),
    DONE(CommandEnum.DONE, "done", "complete"),
    TAG(CommandEnum.TAG, "\\+"),
    LEVEL(CommandEnum.LEVEL, "level", "prioirty"),
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
