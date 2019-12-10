package com.mxgraph.examples.swing.map;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 11:37 2018/7/27
 * @Modify By:
 */
public class OpenMap {

    public static void open(String urlpath) throws IOException {
        /**
         * @Author:zhoayi
         * @Description：
         * @Date：14:38 2018/7/27
         * @Modify：
         */
        Properties properties = System.getProperties();
        String osName = properties.getProperty("os.name");
        System.out.println (osName);
        if (osName.indexOf("Linux") != -1) {
            Runtime.getRuntime().exec("htmlview");
        } else if (osName.indexOf("Windows") != -1){
            Runtime.getRuntime().exec("explorer "+urlpath);
        } else {
            throw new RuntimeException("Unknown OS.");
        }

    }
    public static void main(String[] args) throws IOException {

        Properties properties = System.getProperties();
        String osName = properties.getProperty("os.name");
        System.out.println (osName);

        if (osName.indexOf("Linux") != -1) {
            Runtime.getRuntime().exec("htmlview");
        } else if (osName.indexOf("Windows") != -1){
            Runtime.getRuntime().exec("explorer http://localhost:8080/javascript/examples/grapheditor/www/sitemap.html");
        } else {
            throw new RuntimeException("Unknown OS.");
        }
    }
}
