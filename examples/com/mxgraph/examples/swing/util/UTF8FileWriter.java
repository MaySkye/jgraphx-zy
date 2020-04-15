package com.mxgraph.examples.swing.util;

import java.io.*;
import java.net.URLDecoder;

public class UTF8FileWriter {
    public static void transfer(String filePath){
        try {
            // 读取文件
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            char[] content = new char[fileInputStream.available()];
            bufferedReader.read(content);
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
            // 保存文件
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            outputStreamWriter.write(URLDecoder.decode(new String(content), "UTF-8"));
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(String content, File file)
    {
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            outputStreamWriter.write(URLDecoder.decode(new String(content), "UTF-8"));
            outputStreamWriter.close();
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
