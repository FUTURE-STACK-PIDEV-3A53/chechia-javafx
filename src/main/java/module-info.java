module PIDEV1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens chechia.tn.controllers to javafx.fxml;

    exports chechia.tn.test;
}
