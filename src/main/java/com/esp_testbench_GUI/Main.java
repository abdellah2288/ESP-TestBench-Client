package com.esp_testbench;

import com.fazecast.jSerialComm.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.Event;

import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application
{
    private SerialPort previousPort = null;
    private SerialPort currentPort = null;
    private final GridPane rootGrid = new GridPane();
    private final Scene rootScene = new Scene(rootGrid, Color.rgb(250,250,250));
    /**
     * Buttons
     */
    private final Button testButton = new Button("test 1");
    private final Button testButton2 = new Button("test 2");
    private final Button testButton3 = new Button("test 3");
    private final Button submitButton = new Button("submit");
    private final Button clearButton = new Button("Clear");
    private final Button connectButton = new Button("Connect");
    private final Button disconnectButton = new Button("Disconnect");
    /**
     * text areas
     */
    private final TextField testInput = new TextField();
    private final TextArea testConsole = new TextArea();

    /**
     * Combo boxes
     *
     */
    ChoiceBox<Pair<String,Integer>> baudrates = new ChoiceBox<>();
    ChoiceBox<Pair<String,SerialPort>> serialDevices = new ChoiceBox<>();
    public static void main(String[] args)
    {
        Application.launch(args);
    }
    @Override
    public void start(Stage rootStage) throws Exception
    {
        initBaudRateList();
        refreshSerialList();
        connectButton.setOnMouseClicked(event ->
        {
            if(this.previousPort != null)
            {
                this.previousPort.closePort();
            }
            if(this.currentPort != null) this.currentPort.openPort();
        });
        disconnectButton.setOnMouseClicked(event ->{
            if(this.currentPort != null && this.currentPort.isOpen()) this.currentPort.closePort();
        });
        clearButton.setOnMouseClicked(mouseEvent -> {
            this.testConsole.clear();
        });

        serialDevices.setOnAction(this::selectedSerialPort);



        rootGrid.add(baudrates,0,0);
        rootGrid.add(serialDevices,2,0);
        rootGrid.add(connectButton,3,0);
        rootGrid.add(disconnectButton,4,0);
        rootGrid.setPadding(new Insets(10));
        rootGrid.setHgap( 4 );
        rootGrid.setVgap( 4 );
        rootGrid.add(testInput,0,1,4,1);
        rootGrid.add(submitButton,4,1,2,1);
        rootGrid.add(clearButton,4,2,2,1);
        rootGrid.add(testConsole,0,2,4,4);

        RowConstraints noResizeConst = new RowConstraints();
        noResizeConst.setVgrow(Priority.NEVER);

        ColumnConstraints colConsts = new ColumnConstraints();
        colConsts.setHgrow(Priority.ALWAYS);

        RowConstraints rowConsts = new RowConstraints();
        rowConsts.setVgrow(Priority.ALWAYS);

        for(int i = 0; i < rootGrid.getColumnCount();i++)
        {
            rootGrid.getColumnConstraints().add(colConsts);
        }
        for(int i = 0; i < rootGrid.getRowCount();i++)
        {
            rootGrid.getRowConstraints().add(rowConsts);
        }
        rootGrid.getRowConstraints().set(0, noResizeConst);
        rootGrid.getRowConstraints().set(1, noResizeConst);
        serialDevices.setMinWidth(Control.USE_PREF_SIZE);
        baudrates.setMinWidth(Control.USE_PREF_SIZE);
        testConsole.setEditable(false);

        rootStage.setScene(rootScene);
        rootStage.setTitle("ESP-TestBench demo");

        submitButton.setOnMouseClicked(this::submitButtonCallback);

        rootStage.show();
        new AnimationTimer()
        {
            @Override public void handle(long currentNanoTime)
            {

                refreshSerialList();
                updateConsole();
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                }
            }
        }.start();
    }

   private void submitButtonCallback(Event event)
    {
        this.testConsole.appendText(this.testInput.getText());
    }
    private void initBaudRateList()
    {
        List<Pair<String, Integer>> baudRates = new ArrayList<>();

        // Add supported baud rates for Linux
        baudRates.add(new Pair<>("B50", 50));
        baudRates.add(new Pair<>("B75", 75));
        baudRates.add(new Pair<>("B110", 110));
        baudRates.add(new Pair<>("B134", 134));
        baudRates.add(new Pair<>("B150", 150));
        baudRates.add(new Pair<>("B200", 200));
        baudRates.add(new Pair<>("B300", 300));
        baudRates.add(new Pair<>("B600", 600));
        baudRates.add(new Pair<>("B1200", 1200));
        baudRates.add(new Pair<>("B1800", 1800));
        baudRates.add(new Pair<>("B2400", 2400));
        baudRates.add(new Pair<>("B4800", 4800));
        baudRates.add(new Pair<>("B9600", 9600));
        baudRates.add(new Pair<>("B19200", 19200));
        baudRates.add(new Pair<>("B38400", 38400));
        baudRates.add(new Pair<>("B57600", 57600));
        baudRates.add(new Pair<>("B115200", 115200));
        baudRates.add(new Pair<>("B230400", 230400));
        baudRates.add(new Pair<>("B460800", 460800));
        baudRates.add(new Pair<>("B500000", 500000));
        baudRates.add(new Pair<>("B576000", 576000));
        baudRates.add(new Pair<>("B921600", 921600));
        this.baudrates.getItems().addAll(baudRates);
    }
    private void refreshSerialList()
    {
        List<Pair<String,SerialPort>> tempList = new ArrayList<>();
        for(var port : SerialPort.getCommPorts())
        {
            tempList.add(new Pair<>(port.toString(),port));
        }
        serialDevices.getItems().setAll(tempList);
    }
    private void selectedSerialPort(Event event)
    {
        System.out.println(event.toString());
        SerialPort selected =serialDevices.getSelectionModel().getSelectedItem().getValue();
        Integer baudrate = baudrates.getSelectionModel().getSelectedItem() == null ? 0 : baudrates.getSelectionModel().getSelectedItem().getValue();
        if(baudrate.intValue() != 0 && selected != null)
        {
            selected.setBaudRate(baudrate.intValue());
            selected.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED);
            selected.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
            selected.setParity(SerialPort.NO_PARITY);
            previousPort = currentPort;
            currentPort = selected;
        }
    }

    private void updateConsole()
    {
        if(this.testConsole.getText().length() > 2500) this.testConsole.clear();
        if(this.currentPort != null && this.currentPort.isOpen())
        {
            if(this.currentPort.bytesAvailable() != 0)
            {
                byte[] recievedBytes = new byte[this.currentPort.bytesAvailable()];
                this.currentPort.readBytes(recievedBytes,recievedBytes.length);
                String recievedMessage = new String(recievedBytes, StandardCharsets.UTF_8);
                this.testConsole.appendText(recievedMessage);
            }
        }
    }
}