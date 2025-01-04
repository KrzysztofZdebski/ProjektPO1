package org.main.gamblingapp;

import Interfaces.Listener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Event {
    private final StringProperty eventName;
    private final StringProperty eventDate;
    private final ObservableList<String> participants = FXCollections.observableArrayList();
    private final ObservableList<Integer> bet = FXCollections.observableArrayList();
    private final ObservableList<Double> odds = FXCollections.observableArrayList();
    private final List<Listener> listeners = new ArrayList<>();
    private final double maxOdds = 10.0;
    private final double ownerMargin = 1.05;

    public Event(String eventName, String eventDate, String[] participants, Integer[] bet) throws IllegalArgumentException {
        if(participants.length != 2 || bet.length != 2) throw new IllegalArgumentException();
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDate = new SimpleStringProperty(eventDate);
        this.participants.addAll(FXCollections.observableArrayList(participants));
        this.bet.addAll(FXCollections.observableArrayList(bet));
        this.odds.addAll(FXCollections.observableArrayList(0.0, 0.0));
        countOdds();
        notifyListeners();
    }

    private void countOdds() {
        if(bet.getFirst() == 0 || bet.getLast() == 0){
            if(bet.getFirst() == 0 && bet.getLast() == 0){
                odds.addAll(FXCollections.observableArrayList(1.0, 1.0));
                return;
            }
            odds.set(bet.getFirst() == 0 ? 0 : 1, maxOdds);
            odds.set(bet.getFirst() == 0 ? 1 : 0, 1.0);
            return;
        }
        double totalBet = 0;
        for(Integer i : bet) {totalBet += i;}
        for(int i = 0; i < bet.size(); i++) {
            double calcOdds = (double) round(100 * ownerMargin * totalBet / bet.get(i)) / 100.0;
            odds.set(i, Math.min(maxOdds, calcOdds));
        }
    }

    public void addBet(String participant, int bet) {
        int participantIdx = participants.indexOf(participant);
        this.bet.set(participantIdx, this.bet.get(participantIdx) + bet);
        countOdds();
        notifyListeners();
    }

    public String getEventName() {return eventName.get();}
    public StringProperty eventNameProperty() {return eventName;}
    public StringProperty eventDateProperty() {return eventDate;}
    public ObservableList<String> participantsList() {return participants;}
    public ObservableList<Integer> betList() {return bet;}
    public ObservableList<Double> oddsList() {return odds;}

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
    private void notifyListeners() {
        for (Listener listener : listeners) {
            listener.update();
        }
    }
}
