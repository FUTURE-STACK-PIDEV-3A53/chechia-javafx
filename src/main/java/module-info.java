module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires javafx.media;
    requires java.sql;
    requires java.logging;
    
    opens org.example to javafx.fxml, javafx.graphics;
    opens org.example.controller to javafx.fxml;
    opens org.example.model to javafx.base;
    
    exports org.example;
    exports org.example.controller;
    exports org.example.model;
} 