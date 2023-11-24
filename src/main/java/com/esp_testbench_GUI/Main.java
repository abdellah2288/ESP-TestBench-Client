package com.esp_testbench_GUI;

import com.esp_testbench_Logic.programLoop;
import com.fazecast.jSerialComm.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;


import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.*;
import java.util.*;

import static com.esp_testbench_Logic.etbParser.parseConfigFile;
import static com.esp_testbench_Logic.etbParser.parseSerialIn;


public class Main extends Application
{
    private Map<String,String> configMap = new HashMap<>()
    {{
        put("dht11_tem_tag","[DHT11-Temp]");
        put("dht11_hum_tag","[DHT11-Hum]");
        put("baudrate","115200");
        put("config_path","/home/abdellah/.config/etbConf.conf");
    }};

    static public ArrayList<Pair<String,Float>> sensorReadings = new ArrayList<>();
    private final MenuBar topBar = new MenuBar();
    private final Menu file = new Menu("File");
    private final Menu tools = new Menu("Tools");
    private final MenuItem console = new MenuItem("Console");
    private final MenuItem communications = new MenuItem("Communications analyzer");
    private final MenuItem export = new MenuItem("Export to csv");
    private final MenuItem configuration = new MenuItem("Configuration");

    private final BorderPane baseContainer = new BorderPane();
    private SerialPort previousPort = null;
    private SerialPort currentPort = null;
    private final GridPane rootGrid = new GridPane();
    private final Label sensorLabel_1 = new Label();
    private final Label sensorLabel_2 = new Label();
    private final Circle connectIndicator = new Circle(){{
       setRadius(10);
       setFill(Color.RED);
    }};
    private final Scene rootScene = new Scene(baseContainer, Color.rgb(250,250,250));
    /**
     * Buttons
     */
    private final Button connectButton = new Button("Connect");
    private final Button disconnectButton = new Button("Disconnect");
    /**
     * Combo boxes
     *
     */

    ChoiceBox<Pair<String,SerialPort>> serialDevices = new ChoiceBox<>();

