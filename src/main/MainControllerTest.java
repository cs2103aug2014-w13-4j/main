package main;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MainControllerTest {

	Class<MainController> MainControllerClass = MainController.class;
	Method validateUserInput = MainControllerClass.
			getDeclaredMethod("validateUserInput", String.class);

	public MainControllerTest() throws NoSuchMethodException {
	}

	@Before
	public void setFunctionsAccessible(){
		validateUserInput.setAccessible(true);
	}

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
				validateUserInput.invoke(controller, ""));
		assertEquals("Valid input did not validate to true!", true,
				validateUserInput.invoke(controller, "test command"));
	}
	}
}