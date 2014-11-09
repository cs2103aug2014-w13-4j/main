package tests;

import javafx.scene.layout.BorderPane;
import main.controllers.RootController;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class RootControllerTest {

    private Class<RootController> rootLayoutControllerClass = RootController.class;
    private Method validateUserInput = rootLayoutControllerClass
            .getDeclaredMethod("validateUserInput", String.class);
    private Method initRootLayout = rootLayoutControllerClass
        .getDeclaredMethod("initRootLayout");
    private Method initTabLayout = rootLayoutControllerClass
        .getDeclaredMethod("initTabLayout");

    private Field rootLayout = rootLayoutControllerClass.getDeclaredField("rootLayout");
    private Field tabLayout = rootLayoutControllerClass.getDeclaredField("tabLayout");

    public RootControllerTest() throws NoSuchMethodException, NoSuchFieldException {
    }

    @Before
    public void setFunctionsAccessible() {
        // setFocusToUserInputField.setAccessible(true);
        validateUserInput.setAccessible(true);
        initRootLayout.setAccessible(true);
        initTabLayout.setAccessible(true);
        rootLayout.setAccessible(true);
        tabLayout.setAccessible(true);
    }

    public void testExecuteCommand() throws Exception {

    }

    @Test
    public void testInitRootLayout() throws Exception {
        RootController controller = new RootController();
        initRootLayout.invoke(controller);
        assertNotNull("Root Layout should not be null!", rootLayout.get(controller));
    }

    @Test
    public void testInitTabLayout() throws Exception {
        RootController controller = new RootController();
        initRootLayout.invoke(controller);
        initTabLayout.invoke(controller);
        assertNotNull("Tab Layout should not be null!", tabLayout.get(controller));
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