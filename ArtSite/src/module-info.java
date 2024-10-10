module example{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens example.controller to javafx.fxml;
    exports example.controller;
    opens example.model to javafx.fxml;
    exports example.model;
    opens example.view to javafx.fxml;
    exports example;
    opens example to javafx.fxml;


}