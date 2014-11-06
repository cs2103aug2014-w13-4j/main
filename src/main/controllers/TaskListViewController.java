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
        Collections.sort(observableTaskList);
    }

    protected void updateTaskList(ArrayList<Task> taskList) {
        assert (taskList != null && taskList.size() >= 0);

        taskTableView.getItems().clear();
        observableTaskList = FXCollections
                .observableArrayList(taskList);
        sortTaskListByDueDate();
        taskTableView.getItems().addAll(observableTaskList);
    }
}
