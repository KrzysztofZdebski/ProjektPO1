package org.main.gamblingapp;

public class Bet {
    private Event event;
    private int amount;
    private String team;

    public Bet(Event event, int amount, String team) {
        this.event = event;
        this.amount = amount;
        this.team = team;
    }

    public Event getEvent() {
        return event;
    }
    public int getAmount() {
        return amount;
    }
    public String getTeam() {return team;}
}