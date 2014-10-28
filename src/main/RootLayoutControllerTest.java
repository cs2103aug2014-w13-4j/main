package main;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class RootLayoutControllerTest {

	private Class<RootLayoutController> rootLayoutControllerClass = RootLayoutController.class;
	private Method validateUserInput = rootLayoutControllerClass.
			getDeclaredMethod("validateUserInput", String.class);

	public RootLayoutControllerTest() throws NoSuchMethodException {
	}

	@Before
	public void setFunctionsAccessible(){
		//setFocusToUserInputField.setAccessible(true);
		validateUserInput.setAccessible(true);
	}

	public void testExecuteCommand() throws Exception {

	}

	@Test
	public void testValidateUserInput() throws Exception {
		RootLayoutController controller = new RootLayoutController();
		assertEquals("Empty input did not validate to false!", false,
				validateUserInput.invoke(controller, ""));
		assertEquals("Valid input did not validate to true!", true,
				validateUserInput.invoke(controller, "test command"));
	}
}