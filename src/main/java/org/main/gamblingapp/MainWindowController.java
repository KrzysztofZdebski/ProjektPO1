package org.main.gamblingapp;

import Interfaces.Listener;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class MainWindowController implements Listener {

    @FXML
    private ComboBox<Category> categoryBox;
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
    private ComboBox<String> clientBox;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private ObservableList<Event> events = FXCollections.observableArrayList();
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
        categories.add(new Category("cat 1", events));
        categories.add(new Category("cat 2"));

        eventsTable.setItems(events);
        eventsTable.getSortOrder().add(eventDateColumn);

        // Add dummy data to clients
        clients.addAll("Client 1", "Client 2", "Client 3");

        clientBox.setItems(clients);
        clientBox.setOnAction(event -> {update();});
        categoryBox.setItems(categories);
        categoryBox.getSelectionModel().select(categories.getFirst());
        categoryBox.setOnAction(event -> {update();});
        categoryBox.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>(){
            @Override
            public ListCell<Category> call(ListView<Category> param) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        }else{
                            setText(item.getCategoryName());
                        }
                    }
                };
            }
        });
        categoryBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCategoryName());
                }
            }
        });
    }

    @FXML
    private void handlePlaceBet() throws IOException {
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            if (selectedClient != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/main/gamblingapp/new-bet-window.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Add New Bet");
                stage.show();
                NewBetWindowController newBetWindowController = loader.getController();
                newBetWindowController.setMainWindowController(this);
                newBetWindowController.setSelectedEvent(selectedEvent);
                newBetWindowController.setSelectedClient(selectedClient);
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
//        events.get(1).addBet("Participant C",10);
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
                update();
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
            update();
        } else {
            showAlert("No client selected", "Please select a client before attempting to remove.");
        }
    }

    public void handleNewEvent() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/main/gamblingapp/new-event-window.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Add New Event");
        stage.show();
        NewEventWindowController newEventWindowController = loader.getController();
        newEventWindowController.setParentController(this);
    }

    public void handleRemoveEvent() {
        if(eventsTable.getSelectionModel().getSelectedItem() == null) return;
        events.remove(eventsTable.getSelectionModel().getSelectedItem());
        update();
    }

    public void handleNewCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Add a new category");
        dialog.setContentText("Category name:");
        dialog.showAndWait().ifPresent(categoryName -> {
            for(Category category : categories) {
                if(category.equals(categoryName)) {
                    showAlert("Category already exists", "A category with this name already exists.");
                    return;
                }
            }
            Category newCategory = new Category(categoryName);
            categories.add(newCategory);
            categoryBox.getSelectionModel().select(newCategory);
            update();
        });
    }

    public void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void update() {
        events = categoryBox.getValue().getEvents();
        selectedClient = clientBox.getSelectionModel().getSelectedItem();
        eventsTable.setItems(events);
        eventsTable.refresh();
        eventsTable.sort();
    }

    public void addEvent(Event event) {
        events.add(event);
        update();
    }

    public void addBet(Event event, int bet, String team) {
        event.addBet(team, bet);
        update();
    }
}
