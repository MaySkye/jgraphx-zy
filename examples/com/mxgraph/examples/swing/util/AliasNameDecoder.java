package com.mxgraph.examples.swing.util;

import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class AliasNameDecoder {

    private static final InputStream inputStream = AliasNameDecoder.class.getResourceAsStream("/com/mxgraph/examples/swing/data/alias_name.xml");

    private static List<NameAliasEle> data = null;

    private AliasNameDecoder() {
        // static class
    }

    public static List<NameAliasEle> decodeDoc() {

        if (data == null) {
            try {
                data = decodeDoc(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private static List<NameAliasEle> decodeDoc(InputStream inputStream) throws IOException {



        List<NameAliasEle> list = new ArrayList<>();

        String content = FileUtil.readFile(inputStream);
        Document document = mxXmlUtils.parseXml(content);

        Node root = document.getFirstChild();
        Node child = root.getFirstChild();
        while (child != null) {
            NameAliasEle ele = decodeAlias(child);
            if (ele != null) {
                list.add(ele);
            }
            child = child.getNextSibling();
        }

        return list;
    }

    private static NameAliasEle decodeAlias(Node node) {
        /*
        <map name="template_dialog" alias="模板关系图"/>
         */

        //System.out.println(node);
        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {// "[#text:\n\t]"
            return null;
        }

        NameAliasEle ele = decodeAttrs(node, null);
        ele = decodeChildren(node, ele);

        return ele;
    }

    private static NameAliasEle decodeAttrs(Node node, NameAliasEle ele) {
        /*
        <map name="template_dialog" alias="模板关系图"/>
         */
        NameAliasEle obj = ele;
        if (obj == null) {
            obj = new NameAliasEle();
        }
        NamedNodeMap attrs = node.getAttributes();

        Node nameNode = attrs.getNamedItem("name");
        obj.setName(nameNode.getNodeValue());
        Node aliasNode = attrs.getNamedItem("alias");
        obj.setAlias(aliasNode.getNodeValue());

        return obj;
    }

    private static NameAliasEle decodeChildren(Node node, NameAliasEle ele) {
        return ele;
    }

}
