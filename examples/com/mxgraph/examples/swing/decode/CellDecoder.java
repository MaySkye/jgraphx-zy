package com.mxgraph.examples.swing.decode;

import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 图元解析类
 */
public class CellDecoder {
    public static List<CellEle> opticalDeviceCellList = null;
    public static List<CellEle> networkDeviceCellList = null;

    // 读取图元xml，并解析成List<CellEle>
    static {
        try {
            opticalDeviceCellList = decodeDoc(CellDecoder.class.getResourceAsStream("/com/mxgraph/examples/swing/config_files/cell_template_optical_device"));
            networkDeviceCellList = decodeDoc(CellDecoder.class.getResourceAsStream("/com/mxgraph/examples/swing/config_files/cell_template_network"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析xml文档
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static List<CellEle> decodeDoc(InputStream inputStream) throws IOException {
        // 将xml解析为Document实例
        String content = FileUtil.readFile(inputStream);
        Document document = mxXmlUtils.parseXml(content);
        // 遍历节点并转换为CellEle实例，并放入List
        Node root = document.getFirstChild();
        NodeList childNodes = root.getChildNodes();
        ArrayList<CellEle> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            CellEle cellEle = decodeCell(child);
            if (cellEle != null) {
                list.add(cellEle);
            }
        }
        return list;
    }

    /**
     * 解析图元
     * @param node
     * @return
     */
    private static CellEle decodeCell(Node node) {
        // 若不是Element，则返回null
        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }
        // 若是Element则解析属性
        CellEle ele = decodeAttrs(node, null);
        ele = decodePorts(node, ele);
        return ele;
    }

    /**
     * 解析属性，添加至图元中
     * @param node
     * @param ele
     * @return
     */
    private static CellEle decodeAttrs(Node node, CellEle ele) {
        /* 结点示例： <cell name="boiler1" type="Boiler" icon="/com/mxgraph/examples/swing/images/boiler1.png" style="">  */

        // 若ele为空，则创建CellEle实例
        if (ele == null) {
            ele = new CellEle();
        }

        // 反射原理添加所有属性name、type、icon、style
        NamedNodeMap attrMap = node.getAttributes();
        for(int i =0;i<attrMap.getLength();i++)
        {
            try {
                Node item = attrMap.item(i);
                String fieldName = item.getNodeName();
                String value = item.getNodeValue();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Class fieldClass = CellEle.class.getDeclaredField(fieldName).getType();
                Method setter = CellEle.class.getMethod(methodName,fieldClass);
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

        // 其他属性
        Node multiValueNode = attrMap.getNamedItem("multiValue");
        if (multiValueNode != null) {
            ele.setMultiValue(multiValueNode.getNodeValue());
        }
        Node stylesNode = attrMap.getNamedItem("styles");
        if (stylesNode != null) {
            ele.setStyles(stylesNode.getNodeValue());
        }
        return ele;
    }

    /**
     * 解析连接端口（多个）
     * @param node
     * @param ele
     * @return
     */
    private static CellEle decodePorts(Node node, CellEle ele) {
        /* 结点示例
        <port attr="electric" location="up" direction="out" x="0.5" y="0.28"/>
        <port attr="electric" location="up" direction="out" x="0.5" y="0.28"/>
         */
        List<CellPort> ports = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for(int i=0;i<childNodes.getLength();i++){
            CellPort port = decodePort(childNodes.item(i));
            if(port!=null){
                ports.add(port);
            };
        }
        ele.setPorts(ports);
        return ele;
    }

    /**
     * 解析连接端口（单个）
     * @param node
     * @return
     */
    private static CellPort decodePort(Node node) {
        /*
        <port attr="electric" location="up" direction="out" x="0.5" y="0.28"/>
         */

        // 新建CellPort实例
        CellPort port = new CellPort();

        // 异常则返回null
        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }

        // 设置属性
        NamedNodeMap attrMap = node.getAttributes();
        port.setAttr(attrMap.getNamedItem("attr").getNodeValue());
        port.setLocation(attrMap.getNamedItem("location").getNodeValue());
        port.setDirection(attrMap.getNamedItem("direction").getNodeValue());
        port.setX(Double.valueOf(attrMap.getNamedItem("x").getNodeValue()));
        port.setY(Double.valueOf(attrMap.getNamedItem("y").getNodeValue()));

        //port的attr不同，port的width,height,style都不同
        port.setWidth(CellPort.getWidthByAttr(port.getAttr(), port.getLocation(), port.getDirection()));
        port.setHeight(CellPort.getHeightByAttr(port.getAttr(), port.getLocation(), port.getDirection()));
        port.setStyle(CellPort.getStyleByAttr(port.getAttr(), port.getLocation(), port.getDirection()));

        return port;
    }
}
