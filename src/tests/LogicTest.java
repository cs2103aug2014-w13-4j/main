package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import logic.Logic;
import logic.LogicApi;
import logic.TaskModifier;

import org.junit.Before;
import org.junit.Test;

import command.ParamEnum;
import common.DateParser;
import common.Feedback;
import common.PriorityLevelEnum;
import common.Task;
import common.exceptions.FileFormatNotSupportedException;

public class LogicTest {
    Class<LogicApi> logicApiClass = LogicApi.class;
    Class<Logic> logicClass = Logic.class;
    Class<TaskModifier> taskModifierClass = TaskModifier.class;
    Field logic = logicApiClass.getDeclaredField("logic");
    Method update = logicClass.getDeclaredMethod("update", Hashtable.class);
    Method add = logicClass.getDeclaredMethod("add", Hashtable.class);
    Method complete = logicClass.getDeclaredMethod("complete", Hashtable.class);
    Method delete = logicClass.getDeclaredMethod("delete", Hashtable.class);
    Method display = logicClass.getDeclaredMethod("display", Hashtable.class);
    LogicApi logicApiObject;
    Logic logicObject;
    int thisYear = Calendar.getInstance().get(Calendar.YEAR);

    public LogicTest() throws NoSuchMethodException, NoSuchFieldException {
    }

