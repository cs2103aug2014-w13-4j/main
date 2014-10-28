package tests;

import main.MainController;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MainControllerTest {

	private Class<MainController> MainControllerClass = MainController.class;
	//Method setFocusToUserInputField = MainControllerClass.getDeclaredMethod("setFocusToUserInputField");
	Method validateUserInput = MainControllerClass.
			getDeclaredMethod("validateUserInput", String.class);

	public MainControllerTest() throws NoSuchMethodException {
	}

	@Before
	public void setFunctionsAccessible(){
		//setFocusToUserInputField.setAccessible(true);
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

	@Test
	public void testSetFocusToUserInputField() throws Exception {
		// TODO: Figure out how to test
		// http://stackoverflow.com/questions/11385604/how-do-you-unit-test-a-javafx-controller-with-junit
		// http://blog.buildpath.de/javafx-testrunner/
		// http://awhite.blogspot.de/2013/04/javafx-junit-testing.html

		//final MainController controller = new MainController();
		//setFocusToUserInputField.invoke(controller);
		//assertEquals("asd", true, controller.userInputField.isFocused());
	}
}