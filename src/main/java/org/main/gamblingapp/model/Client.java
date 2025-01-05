package org.main.gamblingapp.model;
import org.main.gamblingapp.exceptions.ClientException;
import org.main.gamblingapp.interfaces.Listener;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements Listener {
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
    public List<Bet> getBets() {return bets;}
    public void placeBet(Event event, int amount, String team) throws ClientException {
        if (amount > clientAccBalance) {
//            System.out.println("You don't have enough money to make a bet");
            throw new ClientException("Amount must be lower than the client balance");
        }
        for (Bet bet : bets) {
            if (bet.getEvent().equals(event.getEventName())) {
                if(!bet.getTeam().equals(team)) {
                    throw new ClientException("Bet does not match the team");
                }
                clientAccBalance -= amount;
                bet.increaseBet(amount);
                return;
            }
        }
        clientAccBalance -= amount;
        bets.add(new Bet(event, amount, team));
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
    public void update(){
        for(Bet bet : bets) {
            if(bet.getEvent().isFinished()){
                addBalance((int) (bet.getAmount() * bet.getEvent().oddsList().get(bet.getEvent().participantsList().indexOf(bet.getTeam()))));
                bets.remove(bet);
            }
        }
    }
    public JSONObject toJSONObj() {
        Map<String,String> map = new HashMap<>();
        map.put("clientName", clientName);
        map.put("clientAccBalance", String.valueOf(clientAccBalance));
        for(Bet bet : bets) {
            map.put(bet.getEvent().getEventName(), String.valueOf(bet.getAmount()));
        }
        return new JSONObject(map);
    }
}

