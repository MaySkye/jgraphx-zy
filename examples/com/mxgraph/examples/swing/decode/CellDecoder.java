package com.mxgraph.examples.swing.decode;

import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class CellDecoder {
    private static final InputStream inputStream = CellDecoder.class.getResourceAsStream("/com/mxgraph/examples/swing/config_files/cell_template");

    private static List<CellEle> data = null;

    private CellDecoder() {
        // static class
    }

    public static List<CellEle> decodeDoc() {

        if (data == null) {
            try {
                data = decodeDoc(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private static List<CellEle> decodeDoc(InputStream path) throws IOException {
        //System.out.println("path: " + path);
        List<CellEle> list = new ArrayList<>();

        String content = FileUtil.readFile(inputStream);

        Document document = mxXmlUtils.parseXml(content);

        Node root = document.getFirstChild();
        Node child = root.getFirstChild();
        while (child != null) {
            CellEle ele = decodeCell(child);
            if (ele != null) {
                list.add(ele);
            }
            child = child.getNextSibling();
        }

        return list;
    }

    private static CellEle decodeCell(Node node) {
        /*
        <cell name="boiler1" type="Boiler" icon="/com/mxgraph/examples/swing/images/boiler1.png">
            <port attr="electric" location="up" direction="out" x="0.5" y="0.28"/>
        </cell>
         */
        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }

        CellEle ele = decodeAttrs(node, null);
        ele = decodeChildren(node, ele);

        return ele;
    }

    private static CellEle decodeAttrs(Node node, CellEle ele) {
        /*
        <cell name="boiler1" type="Boiler" icon="/com/mxgraph/examples/swing/images/boiler1.png" style="">
         */
        CellEle obj = ele;
        if (obj == null) {
            obj = new CellEle();
        }
        NamedNodeMap attrs = node.getAttributes();

        Node nameNode = attrs.getNamedItem("name");
        obj.setName(nameNode.getNodeValue());
        Node typeNode = attrs.getNamedItem("type");
        obj.setType(typeNode.getNodeValue());
        Node imageNode = attrs.getNamedItem("icon");
        obj.setIcon(imageNode.getNodeValue());
        Node styleNode = attrs.getNamedItem("style");
        obj.setStyle(styleNode.getNodeValue());

        Node multiValueNode = attrs.getNamedItem("multiValue");
        if (multiValueNode != null) {
            obj.setMultiValue(multiValueNode.getNodeValue());
        }
        Node stylesNode = attrs.getNamedItem("styles");
        if (stylesNode != null) {
            obj.setStyles(stylesNode.getNodeValue());
        }

        Node widthNode = attrs.getNamedItem("width");
        obj.setWidth(Integer.parseInt(widthNode.getNodeValue()));
        Node heightNode = attrs.getNamedItem("height");
        obj.setHeight(Integer.parseInt(heightNode.getNodeValue()));

        return obj;
    }

    private static CellEle decodeChildren(Node node, CellEle ele) {
        /*
        <port attr="electric" location="up" direction="out" x="0.5" y="0.28"/>
        <port attr="electric" location="up" direction="out" x="0.5" y="0.28"/>
         */

        List<CellPort> ports = new ArrayList<>();
        Node child = node.getFirstChild();
        while (child != null) {
            CellPort port = decodeChild(child);
            if (port != null) {
                ports.add(port);
            }
            child = child.getNextSibling();
        }
        ele.setPorts(ports);

        return ele;
    }

    private static CellPort decodeChild(Node node) {
        /*
        <port attr="electric" location="up" direction="out" x="0.5" y="0.28"/>
         */

        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }

        CellPort port = new CellPort();

        NamedNodeMap attrs = node.getAttributes();

        Node attrNode = attrs.getNamedItem("attr");
        port.setAttr(attrNode.getNodeValue());
        Node locationNode = attrs.getNamedItem("location");
        port.setLocation(locationNode.getNodeValue());
        Node directionNode = attrs.getNamedItem("direction");
        port.setDirection(directionNode.getNodeValue());
        Node xNode = attrs.getNamedItem("x");
        port.setX(Double.valueOf(xNode.getNodeValue()));
        Node yNode = attrs.getNamedItem("y");
        port.setY(Double.valueOf(yNode.getNodeValue()));

        //port的attr不同，port的width,height,style都不同
        port.setWidth(CellPort.getWidthByAttr(port.getAttr(), port.getLocation(), port.getDirection()));
        port.setHeight(CellPort.getHeightByAttr(port.getAttr(), port.getLocation(), port.getDirection()));
        port.setStyle(CellPort.getStyleByAttr(port.getAttr(), port.getLocation(), port.getDirection()));

        return port;
    }


}
