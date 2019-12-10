package com.mxgraph.examples.swing.decode;

import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class EdgeDecoder {

    private static final InputStream inputStream = EdgeDecoder.class.getResourceAsStream("/com/mxgraph/examples/swing/config_files/edge_template");
    private static List<EdgeEle> data;


    private EdgeDecoder() {
        // static class, decode edge cell, eg: pipe
    }

    public static List<EdgeEle> decodeDoc() {

        if (data == null) {
            try {
                data = decodeDoc(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private static List<EdgeEle> decodeDoc(InputStream inputStream) throws IOException {

        List<EdgeEle> list = new ArrayList<>();

        String content = FileUtil.readFile(inputStream);
        Document document = mxXmlUtils.parseXml(content);

        Node root = document.getFirstChild();
        Node child = root.getFirstChild();
        while (child != null) {
            EdgeEle ele = decodeCell(child);
            if (ele != null) {
                list.add(ele);
            }
            child = child.getNextSibling();
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

        Node child = node.getFirstChild();
        while (child != null) {
            Pair<String, String> par = decodeChild(child);
            if (par != null) {
                switch (par.getFirst()) {
                    case "name":
                        ele.setName(par.getSecond());
                        break;
                    case "type":
                        ele.setType(par.getSecond());
                        break;
                    case "width":
                        ele.setWidth(Integer.parseInt(par.getSecond()));
                        break;
                    case "height":
                        ele.setHeight(Integer.parseInt(par.getSecond()));
                        break;
                    case "icon":
                        ele.setIcon(par.getSecond());
                        break;
                    case "style":
                        ele.setStyle(par.getSecond());
                        break;
                    default:
                        System.out.println("Error: " + par);
                        break;
                }
            }
            child = child.getNextSibling();
        }

        return ele;
    }

    private static Pair<String, String> decodeChild(Node node) {
        /*
        <name>pipe_horizontal</name>
         */

        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }

        Pair<String, String> par = new Pair<>(node.getNodeName(), node.getFirstChild().getNodeValue());
        //System.out.println(node.getNodeName());
        //System.out.println(node.getFirstChild().getNodeValue());
        //System.out.println();
        return par;
    }

}
