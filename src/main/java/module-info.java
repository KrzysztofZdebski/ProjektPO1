module org.main.gamblingapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.main.gamblingapp to javafx.fxml;
    exports org.main.gamblingapp;
}