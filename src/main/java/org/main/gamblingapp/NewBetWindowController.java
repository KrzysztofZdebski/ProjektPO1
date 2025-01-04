package org.main.gamblingapp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
            if(!selectedEvent.participantsList().contains(team)){throw new IllegalArgumentException();}
            parentController.addBet(selectedEvent, betAmount, team);
            selectedClient.placeBet(selectedEvent, betAmount, team);
            cancel();
        } catch(IllegalArgumentException e){
            parentController.showAlert("Not enough founds","Not enough founds");
        } catch (Exception e) {
            parentController.showAlert("Invalid values", "Please enter a valid value");
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
