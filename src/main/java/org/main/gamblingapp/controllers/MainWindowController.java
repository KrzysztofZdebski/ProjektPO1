package org.main.gamblingapp.controllers;

import javafx.scene.control.Label;
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
import java.util.HashMap;
import java.util.Map;

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
//        events.add(new Event("Event1", "2026-01-01", new String[]{"Participant A", "Participant B"}, new Integer[]{8546,6742}));
//        events.getFirst().addListener(this);
//        events.add(new Event("Event2", "2024-01-02", new String[]{"Participant C", "Participant D"}, new Integer[]{0,100}));
//        events.get(1).addListener(this);
//        categories.add(new Category("cat1", events));
//        categories.add(new Category("cat2"));
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
        if(categoryBox.getValue() != null) {
            events = categoryBox.getValue().getEvents();
        }
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
        try {
            Map<String, Event> eventMap = new HashMap<>();

            // cycling
            ObservableList<Event> cyclingEvents = FXCollections.observableArrayList();
            JSONArray cyclingData = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/cycling.json").toURI())));
            for (Object obj : cyclingData) {
                JSONObject jsonObject = (JSONObject) obj;
                String eventName = (String) jsonObject.get("eventName");
                String eventDate = (String) jsonObject.get("eventDate");
                String participant0 = (String) jsonObject.get("participant0");
                int bet0 = Integer.parseInt(jsonObject.get("bet0").toString());
                String participant1 = (String) jsonObject.get("participant1");
                int bet1 = Integer.parseInt(jsonObject.get("bet1").toString());

                Event event = new Event(eventName, eventDate, new String[]{participant0, participant1}, new Integer[]{bet0, bet1});
                event.addListener(this);
                cyclingEvents.add(event);
                eventMap.put(eventName, event);

            }
            categories.add(new Category("Cycling", cyclingEvents));
            //football
            ObservableList<Event> footballEvents = FXCollections.observableArrayList();
            JSONArray footballData = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/football.json").toURI())));
            for (Object obj : footballData) {
                JSONObject jsonObject = (JSONObject) obj;
                String eventName = (String) jsonObject.get("eventName");
                String eventDate = (String) jsonObject.get("eventDate");
                String participant0 = (String) jsonObject.get("participant0");
                int bet0 = Integer.parseInt(jsonObject.get("bet0").toString());
                String participant1 = (String) jsonObject.get("participant1");
                int bet1 = Integer.parseInt(jsonObject.get("bet1").toString());

                Event event = new Event(eventName, eventDate, new String[]{participant0, participant1}, new Integer[]{bet0, bet1});
                event.addListener(this);
                footballEvents.add(event);
                eventMap.put(eventName, event);
            }
            categories.add(new Category("Football", footballEvents));
            //athletics
            ObservableList<Event> athleticsEvents = FXCollections.observableArrayList();
            JSONArray athleticsData = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/athletics.json").toURI())));
            for (Object obj : athleticsData) {
                JSONObject jsonObject = (JSONObject) obj;
                String eventName = (String) jsonObject.get("eventName");
                String eventDate = (String) jsonObject.get("eventDate");
                String participant0 = (String) jsonObject.get("participant0");
                int bet0 = Integer.parseInt(jsonObject.get("bet0").toString());
                String participant1 = (String) jsonObject.get("participant1");
                int bet1 = Integer.parseInt(jsonObject.get("bet1").toString());

                Event event = new Event(eventName, eventDate, new String[]{participant0, participant1}, new Integer[]{bet0, bet1});
                event.addListener(this);
                athleticsEvents.add(event);
                eventMap.put(eventName, event);
            }
            categories.add(new Category("Athletics", athleticsEvents));
            //basketball
            ObservableList<Event> basketballEvents = FXCollections.observableArrayList();
            JSONArray basketballData = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/basketball.json").toURI())));
            for (Object obj : basketballData) {
                JSONObject jsonObject = (JSONObject) obj;
                String eventName = (String) jsonObject.get("eventName");
                String eventDate = (String) jsonObject.get("eventDate");
                String participant0 = (String) jsonObject.get("participant0");
                int bet0 = Integer.parseInt(jsonObject.get("bet0").toString());
                String participant1 = (String) jsonObject.get("participant1");
                int bet1 = Integer.parseInt(jsonObject.get("bet1").toString());

                Event event = new Event(eventName, eventDate, new String[]{participant0, participant1}, new Integer[]{bet0, bet1});
                event.addListener(this);
                basketballEvents.add(event);
                eventMap.put(eventName, event);
            }
            categories.add(new Category("Basketball", basketballEvents));
            //boxing
            ObservableList<Event> boxingEvents = FXCollections.observableArrayList();
            JSONArray boxingData = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/boxing.json").toURI())));
            for (Object obj : boxingData) {
                JSONObject jsonObject = (JSONObject) obj;
                String eventName = (String) jsonObject.get("eventName");
                String eventDate = (String) jsonObject.get("eventDate");
                String participant0 = (String) jsonObject.get("participant0");
                int bet0 = Integer.parseInt(jsonObject.get("bet0").toString());
                String participant1 = (String) jsonObject.get("participant1");
                int bet1 = Integer.parseInt(jsonObject.get("bet1").toString());

                Event event = new Event(eventName, eventDate, new String[]{participant0, participant1}, new Integer[]{bet0, bet1});
                event.addListener(this);
                boxingEvents.add(event);
                eventMap.put(eventName, event);
            }
            categories.add(new Category("Boxing", boxingEvents));
            //swimming
            ObservableList<Event> swimmingEvents = FXCollections.observableArrayList();
            JSONArray swimmingData = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/swimming.json").toURI())));
            for (Object obj : swimmingData) {
                JSONObject jsonObject = (JSONObject) obj;
                String eventName = (String) jsonObject.get("eventName");
                String eventDate = (String) jsonObject.get("eventDate");
                String participant0 = (String) jsonObject.get("participant0");
                int bet0 = Integer.parseInt(jsonObject.get("bet0").toString());
                String participant1 = (String) jsonObject.get("participant1");
                int bet1 = Integer.parseInt(jsonObject.get("bet1").toString());

                Event event = new Event(eventName, eventDate, new String[]{participant0, participant1}, new Integer[]{bet0, bet1});
                event.addListener(this);
                swimmingEvents.add(event);
                eventMap.put(eventName, event);
            }
            categories.add(new Category("Swimming", swimmingEvents));
            //tennis
            ObservableList<Event> tennisEvents = FXCollections.observableArrayList();
            JSONArray tennisData = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/tennis.json").toURI())));
            for (Object obj : tennisData) {
                JSONObject jsonObject = (JSONObject) obj;
                String eventName = (String) jsonObject.get("eventName");
                String eventDate = (String) jsonObject.get("eventDate");
                String participant0 = (String) jsonObject.get("participant0");
                int bet0 = Integer.parseInt(jsonObject.get("bet0").toString());
                String participant1 = (String) jsonObject.get("participant1");
                int bet1 = Integer.parseInt(jsonObject.get("bet1").toString());

                Event event = new Event(eventName, eventDate, new String[]{participant0, participant1}, new Integer[]{bet0, bet1});
                event.addListener(this);
                tennisEvents.add(event);
                eventMap.put(eventName, event);
            }
            categories.add(new Category("Tennis", tennisEvents));

            JSONArray jsonArrayClients = (JSONArray) new JSONParser().parse(new FileReader(new File(MainWindowController.class.getResource("/Database/clients.json").toURI())));
            for (Object obj : jsonArrayClients) {
                JSONObject jsonObject = (JSONObject) obj;
                String clientName = (String) jsonObject.get("clientName");
                int clientAccBalance = Integer.parseInt(jsonObject.get("clientAccBalance").toString());
                Client client = new Client(clientName, clientAccBalance);

                JSONArray bets = (JSONArray) jsonObject.get("bets");
                for (Object bet : bets) {
                    JSONObject betObject = (JSONObject) bet;
                    String eventName = (String) betObject.get("eventName");
                    int betAmount = Integer.parseInt(betObject.get("amount").toString());
                    String betTeam = (String) betObject.get("team");
                    Event event = eventMap.get(eventName);
                    if (event != null) {
                        client.getBets().add(new Bet(event, betAmount, betTeam));
                        event.addListener(client);
                    } else {
                        showAlert("Event not found", "Event not found for bet: " + eventName);
                    }
                }
//                for (Object keyObj : jsonObject.keySet()) {
//                    String key = (String) keyObj;
//                    if (!key.equals("clientName") && !key.equals("clientAccBalance")) {
//                        String eventName = key;
//                        int betAmount = Integer.parseInt(jsonObject.get(key).toString());
//
//                        Event event = eventMap.get(eventName);
//                        if (event != null) {
//                            Bet bet = new Bet(event, betAmount, "Unknown");
//                            client.getBets().add(bet);
//                            event.addListener(client);
//                        } else {
//                            System.err.println("Event not found for bet: " + eventName);
//                        }
//                    }
//                }
                clients.add(client);
            }
        } catch (Exception e) {
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
    private void resizeColumns() {

    }
}
