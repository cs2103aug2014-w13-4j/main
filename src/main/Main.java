package main;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import common.ApplicationLogger;
import common.exceptions.FileFormatNotSupportedException;
import main.controllers.RootController;

//@author A0111010R
/**
 * The main method of the program; program execution starts here. In charge of
 * initializing the primary Stage, and also initializes the layouts via RootController.
 */
public class Main extends Application {
    private Stage primaryStage;

    private RootController rootController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationLogger.getLogger().log(Level.INFO,
                "Initializing JavaFX UI.");

        initPrimaryStage(primaryStage);
        initLayouts();
    }

    private void initPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Awesome Task Manager");
    }

    private void initLayouts() throws FileFormatNotSupportedException {
        assert (primaryStage != null);
        try {
            rootController = new RootController();
            rootController.initialize(primaryStage);
        } catch (IOException e) {
            ApplicationLogger.getLogger().log(Level.SEVERE,
                    e.getMessage());
        }
    }
}

/* CalendarViewStyleSheet.css

.background {
  -fx-background-color: #1d1d1d;
}

.AgendaWeekSkin .Week {
  -fx-background-color: black;
}

.AgendaWeekSkin .HourLabel {
  -fx-fill: white;
  -fx-stroke: transparent;
}

.AgendaWeekSkin .DayHeader {
  -fx-background-color: black;
}

.AgendaWeekSkin .DayHeader .weekday {
  -fx-fill: white;
}

.AgendaWeekSkin .DayHeader .weekend {
  -fx-fill: white;
}

*/

/* TaskDisplayViewStyleSheet.css

.background {
  -fx-background-color: #1d1d1d;
}

.label {
  -fx-font-size: 11pt;
  -fx-font-family: "Segoe UI";
  -fx-text-fill: white;
  -fx-opacity: 0.6;
}

.label-bright {
  -fx-font-size: 11pt;
  -fx-font-family: "Segoe UI";
  -fx-text-fill: white;
  -fx-opacity: 1;
}

.label-header {
  -fx-font-size: 32pt;
  -fx-font-family: "Segoe UI Light";
  -fx-text-fill: white;
  -fx-opacity: 1;
}

*/

/* TaskListViewStyleSheet.css

.background {
  -fx-background-color: #1d1d1d;
}

.table-view {
  -fx-base: #1d1d1d;
  -fx-control-inner-background: #1d1d1d;
  -fx-background-color: #1d1d1d;
  -fx-table-cell-border-color: transparent;
  -fx-table-header-border-color: transparent;
  -fx-padding: 5;
}

.table-view .column-header-background {
  -fx-background-color: transparent;
}

.table-view .column-header, .table-view .filler {
  -fx-size: 35;
  -fx-border-width: 0 0 1 0;
  -fx-background-color: transparent;
  -fx-border-color:
  transparent
  transparent
  derive(-fx-base, 80%)
  transparent;
  -fx-border-insets: 0 10 1 0;
}

.table-view .column-header .label {
  -fx-font-size: 11pt;
  -fx-font-family: "Segoe UI Light";
  -fx-text-fill: white;
  -fx-alignment: center-left;
  -fx-opacity: 1;
}

.table-view:focused .table-row-cell:filled:focused:selected {
  -fx-background-color: -fx-focus-color;
}

.priority-green {
  -fx-background-color: green;
}

.priority-orange {
  -fx-background-color: orange;
}

.priority-red {
  -fx-background-color: red;
}

*/

/* UserInputViewStyleSheet.css

//
//Metro style
//Author: Pedro Duque Vieira
//http://pixelduke.wordpress.com/2012/10/23/jmetro-windows-8-controls-on-java/
//

.text-input{
      -fx-background-radius: 0, 0;

      -fx-background-color: #d2d2d2;
      -fx-background-insets: 0;

      -fx-prompt-text-fill: #818181;

      -fx-highlight-fill: #008287;
      -fx-highlight-text-fill: white;
      }

      .text-input:hover{
      -fx-background-color: #e2e2e2;
      }

      .text-input:focused{
      -fx-background-color: #5c5c5c, white;

      -fx-text-fill: black;
      }

*/

/* CalendarView.fxml

<?import jfxtras.scene.control.agenda.Agenda?>

<Agenda fx:id="calendarView" xmlns:fx="http://javafx.com/fxml/1" prefHeight="600" prefWidth="650.0" xmlns="http://javafx.com/javafx/8"
      fx:controller="main.controllers.CalendarViewController" stylesheets="main/stylesheets/CalendarViewStyleSheet.css" styleClass="background">
</Agenda>

*/

