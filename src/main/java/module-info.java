module com.example.esptestbenchclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.esptestbenchclient to javafx.fxml;
    exports com.example.esptestbenchclient;
}