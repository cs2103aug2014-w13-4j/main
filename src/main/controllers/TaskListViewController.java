package main.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Collections;

import common.Feedback;
import common.Task;

//@author A0111010R

/**
 * This is the controller responsible for showing and updating the list of tasks in the
 * task list view.
 *
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
        taskTableView.getItems().addAll(observableTaskList);
        sortTaskListByDueDate();
    }

    private void sortTaskListByDueDate() {
        FXCollections.sort(taskTableView.getItems());
    }

    void updateTaskList(ArrayList<Task> taskList) {
        assert (taskList != null && taskList.size() >= 0);

        taskTableView.getItems().clear();
        observableTaskList = FXCollections
                .observableArrayList(taskList);
        taskTableView.getItems().addAll(observableTaskList);
        sortTaskListByDueDate();
    }
}
