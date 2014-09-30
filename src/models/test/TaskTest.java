package models.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

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

}
