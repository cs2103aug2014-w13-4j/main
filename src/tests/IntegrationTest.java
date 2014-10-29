package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import logic.LogicApi;
import models.Command;
import models.DateParser;
import models.Feedback;
import models.PriorityLevelEnum;
import models.Task;

import org.junit.Before;
import org.junit.Test;

import storage.Storage;
import command.CommandParser;
import exceptions.FileFormatNotSupportedException;
import exceptions.HistoryNotFoundException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;

public class IntegrationTest {

    CommandParser parser;
    LogicApi logicApiObject;

    @Before
    public void clearData() throws IOException, FileFormatNotSupportedException {
        PrintWriter writer = new PrintWriter("taskStorage.data");
        writer.print("");
        writer.close();
        parser = new CommandParser();
        logicApiObject = new LogicApi();
        logicApiObject.initialize();
    }

    @Test
    /**
     * Tests that task can be added
     * @throws Exception
     */
    public final void testAddTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20 February 1999 to 21 February 1999 note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task newTask = feedback.getTaskList().get(0);
        assertEquals("eat my pet dog", newTask.getName());
        assertEquals("I don't know why I want that?", newTask.getNote());
        assertEquals(PriorityLevelEnum.RED, newTask.getPriorityLevel());
        assertEquals(20, newTask.getDateStart().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateStart().get(Calendar.MONTH) + 1);
        assertEquals(1999, newTask.getDateStart().get(Calendar.YEAR));
        assertEquals(21, newTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateEnd().get(Calendar.MONTH) + 1);
        assertEquals(1999, newTask.getDateEnd().get(Calendar.YEAR));
    }

    public final void testAddMultipleTasks() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20 February 1999 to 21 February 1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
        addCommand = parser
                .parseCommand("add second from 22 February 1999 to 23 February 1999 note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        assertEquals(2, feedback.getTaskList().size());
        Task firstTask = feedback.getTaskList().get(0);
        Task secondTask = feedback.getTaskList().get(0);
        assertEquals("eat my pet dog", firstTask.getName());
        assertEquals("second", secondTask.getName());
    }

    /**
     * Tests that a task must have a name before it is added
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotAddTaskWithoutName() throws Exception {
        Command addCommand = parser
                .parseCommand("add from 20-02-1999 due 21-02-1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
    }

    /**
     * Tests that a timed task cannot be added with conflicting timeslots.
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotAddTaskWithConflictingTimeslot()
            throws Exception {
        Command addCommand = parser
                .parseCommand("add from 20-02-1999 to 25-02-1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
        addCommand = parser
                .parseCommand("add from 22-02-1999 to 26-02-1999 note I don't know why I want that? level 2");
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
        Command displayCommand = parser.parseCommand("display -1");
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
        assertTrue(task.isDeleted() == false);
        Command deleteCommand = parser.parseCommand("delete 0");
        Feedback deletedFeedback = logicApiObject.executeCommand(deleteCommand);
        assertEquals(new ArrayList<Task>(), deletedFeedback.getTaskList());
        Command updateCommand = parser.parseCommand("update 0 name blah");
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
                .parseCommand("update from 20-02-1999 due 21-02-1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(updateCommand);
    }

    /**
     * Tests that the date range of a task cannot be updated such that it
     * conflicts with that of another task
     *
     * @throws Exception
     */
    @Test(expected = InvalidInputException.class)
    public final void testCannotUpdateTaskWithConflictingDates()
            throws Exception {
        Command addCommand = parser
                .parseCommand("add first from 20 Feb 1999 to 24 Feb 1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
        addCommand = parser
                .parseCommand("add second from 25 Feb 1999 to 27 Feb 1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser.parseCommand("update 1 from 23 Feb 1999");
        logicApiObject.executeCommand(updateCommand);
    }

    @Test
    /**
     * Tests that the task can be marked as completed
     * @throws Exception
     */
    public final void testCompleteTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog due 20-02-1999 note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task uncompletedTask = feedback.getTaskList().get(0);
        assertTrue(uncompletedTask.getDateEnd() == null);
        Command completeCommand = parser.parseCommand("done 0");
        Feedback completedFeedback = logicApiObject
                .executeCommand(completeCommand);
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
                .parseCommand("done 0 date 30 January 1992");
        Feedback completedFeedback = logicApiObject
                .executeCommand(completeCommand);
        Task completedTask = completedFeedback.getTaskList().get(0);
        assertTrue(completedFeedback.getTaskList().get(0).isCompleted());
        assertEquals(30, completedTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
        assertEquals(1, completedTask.getDateEnd().get(Calendar.MONTH) + 1);
        assertEquals(1992, completedTask.getDateEnd().get(Calendar.YEAR));
    }

    /**
     * Tests that conditional dates are added correctly
     *
     * @throws Exception
     */
    @Test
    public final void testConditionalTasks() throws Exception {
        Command addCommand = parser
                .parseCommand("Add CS2103T from 23 Dec 1992 00:00 to 23 Dec 2002 00:00 or from 7 Oct 2014 00:00 to 8 Oct 2014 00:00");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task task = feedback.getTaskList().get(0);
        assertEquals("Task name is correct", "CS2103T", task.getName());
        assertTrue("Conditional dates are present", task.getConditionalDates()
                .size() == 2);
        assertEquals(
                "First start date is correct",
                "23-12-1992 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(0)
                        .getStartDate()));
        assertEquals(
                "Second start date is correct",
                "7-10-2014 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(1)
                        .getStartDate()));
        assertEquals(
                "First due date is correct",
                "23-12-2002 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(0)
                        .getDueDate()));
        assertEquals(
                "Second due date is correct",
                "8-10-2014 00:00",
                DateParser.parseCalendar(task.getConditionalDates().get(1)
                        .getDueDate()));
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
                .parseCommand("Add CS2103T from 23.12.1992 to 23.12.2002 or from 7.10.2014 to 8.10.2014");
        logicApiObject.executeCommand(addCommand);
        Command confirmCommand = parser.parseCommand("confirm 0 id 1");
        Feedback feedback = logicApiObject.executeCommand(confirmCommand);
        Task task = feedback.getTaskList().get(0);
        assertEquals("Task name is correct", "CS2103T", task.getName());
        assertTrue("Task is confirmed", task.isConfirmed());
        assertEquals("Confirmed start date is correct", task
                .getConditionalDates().get(1).getStartDate(),
                task.getDateStart());
        assertEquals("Confirmed due date is correct", task
                .getConditionalDates().get(1).getDueDate(), task.getDateEnd());
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
                .parseCommand("Add CS2103T from 23.12.1992 due 23.12.2002 or due 8.10.2014");
        logicApiObject.executeCommand(addCommand);
        Command confirmCommand = parser.parseCommand("confirm 0 id 2");
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
                .parseCommand("Add CS2103T from 23.12.1992 due 23.12.2002 or due 8.10.2014");
        logicApiObject.executeCommand(addCommand);
        Command confirmCommand = parser.parseCommand("confirm 0 id -1");
        logicApiObject.executeCommand(confirmCommand);
    }

    @Test
    /**
     * Tests that a task can be deleted
     * @throws Exception
     */
    public final void testDeleteTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog due 20-02-1999 note I don't know why I want that? level 2");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Task task = feedback.getTaskList().get(0);
        assertTrue(task.isDeleted() == false);
        Command deleteCommand = parser.parseCommand("delete 0");
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
        Command displayCommand = parser.parseCommand("display 0");
        Feedback feedback = logicApiObject.executeCommand(displayCommand);
        assertEquals("ID is the same", 0, feedback.getTaskDisplay().getId());
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
                .parseCommand("Add completed task due 10/10/2013");
        logicApiObject.executeCommand(addCommand);
        Command completeCommand = parser.parseCommand("done 0");
        logicApiObject.executeCommand(completeCommand);
        addCommand = parser.parseCommand("Add nocompleted task");
        logicApiObject.executeCommand(addCommand);
        Command filterCommand = parser.parseCommand("search status active");
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
                .parseCommand("Add completed task due 23.12.2002");
        logicApiObject.executeCommand(addCommand);
        Command completeCommand = parser.parseCommand("done 0");
        logicApiObject.executeCommand(completeCommand);
        addCommand = parser.parseCommand("Add nocompleted task");
        logicApiObject.executeCommand(addCommand);
        Command filterCommand = parser.parseCommand("search status completed");
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
                .parseCommand("Add blah from 23.12.1992 to 23.12.2002");
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
                .parseCommand("Add blah from 23.12.1992 to 23.12.2002");
        logicApiObject.executeCommand(addCommand);
        Command deleteCommand = parser.parseCommand("Delete 0");
        logicApiObject.executeCommand(deleteCommand);
        Command undoCommand = parser.parseCommand("undo");
        Feedback feedback = logicApiObject.executeCommand(undoCommand);
    }

    /**
     * Tests that update task can be undone
     *
     * @throws Exception
     */
    @Test
    public final void testUndoUpdateTask() throws Exception {
        CommandParser parser = new CommandParser();
        Command addCommand = parser.parseCommand("Add blah due 23.12.2002");
        Feedback feedback = logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser.parseCommand("Update 0 name changed");
        logicApiObject.executeCommand(updateCommand);
        Command undoCommand = parser.parseCommand("undo");
        logicApiObject.executeCommand(undoCommand);
    }

    @Test
    /**
     * Tests that task can be updated
     * @throws Exception
     */
    public final void testUpdateTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20 Feb 1999 to 21 Feb 1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser
                .parseCommand("update 0 name changed from 01 Jan 1999 note changed description level 1");
        Feedback feedback = logicApiObject.executeCommand(updateCommand);
        Task newTask = feedback.getTaskList().get(0);
        assertEquals("changed", newTask.getName());
        assertEquals("changed description", newTask.getNote());
        assertEquals(PriorityLevelEnum.ORANGE, newTask.getPriorityLevel());
        assertEquals(1, newTask.getDateStart().get(Calendar.DAY_OF_MONTH));
        assertEquals(1, newTask.getDateStart().get(Calendar.MONTH) + 1);
        assertEquals(1999, newTask.getDateStart().get(Calendar.YEAR));
        assertEquals(21, newTask.getDateEnd().get(Calendar.DAY_OF_MONTH));
        assertEquals(2, newTask.getDateEnd().get(Calendar.MONTH) + 1);
        assertEquals(1999, newTask.getDateEnd().get(Calendar.YEAR));
    }

    @Test(expected = InvalidInputException.class)
    /**
     * Tests that task cannot be changed from a timed task to a deadline task
     * Some code is commented out due to bug in command parser
     * @throws Exception
     */
    public final void testErrorUpdateTask() throws Exception {
        Command addCommand = parser
                .parseCommand("add eat my pet dog from 20-02-1999 to 21-02-1999 note I don't know why I want that? level 2");
        logicApiObject.executeCommand(addCommand);
        Command updateCommand = parser.parseCommand("update 0 due 10.10.2013");
        logicApiObject.executeCommand(updateCommand);
    }

}