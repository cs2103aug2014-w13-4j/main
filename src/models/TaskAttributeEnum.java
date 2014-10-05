package models;

public enum TaskAttributeEnum {
    ID ("ID"),
    NAME ("Name"),
    DATE_DUE ("Due date"),
    DATE_START ("Start date"),
    DATE_END ("End date"),
    PRIORITY_LEVEL ("Priority level"),
    NOTE ("Note"),
    TAGS ("Tags"),
    PARENT_TASKS ("Parent tasks"),
    CHILD_TASKS ("Child tasks"),
    CONDITIONAL_DATES ("Conditional Dates"),
    IS_DELETED ("Is deleted"),
    IS_CONFIRMED ("Is confirmed");
    
    private final String attributeType;
    
    TaskAttributeEnum(String attributeType) {
        this.attributeType = attributeType;
    }
    
    public String attributeType() { return attributeType; }
}
