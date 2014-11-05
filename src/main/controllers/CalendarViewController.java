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
    private Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap;

    public void initialize(Feedback initialTasks) {
        lAppointmentGroupMap = new HashMap<String, Agenda.AppointmentGroup>();
        lAppointmentGroupMap.put("group0", new Agenda.AppointmentGroupImpl().withStyleClass("group0"));

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
                if (task.getDateStart() == null){ // deadline task
                    calendarView.appointments().add(
                        new Agenda.AppointmentImpl()
                            .withStartTime(task.getDateDue())
                            .withSummary("ID: " + task.getId())
                            .withDescription(task.getName())
                            .withAppointmentGroup(lAppointmentGroupMap.get("group0"))
                            .withWholeDay(true)
                    );
                } else if (task.getDateEnd() != null){ // event task
                    calendarView.appointments().add(
                        new Agenda.AppointmentImpl()
                            .withStartTime(task.getDateStart())
                            .withEndTime(task.getDateEnd())
                            .withSummary("ID: " + task.getId())
                            .withDescription(task.getName())
                            .withAppointmentGroup(lAppointmentGroupMap.get("group0"))
                    );
                }
            }
        }
    }
}
