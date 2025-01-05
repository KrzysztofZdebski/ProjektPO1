module org.main.gamblingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires jdk.compiler;
    requires java.desktop;


    opens org.main.gamblingapp to javafx.fxml;
    exports org.main.gamblingapp;
    exports org.main.gamblingapp.controllers;
    opens org.main.gamblingapp.controllers to javafx.fxml;
    exports org.main.gamblingapp.model;
    opens org.main.gamblingapp.model to javafx.fxml;
}