/* NotificationPaneWrapper.fxml

<?xml version="1.0" encoding="UTF-8"?>

<?import org.controlsfx.control.NotificationPane?>

<NotificationPane xmlns:fx="http://javafx.com/fxml/1" xmlns="http://javafx.com/javafx/8"/>

*/

/* RootLayout.fxml

<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.layout.BorderPane?>
<BorderPane xmlns:fx="http://javafx.com/fxml/1" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity"
          minWidth="-Infinity" xmlns="http://javafx.com/javafx/8"
          fx:controller="main.controllers.RootController"/>

*/

/* TabLayout.fxml

<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.TabPane?>

<TabPane xmlns:fx="http://javafx.com/fxml/1" xmlns="http://javafx.com/javafx/8" minHeight="600" minWidth="650.0"/>

*/

/* TaskDisplayView.fxml

<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<AnchorPane xmlns:fx="http://javafx.com/fxml/1" minHeight="160.0" minWidth="400.0" prefHeight="160.0" prefWidth="400.0"
          xmlns="http://javafx.com/javafx/8" fx:controller="main.controllers.TaskDisplayViewController"
          stylesheets="main/stylesheets/TaskDisplayViewStyleSheet.css" styleClass="background">
  <children>
      <Label layoutX="7.0" layoutY="7.0" text="Task Details" AnchorPane.leftAnchor="5.0"
             AnchorPane.topAnchor="5.0" styleClass="label-header"/>
      <GridPane layoutX="10.0" layoutY="10.0" AnchorPane.leftAnchor="5.0"
                AnchorPane.topAnchor="60.0">
          <columnConstraints>
              <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
              <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="390.0"/>
          </columnConstraints>
          <rowConstraints>
              <RowConstraints minHeight="30.0" prefHeight="30.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="30.0" prefHeight="30.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="30.0" prefHeight="30.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="30.0" prefHeight="30.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="30.0" prefHeight="30.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="30.0" prefHeight="30.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="80.0" prefHeight="80.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="50.0" prefHeight="50.0" vgrow="SOMETIMES"/>
              <RowConstraints minHeight="30.0" prefHeight="30.0" vgrow="SOMETIMES"/>
          </rowConstraints>
          <children>
              <Label text="ID:" GridPane.columnIndex="0" GridPane.rowIndex="0"/>
              <Label fx:id="idLabel" text="-" GridPane.columnIndex="1" GridPane.rowIndex="0" styleClass="label-bright"/>
              <Label text="Task Name:" GridPane.columnIndex="0" GridPane.rowIndex="1"/>
              <Label fx:id="taskNameLabel" text="-" wrapText="true" GridPane.columnIndex="1" GridPane.rowIndex="1" styleClass="label-bright"/>
              <Label text="Due Date:" GridPane.columnIndex="0" GridPane.rowIndex="2"/>
              <Label fx:id="dueDateLabel" text="-" GridPane.columnIndex="1" GridPane.rowIndex="2" styleClass="label-bright"/>
              <Label text="Start Date:" GridPane.columnIndex="0" GridPane.rowIndex="3"/>
              <Label fx:id="startDateLabel" text="-" GridPane.columnIndex="1" GridPane.rowIndex="3" styleClass="label-bright"/>
              <Label text="End Date:" GridPane.columnIndex="0" GridPane.rowIndex="4"/>
              <Label fx:id="endDateLabel" text="-" GridPane.columnIndex="1" GridPane.rowIndex="4" styleClass="label-bright"/>
              <Label text="Priority:" GridPane.columnIndex="0" GridPane.rowIndex="5"/>
              <Label fx:id="priorityLevelLabel" text="-" GridPane.columnIndex="1" GridPane.rowIndex="5" styleClass="label-bright"/>
              <Label text="Description:" GridPane.columnIndex="0" GridPane.rowIndex="6" GridPane.valignment="TOP"/>
              <Label fx:id="noteLabel" text="-" wrapText="true" GridPane.columnIndex="1" GridPane.rowIndex="6"
                     GridPane.valignment="TOP" styleClass="label-bright"/>
              <Label text="Conditional Dates:" GridPane.columnIndex="0" GridPane.rowIndex="7"
                     GridPane.valignment="TOP" wrapText="true"/>
              <Label fx:id="conditionalDateLabel" text="-" GridPane.columnIndex="1" GridPane.rowIndex="7"
                     GridPane.valignment="TOP" styleClass="label-bright"/>
              <Label text="Tags:" GridPane.columnIndex="0" GridPane.rowIndex="8"/>
              <Label fx:id="tagLabel" text="-" GridPane.columnIndex="1" GridPane.rowIndex="8" styleClass="label-bright"/>
          </children>
      </GridPane>
  </children>
</AnchorPane>

*/

