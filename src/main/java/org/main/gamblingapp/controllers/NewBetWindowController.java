package org.main.gamblingapp.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.main.gamblingapp.exceptions.EventException;
import org.main.gamblingapp.model.Client;
import org.main.gamblingapp.model.Event;

public class NewBetWindowController {
    @FXML
    private ComboBox<String> teamBox;
    @FXML
    private TextField betAmountTextField;

    private MainWindowController parentController;
    private Event selectedEvent;
    private Client selectedClient;

    public void handleNewBet() {
        try{
            String team = teamBox.getSelectionModel().getSelectedItem();
            int betAmount = Integer.parseInt(betAmountTextField.getText());
            if(!selectedEvent.participantsList().contains(team)) throw new EventException("Invalid team name");
            selectedClient.placeBet(selectedEvent, betAmount, team);
            selectedEvent.addBet(team, betAmount);
            cancel();
        } catch (Exception e) {
            parentController.showAlert(e.getMessage(), e.getMessage());
        }
    }

    public void cancel() {
        Stage stage = (Stage) teamBox.getScene().getWindow();
        stage.close();
    }

    public void setParentController(MainWindowController parentController) {this.parentController = parentController;}
    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
        teamBox.setItems(FXCollections.observableArrayList(selectedEvent.participantsList()));
    }
    public void setSelectedClient(Client selectedClient) {this.selectedClient = selectedClient;}
}
