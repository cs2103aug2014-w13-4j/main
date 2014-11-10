package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import command.CommandParser;
import command.ParamEnum;
import common.Command;

public class CommandParserTest {

    CommandParser cp;

    @Before
    public void initialize() {
        cp = new CommandParser();
    }
    
    @Test
    /**
     * Test adding of a single param for a param type
     * @throws Exception
     */
    public void testAddSingleParamPerParamType() throws Exception {
        String oneKind = "add test1 +oneKind";
        Command oneKindResult = cp.parseCommand(oneKind);
        ArrayList<String> oneKindArray = new ArrayList<String>();
        oneKindArray.add("oneKind");
        assertEquals(oneKindArray,
                oneKindResult.getParam().get(ParamEnum.TAG));
    }
    
    @Test
    /**
     * Test adding of a multiple param for a param type
     * @throws Exception
     */
    public void testAddMultipleParamPerParamType() throws Exception {
        String multipleKind = "add test1 +oneKind +twoKind +threeKind";
        Command multipleKindResult = cp.parseCommand(multipleKind);
        ArrayList<String> multipleKindArray = new ArrayList<String>();
        multipleKindArray.add("oneKind");
        multipleKindArray.add("twoKind");
        multipleKindArray.add("threeKind");
        assertEquals(3, multipleKindResult.getParam().get(ParamEnum.TAG).size());
        assertEquals(multipleKindArray, multipleKindResult.getParam().get(ParamEnum.TAG));
    }

    // Test the command parser on different combinations of dates 
    @Test
    /**
     * Test command parser on a single due date
     * This is the boundary case for adding only one due date
     * @throws Exception
     */
    public void testSingleDate() throws Exception {
        String oneDate = "add test1 due 12-12-12";
        Command oneDateResult = cp.parseCommand(oneDate);
        ArrayList<String> oneDateArray = new ArrayList<String>();
        oneDateArray.add("12-12-12");
        assertEquals(oneDateArray,
                oneDateResult.getParam().get(ParamEnum.DUE_DATE));
    }
    
    @Test
    /**
     * Test the command parser on a date pair
     * This is the boundary for a single case of date pair
     * @throws Exception
     */
    public void testDatePair() throws Exception {
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
    }
    
    @Test
    /**
     * This is the boundary for a multiple case of date pair
     * @throws Exception
     */
    public void testMultipleConditionalDatePair() throws Exception {
        String multipleDate = "add test2 from 11-11-11 to 11-11-12 or from 12-12-11 to 12-12-12";
        Command multipleDateResult = cp.parseCommand(multipleDate);
        ArrayList<String> multipleDueDateArray = new ArrayList<String>();
        multipleDueDateArray.add("11-11-12");
        multipleDueDateArray.add("12-12-12");
        ArrayList<String> multipleFromDateArray = new ArrayList<String>();
        multipleFromDateArray.add("11-11-11");
        multipleFromDateArray.add("12-12-11");
        assertEquals(multipleDueDateArray,
            multipleDateResult.getParam().get(ParamEnum.END_DATE));
        assertEquals(multipleFromDateArray,
            multipleDateResult.getParam().get(ParamEnum.START_DATE));
    }
    
    
    // Test the command parser on different combinations of parameters
    
    @Test
    /**
     * Test command parser on a single paramters
     * @throws Exception
     */
    public void testSingleParam() throws Exception {
        String singleParam = "add test1";
        Command singleParamResult = cp.parseCommand(singleParam);
        
        ArrayList<String> nameArray = new ArrayList<String>();
        nameArray.add("test1");
        assertEquals(nameArray, singleParamResult.getParam().get(ParamEnum.NAME));
    }
    
    @Test
    /**
     * Test command parser on adding multiple parameters
     * @throws Exception
     */
    public void testMultipleParam() throws Exception {
        String multipleParam = "add test1 from 12-12-12 to 13-12-12 note testtesttest +abc + def";
        Command multipleParamResult = cp.parseCommand(multipleParam);
        
        ArrayList<String> startDateArray = new ArrayList<String>();
        startDateArray.add("12-12-12");
        assertEquals(startDateArray,
                multipleParamResult.getParam().get(ParamEnum.START_DATE));
        
        ArrayList<String> endDateArray = new ArrayList<String>();
        endDateArray.add("13-12-12");
        assertEquals(endDateArray, multipleParamResult.getParam().get(ParamEnum.END_DATE));
        
        ArrayList<String> noteArray = new ArrayList<String>();
        noteArray.add("testtesttest");
        assertEquals(noteArray, multipleParamResult.getParam().get(ParamEnum.NOTE));
        
        ArrayList<String> tagArray = new ArrayList<String>();
        tagArray.add("abc");
        tagArray.add("def");
        assertEquals(tagArray, multipleParamResult.getParam().get(ParamEnum.TAG));
    }
    
    @Test
    /**
     * Test the command parser to parse the same command string with different order of inputs params
     * @throws Exception
     */
    public void testOrderOfParam() throws Exception {
        String orderedParam = "add test1 due 13-12-12 note testtesttest +abc + def";
        Command orderedParamResult = cp.parseCommand(orderedParam);
        
        ArrayList<String> endDateArray = new ArrayList<String>();
        endDateArray.add("13-12-12");
        assertEquals(endDateArray, orderedParamResult.getParam().get(ParamEnum.DUE_DATE));
        
        ArrayList<String> tagArray = new ArrayList<String>();
        tagArray.add("abc");
        tagArray.add("def");
        assertEquals(tagArray, orderedParamResult.getParam().get(ParamEnum.TAG));
        
        ArrayList<String> noteArray = new ArrayList<String>();
        noteArray.add("testtesttest");
        assertEquals(noteArray, orderedParamResult.getParam().get(ParamEnum.NOTE));
        
        String unorderedParam = "add test1 +abc note testtesttest due 13-12-12 + def";
        Command unorderedParamResult = cp.parseCommand(unorderedParam);
        
        assertEquals(endDateArray, unorderedParamResult.getParam().get(ParamEnum.DUE_DATE));      
        assertEquals(tagArray, unorderedParamResult.getParam().get(ParamEnum.TAG));
        assertEquals(noteArray, unorderedParamResult.getParam().get(ParamEnum.NOTE));
    }
    
    @Test
    /**
     * Test the escape functionality of the command parser
     * @throws Exception
     */
    public void testEscapeParam() throws Exception {
        String escapedParam = "add test1 from 12-12-12 to 13-12-12 note give present \\to anna";
        Command escapedParamResult = cp.parseCommand(escapedParam);
        
        // The actual end date from the "to" param
        ArrayList<String> endDateArray = new ArrayList<String>();
        endDateArray.add("13-12-12");
        assertEquals(endDateArray, escapedParamResult.getParam().get(ParamEnum.END_DATE));
        
        ArrayList<String> noteArray = new ArrayList<String>();
        noteArray.add("give present to anna");
        assertEquals(noteArray, escapedParamResult.getParam().get(ParamEnum.NOTE));
    }
}
