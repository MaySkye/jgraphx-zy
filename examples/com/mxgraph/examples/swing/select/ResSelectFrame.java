package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.match.ModifyTemplateCore;
import com.mxgraph.examples.swing.match.ResMatchCore;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlObjectAttribute;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.findSubsystem;
import static com.mxgraph.examples.swing.owl.OwlResourceUtil.printOwlobject;

/**
 * @Author:zhoayi
 * @Description:提供资源选择模板，返回new_owlResourceData
 * @Data: Created in 11:04 2018/11/26
 * @Modify By:
 */
public class ResSelectFrame extends Frame{
    private OwlResourceData origin_owlResourceData;
    private OwlResourceData new_owlResourceData;
    private BasicGraphEditor editor;

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel contentPanel;
    private JPanel devicePanel;
    private JPanel devinfoPanel;
    private JPanel troductionPanel;
    private JPanel monitorPanel;
    private JPanel coninfoPanel;

    private JButton button_light_fre;
    private JButton button_time;
    private JButton button_microwave;

    private ArrayList<String> light_device_list = new ArrayList<String>();
    private ArrayList<String> time_device_list = new ArrayList<String>();
    private ArrayList<String> microwave_device_list = new ArrayList<String>();

    private ArrayList<JButton> light_device_btn = new ArrayList<JButton>();
    private ArrayList<JButton> time_device_btn = new ArrayList<JButton>();
    private ArrayList<JButton> microwave_device_btn = new ArrayList<JButton>();

    private JRadioButton jRadioButton;
    private boolean jRadioButton_isSelected=true;
    private String cur_device_name;
    private JTextField jTextField;
    //设备介绍信息字符串
    private String device_info="";
    //监测量单选控件(大小 写死了)
    private ArrayList<JRadioButton> jrbtn_list = new ArrayList<JRadioButton>(20);
    private ArrayList<showClass> jrbtn_list_name=new ArrayList<showClass>();
    //连接关系单选控件
    private ArrayList<JRadioButton> conn_list = new ArrayList<JRadioButton>(20);
    private Map<String,showClass> conn_list_name=new HashMap<>();

    private Map<String, OwlObject> origin_objMap;
    private Map<String, OwlObject> new_objMap;


    public ResSelectFrame(BasicGraphEditor editor,OwlResourceData origin_owlResourceData) throws HeadlessException {
        this.editor=editor;
        this.origin_owlResourceData=origin_owlResourceData;
        this.new_owlResourceData=editor.getNew_owlResourceData();

        this.origin_objMap=this.origin_owlResourceData.objMap;
        this.new_objMap=new_owlResourceData.objMap;

        initButton();
        initPanel();
        initFrame();
        init_devicePanel_data();

        init_devinfoPanel_data();

        addListener(light_device_btn);
        addListener(time_device_btn);
        addListener(microwave_device_btn);
    }

