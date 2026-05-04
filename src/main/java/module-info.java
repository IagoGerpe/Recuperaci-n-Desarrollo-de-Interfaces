module com.recuperacion.carmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.recuperacion.carmanager to javafx.fxml;
    opens com.recuperacion.carmanager.controller to javafx.fxml;

    exports com.recuperacion.carmanager;
    exports com.recuperacion.carmanager.controller;
    exports com.recuperacion.carmanager.utils;
    exports com.recuperacion.carmanager.dao;
    exports com.recuperacion.carmanager.model;
}