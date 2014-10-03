package main;

import javafx.scene.control.TextField;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainControllerTest {

	@Test
	public void testHandleUserInput() throws Exception {
//		MainController controller = new MainController();
//		controller.userInputField.setText("");
		// TODO: Figure out how to test
//		controller.handleUserInput();
	}

	@Test
	public void testValidateUserInput() throws Exception {
		MainController controller = new MainController();
		assertEquals("Empty input did not validate to false!", false,
				controller.validateUserInput(""));
		assertEquals("Null input did not validate to false!", false,
				controller.validateUserInput(null));
		assertEquals("Valid input did not validate to true!", true,
				controller.validateUserInput("test command"));
	}
}