package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import logic.LogicApi;

import org.junit.Before;
import org.junit.Test;

import command.CommandParser;
import common.Command;
import common.DateParser;
import common.Feedback;
import common.PriorityLevelEnum;
import common.Task;
import common.exceptions.FileFormatNotSupportedException;
import common.exceptions.HistoryNotFoundException;
import common.exceptions.InvalidInputException;
import common.exceptions.TaskNotFoundException;

//@author A0114368E
public class IntegrationTest {

    CommandParser parser;
    LogicApi logicApiObject;

    @Before
    public void clearData() throws IOException, FileFormatNotSupportedException {
        PrintWriter writer = new PrintWriter("taskStorage.data");
        writer.print("");
        writer.close();
        parser = new CommandParser();
        logicApiObject = LogicApi.getNewInstance();
    }

    @Test
    /**
     * Tests that task can be added
     * @throws Exception
     */
    public final void testAddTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20 February 2015 to 21 February 2015 note I don't know why I want that? level red");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task newTask = feedback.getTaskList().get(0);
        assertEquals("eat my pet dog", newTask.getName());
        assertEquals("I don't know why I want that?", newTask.getNote());
        assertEquals(PriorityLevelEnum.RED, newTask.getPriorityLevel());
        assertEquals(20, newTask.getDateStart().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateStart().get(Calendar.MONTH) + 1);
        assertEquals(2015, newTask.getDateStart().get(Calendar.YEAR));
        assertEquals(21, newTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateEnd().get(Calendar.MONTH) + 1);
        assertEquals(2015, newTask.getDateEnd().get(Calendar.YEAR));
    }

    public final void testAddMultipleTasks() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20 February 2015 to 21 February 2015 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
        addCommand = parser
                .parseCommand("add second from 22 February 2015 to 23 February 2015 note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        assertEquals(2, feedback.getTaskList().size());
        Task firstTask = feedback.getTaskList().get(0);
        Task secondTask = feedback.getTaskList().get(1);
        assertEquals("eat my pet dog", firstTask.getName());
        assertEquals("second", secondTask.getName());
    }

    @Test
    /**
     * Tests that active tasks are displayed by default
     * @throws Exception
     */
    public final void testDefaultDisplay() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20 February 2015 to 21 February 2015");
        logicApiObject.executeCommand(addCommand);
        Command nextAddCommand = parser
                .parseCommand("add another from 21 February 2014 to 21 February 2014");
        Feedback feedback = logicApiObject.executeCommand(nextAddCommand);
        Task newTask = feedback.getTaskList().get(0);
        assertEquals(1, feedback.getTaskList().size());
        assertEquals("eat my pet dog", newTask.getName());
        assertEquals(20, newTask.getDateStart().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateStart().get(Calendar.MONTH) + 1);
        assertEquals(2015, newTask.getDateStart().get(Calendar.YEAR));
        assertEquals(21, newTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateEnd().get(Calendar.MONTH) + 1);
        assertEquals(2015, newTask.getDateEnd().get(Calendar.YEAR));
    }

    /**
     * Tests that start date must be before end date when adding a task
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testStartDateCannotBeAfterEndDateForAdd()
            throws Exception {
        Command addCommand = parser
                .parseCommand("add test from 20 Feb 2015 to 19 Feb 2015");
        logicApiObject.executeCommand(addCommand);
    }

    /**
     * Tests that start date before end date when adding a conditional task
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testStartDateCannotBeAfterEndDateForConditionalTask()
            throws Exception {
        Command addCommand = parser
                .parseCommand("add test from 21 Feb 2015 to 20 Feb 2015 or from 20 Feb 2015 to 21 Feb 2015");
        logicApiObject.executeCommand(addCommand);
    }

    /**
     * Tests that start date must be before end date when updating a task
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testStartDateCannotBeAfterEndDateForUpdate()
            throws Exception {
        Command addCommand = parser
                .parseCommand("add test from 20 Jan 2015 to 20 Feb 2015");
        logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser
                .parseCommand("update 1 from 21 Feb 2015");
        logicApiObject.executeCommand(updateCommand);
    }

    /**
     * Tests that an invalid priority level cannot be added
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotAddTaskWithInvalidPriorityInt() throws Exception {
        Command addCommand = parser.parseCommand("add test level -1");
        logicApiObject.executeCommand(addCommand);
    }

    /**
     * Tests that an invalid priority level cannot be added
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotAddTaskWithInvalidPriorityInt2()
            throws Exception {
        Command addCommand = parser.parseCommand("add test level 4");
        logicApiObject.executeCommand(addCommand);
    }

    /**
     * Tests that an invalid priority level cannot be added
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotAddTaskWithInvalidPriorityString()
            throws Exception {
        Command addCommand = parser.parseCommand("add test level gren");
        logicApiObject.executeCommand(addCommand);
    }

    /**
     * Tests that a task must have a name before it is added
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotAddTaskWithoutName() throws Exception {
        Command addCommand = parser
                .parseCommand("add from 20 Feb 2015 due 21 Feb 2015 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
    }

    /**
     * Tests that tasks that are absent cannot be displayed
     *
     * @throws Exception
     */
    @Test(expected = TaskNotFoundException.class)
    public final void testCannotDisplayAbsentTask() throws Exception {
        Command addCommand = parser.parseCommand("add eat my pet dog");
        logicApiObject.executeCommand(addCommand);
        Command displayCommand = parser.parseCommand("display 2");
        logicApiObject.executeCommand(displayCommand);
    }

    /**
     * Tests that tasks with invalid id cannot be displayed
     *
     * @throws Exception
     */
    @Test(expected = TaskNotFoundException.class)
    public final void testCannotDisplayTaskWithNegativeId() throws Exception {
        Command addCommand = parser.parseCommand("add eat my pet dog");
        logicApiObject.executeCommand(addCommand);
        Command displayCommand = parser.parseCommand("display 0");
        logicApiObject.executeCommand(displayCommand);
    }

    @Test
    /**
     * Tests that a deleted task cannot be updated anymore
     *
     * @throws Exception
     */
    (expected = TaskNotFoundException.class)
    public final void testCannotModifyDeleteTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task task = feedback.getTaskList().get(0);
        assertTrue(!task.isDeleted());
        Command deleteCommand = parser.parseCommand("delete 1");
        Feedback deletedFeedback = logicApiObject.executeCommand(deleteCommand);
        assertEquals(new ArrayList<Task>(), deletedFeedback.getTaskList());
        Command updateCommand = parser.parseCommand("update 1 name blah");
        logicApiObject.executeCommand(updateCommand);
    }

    /**
     * Tests that display cannot be undone
     *
     * @throws Exception
     */
    @Test(expected = HistoryNotFoundException.class)
    public final void testCannotUndoDisplayTask() throws Exception {
        CommandParser parser = new CommandParser();
        Command displayCommand = parser.parseCommand("display");
        logicApiObject.executeCommand(displayCommand);
        Command undoCommand = parser.parseCommand("undo");
        logicApiObject.executeCommand(undoCommand);
    }

    /**
     * Tests that undo cannot be done if there are no prior actions taken
     *
     * @throws Exception
     */
    @Test(expected = HistoryNotFoundException.class)
    public final void testCannotUndoWithoutHistory() throws Exception {
        Command undoCommand = parser.parseCommand("undo");
        logicApiObject.executeCommand(undoCommand);
    }

    /**
     * Tests that a task must have a keyword before it is deleted
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotUpdateTaskWithoutId() throws Exception {
        Command addCommand = parser.parseCommand("add test");
        logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser
                .parseCommand("update from 20 Feb 2015 due 21 Feb 2015 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(updateCommand);
    }

    @Test
    /**
     * Tests that the task can be marked as completed
     * @throws Exception
     */
    public final void testCompleteTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog due 20 Feb 2015 note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task uncompletedTask = feedback.getTaskList().get(0);
        assertTrue(uncompletedTask.getDateEnd() == null);
        Command completeCommand = parser.parseCommand("done 1");
        logicApiObject.executeCommand(completeCommand);
        Command displayCommand = parser.parseCommand("display completed");
        Feedback completedFeedback = logicApiObject
                .executeCommand(displayCommand);
        assertEquals(1, completedFeedback.getTaskList().size());
        assertTrue(completedFeedback.getTaskList().get(0).isCompleted());
    }

    @Test
    /**
     * Tests that a task can be completed with a specified complete date
     * @throws Exception
     */
    public final void testCompleteTaskWithDate() throws Exception {
        Command addCommand = parser.parseCommand("add eat my pet dog");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task uncompletedTask = feedback.getTaskList().get(0);
        assertNull(uncompletedTask.getDateEnd());
        Command completeCommand = parser
                .parseCommand("done 1 date 30 January 2015");
        logicApiObject.executeCommand(completeCommand);
        Command displayCommand = parser.parseCommand("display completed");
        Feedback completedFeedback = logicApiObject
                .executeCommand(displayCommand);
        Task completedTask = completedFeedback.getTaskList().get(0);
        assertTrue(completedFeedback.getTaskList().get(0).isCompleted());
        assertEquals(30, completedTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
        assertEquals(1, completedTask.getDateEnd().get(Calendar.MONTH) + 1);
        assertEquals(2015, completedTask.getDateEnd().get(Calendar.YEAR));
    }

    /**
     * Tests that conditional dates are added correctly
     *
     * @throws Exception
     */
    @Test
    public final void testConditionalTasks() throws Exception {
        Command addCommand = parser
                .parseCommand("Add CS2103T from 23 Dec 1992 00:00 to 23 Dec 2002 00:00 or from 7 Oct 2013 00:00 to 8 Oct 2013 00:00");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task task = feedback.getTaskList().get(0);
        assertEquals("Task name is correct", "CS2103T", task.getName());
        assertTrue("Conditional dates are present", task.getConditionalDates()
                .size() == 2);
        assertEquals(
                "First start date is correct",
                "23 Dec 1992 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(0)
                        .getStartDate()));
        assertEquals(
                "Second start date is correct",
                "7 Oct 2013 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(1)
                        .getStartDate()));
        assertEquals(
                "First due date is correct",
                "23 Dec 2002 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(0)
                        .getEndDate()));
        assertEquals(
                "Second due date is correct",
                "8 Oct 2013 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(1)
                        .getEndDate()));
    }

    /**
     * Tests that conditional tasks can be confirmed with a valid id
     *
     * @throws Exception
     */
    @Test
    public final void testConfirmConditionalTasks() throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add CS2103T from 23 Dec 2015 to 24 Dec 2015 or from 7 Oct 2016 to 8 Oct 2016");
        logicApiObject.executeCommand(addCommand);
        Command confirmCommand = parser.parseCommand("confirm 1 id 2");
        Feedback feedback = logicApiObject.executeCommand(confirmCommand);
        Task task = feedback.getTaskList().get(0);
        assertEquals("Task name is correct", "CS2103T", task.getName());
        assertTrue("Task is confirmed", task.isConfirmed());
        assertEquals("Confirmed start date is correct", task
                .getConditionalDates().get(1).getStartDate(),
                task.getDateStart());
        assertEquals("Confirmed due date is correct", task
                .getConditionalDates().get(1).getEndDate(), task.getDateEnd());
    }

    /**
     * Tests that error will be thrown if invalid date id is given for
     * confirming conditional tasks Boundary testing
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testConfirmConditionalTasksWithInvalidId()
            throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add CS2103T from 23 Dec 1992 to 23 Dec 2002 or from 7 Oct 2014 to 8 Oct 2014");
        logicApiObject.executeCommand(addCommand);
        Command confirmCommand = parser.parseCommand("confirm 1 id 3");
        logicApiObject.executeCommand(confirmCommand);
    }

    /**
     * Tests that error will be thrown if invalid date id is given for
     * confirming conditional tasks Boundary testing
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testConfirmConditionalTasksWithNegativeId()
            throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add CS2103T from 23 Dec 1992 to 23 Dec 2002 or from 7 Oct 2014 to 8 Oct 2014");
        logicApiObject.executeCommand(addCommand);
        Command confirmCommand = parser.parseCommand("confirm 1 id -1");
        logicApiObject.executeCommand(confirmCommand);
    }

    @Test
    /**
     * Tests that a task can be deleted
     * @throws Exception
     */
    public final void testDeleteTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog due 20 Feb 2015 note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task task = feedback.getTaskList().get(0);
        assertTrue(!task.isDeleted());
        Command deleteCommand = parser.parseCommand("delete 1");
        Feedback completedFeedback = logicApiObject
                .executeCommand(deleteCommand);
        assertEquals(0, completedFeedback.getTaskList().size());
    }

    @Test
    /**
     * Tests that display all tasks display the correct tasks
     * @throws Exception
     */
    public final void testDisplayAll() throws Exception {
        Command addCommand = parser.parseCommand("add first");
        logicApiObject.executeCommand(addCommand);
        addCommand = parser.parseCommand("add second thing");
        logicApiObject.executeCommand(addCommand);
        Command displayCommand = parser.parseCommand("display");
        Feedback feedback = logicApiObject.executeCommand(displayCommand);
        assertEquals("Task length is correct", 2, feedback.getTaskList().size());
        assertNull("Task Display is empty", feedback.getTaskDisplay());
    }

    @Test
    /**
     * Tests that an individual task can be displayed
     * @throws Exception
     */
    public final void testDisplayIndividualTask() throws Exception {
        Command addCommand = parser.parseCommand("add eat my pet dog");
        logicApiObject.executeCommand(addCommand);
        Command displayCommand = parser.parseCommand("display 1");
        Feedback feedback = logicApiObject.executeCommand(displayCommand);
        assertEquals("ID is the same", 1, feedback.getTaskDisplay().getId());
        assertEquals("Name is correct", "eat my pet dog", feedback
                .getTaskDisplay().getName());
        assertNull("Task list is empty", feedback.getTaskList());
    }

    /**
     * Tests that tasks can be filtered by active status
     *
     * @throws Exception
     */
    @Test
    public final void testFilterActiveTask() throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add completed task due 10 Oct 2013");
        logicApiObject.executeCommand(addCommand);
        Command completeCommand = parser.parseCommand("done 1");
        logicApiObject.executeCommand(completeCommand);
        addCommand = parser.parseCommand("Add uncompleted task");
        logicApiObject.executeCommand(addCommand);
        Command filterCommand = parser.parseCommand("display");
        Feedback feedback = logicApiObject.executeCommand(filterCommand);
        ArrayList<Task> taskList = feedback.getTaskList();
        assertEquals("Only 1 task is shown", 1, taskList.size());
    }

    /**
     * Tests that tasks can be filtered by completed status
     *
     * @throws Exception
     */
    @Test
    public final void testFilterCompletedTask() throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add completed task due 23 December 2002");
        logicApiObject.executeCommand(addCommand);
        Command completeCommand = parser.parseCommand("done 1");
        logicApiObject.executeCommand(completeCommand);
        addCommand = parser.parseCommand("Add uncompleted task");
        logicApiObject.executeCommand(addCommand);
        Command filterCommand = parser.parseCommand("display completed");
        Feedback feedback = logicApiObject.executeCommand(filterCommand);
        ArrayList<Task> taskList = feedback.getTaskList();
        assertEquals("Only 1 task is shown", 1, taskList.size());
    }

    /**
     * Tests that add task can be undone by deleting the task
     *
     * @throws Exception
     */
    @Test
    public final void testUndoAddTask() throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add blah from 23 December 1992 to 23 December 2002");
        logicApiObject.executeCommand(addCommand);
        Command undoCommand = parser.parseCommand("undo");
        Feedback feedback = logicApiObject.executeCommand(undoCommand);
        assertEquals("No task is displayed", 0, feedback.getTaskList().size());
    }

    /**
     * Tests that delete task can be undone
     *
     * @throws Exception
     */
    @Test
    public final void testUndoDeleteTask() throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add blah from 23 December 2015 to 23 December 2015");
        logicApiObject.executeCommand(addCommand);
        Command deleteCommand = parser.parseCommand("Delete 1");
        logicApiObject.executeCommand(deleteCommand);
        Command undoCommand = parser.parseCommand("undo");
        Feedback feedback = logicApiObject.executeCommand(undoCommand);
        assertEquals(1, feedback.getTaskList().size());
        assertEquals("blah", feedback.getTaskList().get(0).getName());

    }

    /**
     * Tests that update task can be undone
     *
     * @throws Exception
     */
    @Test
    public final void testUndoUpdateTask() throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser
                .parseCommand("Add blah due 23 December 1992");
        logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser.parseCommand("Update 1 name changed");
        Feedback oldFeedback = logicApiObject.executeCommand(updateCommand);
        assertEquals("changed", oldFeedback.getTaskList().get(0).getName());
        Command undoCommand = parser.parseCommand("undo");
        Feedback feedback = logicApiObject.executeCommand(undoCommand);
        assertEquals(1, feedback.getTaskList().size());
        assertEquals("blah", feedback.getTaskList().get(0).getName());
    }

    @Test
    /**
     * Tests that task can be updated
     * @throws Exception
     */
    public final void testUpdateTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20 Feb 2015 to 21 Feb 2015 note I don't know why I want that? level 3");
        logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser
                .parseCommand("update 1 name changed from 01 Jan 2015 note changed description level 2");
        Feedback feedback = logicApiObject.executeCommand(updateCommand);
        Task newTask = feedback.getTaskList().get(0);
        assertEquals("changed", newTask.getName());
        assertEquals("changed description", newTask.getNote());
        assertEquals(PriorityLevelEnum.ORANGE, newTask.getPriorityLevel());
        assertEquals(1, newTask.getDateStart().get(Calendar.DAY_OF_MONTH));
        assertEquals(1, newTask.getDateStart().get(Calendar.MONTH) + 1);
        assertEquals(2015, newTask.getDateStart().get(Calendar.YEAR));
        assertEquals(21, newTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateEnd().get(Calendar.MONTH) + 1);
        assertEquals(2015, newTask.getDateEnd().get(Calendar.YEAR));
    }

}