module com.recuperacion.carmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.recuperacion.carmanager to javafx.fxml;

    exports com.recuperacion.carmanager;
}