/* TaskListView.fxml

<?xml version="1.0" encoding="UTF-8"?>

<?import main.factories.TaskDoneFactory?>
<?import main.factories.TaskDateFactory?>
<?import main.factories.TaskPriorityFactory?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.control.cell.PropertyValueFactory?>

<AnchorPane xmlns:fx="http://javafx.com/fxml/1" minHeight="600.0" minWidth="650.0" prefHeight="600.0"
          prefWidth="650.0" xmlns="http://javafx.com/javafx/8"
          fx:controller="main.controllers.TaskListViewController" stylesheets="main/stylesheets/TaskListViewStyleSheet.css">
  <children>
      <TableView fx:id="taskTableView" layoutX="122.0" layoutY="62.0" prefHeight="200.0"
                 prefWidth="400.0" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0"
                 AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
          <columns>
              <TableColumn prefWidth="30.0" minWidth="30.0" maxWidth="30.0" text="ID">
                  <cellValueFactory>
                      <PropertyValueFactory property="id"/>
                  </cellValueFactory>
              </TableColumn>
              <TableColumn prefWidth="30.0" minWidth="30.0" maxWidth="20.0" text="✓">
                  <cellValueFactory>
                      <PropertyValueFactory property="dateEnd"/>
                  </cellValueFactory>
                  <cellFactory>
                      <TaskDoneFactory/>
                  </cellFactory>
              </TableColumn>
              <TableColumn prefWidth="100.0" minWidth="100.0" text="Due">
                  <cellValueFactory>
                      <PropertyValueFactory property="dateDue"/>
                  </cellValueFactory>
                  <cellFactory>
                      <TaskDateFactory/>
                  </cellFactory>
              </TableColumn>
              <TableColumn prefWidth="100.0" minWidth="100.0" text="Start">
                  <cellValueFactory>
                      <PropertyValueFactory property="dateStart"/>
                  </cellValueFactory>
                  <cellFactory>
                      <TaskDateFactory/>
                  </cellFactory>
              </TableColumn>
              <TableColumn prefWidth="100.0" minWidth="100.0" text="End">
                  <cellValueFactory>
                      <PropertyValueFactory property="dateEnd"/>
                  </cellValueFactory>
                  <cellFactory>
                      <TaskDateFactory/>
                  </cellFactory>
              </TableColumn>
              <TableColumn prefWidth="225.0" minWidth="225.0" text="Task Name">
                  <cellValueFactory>
                      <PropertyValueFactory property="name"/>
                  </cellValueFactory>
              </TableColumn>
              <TableColumn prefWidth="5.0" minWidth="5.0" maxWidth="5.0" text="">
                  <cellValueFactory>
                      <PropertyValueFactory property="priorityLevel"/>
                  </cellValueFactory>
                  <cellFactory>
                      <TaskPriorityFactory/>
                  </cellFactory>
              </TableColumn>
          </columns>
          <columnResizePolicy>
              <TableView fx:constant="CONSTRAINED_RESIZE_POLICY"/>
          </columnResizePolicy>
      </TableView>
  </children>
</AnchorPane>

*/

/* UserInputView.fxml

<?import javafx.scene.control.TextField?>

<?import javafx.scene.layout.AnchorPane?>
<AnchorPane xmlns:fx="http://javafx.com/fxml/1" minHeight="40.0" maxHeight="40.0" minWidth="0.0" prefHeight="40.0"
          prefWidth="160.0" xmlns="http://javafx.com/javafx/8"
          fx:controller="main.controllers.UserInputViewController" stylesheets="main/stylesheets/UserInputViewStyleSheet.css">
  <children>
      <TextField fx:id="userInputField" onKeyReleased="#handleUserIncrementalInput" onAction="#handleUserInput"
                 promptText="enter command here..." layoutX="21.0" layoutY="6.0" AnchorPane.bottomAnchor="0.0"
                 AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0" styleClass="text-input"/>
  </children>
</AnchorPane>

*/