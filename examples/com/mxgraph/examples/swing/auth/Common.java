package com.mxgraph.examples.swing.auth;

import com.mxgraph.examples.swing.frame.UploadMxeFileFrame;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

public class Common {
    // 开发环境url
    public static String devUrlPrefix = "";
    // 正式环境url
    public static String proUrlPrefix = "";
    public static String opensslPath = "";

    public static void init() {
        Properties prop = new Properties();
        try {
            //读取属性文件config.properties
            prop.load(Common.class.getResourceAsStream("/config/kong_conf.properties"));
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext()) {
                String key = it.next();
                System.out.println(key + ":  " + prop.getProperty(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        devUrlPrefix = prop.getProperty("devUrlPrefix");
        proUrlPrefix = prop.getProperty("proUrlPrefix");
        opensslPath = prop.getProperty("opensslPath");
    }

    public static String getFileContent(FileInputStream fis, String encoding) throws IOException {
        try(BufferedReader br = new BufferedReader( new InputStreamReader(fis, encoding))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }
}
