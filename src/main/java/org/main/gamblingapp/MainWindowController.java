package org.main.gamblingapp;

import Interfaces.Listener;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.util.Objects;

public class MainWindowController implements Listener {

    @FXML
    private TableView<Event> eventsTable;
    @FXML
    private TableColumn<Event, String> eventNameColumn;
    @FXML
    private TableColumn<Event, String> eventDateColumn;
    @FXML
    private TableColumn<Event, String> participantsColumn;
    @FXML
    private TableColumn<Event, String> betColumn;
    @FXML
    private TableColumn<Event,String> oddsColumn;

    @FXML
    private Menu clientMenu;

    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private final ObservableList<String> clients = FXCollections.observableArrayList();
    private String selectedClient;

    @FXML
    private void initialize() {
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().eventNameProperty());
        eventDateColumn.setCellValueFactory(cellData -> cellData.getValue().eventDateProperty());
        participantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().participantsList().getFirst() + " vs " + cellData.getValue().participantsList().get(1)));
        betColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().betList().getFirst() + " | " + cellData.getValue().betList().get(1)));
        oddsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().oddsList().getFirst() + " | " + cellData.getValue().oddsList().get(1)));

        // Add dummy data to events
        events.add(new Event("Event 1", "2024-01-01", new String[]{"Participant A", "Participant B"}, new Integer[]{8546,6742}));
        events.getFirst().addListener(this);
        events.add(new Event("Event 2", "2024-01-02", new String[]{"Participant C", "Participant D"}, new Integer[]{0,100}));
        events.get(1).addListener(this);

        eventsTable.setItems(events);

        // Add dummy data to clients
        clients.addAll("Client 1", "Client 2", "Client 3");
        updateClientMenu();
    }

    @FXML
    private void handlePlaceBet() {
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            if (selectedClient != null) {
                // Place bet logic here
                System.out.println("Placing bet on: " + selectedEvent.getEventName() + " for client: " + selectedClient);
            } else {
                showAlert("No client selected", "Please select a client before placing a bet.");
            }
        } else {
            showAlert("No event selected", "Please select an event before placing a bet.");
        }
    }

    @FXML
    private void handleClearSelection() {
        eventsTable.getSelectionModel().clearSelection();
        events.get(1).addBet("Participant C",10);
    }

    @FXML
    private void handleAddClient() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Client");
        dialog.setHeaderText("Add a new client");
        dialog.setContentText("Client name:");

        dialog.showAndWait().ifPresent(clientName -> {
            if (!clients.contains(clientName)) {
                clients.add(clientName);
                updateClientMenu();
            } else {
                showAlert("Client already exists", "A client with this name already exists.");
            }
        });
    }

    @FXML
    private void handleRemoveClient() {
        if (selectedClient != null) {
            clients.remove(selectedClient);
            selectedClient = null;
            updateClientMenu();
        } else {
            showAlert("No client selected", "Please select a client before attempting to remove.");
        }
    }

    private void updateClientMenu() {
        clientMenu.getItems().clear();
        ToggleGroup clientToggleGroup = new ToggleGroup();

        for (String client : clients) {
            RadioMenuItem menuItem = new RadioMenuItem(client);
            menuItem.setToggleGroup(clientToggleGroup);
            menuItem.setOnAction(event -> {
                selectedClient = client;
                System.out.println("Selected " + client);
            });

            if (client.equals(selectedClient)) {
                menuItem.setSelected(true);
            }

            clientMenu.getItems().add(menuItem);
        }
        update();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void update() {
        eventsTable.refresh();
        clientMenu.setText(Objects.equals(selectedClient, "") ? "Client" : selectedClient);
    }
}
