module com.example.noticias {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.noticias to javafx.fxml;
    exports com.example.noticias;
    exports com.example.noticias.controladores;
    opens com.example.noticias.controladores to javafx.fxml;
}