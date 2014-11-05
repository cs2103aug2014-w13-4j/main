package main.controllers;

import javafx.fxml.FXML;
import jfxtras.scene.control.agenda.Agenda;
import models.Feedback;
import models.PriorityLevelEnum;
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
        initAppointmentGroups();

        ArrayList<Task> taskList = initialTasks.getTaskList();
        addTasksToCalendarView(taskList);
    }

    private void initAppointmentGroups() {
        appointmentGroupMap = new HashMap<String, Agenda.AppointmentGroup>();
        appointmentGroupMap.put("group2", new Agenda.AppointmentGroupImpl().withStyleClass("group2"));
        appointmentGroupMap.put("group5", new Agenda.AppointmentGroupImpl().withStyleClass("group5"));
        appointmentGroupMap.put("group7", new Agenda.AppointmentGroupImpl().withStyleClass("group7"));
        appointmentGroupMap.put("group12", new Agenda.AppointmentGroupImpl().withStyleClass("group12"));
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
        String appointmentGroup = determineAppointmentGroup(task);
        calendarView.appointments().add(
            new Agenda.AppointmentImpl()
                .withStartTime(task.getDateDue())
                .withSummary("ID: " + task.getId())
                .withDescription(task.getName())
                .withAppointmentGroup(appointmentGroupMap.get(appointmentGroup))
                .withWholeDay(true)
        );
    }

    private void addEventTaskToCalendarView(Task task) {
        assert (appointmentGroupMap != null) : "appointmentGroupMap was not initialized!";
        String appointmentGroup = determineAppointmentGroup(task);
        calendarView.appointments().add(
            new Agenda.AppointmentImpl()
                .withStartTime(task.getDateStart())
                .withEndTime(task.getDateEnd())
                .withSummary("ID: " + task.getId())
                .withDescription(task.getName())
                .withAppointmentGroup(appointmentGroupMap.get(appointmentGroup))
        );
    }

    private String determineAppointmentGroup(Task task) {
        if (task.getPriorityLevel().getName().equals(PriorityLevelEnum.GREEN.getName())){
            return "group7";
        } else if(task.getPriorityLevel().getName().equals(PriorityLevelEnum.ORANGE.getName())){
            return "group5";
        } else if(task.getPriorityLevel().getName().equals(PriorityLevelEnum.RED.getName())){
            return "group2";
        } else {
            return "group12";
        }
    }
}
