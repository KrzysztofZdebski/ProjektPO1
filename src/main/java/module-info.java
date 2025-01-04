module org.main.gamblingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires jdk.compiler;


    opens org.main.gamblingapp to javafx.fxml;
    exports org.main.gamblingapp;
}