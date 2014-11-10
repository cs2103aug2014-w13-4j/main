package main.factories;

import common.PriorityLevelEnum;
import common.Task;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

//@author A0111010R

/**
 * This class formats the date in the GUI TableView to a human-readable format
 * using the methods provided by the models.DateParser class.
 */
public class TaskPriorityFactory<T, C>
        implements
        Callback<TableColumn<Task, PriorityLevelEnum>, TableCell<Task, PriorityLevelEnum>> {

    @Override
    public TableCell<Task, PriorityLevelEnum> call(
            TableColumn<Task, PriorityLevelEnum> param) {
        TableCell<Task, PriorityLevelEnum> cell = new TableCell<Task, PriorityLevelEnum>() {
            @Override
            public void updateItem(PriorityLevelEnum item, boolean empty) {

                // Remove any CSS styles previously assigned
                getStyleClass().remove("priority-green");
                getStyleClass().remove("priority-orange");
                getStyleClass().remove("priority-red");

                super.updateItem(item, empty);

                Task task = null;
                if (getTableRow() != null) {
                    task = (Task) getTableRow().getItem();
                }

                if (task != null) {
                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        String priorityColour = "priority-"
                                + String.valueOf(task.getPriorityLevel())
                                        .toLowerCase();
                        getStyleClass().add(priorityColour);
                    }
                } else {
                    setText(null);
                    setStyle("");
                }
            }
        };
        return cell;
    }
}
