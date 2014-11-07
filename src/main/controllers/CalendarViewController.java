package main.controllers;

import javafx.fxml.FXML;
import jfxtras.scene.control.agenda.Agenda;

import java.util.*;

import common.Feedback;
import common.PriorityLevelEnum;
import common.StartDueDatePair;
import common.Task;

/**
 * @author szhlibrary
 */
public class CalendarViewController {
    @FXML
    private Agenda calendarView;
    private Map<String, Agenda.AppointmentGroup> appointmentGroupMap;

    public void initialize(Feedback initialTasks) {
        initAppointmentGroups();

        ArrayList<Task> taskList = initialTasks.getTaskList();
        addTasksToCalendarView(taskList);
    }

    private void initAppointmentGroups() {
        appointmentGroupMap = new HashMap<String, Agenda.AppointmentGroup>();
        appointmentGroupMap.put("group2",
                new Agenda.AppointmentGroupImpl().withStyleClass("group2"));
        appointmentGroupMap.put("group5",
                new Agenda.AppointmentGroupImpl().withStyleClass("group5"));
        appointmentGroupMap.put("group7",
                new Agenda.AppointmentGroupImpl().withStyleClass("group7"));
        appointmentGroupMap.put("group12",
                new Agenda.AppointmentGroupImpl().withStyleClass("group12"));
    }

    void updateCalendarView(ArrayList<Task> taskList) {
        calendarView.appointments().clear();
        addTasksToCalendarView(taskList);
    }

    private void addTasksToCalendarView(ArrayList<Task> taskList) {
        for (Task task : taskList) {
            if (!task.isDeleted()) {
                if (task.isDeadlineTask()) {
                    addDeadlineTaskToCalendarView(task);
                } else if (task.isConditionalTask()) {
                    addConditionalTaskToCalendarView(task);
                } else if (task.isTimedTask()) {
                    addTimedTaskToCalendarView(task);
                }
            }
        }
    }

    private void addDeadlineTaskToCalendarView(Task task) {
        assert (appointmentGroupMap != null) : "appointmentGroupMap was not initialized!";
        String appointmentGroup = determineAppointmentGroup(task);
        calendarView.appointments().add(
                new Agenda.AppointmentImpl()
                .withStartTime(task.getDateDue())
                .withSummary("ID: " + task.getId())
                .withDescription(task.getName())
                .withAppointmentGroup(
                        appointmentGroupMap.get(appointmentGroup))
                        .withWholeDay(true));
    }

    private void addTimedTaskToCalendarView(Task task) {
        assert (appointmentGroupMap != null) : "appointmentGroupMap was not initialized!";
        String appointmentGroup = determineAppointmentGroup(task);
        calendarView.appointments().add(
                new Agenda.AppointmentImpl()
                .withStartTime(task.getDateStart())
                .withEndTime(task.getDateEnd())
                .withSummary("ID: " + task.getId())
                .withDescription(task.getName())
                .withAppointmentGroup(
                        appointmentGroupMap.get(appointmentGroup)));
    }

    private void addConditionalTaskToCalendarView(Task task) {
        assert (appointmentGroupMap != null) : "appointmentGroupMap was not initialized!";
        assert !task.getConditionalDates().isEmpty() : "Conditional task has no conditional dates!";
        String appointmentGroup = determineAppointmentGroup(task);
        for (StartDueDatePair datePair : task.getConditionalDates()) {
            calendarView.appointments().add(
                    new Agenda.AppointmentImpl()
                    .withStartTime(datePair.getStartDate())
                    .withEndTime(datePair.getDueDate())
                    .withSummary("ID: " + task.getId())
                    .withDescription(task.getName())
                    .withAppointmentGroup(
                            appointmentGroupMap.get(appointmentGroup)));
        }
    }

    private String determineAppointmentGroup(Task task) {
        if (task.getPriorityLevel().getName()
                .equals(PriorityLevelEnum.GREEN.getName())) {
            return "group7";
        } else if (task.getPriorityLevel().getName()
                .equals(PriorityLevelEnum.ORANGE.getName())) {
            return "group5";
        } else if (task.getPriorityLevel().getName()
                .equals(PriorityLevelEnum.RED.getName())) {
            return "group2";
        } else {
            return "group12";
        }
    }
}
