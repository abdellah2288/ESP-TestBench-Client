package com.esp_testbench_Logic;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.ArrayList;

public class etbChartGenerator
{
    static public LineChart<Number,Number> generateChart(ArrayList<Pair<String,Number>> dataSet,String filter)
    {
        if(dataSet == null) return null;
        XYChart.Series dataSeries = new XYChart.Series();
        int counter = 0;

        for(Pair<String,Number> data : dataSet)
        {
            if(data.getKey().equals(filter))
            {
                dataSeries.getData().add(new XYChart.Data(counter,data.getValue()));
                counter++;
            }
        }
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number,Number> generatedChart = new LineChart<>(xAxis,yAxis);
        generatedChart.setTitle(filter);
        generatedChart.getData().add(dataSeries);
        return generatedChart;
    }
}
