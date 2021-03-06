package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import command.ParamEnum;
import common.*;
import common.StartEndDatePair;
import common.exceptions.InvalidDateFormatException;
import common.exceptions.InvalidInputException;
import common.exceptions.InvalidPriorityLevelException;

//@author A0114368E
/**
 * This class provides the various methods to modify a task, given the command
 * and the task to be modified.
 *
 *
 */
public class TaskModifier {

    private static final int OFFSET_FOR_ARRAY = 1;
    private static final String INVALID_PRIORITY_LEVEL_MESSAGE = "%1$s is not a valid priority level.";
    private static final String INVALID_CONDITIONAL_DATE_ID_MESSAGE = "The conditional date id is invalid.";
    private static final int MIN_ID = 0;
    private static final String INVALID_CONFIRMED_TASK_MESSAGE = "The task is already confirmed.";
    private static final String INVALID_START_END_DATE_MESSAGE = "The start date should occur before the end date";

    /**
     * Marks a task as completed, and updates the end date if it is a deadline
     * task
     *
     * @param param
     *            : the input from the user
     * @param task
     *            : the task to be marked as completed
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    static void completeTask(Hashtable<ParamEnum, ArrayList<String>> param,
            Task task) throws InvalidDateFormatException, InvalidInputException {
        if (param.containsKey(ParamEnum.DATE)) {
            Calendar completedDate = DateParser.parseString(param.get(
                    ParamEnum.DATE).get(0));
            task.setDateEnd(completedDate);
        } else {
            task.setDateEnd(Calendar.getInstance());
        }

    }

    /**
     * Sets the start and end date of the task from its conditional dates
     *
     * @param dateId
     *            : the id of the conditional date
     * @param event
     *            : the conditional task to be updated
     * @throws InvalidInputException
     */

    static void confirmTask(int dateId, Task event)
            throws InvalidInputException {
        int dateIndex = dateId - OFFSET_FOR_ARRAY;
        if (isLessThanMinId(dateIndex) || hasNullConditionalDates(event)
                || isIdOutsideConditionalDatesRange(dateIndex, event)) {
            throw new InvalidInputException(INVALID_CONDITIONAL_DATE_ID_MESSAGE);
        } else if (event.isConfirmed()) {
            throw new InvalidInputException(INVALID_CONFIRMED_TASK_MESSAGE);
        } else {
            StartEndDatePair conditionalDatesToConfirm = event
                    .getConditionalDates().get(dateIndex);
            Calendar startDate = conditionalDatesToConfirm.getStartDate();
            event.setDateStart(startDate);
            Calendar endDate = conditionalDatesToConfirm.getEndDate();
            event.setDateEnd(endDate);
        }
    }

    static void resetId(Task task) {
        task.setId(Task.ID_FOR_NEW_TASK);
    }

    static void deleteTask(Task task) {
        task.setDeleted(true);
    }

    /**
     * Updates the attributes of the conditional task according to the input
     * given by the user
     *
     * @param param
     *            : the input from the user
     * @param task
     *            : the conditional task to be updated
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    static void modifyConditionalTask(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException, InvalidInputException {
        setNameFromCommand(param, task);
        setTagsFromCommand(param, task);
        setLevelFromCommand(param, task);
        setNoteFromCommand(param, task);
        setConditionalDatesFromCommand(param, task);
    }

    /**
     * Updates the attributes of the deadline task according to the input given
     * by the user
     *
     * @param param
     *            : the input from the user
     * @param task
     *            : the deadline task to be updated
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    static void modifyDeadlineTask(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException, InvalidInputException {
        setNameFromCommand(param, task);
        setTagsFromCommand(param, task);
        setLevelFromCommand(param, task);
        setNoteFromCommand(param, task);
        setDueDateFromCommand(param, task);
        setStartAndEndDateToNull(task);
    }

    /**
     * Updates the attributes of the floating task according to the input given
     * by the user
     *
     * @param param
     *            : the input from the user
     * @param task
     *            : the deadline task to be updated
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    static void modifyFloatingTask(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException, InvalidInputException {
        setNameFromCommand(param, task);
        setTagsFromCommand(param, task);
        setLevelFromCommand(param, task);
        setNoteFromCommand(param, task);
    }

    /**
     *
     * Updates the attributes of the timed task according to the input given by
     * the user
     *
     * @param param
     *            : the input from the user
     * @param task
     *            : the floating task to be updated
     * @throws InvalidDateFormatException
     * @throws InvalidInputException
     */
    static void modifyTimedTask(Hashtable<ParamEnum, ArrayList<String>> param,
            Task task) throws InvalidDateFormatException, InvalidInputException {
        setNameFromCommand(param, task);
        setTagsFromCommand(param, task);
        setLevelFromCommand(param, task);
        setNoteFromCommand(param, task);
        setStartDateFromCommand(param, task);
        setEndDateFromCommand(param, task);
        checkStartDateIsBeforeEndDate(task.getDateStart(), task.getDateEnd());
        setDueDateToNull(task);
    }

    static void uncompleteTask(Task task) {
        assert (task.getDateEnd() != null);
        task.setDateEnd(null);
    }

    static void unconfirmTask(Task task) {
        assert (task.isTimedTask());
        task.setDateStart(null);
        task.setDateEnd(null);
    }

    static void undeleteTask(Task task) {
        assert (task.isDeleted());
        task.setDeleted(false);
    }

