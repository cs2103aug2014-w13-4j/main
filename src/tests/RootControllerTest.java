package tests;

import main.controllers.RootController;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class RootControllerTest {

    private Class<RootController> rootLayoutControllerClass = RootController.class;
    private Method validateUserInput = rootLayoutControllerClass
            .getDeclaredMethod("validateUserInput", String.class);

    public RootControllerTest() throws NoSuchMethodException {
    }

    @Before
    public void setFunctionsAccessible() {
        // setFocusToUserInputField.setAccessible(true);
        validateUserInput.setAccessible(true);
    }

    public void testExecuteCommand() throws Exception {

    }

    @Test
    public void testValidateUserInput() throws Exception {
        RootController controller = new RootController();
        assertEquals("Empty input did not validate to false!", false,
                validateUserInput.invoke(controller, ""));
        assertEquals("Valid input did not validate to true!", true,
                validateUserInput.invoke(controller, "test command"));
    }
}