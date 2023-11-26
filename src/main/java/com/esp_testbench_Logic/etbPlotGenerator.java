package com.esp_testbench_Logic;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class etbPlotGenerator
{
    static public int lastCount = 0;
    static public LineChart<Number,Number> generatePlot(ArrayList<Pair<String,Float>> dataSet, List<String> measurements)
    {
        if(dataSet == null) return null;

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Number of readings");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(measurements.stream().reduce("",(x,y) -> x+"/"+y));

        LineChart<Number,Number> generatedChart = new LineChart<>(xAxis,yAxis);

        for(var dataSeries : generateDataSeries(dataSet,measurements))
        {
            generatedChart.getData().add(dataSeries);
        }

        return generatedChart;
    }
    static public List<XYChart.Series> generateDataSeries(ArrayList<Pair<String,Float>> dataSet,List<String> measurements)
    {
        List<XYChart.Series> dataSeries = new ArrayList<>();
        int counter = 0;
        for(var measurement : measurements) dataSeries.add(new XYChart.Series());
        for(Pair<String,Float> data : dataSet)
        {
            if(measurements.contains(data.getKey()))
            {
                if(counter > lastCount)
                {
                    dataSeries.get(measurements.indexOf(data.getKey())).getData().add(new XYChart.Data(counter,data.getValue()));
                }
                counter++;
            }
        }
        lastCount = counter - 1;
        return dataSeries;
    }
}