    private static void checkStartDateIsBeforeEndDate(Calendar dateStart,
            Calendar dateEnd) throws InvalidInputException {
        if (!isStartDateBeforeEndDate(dateStart, dateEnd)) {
            throw new InvalidInputException(INVALID_START_END_DATE_MESSAGE);
        }
    }

    private static PriorityLevelEnum getPriorityEnumAsInteger(String levelString)
            throws InvalidInputException {
        try {
            return parsePriorityAsInteger(levelString);
        } catch (InvalidPriorityLevelException | NumberFormatException e1) {
            throw new InvalidInputException(MessageCreator.createMessage(
                    INVALID_PRIORITY_LEVEL_MESSAGE, levelString, null));
        }
    }

    private static PriorityLevelEnum getPriorityEnumAsString(String levelString)
            throws InvalidPriorityLevelException {
        return PriorityLevelEnum.fromString(levelString);
    }

    private static PriorityLevelEnum getPriorityLevel(String levelString)
            throws InvalidInputException {
        PriorityLevelEnum priorityEnum;
        try {
            priorityEnum = getPriorityEnumAsString(levelString);
        } catch (InvalidPriorityLevelException e) {
            priorityEnum = getPriorityEnumAsInteger(levelString);
        }
        return priorityEnum;
    }

    private static boolean hasNullConditionalDates(Task task) {
        return task.getConditionalDates() == null;
    }

    private static boolean isIdOutsideConditionalDatesRange(int dateId,
            Task task) {
        return task.getConditionalDates().size() <= dateId;
    }

    private static boolean isLessThanMinId(int dateId) {
        return dateId < MIN_ID;
    }

    private static boolean isStartDateBeforeEndDate(Calendar dateStart,
            Calendar dateEnd) {
        return dateStart.getTimeInMillis() < dateEnd.getTimeInMillis();
    }

    private static PriorityLevelEnum parsePriorityAsInteger(String levelString)
            throws InvalidInputException, InvalidPriorityLevelException {
        int level = Integer.parseInt(levelString);
        return PriorityLevelEnum.fromInteger(level);
    }

    private static void setConditionalDatesFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException, InvalidInputException {
        if (param.containsKey(ParamEnum.START_DATE)
                && param.containsKey(ParamEnum.END_DATE)) {
            ArrayList<String> startDates = param.get(ParamEnum.START_DATE);
            ArrayList<String> endDates = param.get(ParamEnum.END_DATE);
            ArrayList<StartEndDatePair> conditionalDates = new ArrayList<StartEndDatePair>();
            for (int i = 0; i < startDates.size(); i++) {
                String startDateString = startDates.get(i);
                String endDateString = endDates.get(i);
                Calendar startDate = DateParser.parseString(startDateString);
                Calendar endDate = DateParser.parseString(endDateString);
                checkStartDateIsBeforeEndDate(startDate, endDate);
                StartEndDatePair datePair = new StartEndDatePair(startDate,
                        endDate);
                conditionalDates.add(datePair);
            }
            task.setConditionalDates(conditionalDates);
        }
    }

    private static void setDueDateFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException {
        if (param.containsKey(ParamEnum.DUE_DATE)
                && !param.get(ParamEnum.DUE_DATE).get(0).isEmpty()) {
            Calendar dueDate = DateParser.parseString(param.get(
                    ParamEnum.DUE_DATE).get(0));
            task.setDateDue(dueDate);
        }
    }

    private static void setDueDateToNull(Task task) {
        task.setDateDue(null);
    }

    private static void setEndDateFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException {
        if (param.containsKey(ParamEnum.END_DATE)
                && !param.get(ParamEnum.END_DATE).get(0).isEmpty()) {
            Calendar endDate = DateParser.parseString(param.get(
                    ParamEnum.END_DATE).get(0));
            task.setDateEnd(endDate);
        }
    }

    private static void setLevelFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidInputException {
        if (param.containsKey(ParamEnum.LEVEL)) {
            assert param.get(ParamEnum.LEVEL).size() == 1;
            String levelString = param.get(ParamEnum.LEVEL).get(0);
            PriorityLevelEnum priorityEnum = getPriorityLevel(levelString);
            task.setPriorityLevel(priorityEnum);
        }
    }

    private static void setNameFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task) {
        if (param.containsKey(ParamEnum.NAME)) {
            String taskName = param.get(ParamEnum.NAME).get(0);
            task.setName(taskName);
        }
    }

    private static void setNoteFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task) {
        if (param.containsKey(ParamEnum.NOTE)) {
            String note = param.get(ParamEnum.NOTE).get(0);
            task.setNote(note);
        }
    }

    private static void setStartAndEndDateToNull(Task task) {
        task.setDateStart(null);
        task.setDateEnd(null);
    }

    private static void setStartDateFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task)
            throws InvalidDateFormatException {
        if (param.containsKey(ParamEnum.START_DATE)
                && !param.get(ParamEnum.START_DATE).get(0).isEmpty()) {
            Calendar startDate = DateParser.parseString(param.get(
                    ParamEnum.START_DATE).get(0));
            task.setDateStart(startDate);
        }
    }

    private static void setTagsFromCommand(
            Hashtable<ParamEnum, ArrayList<String>> param, Task task) {
        if (param.containsKey(ParamEnum.TAG)) {
            ArrayList<String> tags = param.get(ParamEnum.TAG);
            task.setTags(tags);
        }
    }
}
