module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires java.net.http;
    requires itextpdf;
    requires java.desktop;

    opens org.example to javafx.fxml;
    opens org.example.controller to javafx.fxml;
    opens org.example.model to javafx.fxml;
    opens org.example.dao to javafx.fxml;
    opens org.example.utils to javafx.fxml;
    exports org.example;
    exports org.example.controller;
    exports org.example.model;
    exports org.example.dao;
    exports org.example.utils;
}