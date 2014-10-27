package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Feedback;
import models.Task;

import java.util.ArrayList;

/**
 * @author szhlibrary
 */
public class TaskListViewController {
	@FXML
	public TableView<Task> taskTableView;
	public TableColumn dueDateTableColumn;

	public void initialize(Feedback initialTasks){
		initializeGuiTaskList(initialTasks);
	}

	private void initializeGuiTaskList(Feedback initialTasks) {
		ArrayList<Task> taskList = initialTasks.getTaskList();
		ObservableList<Task> observableList = FXCollections.observableArrayList(taskList);
		taskTableView.getItems().addAll(observableList);
		sortTaskListByDueDate();
	}

	private void sortTaskListByDueDate() {
		dueDateTableColumn.setSortType(TableColumn.SortType.ASCENDING);
		taskTableView.getSortOrder().add(dueDateTableColumn);
	}

	private void updateTaskList(ArrayList<Task> taskList) {
		assert(taskList != null && taskList.size() >= 0);

		taskTableView.getItems().clear();
		ObservableList<Task> observableList = FXCollections.observableArrayList(taskList);
		taskTableView.getItems().addAll(observableList);
	}
}
