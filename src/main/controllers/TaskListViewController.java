package main.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import common.Feedback;
import common.Task;

/**
 * @author szhlibrary
 */
public class TaskListViewController {
    @FXML
    private TableView<Task> taskTableView;

    private ObservableList<Task> observableTaskList;

    public void initialize(Feedback initialTasks) {
        initializeGuiTaskList(initialTasks);
    }

    private void initializeGuiTaskList(Feedback initialTasks) {
        ArrayList<Task> taskList = initialTasks.getTaskList();
        observableTaskList = FXCollections
                .observableArrayList(taskList);
        sortTaskListByDueDate();
        taskTableView.getItems().addAll(observableTaskList);
    }

    private void sortTaskListByDueDate() {
        sortTasks();
    }

    void updateTaskList(ArrayList<Task> taskList) {
        assert (taskList != null && taskList.size() >= 0);

        taskTableView.getItems().clear();
        observableTaskList = FXCollections
                .observableArrayList(taskList);
        sortTaskListByDueDate();
        taskTableView.getItems().addAll(observableTaskList);
    }

    void sortTasks() {
        Collections.sort(observableTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                Calendar firstDate = getDateForComparison(o1);
                Calendar secondDate = getDateForComparison(o2);
                if (firstDate == null && secondDate == null) {
                    return 0;
                } else if (firstDate == null) {
                    return 1;
                } else if (secondDate == null) {
                    return -1;
                } else {
                    return (firstDate.compareTo(secondDate));
                }
            }
        });
    }

    private Calendar getDateForComparison(Task task) {
        if (task.isDeadlineTask()) {
            return task.getDateDue();
        } else if (task.isTimedTask()) {
            return task.getDateStart();
        } else {
            return null;
        }
    }
}
