module diyetrehberi.diyetrehberi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens diyetrehberi.diyetrehberi to javafx.fxml;
    exports diyetrehberi.diyetrehberi;
}