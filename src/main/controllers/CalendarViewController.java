package main.controllers;

import javafx.fxml.FXML;
import jfxtras.scene.control.agenda.Agenda;
import models.Feedback;
import models.Task;

import java.util.*;

/**
 * @author szhlibrary
 */
public class CalendarViewController {
    @FXML
    private Agenda calendarView;
    private Map<String, Agenda.AppointmentGroup> appointmentGroupMap;

    public void initialize(Feedback initialTasks) {
        appointmentGroupMap = new HashMap<String, Agenda.AppointmentGroup>();
        appointmentGroupMap.put("group0", new Agenda.AppointmentGroupImpl().withStyleClass("group0"));

        ArrayList<Task> taskList = initialTasks.getTaskList();
        addTasksToCalendarView(taskList);
    }

    protected void updateCalendarView(ArrayList<Task> taskList) {
        calendarView.appointments().clear();
        addTasksToCalendarView(taskList);
    }

    private void addTasksToCalendarView(ArrayList<Task> taskList) {
        for (Task task : taskList){
            if (!task.isDeleted()){
                if (task.getDateStart() == null){
                    addDeadlineTaskToCalendarView(task);
                } else if (task.getDateEnd() != null){
                    addEventTaskToCalendarView(task);
                }
            }
        }
    }

    private void addDeadlineTaskToCalendarView(Task task) {
        assert (appointmentGroupMap != null) : "appointmentGroupMap was not initialized!";
        calendarView.appointments().add(
            new Agenda.AppointmentImpl()
                .withStartTime(task.getDateDue())
                .withSummary("ID: " + task.getId())
                .withDescription(task.getName())
                .withAppointmentGroup(appointmentGroupMap.get("group0"))
                .withWholeDay(true)
        );
    }

    private void addEventTaskToCalendarView(Task task) {
        assert (appointmentGroupMap != null) : "appointmentGroupMap was not initialized!";
        calendarView.appointments().add(
            new Agenda.AppointmentImpl()
                .withStartTime(task.getDateStart())
                .withEndTime(task.getDateEnd())
                .withSummary("ID: " + task.getId())
                .withDescription(task.getName())
                .withAppointmentGroup(appointmentGroupMap.get("group0"))
        );
    }
}
