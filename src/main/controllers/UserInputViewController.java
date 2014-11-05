package main.controllers;

import command.CommandEnum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

/**
 * @author szhlibrary
 */
public class UserInputViewController {

    public TextField userInputField;
    private RootLayoutController rootLayoutController;

    private AutoCompletionBinding<String> autoCompletionBinding;
    private boolean autoCompleteCommandInitialized = false;
    private ObservableList<String> autoCompleteStringList = FXCollections
            .observableArrayList();

    public void initialize(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
        initializeAutoComplete();
        setFocusToUserInputField();
    }

    private void initializeAutoComplete() {
        initializeAutoCompleteForCommands();
    }

    private void initializeAutoCompleteForCommands() {
        if (!autoCompleteCommandInitialized) {
            if (autoCompletionBinding != null) {
                autoCompletionBinding.dispose();
            }
            autoCompleteStringList.clear();
            for (CommandEnum command : CommandEnum.values()) {
                autoCompleteStringList.add(String.valueOf(command)
                        .toLowerCase() + " ");
            }

            autoCompletionBinding = TextFields.bindAutoCompletion(
                    userInputField, autoCompleteStringList);
            autoCompleteCommandInitialized = true;
        }
    }

    public void handleUserIncrementalInput() {
        String userInput = userInputField.getText();
        if (userInput.split(" ")[0].equalsIgnoreCase(String
                .valueOf(CommandEnum.SEARCH))) {
            rootLayoutController.executeCommand(userInput);
        }
        System.out.println(userInput);
    }

    private void setFocusToUserInputField() {
        userInputField.requestFocus();
    }

    public void handleUserInput() {
        rootLayoutController.executeCommand(userInputField.getText());
        userInputField.clear();
    }
}
