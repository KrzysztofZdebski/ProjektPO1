package org.main.gamblingapp;

import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewEventWindowController {
    @FXML
    private TextField eventNameTextField;
    @FXML
    private TextField fisrtTeamTextField;
    @FXML
    private TextField secondTeamTextField;
    @FXML
    private DatePicker datePicker;

    private MainWindowController mainWindowController;

    public void cancel() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }

    public void createEvent() {
        try{
            String eventName = eventNameTextField.getText();
            String fisrtTeam = fisrtTeamTextField.getText();
            String secondTeam = secondTeamTextField.getText();
            String date = datePicker.getValue().toString();
            if(eventName.isEmpty() || fisrtTeam.isEmpty() || secondTeam.isEmpty()) {throw new NullPointerException();}
            mainWindowController.addEvent(new Event(eventName, date, new String[]{fisrtTeam, secondTeam}, new Integer[]{0,0}));
            cancel();
        }catch(NumberFormatException e){
            mainWindowController.showAlert("Incorrect values", "Please enter a valid values");
        }catch(NullPointerException e){
            mainWindowController.showAlert("Missing values", "Please enter all values");
        }
    }

    public void setParentController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }
}
