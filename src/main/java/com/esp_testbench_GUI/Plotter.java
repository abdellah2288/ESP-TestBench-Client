package com.esp_testbench_GUI;

import com.esp_testbench_Logic.etbPlotGenerator;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Plotter
{
    static private boolean firstRun = true;
    static private VBox baseContainer = new VBox();
    static private LineChart<Number,Number> plot = null;
    static private Scene rootScene = new Scene(baseContainer, Color.rgb(250,250,250));
    static private Stage rootStage = new Stage();
    static void launchPlotter(ArrayList<Pair<String,Float>> dataSet,List<String> measurements)
    {
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
            plot = etbPlotGenerator.generatePlot(dataSet,measurements);

            baseContainer.getChildren().add(plot);
            rootStage.setScene(rootScene);
            rootStage.setResizable(true);
            rootStage.setTitle("Serial Plotter");
            rootStage.setWidth(640);
            rootStage.setHeight(480);
            firstRun = false;
        }
        rootStage.show();
    }
    static public boolean isOpen()
    {
        return rootStage.isShowing();
    }
    static public void updatePlotter(ArrayList<Pair<String,Float>> dataSet, List<String> measurements)
    {
        List<XYChart.Series> refreshedSeries = etbPlotGenerator.generateDataSeries(dataSet,measurements);
        for(int i = 0;i < refreshedSeries.size();i++)
        {
            if(refreshedSeries.get(i).getData().size() > 0)
            {
                if(plot.getData().size() > 0) plot.getData().get(i).getData().addAll(refreshedSeries.get(i).getData());
                else plot.getData().add(refreshedSeries.get(i));
            }
        }
    }
    static public void clearPlot()
    {
        for(var series : plot.getData())
        {
            series.getData().clear();
        }
    }
}
