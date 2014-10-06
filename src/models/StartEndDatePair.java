package models;

import java.util.Calendar;

public class StartEndDatePair {
	private Calendar startDate;
	private Calendar dueDate;
	
	public StartEndDatePair(Calendar startDate, Calendar dueDate) {
		this.startDate = startDate;
		this.dueDate = dueDate;
	}
	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	public Calendar getDueDate() {
		return dueDate;
	}
	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}
	

}
