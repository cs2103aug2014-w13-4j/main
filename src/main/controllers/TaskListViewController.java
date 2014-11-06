package main.controllers;

import common.exceptions.InvalidSortConditionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Collections;

import common.Feedback;
import common.Task;

/**
 * @author szhlibrary
 */
public class TaskListViewController {
    private static final String INVALID_SORT_CONDITION_MESSAGE = "Invalid sort condition!";
    @FXML
    private TableView<Task> taskTableView;

    @FXML
    private TableColumn idTableColumn;
    @FXML
    private TableColumn doneTableColumn;
    @FXML
    private TableColumn dueDateTableColumn;
    @FXML
    private TableColumn startDateTableColumn;
    @FXML
    private TableColumn endDateTableColumn;
    @FXML
    private TableColumn taskNameTableColumn;
    @FXML
    private TableColumn priorityLevelTableColumn;

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

    protected void sortTaskListByDueDate() {
        doneTableColumn.setSortable(false);
        FXCollections.sort(taskTableView.getItems());
    }

    protected void sortTaskListByCondition(String condition) throws InvalidSortConditionException {
        assert (condition != null) : "Sort condition should not be null!";
        switch (condition) {
            case "due":
                taskTableView.getSortOrder().add(dueDateTableColumn);
                dueDateTableColumn.setSortable(true);
                break;
            default:
                throw new InvalidSortConditionException(INVALID_SORT_CONDITION_MESSAGE);
        }
    }

    protected void updateTaskList(ArrayList<Task> taskList) {
        assert (taskList != null && taskList.size() >= 0);

        taskTableView.getItems().clear();
        observableTaskList = FXCollections
                .observableArrayList(taskList);
        taskTableView.getItems().addAll(observableTaskList);
        sortTaskListByDueDate();
    }
}
