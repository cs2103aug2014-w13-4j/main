package main.controllers;

import command.CommandAlias;
import command.CommandEnum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

//@author A0111010R

/**
 * This is the controller responsible for handling the user input from the TextField.
 * It is in charge of initializing autocompletion as well as search-as-you-type
 * functionality.
 */
public class UserInputViewController {

    public TextField userInputField;
    private RootController rootController;

    private AutoCompletionBinding<String> autoCompletionBinding;
    private boolean autoCompleteCommandInitialized = false;
    private ObservableList<String> autoCompleteStringList = FXCollections
            .observableArrayList();

    public void initialize(RootController rootController) {
        this.rootController = rootController;
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
            for (CommandAlias commandAlias : CommandAlias.values()) {
                for (String alias: commandAlias.alias()) {
                    autoCompleteStringList.add(String.valueOf(alias)
                            .toLowerCase() + " ");
                }
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
            rootController.executeCommand(userInput);
        }
        System.out.println(userInput);
    }

    private void setFocusToUserInputField() {
        userInputField.requestFocus();
    }

    public void handleUserInput() {
        rootController.executeCommand(userInputField.getText());
        userInputField.clear();
    }
}
