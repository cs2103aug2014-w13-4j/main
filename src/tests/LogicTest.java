package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;

import logic.Logic;
import logic.LogicApi;
import logic.TaskModifier;
import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;

import org.junit.Before;
import org.junit.Test;

import command.ParamEnum;

public class LogicTest {
    Class<LogicApi> logicApiClass = LogicApi.class;
    Class<Logic> logicClass = Logic.class;
    Class<TaskModifier> taskModifierClass = TaskModifier.class;
    Field logic = logicApiClass.getDeclaredField("logic");
    Method update = logicClass.getDeclaredMethod("update", Hashtable.class);
    Method add = logicClass.getDeclaredMethod("add", Hashtable.class);
    Method complete = logicClass.getDeclaredMethod("complete", Hashtable.class);
    Method delete = logicClass.getDeclaredMethod("delete", Hashtable.class);
    LogicApi logicApiObject;
    Logic logicObject;

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
            IllegalAccessException {
        logicApiObject = new LogicApi();
        logicApiObject.initialize();
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
    public void testDelete() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Hashtable<ParamEnum, ArrayList<String>> params = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("test test");
        params.put(ParamEnum.NAME, nameList);
        ArrayList<String> noteList = new ArrayList<String>();
        noteList.add("Test is good");
        params.put(ParamEnum.NOTE, noteList);
        Feedback feedback = (Feedback) add.invoke(logicObject, params);

        Hashtable<ParamEnum, ArrayList<String>> deleteParams = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> numList = new ArrayList<String>();
        numList.add("0");
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
        Feedback feedback = (Feedback) add.invoke(logicObject, params);

        Hashtable<ParamEnum, ArrayList<String>> completeParams = new Hashtable<ParamEnum, ArrayList<String>>();
        ArrayList<String> numList = new ArrayList<String>();
        numList.add("0");
        completeParams.put(ParamEnum.KEYWORD, numList);
        Feedback completeFeedback = (Feedback) complete.invoke(logicObject,
                completeParams);
        assertEquals(1, completeFeedback.getTaskList().size());

        Task task = completeFeedback.getTaskList().get(0);
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
        ArrayList<String> numList = new ArrayList<String>();
        numList.add("0");
        updateParams.put(ParamEnum.KEYWORD, numList);
        Feedback updateFeedback = (Feedback) update.invoke(logicObject,
                updateParams);
        assertEquals(1, updateFeedback.getTaskList().size());

        assertEquals("Test is good", task.getNote());
    }
}
