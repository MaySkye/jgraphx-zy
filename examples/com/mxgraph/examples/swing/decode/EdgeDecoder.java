package com.mxgraph.examples.swing.decode;

import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EdgeDecoder {

    public static List<EdgeEle> egdeList;


    private EdgeDecoder() {
        // static class, decode edge cell, eg: pipe
    }

    static {
        try {
            egdeList = decodeDoc(EdgeDecoder.class.getResourceAsStream("/com/mxgraph/examples/swing/config_files/edge_template"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<EdgeEle> decodeDoc(InputStream inputStream) throws IOException {
        // 将xml解析为Document实例
        String content = FileUtil.readFile(inputStream);
        Document document = mxXmlUtils.parseXml(content);
        // 遍历节点并转换为CellEle实例，并放入List
        Node root = document.getFirstChild();
        NodeList childNodes = root.getChildNodes();
        ArrayList<EdgeEle> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            EdgeEle cellEle = decodeCell(child);
            if (cellEle != null) {
                list.add(cellEle);
            }
        }
        return list;
    }

    private static EdgeEle decodeCell(Node node) {
        /*
        <edge>
            <name>pipe_horizontal</name>
            <type>Pipe</type>
            <width>150</width>
            <height>150</height>
            <icon>/com/mxgraph/examples/swing/images/pipe_line_connect.png</icon>
            <style></style>
        </edge>
         */

        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }
        EdgeEle ele = decodeAttrs(node, null);
        ele = decodeChildren(node, ele);
        return ele;
    }

    private static EdgeEle decodeAttrs(Node node, EdgeEle ele) {
        /*
        <edge>
         */
        EdgeEle obj = ele;
        if (obj == null) {
            obj = new EdgeEle();
        }
        return obj;
    }

    private static EdgeEle decodeChildren(Node node, EdgeEle ele) {
        /*
        <edge>
            <name>pipe_horizontal</name>
            <type>Pipe</type>
            <width>150</width>
            <height>150</height>
            <icon>/com/mxgraph/examples/swing/images/pipe_line_connect.png</icon>
            <style></style>
        </edge>
         */
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            decodeChild(ele, child);
        }
        return ele;
    }

    private static void decodeChild(EdgeEle ele, Node node) {
        /*
        <name>pipe_horizontal</name>
         */

        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return;
        }
        String fieldName = node.getNodeName();
        String value = node.getFirstChild().getNodeValue();
        try {
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Class fieldClass = EdgeEle.class.getDeclaredField(fieldName).getType();
            Method setter = EdgeEle.class.getMethod(methodName,fieldClass);
            if(fieldClass == int.class)
            {
                setter.invoke(ele, Integer.parseInt(value));
            }
            else
            {
                setter.invoke(ele,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
