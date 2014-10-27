package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import command.CommandParser;
import models.Command;
import command.ParamEnum;

public class CommandParserTest {

    CommandParser cp;

    @Before
    public void initialize() {
        cp = new CommandParser();
    }

    @Test
    /**
     * Test command parser on adding multiple conditional dates
     * @throws Exception
     */
    public void testMuplipleConditionalDate() throws Exception {
        // This is the boundary case for adding only one due date
        String oneDate = "add test1 due 12-12-12";
        Command oneDateResult = cp.parseCommand(oneDate);
        ArrayList<String> oneDateArray = new ArrayList<String>();
        oneDateArray.add("12-12-12");
        assertEquals(oneDateArray,
                oneDateResult.getParam().get(ParamEnum.DUE_DATE));

        // This is the boundary for a single case of date pair
        String fromDueDate = "add test2 from 11-11-11 to 11-11-12";
        Command fromDueDateResult = cp.parseCommand(fromDueDate);
        ArrayList<String> dueDateArray = new ArrayList<String>();
        dueDateArray.add("11-11-12");
        ArrayList<String> fromDateArray = new ArrayList<String>();
        fromDateArray.add("11-11-11");
        assertEquals(dueDateArray,
                fromDueDateResult.getParam().get(ParamEnum.END_DATE));
        assertEquals(fromDateArray,
                fromDueDateResult.getParam().get(ParamEnum.START_DATE));

        // This is the boundary for a multiple case of date pair
        String multipleDate = "add test2 from 11-11-11 to 11-11-12 or from 12-12-11 to 12-12-12";
        Command mutipleDateResult = cp.parseCommand(multipleDate);
        ArrayList<String> multipleDueDateArray = new ArrayList<String>();
        multipleDueDateArray.add("11-11-12");
        multipleDueDateArray.add("12-12-12");
        ArrayList<String> multipleFromDateArray = new ArrayList<String>();
        multipleFromDateArray.add("11-11-11");
        multipleFromDateArray.add("12-12-11");
        assertEquals(multipleDueDateArray,
                mutipleDateResult.getParam().get(ParamEnum.END_DATE));
        assertEquals(multipleFromDateArray,
                mutipleDateResult.getParam().get(ParamEnum.START_DATE));
    }

}
