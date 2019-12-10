package com.mxgraph.examples.swing.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Arrays;
import java.util.List;


public class FileUtil {


    private FileUtil() {
        // static class
    }

    public static String getParentPath(String path) {
        return new File(path).getParent();
    }

    public static String getAppPath(){
        String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int lastIndex = path.lastIndexOf("/");
        path = path.substring(0, lastIndex);
        return path;
    }

    public static String getFileName(String path) {
        if (path == null || !new File(path).exists()) {
            return null;
        }
        return new File(path).getName();
    }

    public static String getRootPath() {
        String projectName = "jgraphx-master";
        String path = FileUtil.getAppPath();
        String rootPath = path.substring(0, path.lastIndexOf(projectName) + projectName.length());
        return rootPath;
    }

    public static String readFile(InputStream inputStream) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        StringBuilder buff = new StringBuilder(1024);
        String line = br.readLine();
        while (line != null) {
            buff.append(line);
            buff.append('\n');
            line = br.readLine();
        }
        return buff.toString();
    }

    public static void appendFile(String path, String content) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writer = new FileWriter(path, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Document createDocument() throws ParserConfigurationException {
        // 创建DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 创建DocumentBuilder
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 创建Document
        Document document = builder.newDocument();

        return document;
    }

    public static void saveDoc(Document document, String path) throws TransformerException {
        // 创建TransformerFactory对象
        TransformerFactory tff = TransformerFactory.newInstance();
        // 创建Transformer对象
        Transformer tf = tff.newTransformer();
        // 使用Transformer的transform()方法将DOM树转换成XML
        tf.transform(new DOMSource(document), new StreamResult(path));
    }

    public static List<String> listResFile() {
        File dir = new File(getRootPath() + "/data/cell_map");
        List<String> ret = Arrays.asList(dir.list((fdir, name) -> name.startsWith("resouce")));

        return ret;
    }

    public static void fork(String cmd) throws IOException {
        Runtime.getRuntime().exec(cmd);
    }


//    public static void main(String[] args) throws IOException {
//
//        System.out.println("file:" + getRootPath() + "/data/img");
//        System.out.println(System.getProperty("user.dir"));
//
////        System.out.println(readFile(FileUtil.getRootPath() + "/data/template"));
//
//    }
}