    public static void main(String[] args)
    {
        Application.launch(args);
    }
    @Override
    public void start(Stage rootStage) throws Exception
    {
        rootStage.setScene(rootScene);
        rootStage.setResizable(false);
        rootStage.setTitle("ESP-TestBench demo");
        rootStage.show();
        rootStage.setHeight(200);
        rootStage.setWidth(380);

        refreshSerialList();
        loadConfig();
        attachHandlers();

        populateRootGrid();
        setConstraints();

        populateMenuBar();

        baseContainer.setCenter(rootGrid);
        baseContainer.setTop(topBar);
        new Thread(() ->
        {
            while (true)
            {
                Platform.runLater(
                        new programLoop() { public void run() {
                            if(sensorReadings.size() > 200) sensorReadings.clear();
                            updateConsole();
                            if(currentPort != null && currentPort.isOpen())
                            {
                                connectIndicator.setFill(Color.LIMEGREEN);
                            }
                            else
                            {
                                connectIndicator.setFill(Color.RED);
                            }
                        }});

                try
                {
                    System.gc();
                    Thread.sleep(2000);
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }


    private void refreshSerialList()
    {
        List<Pair<String,SerialPort>> tempList = new ArrayList<>();
        SerialPort[] portList = SerialPort.getCommPorts();

        for(var port : portList)
        {
            tempList.add(new Pair<>(port.getSystemPortName().toString(),port));
        }
        serialDevices.getItems().setAll(tempList);
    }
    private void selectedSerialPort(Event event)
    {
        SerialPort selected =serialDevices.getSelectionModel().getSelectedItem().getValue();

        if(selected != null)
        {
            selected.setBaudRate(Integer.valueOf(configMap.get("baudrate")));
            selected.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED);
            selected.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
            selected.setParity(SerialPort.NO_PARITY);
            previousPort = currentPort;
            currentPort = selected;
        }

    }

    private void updateConsole()
    {
        if(this.currentPort != null && this.currentPort.isOpen())
        {
            String[] sensorIdentifierlist = {configMap.get("dht11_tem_tag"),configMap.get("dht11_hum_tag")};
            List<Pair<String,Float>> list = parseSerialIn(this.currentPort,sensorIdentifierlist);
            if(list != null && list.size() > 0 )
            {
                for(Pair<String,Float> pair : list)
                {
                    if(pair.getKey().equals(configMap.get("dht11_tem_tag")))
                    {
                        sensorLabel_1.setText("[T]: " + pair.getValue().toString());
                        sensorReadings.add(pair);
                    }
                    if(pair.getKey().equals(configMap.get("dht11_hum_tag")))
                    {
                        sensorLabel_2.setText("[H]: " + pair.getValue().toString());
                        sensorReadings.add(pair);
                    }
                }
            }
        }
    }

    private void attachHandlers()
    {
        configuration.setOnAction(e -> {
            Config.launch(configMap.get("config_path"),configMap);
            loadConfig();
        }
        );
        export.setOnAction(this::exportToCSV);
        serialDevices.setConverter( new StringConverter<Pair<String,SerialPort>>() {
            @Override
            public String toString(Pair<String, SerialPort> pair)
            {
                return pair == null ? null : pair.getKey();
            }

            @Override
            public Pair<String, SerialPort> fromString(String string)
            {
                return null;
            }
        });



        serialDevices.setOnMouseClicked(x -> this.refreshSerialList());
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

        serialDevices.setOnAction(this::selectedSerialPort);
    }
    private void populateRootGrid()
    {
        try
        {
            Image image = new Image(new FileInputStream("/home/abdellah/Pictures/dht11_icon.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(80);
            imageView.setFitWidth(80);
            rootGrid.add(imageView,0,1);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }


        GridPane.setHgrow(serialDevices,Priority.NEVER);

        rootGrid.add(serialDevices,0,0,2,1);
        rootGrid.add(connectIndicator,2,0);
        rootGrid.add(connectButton,3,0,1,1);
        rootGrid.add(disconnectButton,5,0,1,1);

        rootGrid.add(sensorLabel_1,3,1);
        rootGrid.add(sensorLabel_2,5,1);


        rootGrid.setHgap( 4 );
        rootGrid.setVgap( 4 );
    }
    private void setConstraints()
    {
        ColumnConstraints colConsts = new ColumnConstraints();

        colConsts.setHgrow(Priority.ALWAYS);
        colConsts.setMinWidth(Control.USE_PREF_SIZE);

        RowConstraints rowConsts = new RowConstraints();
        rowConsts.setVgrow(Priority.SOMETIMES);

        for(int i = 0; i < rootGrid.getColumnCount();i++)
        {
            rootGrid.getColumnConstraints().add(colConsts);
        }
        for(int i = 0; i < rootGrid.getRowCount();i++)
        {
            rootGrid.getRowConstraints().add(rowConsts);
        }
    }

    private void populateMenuBar()
    {
        topBar.getMenus().addAll(file,tools);
        tools.getItems().addAll(console,communications);
        file.getItems().addAll(export,configuration);
    }

    void exportToCSV(Event e)
    {
        boolean ping = true;
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("/home/abdellah/sensorReadings.csv"));
            writer.write("DHT-11 temperature, DHT-11 Humidity \n");
            for(Pair<String,Float> pair : sensorReadings)
            {
                if(!(ping ^ pair.getKey().equals(configMap.get("dht11_hum_tag")))) continue;
                writer.write(ping ? pair.getValue().toString() + ',' : pair.getValue().toString() + '\n');
                ping = !ping;
            }
            writer.close();
        }
        catch(Exception exception)
        {
            System.out.println(exception.getStackTrace());
        }
    }

    int loadConfig()
    {
        Set<String> keySet = configMap.keySet();
        String[] confIdentifierList = new String[keySet.size()];
        int counter = 0;
        for(String key : configMap.keySet())
        {
            confIdentifierList[counter] = key;
            counter++;
        }
        List<Pair<String,String>> parsedConfig = parseConfigFile(configMap.get("config_path"),confIdentifierList);

        if(parsedConfig == null) return -1;
        for(Pair<String,String> pair : parsedConfig)
        {
            if(configMap.containsKey(pair.getKey()))
            {
                configMap.put(pair.getKey(),pair.getValue());
            }
        }
        return 0;
    }
}