    @Before
    public void clearData() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("taskStorage.data");
        writer.print("");
        writer.close();
    }

    @Before
    public void getLogicAndStorage() throws IllegalArgumentException,
    IllegalAccessException, IOException,
            FileFormatNotSupportedException {
        logicApiObject = LogicApi.getNewInstance();
        logicObject = (Logic) logic.get(logicApiObject);
    }

    @Before
    public void setFieldsAccessible() {
        logic.setAccessible(true);

    }

    @Before
    public void setFunctionsAccessible() {
        add.setAccessible(true);
        complete.setAccessible(true);
        delete.setAccessible(true);
        update.setAccessible(true);
        display.setAccessible(true);
    }

    @Test
    public void testAdd() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        Hashtable<ParamEnum, ArrayList<String>> params = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("test test");
        params.put(ParamEnum.NAME, nameList);
        ArrayList<String> noteList = new ArrayList<String>();
        noteList.add("Test is good");
        params.put(ParamEnum.NOTE, noteList);
        ArrayList<String> priorityList = new ArrayList<String>();
        priorityList.add("RED");
        params.put(ParamEnum.LEVEL, priorityList);
        Feedback feedback = (Feedback) add.invoke(logicObject, params);
        assertEquals(1, feedback.getTaskList().size());
        Task task = feedback.getTaskList().get(0);
        assertEquals("Test is good", task.getNote());
        assertEquals("test test", task.getName());
        assertEquals(PriorityLevelEnum.RED, task.getPriorityLevel());
    }

    @Test
    public void testAddWithIntPriorityLevel() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        Hashtable<ParamEnum, ArrayList<String>> params = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("test test");
        ArrayList<String> priorityList = new ArrayList<String>();
        priorityList.add("3");
        params.put(ParamEnum.LEVEL, priorityList);

        Feedback feedback = (Feedback) add.invoke(logicObject, params);
        assertEquals(1, feedback.getTaskList().size());
        Task task = feedback.getTaskList().get(0);
        assertEquals(PriorityLevelEnum.RED, task.getPriorityLevel());
    }

    @Test
    public void testDelete() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        Hashtable<ParamEnum, ArrayList<String>> params = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("test test");
        params.put(ParamEnum.NAME, nameList);
        ArrayList<String> noteList = new ArrayList<String>();
        noteList.add("Test is good");
        params.put(ParamEnum.NOTE, noteList);
        add.invoke(logicObject, params);

        Hashtable<ParamEnum, ArrayList<String>> deleteParams = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> numList = new ArrayList<String>();
        numList.add("1");
        deleteParams.put(ParamEnum.KEYWORD, numList);
        Feedback deleteFeedback = (Feedback) delete.invoke(logicObject,
                deleteParams);
        assertEquals(0, deleteFeedback.getTaskList().size());
    }

    @Test
    public void testConfirm() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        Hashtable<ParamEnum, ArrayList<String>> params = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("test test");
        params.put(ParamEnum.NAME, nameList);
        ArrayList<String> noteList = new ArrayList<String>();
        noteList.add("Test is good");
        params.put(ParamEnum.NOTE, noteList);
        add.invoke(logicObject, params);

        Hashtable<ParamEnum, ArrayList<String>> completeParams = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> numList = new ArrayList<String>();
        numList.add("1");
        completeParams.put(ParamEnum.KEYWORD, numList);
        complete.invoke(logicObject,
                completeParams);

        Hashtable<ParamEnum, ArrayList<String>> displayParams = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> displayList = new ArrayList<String>();
        displayList.add("completed");
        displayParams.put(ParamEnum.KEYWORD, displayList);
        Feedback displayFeedback = (Feedback) display.invoke(logicObject,
                displayParams);
        assertEquals(1, displayFeedback.getTaskList().size());

        Task task = displayFeedback.getTaskList().get(0);
        assertTrue(task.isCompleted());
    }

    @Test
    public void testUpdate() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        Hashtable<ParamEnum, ArrayList<String>> params = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("test test");
        params.put(ParamEnum.NAME, nameList);
        Feedback feedback = (Feedback) add.invoke(logicObject, params);

        Task task = feedback.getTaskList().get(0);
        assertTrue(task.getNote().isEmpty());

        Hashtable<ParamEnum, ArrayList<String>> updateParams = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> noteList = new ArrayList<String>();
        noteList.add("Test is good");
        updateParams.put(ParamEnum.NOTE, noteList);
        ArrayList<String> dueDateList = new ArrayList<String>();
        dueDateList.add("20 Oct 2015 9am");
        updateParams.put(ParamEnum.DUE_DATE, dueDateList);
        ArrayList<String> numList = new ArrayList<String>();
        numList.add("1");
        updateParams.put(ParamEnum.KEYWORD, numList);
        Feedback updateFeedback = (Feedback) update.invoke(logicObject,
                updateParams);
        assertEquals(1, updateFeedback.getTaskList().size());
        task = updateFeedback.getTaskList().get(0);

        assertEquals("Test is good", task.getNote());
        assertTrue(task.isDeadlineTask());
        if (thisYear == 2015) {
            assertEquals("20 Oct 09:00",
                DateParser.parseCalendar(task.getDateDue()));
        } else {
            assertEquals("20 Oct 2015 09:00",
                DateParser.parseCalendar(task.getDateDue()));
        }

    }

    @Test
    public void testUpdateDeadlineToTimed() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        Hashtable<ParamEnum, ArrayList<String>> params = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("test test");
        params.put(ParamEnum.NAME, nameList);
        ArrayList<String> dueDateList = new ArrayList<String>();
        dueDateList.add("20 Oct 2015 9am");
        params.put(ParamEnum.DUE_DATE, dueDateList);
        Feedback feedback = (Feedback) add.invoke(logicObject, params);

        Task task = feedback.getTaskList().get(0);
        assertTrue(task.getNote().isEmpty());

        Hashtable<ParamEnum, ArrayList<String>> updateParams = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> startDateList = new ArrayList<String>();
        startDateList.add("23 Oct 2015 09:00");
        updateParams.put(ParamEnum.START_DATE, startDateList);
        ArrayList<String> endDateList = new ArrayList<String>();
        endDateList.add("23 Oct 2015 11:00");
        updateParams.put(ParamEnum.END_DATE, endDateList);
        ArrayList<String> numList = new ArrayList<String>();
        numList.add("1");
        updateParams.put(ParamEnum.KEYWORD, numList);
        Feedback updateFeedback = (Feedback) update.invoke(logicObject,
                updateParams);
        assertEquals(1, updateFeedback.getTaskList().size());
        task = updateFeedback.getTaskList().get(0);

        assertTrue(task.isTimedTask());

        if (thisYear == 2015) {
            assertEquals("23 Oct 09:00",
                DateParser.parseCalendar(task.getDateStart()));
            assertEquals("23 Oct 11:00",
                DateParser.parseCalendar(task.getDateEnd()));
        } else {
            assertEquals("23 Oct 2015 09:00",
                DateParser.parseCalendar(task.getDateStart()));
            assertEquals("23 Oct 2015 11:00",
                DateParser.parseCalendar(task.getDateEnd()));
        }
    }
}
