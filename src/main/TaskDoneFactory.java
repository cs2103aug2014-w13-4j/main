package main;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import models.DateParser;
import models.Task;

import java.util.Calendar;


/**
 * This class formats the dateEnd in the GUI TableView to a checkmark, depending on
 * whether the task was completed. If the task was completed, dateEnd would not be null.
 * @author szhlibrary
 */
public class TaskDoneFactory<Task,Calendar>
		implements Callback<TableColumn<Task, Calendar>, TableCell<Task,java.util.Calendar>> {

	@Override
	public TableCell<Task, java.util.Calendar> call(TableColumn<Task, Calendar> param) {
		TableCell<Task, java.util.Calendar> cell = new TableCell<Task, java.util.Calendar>() {
			@Override
			public void updateItem(java.util.Calendar item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					// dateEnd is not null, therefore task must have been done
					setText("✓");
				}
			}
		};
		return cell;
	}
}
