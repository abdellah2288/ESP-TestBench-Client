package com.esp_testbench_Logic;

import com.fazecast.jSerialComm.SerialPort;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class etbParser
{
    private static String currKeyword = null;
    static public List<Pair<String,String>> parseConfigFile(String confPath,String[] configIdentifierList)
    {
        try
        {
            byte[] readByte = new byte[1];
            char readChar;
            StringBuilder placeHolderStr = new StringBuilder();
            FileInputStream openConfig = new FileInputStream(confPath);
            List<Pair<String,String>> configParams = new ArrayList<>();
            String selected = null;
            while(openConfig.available() > 0)
            {
                openConfig.read(readByte);
                readChar = (char) (readByte[0] & 0xFF);
                if(Character.isWhitespace(readChar) && readChar != '\n')
                {
                    selected = matchedKeyword(placeHolderStr.toString(), configIdentifierList);
                    if(selected == null) return null;
                    placeHolderStr.delete(0,placeHolderStr.length());
                }
                else if(readChar == ';')
                {
                    configParams.add(new Pair<String,String>(selected,placeHolderStr.toString()));
                    placeHolderStr.delete(0,placeHolderStr.length());
                }
                else if( readChar != '\n')
                {
                    placeHolderStr.append(readChar);
                }
            }
            return configParams;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
    static public List<Pair<String,Float>> parseSerialIn(SerialPort port, String[] sensorIdentifierList, LinkedList<Byte> rawOutput)
    {
        try
        {
            byte[] readByte = new byte[1];
            char readChar;
            StringBuilder placeHolderStr = new StringBuilder();
            List<Pair<String,Float>> readingsList = new ArrayList<>();

            if(!port.isOpen()) return null;

            while(port.bytesAvailable() > 0)
            {
                port.readBytes(readByte,1);
                rawOutput.add(readByte[0]);
                readChar = (char) (readByte[0] & 0xFF);

                if(Character.isWhitespace(readChar))
                {
                    if(currKeyword == null)
                    {
                        currKeyword = matchedKeyword(placeHolderStr.toString(),sensorIdentifierList);
                    }
                    placeHolderStr.delete(0,placeHolderStr.length());
                }
                else if (readChar == ';')
                {
                    if(currKeyword != null)
                    {
                        readingsList.add(new Pair<>(currKeyword,Float.valueOf(placeHolderStr.toString())));
                        currKeyword = null;
                    }
                    placeHolderStr.delete(0,placeHolderStr.length());
                }
                else
                {
                    placeHolderStr.append(readChar);
                }
            }
            currKeyword = null;
            return readingsList;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
    private static String matchedKeyword(String word, String[] keywordList)
    {
        for(String str : keywordList)
        {
            if(str.equals(word)) return str;
        }
        return null;
    }
}
