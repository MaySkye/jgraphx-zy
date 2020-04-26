package com.mxgraph.examples.swing.resource_manage;

import com.mxgraph.examples.swing.util.FileUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 14:36 2020/4/20
 * @Modify By:
 */
public class XmlReader {

    private static String opticalDeviceTemplate;
    private static Properties properties = new Properties();
    private static InputStream in = null;
    private static Element rootElement;
    private static Element fistChild;
    private static Document document;

    static {
        try {
            in = new FileInputStream(new File(FileUtil.getAppPath()+"/config/filepath.properties"));
            properties.load(in);
            opticalDeviceTemplate = FileUtil.getRootPath().substring(1)+properties.getProperty("CellTemplateFile");
            System.out.println("opticalDeviceTemplate: "+opticalDeviceTemplate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(String cellName){
        //创建DOM4J解析器对象
        SAXReader saxReader = new SAXReader();
        File file1 = new File(opticalDeviceTemplate);
        try {
            //读取xml文件，并生成document对象 现可通过document来操作文档
            document = saxReader.read(opticalDeviceTemplate);
            //获取到文档的根节点
            rootElement = document.getRootElement();
            //获取子节点列表
            Iterator it = rootElement.elementIterator();
            while (it.hasNext()) {
                fistChild = (Element) it.next();
                String name = fistChild.attribute("name").getValue();
                if(name.equals(cellName)){
                    //找到了对应的节点
                    System.out.println("name: "+name);
                    System.out.println("cellName: "+cellName);
                    break;
                }
            }} catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    /*
     * 得到图元图片文件路径*/
    public static String getCellImageStr (){
        String imgStr = FileUtil.getRootPath().substring(1)+"/examples"+fistChild.attribute("icon").getValue();
        System.out.println("imgStr: "+imgStr);
        return imgStr;
    }

    /*
     * 根据cellName删除图元图片文件*/
    public static void deleteCellImg (){
        String imgStr = getCellImageStr();
        File file = new File(imgStr);
        if (file.exists()) {
            file.delete();
            System.out.println("图片已经被成功删除");
        }
    }

    /*
     * 删除图元Xml*/
    public static void deleteCellXml (){
        rootElement.remove(fistChild);
        System.out.println(rootElement.remove(fistChild));
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("UTF-8");
        XMLWriter xmlWriter = null;
        try {
            xmlWriter = new XMLWriter(new FileWriter(opticalDeviceTemplate), outputFormat);
            xmlWriter.write(document);//开始写入XML文件   写入Document对象
            xmlWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}