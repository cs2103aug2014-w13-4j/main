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

    public void initialize(Feedback initialTasks) {
        final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new HashMap<String, Agenda.AppointmentGroup>();
        lAppointmentGroupMap.put("group0", new Agenda.AppointmentGroupImpl().withStyleClass("group0"));

        ArrayList<Task> taskList = initialTasks.getTaskList();
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

    static private Calendar getFirstDayOfWeekCalendar(Locale locale, Calendar c)
    {
        // result
        int lFirstDayOfWeek = Calendar.getInstance(locale).getFirstDayOfWeek();
        int lCurrentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int lDelta = 0;
        if (lFirstDayOfWeek <= lCurrentDayOfWeek)
        {
            lDelta = -lCurrentDayOfWeek + lFirstDayOfWeek;
        }
        else
        {
            lDelta = -lCurrentDayOfWeek - (7-lFirstDayOfWeek);
        }
        c = ((Calendar)c.clone());
        c.add(Calendar.DATE, lDelta);
        return c;
    }
}
