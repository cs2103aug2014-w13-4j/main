package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import models.StartDueDatePair;
import models.Task;

import org.junit.Test;

public class TaskTest {

	@Test
	public final void testAddTags() {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("1");
		tags.add("2");
		Task testTask = new Task();
		testTask.setTags(tags);
		ArrayList<String> newTags = new ArrayList<String>();
		newTags.add("2");
		newTags.add("3");
		testTask.addTags(newTags);
		ArrayList<String> finalResult = new ArrayList<String>();
		finalResult.add("1");
		finalResult.add("2");
		finalResult.add("3");
		assertEquals("2 is not duplicated", testTask.getTags(), finalResult);
	}
	
	@Test
	public final void testConditional() {
		Task conditionalTask = new Task();
		StartDueDatePair dates = new StartDueDatePair(Calendar.getInstance(), Calendar.getInstance());
		ArrayList<StartDueDatePair> conditionalDates = new ArrayList<StartDueDatePair>();
		conditionalDates.add(dates);
		conditionalTask.setConditionalDates(conditionalDates);
		assertFalse(conditionalTask.isConfirmed());
		conditionalTask.setStartDueDateFromConditional(1);
		assertTrue(conditionalTask.isConfirmed());
		Task notConditionalTask = new Task();
		assertTrue(notConditionalTask.isConfirmed());
	}

}
