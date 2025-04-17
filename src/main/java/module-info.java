module PIDEV1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;

    opens chechia.tn to javafx.fxml;
    opens chechia.tn.controllers to javafx.fxml;

    exports chechia.tn;
    exports chechia.tn.controllers;
    exports chechia.tn.entities;
    exports chechia.tn.services;
}
