package com.mxgraph.examples.swing.util;

public class EncodeUtil {

    public static String GBKTOUTF8(String name){
        if (name.equals("wuhan")) return "武汉";
        if (name.equals("xian")) return "西安";
        else return "郑州";
    }
}
