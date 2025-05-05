module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires java.sql;
    requires java.logging;
    requires java.desktop;
    requires itextpdf;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires jdk.jsobject;

    opens org.example to javafx.fxml;
    opens org.example.controller to javafx.fxml;
    opens org.example.model to javafx.base;
    
    exports org.example;
    exports org.example.controller;
    exports org.example.model;
} 