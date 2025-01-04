package org.main.gamblingapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;

import java.io.FileWriter;
import java.io.IOException;

public class Category {
    private ObservableList<Event> events = FXCollections.observableArrayList();
    private String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }
    Category(String categoryName, ObservableList<Event> events) {
        this.categoryName = categoryName;
        this.events = events;
    }

    public boolean equals(String str) {
        return categoryName.equals(str);
    }
    public void saveToFile() {
        JSONArray jsonArray = new JSONArray();
        for (Event event : events) {
            jsonArray.add(event.toJSONObj());
        }
        try(FileWriter file = new FileWriter("src/main/resources/Database/" + categoryName + ".json")){
            file.write(jsonArray.toJSONString());
            file.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getCategoryName() {return categoryName;}
    public ObservableList<Event> getEvents() {return events;}
    public void addEvents(ObservableList<Event> events) {this.events = events;}
    public void setName(String name) {this.categoryName = name;}
}
