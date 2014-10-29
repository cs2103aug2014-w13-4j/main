package main.factories;

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
public class TaskDateFactory<T,C>
		implements Callback<TableColumn<Task, Calendar>, TableCell<Task,java.util.Calendar>> {

	@Override
	public TableCell<Task, java.util.Calendar> call(TableColumn<Task, Calendar> param) {
		TableCell<Task, java.util.Calendar> cell = new TableCell<Task, java.util.Calendar>() {
			@Override
			public void updateItem(java.util.Calendar item, boolean empty) {
				super.updateItem(item, empty);

				Task task = null;
				if (getTableRow() != null) {
					task = (Task) getTableRow().getItem();
				}

				if (task != null){
					if (task.isConfirmed() && item == null || empty){
						setText("-");
						setStyle("");
					} else if (task.isConfirmed()){
						setText(DateParser.parseCalendar(item));
					} else {
						setText("Unconfirmed");
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
