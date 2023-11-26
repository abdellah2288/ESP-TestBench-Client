package com.esp_testbench_GUI;


import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config
{
    static boolean firstRun = true;
    static final Stage rootStage = new Stage();
    static final GridPane baseContainer = new GridPane();
    static final Scene rootScene = new Scene(baseContainer, Color.rgb(250, 250, 250));
    static private Map<String,String> tempConfigParams = new HashMap<>();
    static private final Label baudrateLabel = new Label("Baudrate");
    static private final Label dht11TempLabel = new Label("Temperature tag");
    static private final Label dht11HumLabel = new Label("Humidity tag");
    static private final Label configPLabel = new Label("Config file");
    static private final Label consoleBufferSizeLabel = new Label("Console buffer size");
    static private final Label csvSampleSizeLabel = new Label("Sample size");
    static private final Label csvAutogenLabel = new Label("Autogen CSV files");
    static private ChoiceBox<Pair<String,Integer>> baudrates = new ChoiceBox<>();
    static private final TextField dhtTempField = new TextField();
    static private final TextField dhtHumField = new TextField();
    static private final TextField configPField = new TextField();
    static private final TextField consoleBufferField = new TextField();
    static private final TextField csvBufferField = new TextField();
    static private final CheckBox csvAutogenCheck = new CheckBox();
    static private final Button applyButton = new Button("Apply");

    static public void launch(String configPath,Map<String,String> configParams)
    {
        initBaudRateList();
        if(firstRun)
        {
            try
            {
                rootStage.getIcons().add(new Image(new FileInputStream("/home/abdellah/IdeaProjects/ESP-TestBench Client/static/esp32.png")));
            }
            catch(Exception e)
            {
                System.out.println(e.getStackTrace());
            }
            dhtHumField.setText(configParams.get("dht11_hum_tag"));
            dhtTempField.setText(configParams.get("dht11_tem_tag"));
            configPField.setText(configParams.get("config_path"));
            consoleBufferField.setText(configParams.get("console_buffer_size"));
            csvBufferField.setText(configParams.get("csv_sample_size"));
            csvAutogenCheck.setSelected(Integer.valueOf(configParams.get("csv_autogen"))  == 1);
            csvAutogenCheck.setText("Autogenerate CSV files");
            applyButton.setOnMouseClicked(e -> writeConfig(configPath, configParams));

            baseContainer.add(baudrateLabel, 1, 0, 5, 1);
            baseContainer.add(baudrates, 5, 0);

            baseContainer.add(csvAutogenCheck, 1, 1);
            baseContainer.add(dht11HumLabel, 1, 2, 5, 1);
            baseContainer.add(dhtHumField, 1, 3, 5, 1);

            baseContainer.add(dht11TempLabel, 1, 4, 5, 1);
            baseContainer.add(dhtTempField, 1, 5, 5, 1);

            baseContainer.add(configPLabel, 1, 6, 5, 1);
            baseContainer.add(configPField, 1, 7, 5, 1);

            baseContainer.add(consoleBufferSizeLabel, 1, 8, 5, 1);
            baseContainer.add(consoleBufferField, 1, 9, 5, 1);

            baseContainer.add(csvSampleSizeLabel, 1, 10, 5, 1);
            baseContainer.add(csvBufferField, 1, 11, 5, 1);

            baseContainer.add(applyButton, 10, 13);

            ColumnConstraints colConsts = new ColumnConstraints();

            colConsts.setHgrow(Priority.ALWAYS);
            colConsts.setMinWidth(Control.USE_PREF_SIZE);

            RowConstraints rowConsts = new RowConstraints();
            rowConsts.setVgrow(Priority.SOMETIMES);
            rowConsts.setMinHeight(Control.USE_PREF_SIZE);
            for (int i = 0; i < baseContainer.getColumnCount(); i++) {
                baseContainer.getColumnConstraints().add(colConsts);
            }
            for (int i = 0; i < baseContainer.getRowCount(); i++) {
                baseContainer.getRowConstraints().add(rowConsts);
            }


            rootStage.setScene(rootScene);
            rootStage.setResizable(false);
            rootStage.setTitle("Configuration");
            rootStage.setWidth(480);
            rootStage.setHeight(350);
            firstRun = false;
        }
        rootStage.show();
    }
    static private void initBaudRateList()
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
        baudrates.getItems().addAll(baudRates);
        baudrates.setConverter( new StringConverter<Pair<String,Integer>>() {
            @Override
            public String toString(Pair<String, Integer> pair)
            {
                return pair == null ? null : pair.getKey();
            }

            @Override
            public Pair<String, Integer> fromString(String string)
            {
                return null;
            }
        });

    }
    static public void writeConfig(String configPath,Map<String,String> configParams)
    {
        if(baudrates.getSelectionModel() != null && baudrates.getSelectionModel().getSelectedItem() != null)
        {
            configParams.put("baudrate",baudrates.getSelectionModel().getSelectedItem().getValue().toString());
        }

        if(dhtHumField.getText().length() > 0) configParams.put("dht11_hum_tag",dhtHumField.getText());
        if(dhtTempField.getText().length() > 0) configParams.put("dht11_tem_tag",dhtTempField.getText());
        if(configPField.getText().length() > 0) configParams.put("config_path",configPField.getText());
        if(csvBufferField.getText().length() > 0) configParams.put("csv_sample_size",csvBufferField.getText());
        if(consoleBufferField.getText().length() > 0) configParams.put("console_buffer_size",consoleBufferField.getText());
        configParams.put("csv_autogen",csvAutogenCheck.isSelected() ? "1": "0" );
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configPath));
            for(String key : configParams.keySet())
            {
                writer.write(key + " " + configParams.get(key) + ";\n");
            }
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getStackTrace());
        }
    }

}
