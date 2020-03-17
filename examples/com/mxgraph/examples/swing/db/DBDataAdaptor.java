package com.mxgraph.examples.swing.db;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.view.mxGraph;
import org.apache.commons.dbutils.QueryRunner;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static com.mxgraph.examples.swing.util.HttpUtil.getTelemetryDTOList;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:29 2019/4/23
 * @Modify By:
 */
public class DBDataAdaptor extends Thread {

    private boolean running = true;
    private mxGraph graph = null;
    private BasicGraphEditor editor = null;
    private Set<String> monitorInfoSet = new HashSet<>();
    private String site_name=null;
    private String resStr = "";
    private Map<String, JTextField> property_data;
    //将每次获取的数据放在一个List集合中
    private List<TelemetryDTO> telemetryDTOList = null;
    //List集合的下标 自增来模拟不同设备的不同属性值
    private int index;

    //使用静态代码块读取配置文件
    private static Properties properties = new Properties();
    private static InputStream in = DBDataAdaptor.class.getResourceAsStream("/config/jdbc.properties");
    private static InputStream _in = null;

    static {
        try {
            _in = new FileInputStream(new File(FileUtil.getAppPath()+"/config/jdbc.properties"));
            properties.load(_in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //创建sql语句封装对象
    private QueryRunner queryRunner = new JdbcConfig(properties).createQueryRunner();

    public DBDataAdaptor(BasicGraphEditor editor, mxGraph graph, Map<String, JTextField> property_data) {
        this.graph = graph;
        this.editor = editor;
        this.property_data=property_data;
    }

    public void cancel() {
        running = false;
    }

    @Override
    public void run() {

        mxCell root = (mxCell) graph.getModel().getRoot();
        getMonitorInfoStr(root,monitorInfoSet);

        for (String str : monitorInfoSet) {
            resStr = resStr + str + "::";
        }
        if (resStr.length() > 0) {
            resStr = resStr.substring(0, resStr.length() - 2);
        } else {
            return;
        }
        System.out.println("monitorInfoSet="+resStr);

        while (running) {
            /*HttpUtil httpUtil = new HttpUtil(properties.getProperty("http.url"));
            //使用http请求获取List集合
            telemetryDTOList = httpUtil.getTelemetryDTOList();
            //将list的下标清零
            index = 29;*/
            String url= null;
            try {
                url = "http://localhost:8888/telemetry/findMonitorValue/"+URLEncoder.encode(site_name,"utf-8")+"/"+ URLEncoder.encode(resStr,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //HttpUtil httpUtil = new HttpUtil(url);
            telemetryDTOList=getTelemetryDTOList(url);
            //输出list的内容
          /*  for(TelemetryDTO telemetryDTO:telemetryDTOList){
                System.out.println(telemetryDTO.getDeviceName()+"  "+telemetryDTO.getDataName()+"  "+telemetryDTO.getDetectedValue());
            }*/

            updatePropertyData(property_data);
            //更新一张图片上所有的属性值
            updateCellData(root);
            graph.refresh();

            try {
                sleep(1 * 1000); //休眠10秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //将当前组态图另存为图片，存在固定的文件夹
            //savePNG(editor);
            //将图片存入数据库 两种方式都可以
           // saveImageToDataBase(editor);
        }
    }

    public void updateCellData(mxCell cell) {
        if (cell.isVertex() && "property_data".equals(cell.getAttr())) {
            doUpdateData(cell); //用于更新组态图的数据
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            updateCellData(child);
        }
    }

    public void doUpdateData(mxCell cell) {
        //获取List集合中的对象 并使用对象总的value字段进行赋值
        for (int i = 0; i < telemetryDTOList.size(); i++) {
            if(cell.getMonitor_property_name().equals(telemetryDTOList.get(i).getDataName())){
                String dataValue = telemetryDTOList.get(i).getDetectedValue();
                String alarm_state=telemetryDTOList.get(i).getAlarmState();
                cell.setValue(dataValue);
            }
        }
    }

    public void getMonitorInfoStr(mxCell cell,Set<String> monitorInfoSet){
        if (cell == null) {
            return ;
        }
        System.out.println("type:"+cell.getType());
        if(cell.getType()!=null){
            if(cell.getType().equals("title")){
                site_name=cell.getValue().toString();
                site_name=site_name.substring(0,site_name.length()-5);
                System.out.println("getMonitorInfoStr  site_name：  "+site_name);
                //site_name="xian";
            }
        }

        if (cell.isVertex()  && cell.getAttr() == "property_data") {
            String monitor_device_name = cell.getMonitor_device_name();
            System.out.println("**********************************");
            System.out.println("monitor_device_name：  "+cell.getMonitor_device_name());
            System.out.println("monitor_property_name：  "+cell.getName());
            System.out.println("monitor_unit：  "+cell.getMonitor_unit());
            System.out.println("**********************************");

            String monitorInfo = cell.getMonitor_device_name()+":"+cell.getName();
            monitorInfoSet.add(monitorInfo);
        }
        for (int i = 0; i < cell.getChildCount(); i++) {
            mxCell child = (mxCell) cell.getChildAt(i);
            getMonitorInfoStr(child, monitorInfoSet);
        }
    }

    public void updatePropertyData(Map<String, JTextField> property_data){
        for (Map.Entry<String, JTextField> entry : property_data.entrySet()) {
            for (int i = 0; i < telemetryDTOList.size(); i++) {
                if(entry.getKey().equals(telemetryDTOList.get(i).getDataName())){
                    String dataValue = telemetryDTOList.get(i).getDetectedValue();
                    entry.getValue().setText(dataValue);
                }
            }
        }
    }

    //将图片直接保存到数据库中
    public void saveImageToDataBase(BasicGraphEditor editor) {
        //创建图片对象
        ImageDTO imageDTO = new ImageDTO();
        //生成时间戳
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        imageDTO.setTimestamp(new Timestamp(System.currentTimeMillis()));
        //获取站点名称
        imageDTO.setSiteName(editor.getNew_owlResourceData().title);
        System.out.println(imageDTO.getSiteName());
        //创建图片文件 文件名暂定为时间戳
        String filename = df.format(imageDTO.getTimestamp()) + ".png";
        try {
            imageDTO.setStateImage(encodeImgageToBase64(editor,Color.white));
            //String path="D:/";
            //ImageUtil.decodeBase64ToImage(imageDTO.getStateImage(),path,filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            queryRunner.update("insert into telemetry_image(siteName,stateImage,timestamp) value(?,?,?)",
                    imageDTO.getSiteName(),imageDTO.getStateImage(),imageDTO.getTimestamp());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //转化成图片流的形式
    public String encodeImgageToBase64(BasicGraphEditor editor, Color bg) throws IOException{
        ByteArrayOutputStream outputStream = null;
        mxGraphComponent graphComponent = editor.getGraphComponent();
        mxGraph graph = graphComponent.getGraph();
        // Creates the image for the PNG file
        BufferedImage bufferedImage = mxCellRenderer.createBufferedImage(graph,
                null, 1, bg, graphComponent.isAntiAlias(), null,
                graphComponent.getCanvas());
        outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
    }

    //将图片保存到本地的文件夹中
    public void savePNG(BasicGraphEditor editor) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        //System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        String fileparent = FileUtil.getAppPath() + "/stateImages";
        String filedirname = editor.getNew_owlResourceData().title;
        String filename = df.format(new Timestamp(System.currentTimeMillis()));
        String filepath = fileparent + "/" + filedirname + "/" + filename + ".png";
        File file = new File(filepath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            saveXmlPng(editor, filepath, Color.WHITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Saves XML+PNG format.
     */
    protected void saveXmlPng(BasicGraphEditor editor, String filename,
                              Color bg) throws IOException {
        mxGraphComponent graphComponent = editor.getGraphComponent();
        mxGraph graph = graphComponent.getGraph();

        // Creates the image for the PNG file
        BufferedImage image = mxCellRenderer.createBufferedImage(graph,
                null, 1, bg, graphComponent.isAntiAlias(), null,
                graphComponent.getCanvas());

        // Creates the URL-encoded XML data
        mxCodec codec = new mxCodec();
        String xml = URLEncoder.encode(
                mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
        mxPngEncodeParam param = mxPngEncodeParam
                .getDefaultEncodeParam(image);
        param.setCompressedText(new String[]{"mxGraphModel", xml});

        // Saves as a PNG file
        FileOutputStream outputStream = new FileOutputStream(new File(
                filename));
        try {
            mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
                    param);

            if (image != null) {
                encoder.encode(image);
                editor.setModified(false);
                editor.setCurrentFile(new File(filename));
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        mxResources.get("noImageData"));
            }
        } finally {
            outputStream.close();
        }
    }
}
