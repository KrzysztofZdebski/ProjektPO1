package org.main.gamblingapp;
import java.util.ArrayList;
import java.util.List;

public class Client{
    private String clientName;
    private int clientAccBalance;
    private List<Bet> bets;
    public Client(String clientName, int clientAccBalance) {
        this.clientName = clientName;
        this.clientAccBalance = clientAccBalance;
        this.bets = new ArrayList<>();
    }
    public String getClientName() {
        return clientName;
    }
    public int getClientAccBalance() {
        return clientAccBalance;
    }
    public void placeBet(Event event, int amount) throws IllegalArgumentException {
        if (amount > clientAccBalance) {
//            System.out.println("You don't have enough money to make a bet");
            throw new IllegalArgumentException("Amount must be lower than the client balance");
        } else {
            clientAccBalance -= amount;
            bets.add(new Bet(event, amount));
        }
    }
    public void addBalance(int amount) {
        clientAccBalance += amount;
    }

    public void showBets() {
        if (bets.isEmpty()) {
            System.out.println("No bets found");
        } else {
            for (Bet bet : bets) {
                System.out.println("Event: " + bet.getEvent().getEventName() +
                        ", Amount: " + bet.getAmount());
            }
        }
    }

    public boolean equals(String clientName) {
        return this.clientName.equals(clientName);
    }
}

