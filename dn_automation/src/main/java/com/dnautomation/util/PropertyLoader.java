package main.java.com.dnautomation.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class PropertyLoader {

    public static HashMap<String,String> getPropertiesData(String filepath) throws IOException {
        System.out.println("file path : "+filepath);
        FileReader reader=new FileReader(filepath);
        HashMap<String, String> keyValueMap = new HashMap<>();
        Properties p=new Properties();
        p.load(reader);
        for (String key : p.stringPropertyNames()) {
            String value = p.getProperty(key);
            keyValueMap.put(key, value);
        }
        return keyValueMap;
    }
}
