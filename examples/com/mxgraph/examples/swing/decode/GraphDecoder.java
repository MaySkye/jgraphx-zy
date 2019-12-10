package com.mxgraph.examples.swing.decode;

import com.mxgraph.examples.swing.match.ResourceStaticData;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphDecoder {

    private static final InputStream inputStream = GraphDecoder.class.getResourceAsStream("/com/mxgraph/examples/swing/config_files/graph_template");

    private static List<GraphEle> data = null;

    private GraphDecoder() {
        // static class
    }

    public static void insertGraphEle(GraphEle ele) {
        reloadData();
        data.removeIf((e) -> {
            return e.getName().equals(ele.getName());
        });
        data.add(ele);
        try {
            saveData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveData(List<GraphEle> data) throws Exception {
        /*
        <Template>
            <graph name="" image="" mxe="">
                <cell type="Boiler" num="1"/>
                <cell type="PipeSensor" num="1"/>
            </graph>
            <graph name="" image="" mxe="">
                <cell type="Boiler" num="1"/>
                <cell type="PipeSensor@Boiler" num="1"/>
            </graph>
        </Template>
         */
        Document document = FileUtil.createDocument();

        // 创建根节点
        Element root = document.createElement("Template");
        for (GraphEle item : data) {
            Element ele = createGraph(document, item);
            root.appendChild(ele);
        }

        // 将根节点添加到Document下
        document.appendChild(root);

       // FileUtil.saveDoc(document, PATH);

        reloadData();
    }

    private static Element createGraph(Document document, GraphEle data) {
        /*
        <graph name="" image="" mxe="">
            <cell type="Boiler" num="1"/>
            <cell type="PipeSensor" num="1"/>
        </graph>
         */
        Element ele = document.createElement("graph");

        ele.setAttribute("name", data.getName());
        ele.setAttribute("image", data.getImagePath());
        ele.setAttribute("mxe", data.getMxePath());
        if (data.getResourceStaticData() != null && data.getResourceStaticData().cellMap != null) {
            data.getResourceStaticData().cellMap.forEach((type, cnt) -> {
                Element child = document.createElement("cell");
                child.setAttribute("type", type);
                child.setAttribute("num", cnt + "");
                ele.appendChild(child);
            });
        }
        if (data.getResourceStaticData() != null && data.getResourceStaticData().connMap != null) {
            data.getResourceStaticData().connMap.forEach((tuple, cnt) -> {
                Element child = document.createElement("cell");
                child.setAttribute("type", tuple.getFirst() + "@" + tuple.getSecond() + "@" + tuple.getThird());
                child.setAttribute("num", cnt + "");
                ele.appendChild(child);
            });
        }

        return ele;
    }

    private static Element createCellCount(Document document, Map.Entry<String, Integer> entry) {
        /*
        <cell type="PipeSensor" num="1"/>
         */
        Element ele = document.createElement("cell");

        ele.setAttribute("type", entry.getKey());
        ele.setAttribute("num", entry.getValue().toString());

        return ele;
    }

    private static void reloadData() {

        try {
            if (data == null) {
                data = decodeDoc(inputStream);
            } else {
                data.clear();
                List<GraphEle> list = decodeDoc(inputStream);
                list.forEach(graphEle -> {
                    data.add(graphEle);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<GraphEle> decodeDoc() {
        try {
            if (data == null) {
                data = decodeDoc(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static List<GraphEle> decodeDoc(InputStream inputStream) throws IOException {

        List<GraphEle> rst = new ArrayList<>();

        String content = FileUtil.readFile(inputStream);
        Document document = mxXmlUtils.parseXml(content);

        Node root = document.getFirstChild();
        Node child = root.getFirstChild();
        while (child != null) {
            GraphEle ele = decodeGraph(child);
            if (ele != null) {
                rst.add(ele);
            }
            child = child.getNextSibling();
        }

        return rst;
    }

    private static GraphEle decodeGraph(Node node) {
        /*
        <graph name="" image="" mxe="">
            <cell type="Boiler" num="1"/>
            <cell type="PipeSensor" num="1"/>
        </graph>
         */
        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }

        GraphEle ele = decodeAttrs(node, null);
        ele = decodeChildren(node, ele);

        return ele;
    }

    private static GraphEle decodeAttrs(Node node, GraphEle ele) {
        /*
        <graph name="" image="" mxe="">
            <cell type="Boiler" num="1"/>
            <cell type="PipeSensor" num="1"/>
        </graph>
         */
        GraphEle obj = ele;
        if (obj == null) {
            obj = new GraphEle();
        }
        NamedNodeMap attrs = node.getAttributes();

        Node nameNode = attrs.getNamedItem("name");
        obj.setName(nameNode.getNodeValue());
        Node imgNode = attrs.getNamedItem("image");
        obj.setImagePath(imgNode.getNodeValue());
        Node mxeNode = attrs.getNamedItem("mxe");
        obj.setMxePath(mxeNode.getNodeValue());

        return obj;
    }

    private static GraphEle decodeChildren(Node node, GraphEle ele) {
        /*
        <graph name="" image="" mxe="">
            <cell type="Boiler" num="1"/>
            <cell type="PipeSensor" num="1"/>
        </graph>
         */
        ResourceStaticData resourceStaticData = new ResourceStaticData();
        resourceStaticData.cellMap = new HashMap<>();
        resourceStaticData.connMap = new HashMap<>();

        //Map<String, Integer> cellCount = new HashMap<>();
        Node child = node.getFirstChild();
        while (child != null) {
            Pair<String, Integer> par = decodeChild(child);
            if (par != null) {
                if (par.getFirst().contains("@")) {
                    String[] words = par.getFirst().split("@");
                    if (words != null) {
                        ResourceStaticData.MTriple<String, String, String> triple = null;
                        if (words.length == 2) {
                            triple = new ResourceStaticData.MTriple<>(words[0], "WaterEdge", words[1]);
                        } else if (words.length == 3) {
                            triple = new ResourceStaticData.MTriple<>(words[0], words[1], words[2]);
                        }
                        if (triple != null) {
                            resourceStaticData.connMap.put(triple, par.getSecond());
                        }
                    }
                } else {
                    resourceStaticData.cellMap.put(par.getFirst(), par.getSecond());
                }
                //cellCount.put(par.getFirst(), par.getSecond());
            }
            child = child.getNextSibling();
        }
        //ele.setCellCountMap(cellCount);
        ele.setResourceStaticData(resourceStaticData);

        return ele;
    }

    private static Pair<String, Integer> decodeChild(Node node) {
        /*
        <cell type="Boiler" num="1"/>
         */
        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }

        Pair<String, Integer> par = new Pair<>();

        NamedNodeMap attrs = node.getAttributes();
        Node typeNode = attrs.getNamedItem("type");
        Node numNode = attrs.getNamedItem("num");
        if (typeNode == null || numNode == null) {
            return null;
        }
        String celltype = typeNode.getNodeValue();
        Integer cellNum = Integer.valueOf(numNode.getNodeValue());

        par.setFirst(celltype);
        par.setSecond(cellNum);

        return par;
    }

}
