package org.main.gamblingapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class MainWindowController {

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
    private Menu clientMenu;

    private ObservableList<Event> events = FXCollections.observableArrayList();
    private ObservableList<String> clients = FXCollections.observableArrayList();
    private String selectedClient;

    @FXML
    private void initialize() {
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().eventNameProperty());
        eventDateColumn.setCellValueFactory(cellData -> cellData.getValue().eventDateProperty());
        participantsColumn.setCellValueFactory(cellData -> cellData.getValue().participantsProperty());
        betColumn.setCellValueFactory(cellData -> cellData.getValue().betProperty());

        // Add dummy data to events
        events.add(new Event("Event 1", "2024-01-01", "Participant A vs Participant B", "Bet Button Here"));
        events.add(new Event("Event 2", "2024-01-02", "Participant C vs Participant D", "Bet Button Here"));

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
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
