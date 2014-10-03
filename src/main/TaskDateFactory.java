package main;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import models.DateParser;
import models.Task;

import java.util.Calendar;


/**
 * This class formats the date in the GUI TableView to a human-readable format using
 * the methods provided by the models.DateParser class.
 * @author szhlibrary
 */
public class TaskDateFactory<Task,Calendar>
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
					// Format date.
					setText(DateParser.parseCalendar(item));
				}
			}
		};
		return cell;
	}
}
