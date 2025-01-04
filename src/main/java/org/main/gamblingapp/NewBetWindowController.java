package org.main.gamblingapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewBetWindowController {
    @FXML
    private ComboBox<String> teamBox;
    @FXML
    private TextField betAmountTextField;

    private MainWindowController mainWindowController;
    private Event selectedEvent;
    private String selectedClient;

    public void handleNewBet() {
        try{
            String team = teamBox.getSelectionModel().getSelectedItem();
            int betAmount = Integer.parseInt(betAmountTextField.getText());
            if(!selectedEvent.participantsList().contains(team)){throw new IllegalArgumentException();}
            mainWindowController.addBet(selectedEvent, betAmount, team);
            cancel();
        } catch (Exception e) {
            mainWindowController.showAlert("Invalid values", "Please enter a valid value");
        }
    }

    public void cancel() {
        Stage stage = (Stage) teamBox.getScene().getWindow();
        stage.close();
    }

    public void setMainWindowController(MainWindowController mainWindowController) {this.mainWindowController = mainWindowController;}
    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
        teamBox.setItems(FXCollections.observableArrayList(selectedEvent.participantsList()));
    }
    public void setSelectedClient(String selectedClient) {this.selectedClient = selectedClient;}
}
