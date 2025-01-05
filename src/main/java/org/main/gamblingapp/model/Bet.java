package org.main.gamblingapp.model;

public class Bet {
    private Event event;
    private int amount;
    private String team;

    public Bet(Event event, int amount, String team) {
        this.event = event;
        this.amount = amount;
        this.team = team;
    }

    public void increaseBet(int amount){this.amount += amount;}
    public Event getEvent() {
        return event;
    }
    public int getAmount() {
        return amount;
    }
    public String getTeam() {return team;}
}