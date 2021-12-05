package edu.gatech.cs6301;

import java.io.*;
import java.util.*;

public class ReadProperties {
    public static Properties readPropertiesFile(String fileName) {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);

        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try{
                fis.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return prop;
    }
}