    private void initStyle(JButton button){
        //初始化透明按钮
        button.setOpaque(true);
        Border lineBorder=new LineBorder(Color.GRAY,1);
        button.setBorder(lineBorder);
        Dimension preferredSize = new Dimension(130,35);//设置尺寸
        button.setPreferredSize(preferredSize );
        //button.setBorderPainted(false);
       /* button.setFocusable(false);
        button.setBackground(null);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);*/
        Font font1=new Font("楷体", Font.PLAIN, 20);
        button.setFont(font1);
    }
    private void initButton(){
        button_light_fre=new JButton("光频分系统");
        button_time=new JButton("时间分系统");
        button_microwave=new JButton("微波分系统");

        initStyle(button_light_fre);
        initStyle(button_time);
        initStyle(button_microwave);
    }
    private void initPanel(){
        //先初始化子Panel,再初始化父Panel
        //初始化troductionPanel，monitorPanel， coninfoPanel
        troductionPanel=new JPanel();
        troductionPanel.setBorder(new TitledBorder("设备介绍："));
        troductionPanel.setLayout(new FlowLayout());

        monitorPanel=new JPanel();
        monitorPanel.setBorder(new TitledBorder("设备监测量："));
        monitorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Dimension preferredSize = new Dimension(130,35);//设置尺寸
        monitorPanel.setPreferredSize(preferredSize);

        coninfoPanel=new JPanel();
        coninfoPanel.setBorder(new TitledBorder("设备连接关系："));
        coninfoPanel.setLayout(new FlowLayout());

        //初始化devicePanel
        devicePanel=new JPanel();
        devicePanel.setBorder(new TitledBorder("设备："));
        devicePanel.setLayout(new FlowLayout());

        JScrollPane scrollPane = new JScrollPane();
        devicePanel.add(scrollPane);

        //初始化devinfoPanel
        devinfoPanel=new JPanel();
        devinfoPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        devinfoPanel.setLayout(new GridLayout(3,1));
        devinfoPanel.add(troductionPanel);
        devinfoPanel.add(monitorPanel);
        devinfoPanel.add(coninfoPanel);

        //初始化contentPanel
        contentPanel=new JPanel();
        contentPanel.setLayout(new BorderLayout());
        Dimension preferredSize2 = new Dimension(200,35);//设置尺寸
        devicePanel.setPreferredSize(preferredSize2);
        contentPanel.add(devicePanel,BorderLayout.WEST);
        Dimension preferredSize1 = new Dimension(130,35);//设置尺寸
        devinfoPanel.setPreferredSize(preferredSize1);
        contentPanel.add(devinfoPanel,BorderLayout.CENTER);

        //初始化topPanel
        topPanel=new JPanel();
        topPanel.setSize(880,200);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(button_light_fre);
        topPanel.add(button_time);
        topPanel.add(button_microwave);

        //初始化mainPanel
        mainPanel=new JPanel();
        mainPanel.setSize(900,500);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel,BorderLayout.NORTH);
        mainPanel.add(contentPanel,BorderLayout.CENTER);

    }
    private void initFrame(){
        this.setTitle("-文件资源模板选择框-");//设置窗口标题内容
        this.setSize(1000, 600);//设置窗口大小,宽度500，高度400
        this.setLocation(200, 100);//设置窗口位置为距离屏幕左边水平方向300，上方垂直方向200
        this.setVisible(true);//设置窗体可见。
        this.setLayout(new BorderLayout());//设置窗体布局为流式布局。
        this.setResizable(false); //禁止改变大小在调用显示之前

        this.addWindowListener (new WindowAdapter()
        {
            @Override
            public void windowClosing ( WindowEvent e )
            {
                editor.setNew_owlResourceData(new_owlResourceData);
                System.out.println("改变后：--------------------------------------------------------------------------");
                printOwlobject(editor.getNew_owlResourceData().objMap);


                //--------------------------------------
                //匹配模板图时使用如下方法
                //为graph匹配相似度最高的match_graph,得到对应模板图的文件名
                String TemplatePath= ResMatchCore.getTemplatePath(editor.getNew_owlResourceData());
                System.out.println("TemplatePath:"+TemplatePath);

                /*此段代码可以显示出模板mxe文件*/
                if (TemplatePath == null) {
                    return;
                }
                Document document = null;
                try {
                    document = mxXmlUtils.parseXml(FileUtil.readFile(new FileInputStream(TemplatePath)));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                mxCodec codec = new mxCodec(document);
                codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());

                // 根据资源模型文件，对模板组态图进行调整,
                // 则可以显示出调整好的组态图，图元没有绑定资源信息
                GraphInterface<String> graph= showGraph.createGraph(editor.getNew_owlResourceData());
                new ModifyTemplateCore(TemplatePath).postProcess(graph,editor,editor.getResourceFile());

                //---------------------------------------
                dispose();
            }
        });
        this.add(mainPanel);
    }

    private void init_devicePanel_data(){
        //先把设备的数据加载好，不论visible为true还是false,都应该显示出来，所以用origin_owlResourceData
        loadDevices(origin_objMap);
        //以下是初始化的一个显示状态
        Border lineBorder=new LineBorder(Color.GRAY,2);
        button_light_fre.setBorder(lineBorder);
        Border lineBorder1=new LineBorder(Color.WHITE,2);
        button_time.setBorder(lineBorder1);
        Border lineBorder2=new LineBorder(Color.WHITE,2);
        button_microwave.setBorder(lineBorder2);

        for(int i=0;i<light_device_btn.size();i++){
            light_device_btn.get(i).setVisible(true);
        }
        for(int i=0;i<time_device_btn.size();i++){
            time_device_btn.get(i).setVisible(false);
        }
        for(int i=0;i<microwave_device_btn.size();i++){
            microwave_device_btn.get(i).setVisible(false);
        }

        button_light_fre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Border lineBorder=new LineBorder(Color.GRAY,2);
                button_light_fre.setBorder(lineBorder);
                Border lineBorder1=new LineBorder(Color.WHITE,2);
                button_time.setBorder(lineBorder1);
                Border lineBorder2=new LineBorder(Color.WHITE,2);
                button_microwave.setBorder(lineBorder2);

                for(int i=0;i<light_device_btn.size();i++){
                    light_device_btn.get(i).setVisible(true);
                }
                for(int i=0;i<time_device_btn.size();i++){
                    time_device_btn.get(i).setVisible(false);
                }
                for(int i=0;i<microwave_device_btn.size();i++){
                    microwave_device_btn.get(i).setVisible(false);
                }
            }

        });

        button_time.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Border lineBorder=new LineBorder(Color.WHITE,2);
                button_light_fre.setBorder(lineBorder);
                Border lineBorder1=new LineBorder(Color.GRAY,2);
                button_time.setBorder(lineBorder1);
                Border lineBorder2=new LineBorder(Color.WHITE,2);
                button_microwave.setBorder(lineBorder2);

                for(int i=0;i<light_device_btn.size();i++){
                    light_device_btn.get(i).setVisible(false);
                }
                for(int i=0;i<time_device_btn.size();i++){
                    time_device_btn.get(i).setVisible(true);
                }
                for(int i=0;i<microwave_device_btn.size();i++){
                    microwave_device_btn.get(i).setVisible(false);
                }

            }
        });

        button_microwave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Border lineBorder=new LineBorder(Color.WHITE,2);
                button_light_fre.setBorder(lineBorder);
                Border lineBorder1=new LineBorder(Color.WHITE,2);
                button_time.setBorder(lineBorder1);
                Border lineBorder2=new LineBorder(Color.GRAY,2);
                button_microwave.setBorder(lineBorder2);

                for(int i=0;i<light_device_btn.size();i++){
                    light_device_btn.get(i).setVisible(false);
                }
                for(int i=0;i<time_device_btn.size();i++){
                    time_device_btn.get(i).setVisible(false);
                }
                for(int i=0;i<microwave_device_btn.size();i++){
                    microwave_device_btn.get(i).setVisible(true);
                }
            }
        });
    }
    private void loadDevices(Map<String, OwlObject> objMap){
         objMap.forEach((uri, obj) -> {
            System.out.println(findSubsystem(obj.type));
            if(findSubsystem(obj.type).equals("光纤光频传递分系统设备")) {
                light_device_list.add(obj.id);
                JButton button=new JButton(obj.id);
                Dimension preferredSize = new Dimension(190,35);//设置尺寸
                button.setPreferredSize(preferredSize);
                Font font=new Font("楷体", Font.PLAIN, 15);
                button.setFont(font);
                button.setToolTipText(button.getText());
                button.setVisible(false);
                light_device_btn.add(button);
                devicePanel.add(button);
            }
            if(findSubsystem(obj.type).equals("光纤时间同步分系统设备")) {
                time_device_list.add(obj.id);
                JButton button=new JButton(obj.id);
                Dimension preferredSize = new Dimension(190,35);//设置尺寸
                button.setPreferredSize(preferredSize);
                Font font=new Font("楷体", Font.PLAIN, 15);
                button.setFont(font);
                button.setToolTipText(button.getText());
                button.setVisible(false);
                time_device_btn.add(button);
                devicePanel.add(button);
            }
            if(findSubsystem(obj.type).equals("微波频率传递分系统设备")) {

                microwave_device_list.add(obj.id);
                JButton button=new JButton(obj.id);
                Dimension preferredSize = new Dimension(190,35);//设置尺寸
                button.setPreferredSize(preferredSize);
                Font font=new Font("楷体", Font.PLAIN, 15);
                button.setFont(font);
                button.setToolTipText(button.getText());
                button.setVisible(false);
                microwave_device_btn.add(button);
                devicePanel.add(button);
            }
        });
    }

    private void init_devinfoPanel_data(){
        //初始化devinfoPanel里面的控件
        jTextField=new JTextField(device_info);
        troductionPanel.add(jTextField);
        jTextField.setVisible(false);

        jRadioButton=new JRadioButton("是否将该设备添加到组态图中",true);
        monitorPanel.add(jRadioButton);
        jRadioButton.setVisible(false);

        jRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    String id=jRadioButton.getText();
                    new_objMap.forEach((uri, obj) -> {
                        if(obj.id.equals(cur_device_name)){
                            if(jRadioButton.isSelected()){
                                obj.visible=true;
                            }
                            else{
                                obj.visible=false;
                            }
                        }
                    });
            }
        });

        //为JRadioButton添加监听事件--监测量
        for(int i=0;i<20;i++){
            JRadioButton jrb=new JRadioButton("111",true);
            jrb.setVisible(false);

            jrb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        String id=jrb.getText();
                        System.out.println("id:"+id);
                        new_objMap.forEach((uri, obj) -> {
                           if(obj.id.equals(id)){
                               if(jrb.isSelected()){
                                   obj.visible=true;
                               }else{
                                   obj.visible=false;
                               }
                           }
                        });
                }
            });

            jrbtn_list.add(jrb);
            monitorPanel.add(jrb);
        }
        //为JRadioButton添加监听事件--连接关系
        for(int i=0;i<20;i++){
            JRadioButton jrb=new JRadioButton("222",true);
            jrb.setVisible(false);

            jrb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        String str=jrb.getText();
                        int index=str.indexOf("-");
                        String first=str.substring(0,index);
                        String last=str.substring(index+1);
                        //先找出last对应的Owlobject
                        OwlObject last_obj=getObjectById(last);
                        new_objMap.forEach((uri, obj) -> {
                            if(obj.id.equals(first)){
                                obj.objAttrs.forEach((objAttr, objSet) -> {
                                    if(objAttr.id.equals("connect")){
                                        if(objSet.contains(last_obj)){
                                            if(!jrb.isSelected()){
                                                objSet.remove(last_obj);
                                            }
                                        }else{
                                            if(jrb.isSelected()){
                                                objSet.add(last_obj);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                }
            });

            conn_list.add(jrb);
            coninfoPanel.add(jrb);
        }
    }

    private void update_monitorPanel_data(){

        jRadioButton.setVisible(true);
        jRadioButton.setSelected(jRadioButton_isSelected);

        for(int i=0;i<jrbtn_list.size();i++){
            jrbtn_list.get(i).setVisible(false);
        }

        for(int i=0;i<jrbtn_list_name.size();i++){
            jrbtn_list.get(i).setText(jrbtn_list_name.get(i).name);
            jrbtn_list.get(i).setSelected(jrbtn_list_name.get(i).isSelected);
            jrbtn_list.get(i).setVisible(true);
        }
    }

    private void update_coninfoPanel_data(){
        for(int i=0;i<conn_list.size();i++){
            conn_list.get(i).setVisible(false);
        }

        int i=0;
        for (Map.Entry<String, showClass> entry : conn_list_name.entrySet()) {
            conn_list.get(i).setText(entry.getKey());
            conn_list.get(i).setSelected(entry.getValue().isSelected);
            conn_list.get(i).setVisible(true);
            i++;
        }
    }

    private void update_troductionPanel_data(){
        if (device_info == null || device_info == "") {
            jTextField.setVisible(false);
        }else {
            jTextField.setVisible(true);
        }
        jTextField.setText(device_info);
    }

    private void addListener(ArrayList<JButton> list){
        //为devicePanel里面的button添加点击事件
        for(int i=0;i<list.size();i++){
             JButton button=list.get(i);
             button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String id=button.getText();
                    System.out.println(id);
                    OwlObject owlObject=new OwlObject();
                    //获取对应的设备信息
                    jrbtn_list_name.clear();
                    conn_list_name.clear();
                    device_info="";

                    new_objMap.forEach((uri, obj) -> {
                        if(obj.id.equals(id)){
                            device_info="Name:"+obj.id+"  Type:"+obj.type.id;
                            jRadioButton_isSelected=obj.visible;
                            cur_device_name=id;
                            obj.objAttrs.forEach((objAttr, objSet) -> {
                                if(objAttr.id.equals("has_property")){
                                    objSet.forEach(obj2 -> {
                                        showClass scl=new showClass();
                                        scl.name=obj2.id;
                                        scl.isSelected=obj2.visible;
                                        jrbtn_list_name.add(scl);
                                    });
                                }
                                if(objAttr.id.equals("connect")){
                                    objSet.forEach(obj2 -> {
                                        showClass scl=new showClass();
                                        scl.name=obj.id+"-"+obj2.id;
                                        scl.isSelected=true;
                                        String s=obj.id+"-"+obj2.id;
                                        conn_list_name.put(s,scl);
                                    });
                                }
                            });
                        }
                    });

                    origin_objMap.forEach((uri, obj) -> {
                        if(obj.id.equals(id)){
                            obj.objAttrs.forEach((objAttr, objSet) -> {
                                if(objAttr.id.equals("connect")){
                                    objSet.forEach(obj2 -> {
                                        showClass scl=new showClass();
                                        scl.name=obj.id+"-"+obj2.id;
                                        scl.isSelected=true;
                                        String ss=obj.id+"-"+obj2.id;
                                        if(conn_list_name.get(ss)==null){
                                            scl.isSelected=false;
                                            conn_list_name.put(ss,scl);
                                        }
                                    });
                                }
                            });
                        }
                    });
                    update_troductionPanel_data();
                    update_monitorPanel_data();
                    update_coninfoPanel_data();
                }
            });
        }
    }

    private OwlObject getObjectById(String id){
        OwlObject temp=new OwlObject();
        for (Map.Entry<String, OwlObject> entry : new_objMap.entrySet()) {
            if(entry.getValue().id.equals(id)){
                temp=entry.getValue();
            }
        }
        return temp;
    }

}
