package org.main.gamblingapp;

import Interfaces.Listener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private final StringProperty eventName;
    private final StringProperty eventDate;
    private final StringProperty participants;
    private final StringProperty bet;
    private final List<Listener> listenerList = new ArrayList<>();

    public Event(String eventName, String eventDate, String participants, String bet) {
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDate = new SimpleStringProperty(eventDate);
        this.participants = new SimpleStringProperty(participants);
        this.bet = new SimpleStringProperty(bet);
    }

    public String getEventName() {
        return eventName.get();
    }

    public StringProperty eventNameProperty() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate.get();
    }

    public StringProperty eventDateProperty() {
        return eventDate;
    }

    public String getParticipants() {
        return participants.get();
    }

    public StringProperty participantsProperty() {
        return participants;
    }

    public String getBet() {
        return bet.get();
    }

    public StringProperty betProperty() {
        return bet;
    }
}
