package org.main.gamblingapp;

public class Bet {
    private Event event;
    private int amount;

    public Bet(Event event, int amount) {
        this.event = event;
        this.amount = amount;
    }

    public Event getEvent() {
        return event;
    }

    public int getAmount() {
        return amount;
    }
}