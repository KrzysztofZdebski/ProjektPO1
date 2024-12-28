module org.main.gamblingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens org.main.gamblingapp to javafx.fxml;
    exports org.main.gamblingapp;
}