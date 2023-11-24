module com.example.esptestbenchclient
{
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;


    opens com.esp_testbench_GUI to javafx.fxml;
    exports com.esp_testbench_GUI;
}