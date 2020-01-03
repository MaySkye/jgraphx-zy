package com.mxgraph.examples.swing.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:34 2019/12/26
 * @Modify By:
 */
public class xmlUtil {

    public ArrayList<String> geticons(String xmlpath) throws IOException, SAXException, ParserConfigurationException {
        //创建一个DOM解析器(文档生成器)工厂对象
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        //通过工厂对象创建一个DOM解析器对象
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlpath);
        //此代码完成后,整个XML文档已经被加载到内存中,以树状形式存储;
        Document parse = documentBuilder.parse(resourceAsStream);
        //4.从内存中读取数据生成对象
        //获取节点名称为person的所有节点,返回节点集合.
        NodeList personNodeList = parse.getElementsByTagName("cell");
        ArrayList<String> icons = new ArrayList<>();
        String iconstr="";
        //循环读取
        for (int i = 0; i < personNodeList.getLength(); i++) {
            Node personNode = personNodeList.item(i);
            //获取节点属性值
            iconstr = personNode.getAttributes().getNamedItem("icon").getNodeValue();
            icons.add(iconstr);
        }

        return icons;
    }

}
