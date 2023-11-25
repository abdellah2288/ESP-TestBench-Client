package com.esp_testbench_GUI;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.LinkedList;

public class Console
{
    static private boolean firstRun = true;
    static private VBox baseContainer = new VBox();
    static private TextArea console = new TextArea();
    static private Scene rootScene = new Scene(baseContainer, Color.rgb(250,250,250));
    static private Stage rootStage = new Stage();
    static void launchConsole()
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
            console.setMaxHeight(Double.MAX_VALUE);
            console.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(console, Priority.ALWAYS);
            baseContainer.getChildren().add(console);
            console.setEditable(false);
            rootStage.setScene(rootScene);
            rootStage.setResizable(true);
            rootStage.setTitle("Console");
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
    static public void clearConsole()
    {
        console.setText("");
    }
    static public int getConsoleSize()
    {
        return console.getText().length();
    }
    static public void refreshConsole(LinkedList<Byte> inputStream)
    {
        char readChar;
        while(inputStream.size() > 0)
        {
            readChar = (char) (0xFF & inputStream.removeFirst() );
            console.appendText(String.valueOf(readChar));
        }
    }
}
