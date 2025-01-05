package org.main.gamblingapp.controllers;

import org.main.gamblingapp.interfaces.Listener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.main.gamblingapp.model.Bet;
import org.main.gamblingapp.model.Category;
import org.main.gamblingapp.model.Client;
import org.main.gamblingapp.model.Event;


public class MainWindowController implements Listener {
    @FXML
    private Label balanceLabel;
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
    private TableColumn<Event, String> finishedColumn;
    @FXML
    private TableColumn<Event,String> clientBetColumn;

    @FXML
    private ComboBox<Client> clientBox;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private ObservableList<Event> events = FXCollections.observableArrayList();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private Client selectedClient;

    @FXML
    private void initialize() {
        loadData();
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().eventNameProperty());
        eventDateColumn.setCellValueFactory(cellData -> cellData.getValue().eventDateProperty());
        participantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().participantsList().getFirst() + " vs " + cellData.getValue().participantsList().get(1)));
        betColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().betList().getFirst() + " | " + cellData.getValue().betList().get(1)));
        oddsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().oddsList().getFirst() + " | " + cellData.getValue().oddsList().get(1)));
        finishedColumn.setCellValueFactory(cellData -> {
            if(cellData.getValue().isFinished()) return new SimpleStringProperty("Finished");
            return new SimpleStringProperty(cellData.getValue().getTimeLeft() + " days");
        });
        clientBetColumn.setCellValueFactory(cellData -> {
            String str = "";
            if(selectedClient == null) return new SimpleStringProperty(str);
            String eventName = cellData.getValue().eventNameProperty().get();
            for(Bet bet : selectedClient.getBets()){
                if(bet.getEvent().equals(eventName)){
                    str = bet.getTeam() + ": " + bet.getAmount();
                }
            }
            return new SimpleStringProperty(str);
        });
        DoubleBinding usedWidth = eventNameColumn.widthProperty().add(eventDateColumn.widthProperty()).add(participantsColumn.widthProperty()).add(betColumn.widthProperty()).add(oddsColumn.widthProperty()).add(finishedColumn.widthProperty());
        clientBetColumn.prefWidthProperty().bind(eventsTable.widthProperty().subtract(usedWidth));

        // Add dummy data to events
        events.add(new Event("Event1", "2026-01-01", new String[]{"Participant A", "Participant B"}, new Integer[]{8546,6742}));
        events.getFirst().addListener(this);
        events.add(new Event("Event2", "2024-01-02", new String[]{"Participant C", "Participant D"}, new Integer[]{0,100}));
        events.get(1).addListener(this);
        categories.add(new Category("cat1", events));
        categories.add(new Category("cat2"));

        eventsTable.setItems(events);
        eventsTable.getSortOrder().add(eventDateColumn);

        // Add dummy data to clients
//        clients.addAll(new Client("Client 1",100), new Client("Client 2",200));

        clientBox.setItems(clients);
        clientBox.setOnAction(_ -> update());
        clientBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Client> call(ListView<Client> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Client item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getClientName());
                        }
                    }
                };
            }
        });
        clientBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getClientName());
                }
            }
        });

        categoryBox.setItems(categories);
        categoryBox.getSelectionModel().select(categories.getFirst());
        categoryBox.setOnAction(_ -> update());
        categoryBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Category> call(ListView<Category> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Category item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getCategoryName());
                        }
                    }
                };
            }
        });
        categoryBox.setButtonCell(new ListCell<>() {
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
            if(selectedEvent.isFinished()){
                showAlert("Event finished", "This event has already finished.");
                return;
            }
            if (selectedClient != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/main/gamblingapp/new-bet-window.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Add New Bet");
                stage.show();
                NewBetWindowController newBetWindowController = loader.getController();
                newBetWindowController.setParentController(this);
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
    }

    @FXML
    private void handleAddClient() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Client");
        dialog.setHeaderText("Add a new client");
        dialog.setContentText("Client name:");

        dialog.showAndWait().ifPresent(clientName -> {
            for(Client client : clients) {
                if(client.equals(clientName)) {
                    showAlert("Client already exists", "A client with this name already exists.");
                    return;
                }
            }
            clients.add(new Client(clientName,0));
            update();
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
        if(selectedClient != null) {
            balanceLabel.setText(Integer.toString(selectedClient.getClientAccBalance()));
        }else{
            balanceLabel.setText("0");
        }
    }

    public void addEvent(Event event) {
        events.add(event);
        update();
    }

//    public void addBet(Event event, int bet, String team) throws IllegalArgumentException{
//        event.addBet(team, bet);
//        update();
//    }

    public void handleCheckDate() {
        for(Category category : categories) {
            for(Event event : category.getEvents()) {
                event.checkDate();
            }
        }
        update();
    }
    public void loadData() {
        try{
            JSONArray jsonArrayClients = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/clients.json").toURI())));
            List<String[]> valuesClients = new ArrayList<>();
            for (Object obj : jsonArrayClients) {
                JSONObject jsonObject = (JSONObject) obj;
                String[] row = new String[2];
                row[0] = (String) jsonObject.get("clientName");
                row[1] = jsonObject.get("clientAccBalance").toString();
                valuesClients.add(row);
            }
            // Convert List to 2D array
            String[][] valuesArrayClients = new String[valuesClients.size()][2];
            for (int i = 0; i < valuesClients.size(); i++) {
                valuesArrayClients[i] = valuesClients.get(i);
            }

        for (int i = 0; i < valuesArrayClients.length; i++) {
            clients.add((new Client(valuesArrayClients[i][0], Integer.parseInt(valuesArrayClients[i][1]))));
        }
            // Dla listy klientow typu String
        /*for (Object obj : jsonArrayClients) {
            JSONObject jsonObject = (JSONObject) obj;
            String clientName = (String) jsonObject.get("clientName");
            int clientAccBalance = Integer.parseInt(jsonObject.get("clientAccBalance").toString());

            // Dodanie imienia klienta do listy (jako tekst)
            clients.add(clientName);
            System.out.println("Załadowano klienta: " + clientName + ", saldo: " + clientAccBalance);
        }

           updateClientMenu();*/
        }catch (Exception e) {
            showAlert(e.getMessage(), "Failed to load data.");
        }
    }
    public void addBalance() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add balance");
        dialog.setHeaderText("Add balance");
        dialog.setContentText("Balance to add:");

        dialog.showAndWait().ifPresent(balanceStr -> {
            int balance;
            try{
                balance = Integer.parseInt(balanceStr);
            }catch(NumberFormatException e){
                showAlert("Invalid balance", "Please enter a valid balance.");
                return;
            }
            if(balance <= 0){
                showAlert("Invalid balance", "Please enter a valid balance.");
                return;
            }
            selectedClient.addBalance(balance);
            update();
        });
    }
    public void saveToFile() {
        for(Category category : categories) {
            category.saveToFile();
        }
        try(FileWriter file = new FileWriter("src/main/resources/Database/clients.json")) {
            JSONArray clientsArray = new JSONArray();
            for (Client client : clients) {
                clientsArray.add(client.toJSONObj());
            }
            file.write(clientsArray.toJSONString());
            file.flush();
        }catch (IOException e) {
            showAlert(e.getMessage(), "Failed to save data.");
        }
    }
}
