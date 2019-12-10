package com.mxgraph.examples.swing.db;

import com.mxgraph.examples.swing.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class DBConfig {

    private static InputStream in = DBConfig.class.getResourceAsStream("/config/jdbc.properties");
    public static String driver;
    public static String url;
    public static String user;
    public static String passwd;
    public static String table; // data table name
    public static String rttable; // data table name
    public static String id_field; // deviceId
    public static String time_field; // updatetime
    public static String data_field; // json data

    static {
        String fileContent = null;
        try {
            fileContent = FileUtil.readFile(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> dbMap = new HashMap();
        if (fileContent != null) {
            String[] lines = fileContent.split("\n");
            if (lines.length >= 4) {
                for (String line : lines) {
                    String[] kvPair = line.split("=");
                    if (kvPair.length != 2) {
                        continue;
                    }
                    dbMap.put(kvPair[0], kvPair[1]);
                }
            }
        }
        driver = dbMap.get("driver");
        url = dbMap.get("url");
        user = dbMap.get("user");
        passwd = dbMap.get("passwd");
        table = dbMap.get("table");
        rttable = dbMap.get("rttable");
        id_field = dbMap.get("id_field");
        time_field = dbMap.get("time_field");
        data_field = dbMap.get("data_field");
    }

}
