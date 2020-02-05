package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.Vertex;
import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.match.ModifyTemplateCore;
import com.mxgraph.examples.swing.match.ResMatchCore;
import com.mxgraph.examples.swing.owl.*;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.examples.swing.util.EncodeUtil;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import static com.mxgraph.examples.swing.editor.EditorActions.showTitle;
import static com.mxgraph.examples.swing.owl.OwlResourceUtil.*;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:58 2018/12/27
 * @Modify By:
 */

public class ResSelectFrame5 extends Frame{

    private JPanel mainPanel;
    private JPanel chartPanel;
    private JPanel buttonPanel;
    private JPanel selectPanel;
    private JPanel introductionPanel;
    private JPanel monitorPanel;
    private JPanel coninfoPanel;

    private BasicGraphEditor editor;
    private OwlResourceData origin_owlResourceData;
    private OwlResourceData new_owlResourceData;
    private Map<String, OwlObject> origin_objMap;
    private Map<String, OwlObject> new_objMap;
    private Map<String, OwlObject> main_objMap=new HashMap<>();
    private Map<String, Object> vertex_objMap=new HashMap<>();
    private Map<String, OwlObject> vertex_main_objMap=new HashMap<>();
    private Map<Object, String> edge_objMap=new HashMap<>();

    private int height;
    private int width;

    private final mxGraph graph ;
    private Object parent ;
    private final mxGraphComponent graphComponent;

    private JTextField jTextField;//显示introductionPanel的信息
    private String info;
    private JRadioButton jRadioButton;//是否添加到组态图中的单选框
    private Set<JRadioButton> jrbtn_list=new HashSet<>();
    private Map<String,JRadioButton> conn_list=new HashMap<>();
    private String selected_obj_name="";

    private JButton return_btn;
    private JButton diagram_btn;
    private JButton refresh_btn;
    private JButton save_btn;
    private JButton open_btn;

    private JTable jTable;
    private AbstractTableModel attrListModel;
    private JScrollPane scrollPanel;
    private String filepath;
    private boolean isreOpen=false;
    private String file_xmi=null;

    public ResSelectFrame5(BasicGraphEditor editor,String sitename){
        this.editor=editor;
        this.origin_owlResourceData=this.editor.getOrigin_owlResourceData();
        this.new_owlResourceData=this.editor.getNew_owlResourceData();
        this.filepath=editor.getResourceFile();

        this.origin_objMap=this.origin_owlResourceData.objMap;
        this.new_objMap=this.new_owlResourceData.objMap;
        this.graph = new mxGraph();
        this.parent = graph.getDefaultParent();
        this.graphComponent = new mxGraphComponent(graph);

        return_btn=new JButton("还原");
        diagram_btn=new JButton("生成组态图");
        refresh_btn=new JButton("刷新");
        save_btn=new JButton("保存");
        open_btn=new JButton("打开");

        initselectHandler();
        initJPanel();
        initFrame();

        jRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("selected_obj_name:"+selected_obj_name);
                new_objMap.forEach((uri, obj) -> {
                    if(obj.id.equals(selected_obj_name)){
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

        return_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("还原......");
                graph.getModel().beginUpdate();
                try
                {
                    //把graph清空
                    graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
                    parent = graph.getDefaultParent();
                    //把数据还原
                    //重新得到一遍OwlResourceData数据，是为了避免和原来的数据使用同一内存
                    OwlResourceData owlResourceData=null;
                    if(isreOpen){
                        if(file_xmi!=null){
                            owlResourceData=readtoGetOwlResourceData(file_xmi);
                        }else{
                            System.out.println("file_xmi is null!!");
                        }
                    }else{
                        owlResourceData = parseResourceFile(filepath);
                    }

                    new_owlResourceData=owlResourceData;
                    new_objMap=new_owlResourceData.objMap;
                    editor.setNew_owlResourceData(owlResourceData);
                    paint(new_objMap);
                }
                finally
                {
                    graph.getModel().endUpdate();
                }
            }
        });

        diagram_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("生成组态图......");

                //--------------------------------------
                //匹配模板图时使用如下方法
                //为graph匹配相似度最高的match_graph,得到对应模板图的文件名
                String TemplatePath= ResMatchCore.getTemplatePath(new_owlResourceData);
               // System.out.println("TemplatePath:"+TemplatePath);

                /*此段代码可以显示出模板mxe文件*/
                if (TemplatePath == null) {
                    return;
                }
                Document document = null;
                try {
                    document = mxXmlUtils.parseXml(FileUtil.readFile(this.getClass().getResourceAsStream(TemplatePath)));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                mxCodec codec = new mxCodec(document);
                codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());

                // 根据资源模型文件，对模板组态图进行调整,
                // 则可以显示出调整好的组态图，图元没有绑定资源信息
                GraphInterface<String> graph= showGraph.createGraph(new_owlResourceData);

                new ModifyTemplateCore(TemplatePath).postProcess(graph,editor,filepath);

                //添加标题
                showTitle(sitename,editor);
                editor.getGraphComponent().getGraph().refresh();

                editor.getOrigin_owlResourceData().title=sitename.substring(0,sitename.length()-4);
                editor.getNew_owlResourceData().title=sitename.substring(0,sitename.length()-4);

               // editor.getOrigin_owlResourceData().title=EncodeUtil.GBKTOUTF8(sitename.substring(0,sitename.length()-4));
               // editor.getNew_owlResourceData().title=EncodeUtil.GBKTOUTF8(sitename.substring(0,sitename.length()-4));
                dispose();
            }

        });

        refresh_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("刷新：.......");
                graph.getModel().beginUpdate();
                try
                {
                    graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
                    parent = graph.getDefaultParent();
                    paint(new_objMap);

                    //打印new_owlResourceData
                   /* new_owlResourceData.objMap.forEach((uri, obj) -> {
                        //解析它的基本信息，对象信息和属性信息
                        obj.objAttrs.forEach((objAttr, objSet) -> {
                            objSet.forEach(obj2 -> {
                                System.out.println(obj.id+"->" + objAttr.id + "->"+obj2.id);
                            });
                        });
                    });*/
                }
                finally
                {
                    graph.getModel().endUpdate();
                }
            }
        });

        save_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    //保存为xmodel文件
                    FileFilter selectedFilter = null;
                    String filename=null;
                    String filename_xmodel = null;
                    String filename_xmi = null;
                    String lastDir=null;

                    String wd;

                    if (lastDir != null) {
                        wd = lastDir;
                    } else {
                        wd = FileUtil.getAppPath();
                    }

                    JFileChooser fc = new JFileChooser(wd);

                    // Adds the default file format
                    DefaultFileFilter defaultFilter = new DefaultFileFilter(".xmodel",
                            "xmodel file " + mxResources.get("file") + " (.xmodel)");
                    // Adds special vector graphics formats and HTML
                    fc.addChoosableFileFilter(defaultFilter);

                    // Adds filter that accepts all supported image formats
                    fc.setFileFilter(defaultFilter);
                    int rc = fc.showDialog(null, mxResources.get("save"));

                    if (rc != JFileChooser.APPROVE_OPTION) {
                        return;
                    } else {
                        lastDir = fc.getSelectedFile().getParent();
                    }

                    filename = fc.getSelectedFile().getAbsolutePath();
                    selectedFilter = fc.getFileFilter();
                    if (selectedFilter == null || !selectedFilter.equals(defaultFilter)) {
                        return;
                    }

                    String ext = ((DefaultFileFilter) selectedFilter).getExtension();

                    if (!filename.toLowerCase().endsWith(ext)) {
                        filename_xmodel =filename+ ext;
                    }

                    if (new File(filename_xmodel).exists()
                            && JOptionPane.showConfirmDialog(graphComponent,
                            mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
                        return;
                    }

                //System.out.println("filename:"+filename);
                //System.out.println("filename_xmodel:"+filename_xmodel);
                //System.out.println("filename_xmi:"+filename_xmi);
                    // save as xmodel file
                mxCodec codec = new mxCodec();
                String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));

                try {
                    mxUtils.writeFile(xml, filename_xmodel);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                SimplifyModelInfo ModelInfo =new SimplifyModelInfo(
                        editor.getResourceFile(),
                        editor.getNew_owlResourceData().classMap,
                        editor.getNew_owlResourceData().objAttrMap,
                        editor.getNew_owlResourceData().dataAttrMap,
                        editor.getNew_owlResourceData().objMap,
                        main_objMap,vertex_objMap,vertex_main_objMap,edge_objMap
                );

                FileOutputStream outStream = null;
                filename_xmi = filename+".xmi";
                System.out.println("filename_xmi:"+filename_xmi);
                try {
                    outStream = new FileOutputStream(filename_xmi);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
                    objectOutputStream.writeObject(ModelInfo);

                    outStream.close();
                    System.out.println("successful");
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });

        open_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("打开：.......");
                isreOpen=true;
                graph.getModel().beginUpdate();
                try
                {
                    if (JOptionPane.showConfirmDialog(graphComponent, mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {

                        if (graph != null) {
                            //清空图案
                            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

                            String lastDir = null;
                            String wd = (lastDir != null) ? lastDir : FileUtil.getAppPath();

                            JFileChooser fc = new JFileChooser(wd);

                            // Adds file filter for supported file format
                            DefaultFileFilter defaultFilter = new DefaultFileFilter(".xmodel",
                                    mxResources.get("allSupportedFormats") + " (.xmodel)") {

                                public boolean accept(File file) {
                                    String lcase = file.getName().toLowerCase();
                                    return super.accept(file) || lcase.endsWith(".xmodel");
                                }
                            };
                            fc.addChoosableFileFilter(defaultFilter);
                            fc.setFileFilter(defaultFilter);
                            fc.setDialogTitle("打开文件");

                            int rc = fc.showDialog(null, AliasName.getAlias("open_file"));

                            if (rc == JFileChooser.APPROVE_OPTION) {
                                File sFile = fc.getSelectedFile();
                                lastDir = sFile.getParent();

                                if (sFile.getAbsolutePath().toLowerCase().endsWith(".xmodel")) {
                                    //打开xmodel文件
                                    System.out.println("open .xmodel file......");
                                    System.out.println("lastDir:"+lastDir);
                                    System.out.println("sFile.getAbsolutePath():"+sFile.getAbsolutePath());

                                    /*此段代码可以显示出xmodel文件*/
                                    if (sFile.getAbsolutePath() == null) {
                                        return;
                                    }
                                    Document document = null;
                                    try {
                                        document = mxXmlUtils.parseXml(FileUtil.readFile(new FileInputStream(sFile.getAbsolutePath())));
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    mxCodec codec = new mxCodec(document);
                                    codec.decode(document.getDocumentElement(),graph.getModel());

                                    //给相关信息重新赋值
                                    //首先得到.xmi文件的路径
                                    String str=sFile.getAbsolutePath();
                                    file_xmi=str.substring(0,str.indexOf("."))+".xmi";
                                    System.out.println("----file_xmi:"+file_xmi);
                                    resetData(file_xmi);
                                }
                            }
                        }
                    }
                    //paint(new_objMap);
                }
                finally
                {
                    graph.getModel().endUpdate();
                }
            }
        });
    }

    //绘制关系图，及关系图中发生的点击事件
    private void initselectHandler() {
        //selectHandler = new selectHandler(editor);
        //selectHandler.setVisible(true);
        //graphComponent.setSize(880,400);
        Dimension preferredSize3 = new Dimension(880,530);//设置尺寸
        graphComponent.setPreferredSize(preferredSize3);
        graphComponent.setBackground(Color.WHITE);
        //this.height=graphComponent.getHeight();
        //this.width=graphComponent.getWidth();
        this.height=graphComponent.getPreferredSize().height;
        this.width=graphComponent.getPreferredSize().width;
        System.out.println("height:"+height+"  width:"+width);
        //graphComponent.setBorder(null);
        //graphComponent.setDragEnabled(true);
        graph.getModel().beginUpdate();
        try
        {
            paint(new_objMap); //绘制关系图
        }
        finally
        {
            graph.getModel().endUpdate(); //刷新
        }

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {

            public void mouseReleased(MouseEvent e)
            {
                Object cell = graphComponent.getCellAt(e.getX(), e.getY());

                if (cell != null)
                {
                    System.out.println("cell="+graph.getLabel(cell));
                    if(graph.getLabel(cell)!=null&&graph.getLabel(cell)!=""){
                        System.out.println("is Vertex...");
                        OwlObject owlObject= vertex_main_objMap.get(graph.getLabel(cell));
                        selected_obj_name=owlObject.id;
                        owlObject_data_Change_Fun(owlObject);
                        //刷新内容
                        update_introductionPanel_data();
                        update_monitorPanel_data();
                        update_coninfoPanel_data();
                    }else{
                        System.out.println("is Edge...");

                        String[] strs=edge_objMap.get(cell).split("-");
                        String source_str=strs[0];
                        String target_str=strs[1];
                        OwlObject source=vertex_main_objMap.get(source_str);
                        OwlObject target=vertex_main_objMap.get(target_str);
                        edge_data_Change_Fun(source,target);

                        //刷新内容
                        update_introductionPanel_data();
                        update_monitorPanel_data();
                        update_coninfoPanel_data();
                    }
                }
            }
        });
    }

    private void initJPanel(){

        jTable=new JTable();
        scrollPanel=new JScrollPane(jTable);
        jTable.setRowHeight(30);
        jTable.setShowGrid(true);
        Dimension preferredSize1 = new Dimension(360,150);//设置尺寸
        scrollPanel.setPreferredSize(preferredSize1);

        introductionPanel=new JPanel();
        introductionPanel.setBorder(new TitledBorder("基本信息："));
        introductionPanel.setLayout(new FlowLayout());
        introductionPanel.add(scrollPanel);
        /*jTextField=new JTextField();
        introductionPanel.add(jTextField);
        jTextField.setVisible(false);*/

        monitorPanel=new JPanel();
        monitorPanel.setBorder(new TitledBorder("监测属性："));
        monitorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Dimension preferredSize = new Dimension(300,200);//设置尺寸
        monitorPanel.setPreferredSize(preferredSize);
        jRadioButton=new JRadioButton("是否添加到组态图中",true);
        jRadioButton.setVisible(false);

        coninfoPanel=new JPanel();
        coninfoPanel.setBorder(new TitledBorder("连接信息："));
        coninfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        buttonPanel=new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(open_btn);
        buttonPanel.add(save_btn);
        buttonPanel.add(return_btn);
        buttonPanel.add(diagram_btn);
        buttonPanel.add(refresh_btn);

        Dimension preferredSize3 = new Dimension(130,40);//设置尺寸
        buttonPanel.setPreferredSize(preferredSize3);

        chartPanel=new JPanel();
        chartPanel.setBorder(new TitledBorder("resource chart："));
        chartPanel.setLayout(new BorderLayout());
        Dimension preferredSize2 = new Dimension(880,600);//设置尺寸
        chartPanel.setPreferredSize(preferredSize2);
        chartPanel.add(graphComponent,BorderLayout.SOUTH);
        chartPanel.add(buttonPanel,BorderLayout.NORTH);

        selectPanel=new JPanel();
        //selectPanel.setBorder(new TitledBorder("resource select："));
        Dimension preferredSize4 = new Dimension(300,780);//设置尺寸
        selectPanel.setPreferredSize(preferredSize4);
        selectPanel.setLayout(new GridLayout(3,1));
        selectPanel.add(introductionPanel);
        selectPanel.add(monitorPanel);
        selectPanel.add(coninfoPanel);

        mainPanel=new JPanel();
        mainPanel.setSize(1000,660);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(chartPanel,BorderLayout.WEST);
        mainPanel.add(selectPanel,BorderLayout.CENTER);
        /*JPanel temp=new JPanel();
        temp.setSize(0,0);
        mainPanel.add(temp,BorderLayout.CENTER);*/
    }

    private void initFrame(){
        this.setTitle("-文件资源模板选择框-");//设置窗口标题内容
        Dimension screen = getToolkit().getScreenSize();
        this.setSize(1280, 680);//设置窗口大小,宽度500，高度400
        this.setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2);;//设置窗口位置为距离屏幕左边水平方向300，上方垂直方向200
        this.setVisible(true);//设置窗体可见
        this.setLayout(new BorderLayout());//设置窗体布局为流式布局。
        this.setResizable(true); //禁止改变大小在调用显示之
        this.addWindowListener (new WindowAdapter()
        {
            @Override
            public void windowClosing ( WindowEvent e )
            {
                dispose();
            }
        });
        this.add(mainPanel,BorderLayout.CENTER);
    }

    public void paint(Map<String, OwlObject> new_objMap){
        main_objMap.clear(); //<url,OwlObject> 存储类型为FOI的对象
        vertex_main_objMap.clear(); //<id,OwlObject> 方便查找对象信息
        vertex_objMap.clear(); //<id,Object>  方便查找图形对象，进行图形的增删改查
        init_main_objMap(new_objMap,main_objMap);

        //先判断个数
        int num= OwlResourceUtil.getFOINum(main_objMap);
        System.out.println("num:"+num);
        //分网格
        int part_num=getPart_num(num);
        int part_height=height/part_num;
        int part_width=width/part_num;

        //放置每个obj的位置
        int i=0;
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            //矩形左上角的坐标
            //int x=part_width*(i%part_num)+part_width/3;
            //int y=part_height*(i/part_num)+part_height/5;
            int x=part_width*(i%part_num)+part_width/3;
            int y=part_height*(i/part_num)+part_height/5;
            //绘制圆
            //Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,30);
            System.out.println("x:"+x+"   y:"+y);
            //显示的属性值
            String attr_str="";
            for (Map.Entry<OwlObjectAttribute, Set<OwlObject>> entry1 : entry.getValue().objAttrs.entrySet()) {
                if(entry1.getKey().id.equals("has_property")){
                    for (OwlObject obj : entry1.getValue()) {
                        if(obj.visible){
                            attr_str=attr_str+obj.id;
                            System.out.println("attr_str:"+attr_str+"\n\r"+"<br/>");
                        }
                    }
                }
            }

            Object obj=graph.insertVertex(parent, null,
                    entry.getValue().id, x, y, 140,80,
                    "shape=ellipse;perimeter=ellipsePerimeter");

            vertex_objMap.put(entry.getValue().id,obj);
            vertex_main_objMap.put(entry.getValue().id,entry.getValue());
            i++;
        }

        //将连接关系连接起来
        //根据坐标来找绘制的图形
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            entry.getValue().objAttrs.forEach((objAttr, objSet) -> {
                if(objAttr.id.equals("connect")){
                    objSet.forEach(obj2 -> {
                        if(obj2.visible) {
                            Object obj=graph.insertEdge(parent,null , null,
                                    vertex_objMap.get(entry.getValue().id), vertex_objMap.get(obj2.id)
                                    //,"strokeWidth=3"
                            );
                            edge_objMap.put(obj,entry.getValue().id+"-"+obj2.id);
                        }
                    });
                }
                if(objAttr.id.equals("data_trans")){
                    objSet.forEach(obj2 -> {
                        if(obj2.visible) {
                            Object obj=graph.insertEdge(parent,null , null,
                                    vertex_objMap.get(entry.getValue().id), vertex_objMap.get(obj2.id),
                                    "strokeColor=#A0A0A0"
                            );
                            edge_objMap.put(obj,entry.getValue().id+"-"+obj2.id);
                        }
                    });
                }
            });
        }

    }

    public Map<String, OwlObject> init_main_objMap(Map<String, OwlObject> objMap,Map<String, OwlObject> main_objMap){
        for (Map.Entry<String, OwlObject> entry : objMap.entrySet()) {
            if((findKind(entry.getValue().type).equals("FeatureOfInterest")
                    //||findKind(entry.getValue().type).equals("Site")
                    ||findKind(entry.getValue().type).equals("ControlRoom"))
                    &&entry.getValue().visible){
                main_objMap.put(entry.getKey(),entry.getValue());
            }
        }
        return main_objMap;
    }

    public int getPart_num(int num){
        int n;
        double m=Math.sqrt(num);
        System.out.println("m:"+m);
        n=(int)m;
        System.out.println("n:"+n);
        if(n==m){
            return n;
        }else{
            return n+1;
        }
    }

    private void update_introductionPanel_data(){
        /*jTextField.setText(info);
        if (info == null || info == "") {
            jTextField.setVisible(false);
        }else {
            jTextField.setVisible(true);
        }*/
        jTable.setModel(attrListModel);
        jTable.revalidate();
        introductionPanel.validate();
    }

    private void update_monitorPanel_data(){
        monitorPanel.removeAll();
        monitorPanel.repaint();
        monitorPanel.add(jRadioButton);

        if(jrbtn_list.size()==0){
            return;
        }else{
            for (JRadioButton jrb : jrbtn_list) {
                //jrb.setBackground(Color.WHITE);
                monitorPanel.add(jrb);
            }
        }

        monitorPanel.revalidate();
    }

    private void update_coninfoPanel_data(){

        coninfoPanel.removeAll();
        coninfoPanel.repaint();

        if(conn_list.size()==0){
            return;
        }else{

            for (Map.Entry<String,JRadioButton> entry : conn_list.entrySet()) {
                //entry.getValue().setBackground(Color.WHITE);
                coninfoPanel.add(entry.getValue());
            }
        }

        coninfoPanel.revalidate();
    }

    private void owlObject_data_Change_Fun(OwlObject owlObject){

        //为info(introduction面板赋值)
        info="Name:"+owlObject.id+"  Type:"+owlObject.type.id;

        jrbtn_list.clear();
        //if(findKind(owlObject.type).equals("FeatureOfInterest")){
         jRadioButton.setVisible(true);
         jRadioButton.setSelected(owlObject.visible);
        //}else{
        //    jRadioButton.setVisible(false);
        //}

        //用表格显示它的信息
        attrListModel = null;
        attrListModel = new AttrListModel(owlObject);

        conn_list.clear();
        for (Map.Entry<OwlObjectAttribute, Set<OwlObject>> entry : owlObject.objAttrs.entrySet()) {
            if(entry.getKey().id.equals("has_property")){
                for (OwlObject obj : entry.getValue()) {
                    JRadioButton jrb=new JRadioButton(obj.id);
                    jrb.setSelected(obj.visible);
                    jrb.setVisible(true);
                    jrbtn_list.add(jrb);
                    jrb.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(jrb.isSelected()){
                                obj.visible=true;
                            }
                            else{
                                obj.visible=false;
                            }
                        }
                    });
                }
            }

            if(entry.getKey().id.equals("connect")||entry.getKey().id.equals("data_trans")){
                for (OwlObject obj : entry.getValue()) {
                    if(obj.visible){
                        String s=owlObject.id+"-"+obj.id;
                        JRadioButton jrb=new JRadioButton(s);
                        jrb.setSelected(true);
                        jrb.setVisible(true);
                        conn_list.put(s,jrb);
                    }

                }
            }

            origin_objMap.forEach((uri, obj) -> {
                if(obj.id.equals(owlObject.id)){
                    obj.objAttrs.forEach((objAttr, objSet) -> {
                        if(objAttr.id.equals("connect")||objAttr.id.equals("data_trans")){
                            objSet.forEach(obj2 -> {
                                if(obj.visible&&obj2.visible){
                                    String s=obj.id+"-"+obj2.id;
                                    if(conn_list.get(s)==null){
                                        JRadioButton jrb=new JRadioButton(s);
                                        jrb.setVisible(true);
                                        jrb.setSelected(false);
                                        conn_list.put(s,jrb);
                                    }
                                }
                            });
                        }
                    });
                }
            });

            for (Map.Entry<String,JRadioButton> entry1 : conn_list.entrySet()) {
                entry1.getValue().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String str=entry1.getValue().getText();
                        int index=str.indexOf("-");
                        String first=str.substring(0,index);
                        String last=str.substring(index+1);
                        //先找出last对应的Owlobject
                        OwlObject last_obj=getObjectById(last);
                        new_objMap.forEach((uri, obj) -> {
                            if(obj.id.equals(first)){
                                obj.objAttrs.forEach((objAttr, objSet) -> {
                                    if(objAttr.id.equals("connect")||objAttr.id.equals("data_trans")){


                                        if(objSet.contains(last_obj)){
                                            if(!entry1.getValue().isSelected()){
                                                System.out.println(last_obj.id+"被移除");
                                                objSet.remove(last_obj);

                                            }
                                        }else{
                                            if(entry1.getValue().isSelected()){
                                                objSet.add(last_obj);
                                                System.out.println(last_obj.id+"被添加");
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }

    private void edge_data_Change_Fun(OwlObject source,OwlObject target){

        jrbtn_list.clear();
        conn_list.clear();
        //用表格显示它的信息
        attrListModel = null;
        Object[] columns={"数据属性","值"};//字段
        Object[][] data=new Object[2][2];//需要展示的数据，一般是二维数组
        data[0][0]="source";
        data[0][1]=source.id;
        data[1][0]="target";
        data[1][1]=target.id;
        attrListModel=new DefaultTableModel(data,columns);

        jRadioButton.setVisible(false);

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

    private void resetData(String file_xmi){

        OwlResourceData new_open_owlResourceData=new OwlResourceData();
        new_open_owlResourceData.model=null;
        OwlResourceData origin_open_owlResourceData=new OwlResourceData();
        origin_open_owlResourceData.model=null;
        FileInputStream new_freader;
        FileInputStream origin_freader;
        try {
            new_freader = new FileInputStream(file_xmi);
            origin_freader = new FileInputStream(file_xmi);
            ObjectInputStream new_objectInputStream = new ObjectInputStream(new_freader);
            ObjectInputStream origin_objectInputStream = new ObjectInputStream(origin_freader);
            SimplifyModelInfo new_simplifyModelInfo=(SimplifyModelInfo) new_objectInputStream.readObject();
            SimplifyModelInfo origin_simplifyModelInfo=(SimplifyModelInfo) origin_objectInputStream.readObject();

            filepath = new_simplifyModelInfo.getFilePath();
            new_open_owlResourceData.classMap=new_simplifyModelInfo.getClassMap();
            new_open_owlResourceData.objAttrMap=new_simplifyModelInfo.getObjAttrMap();
            new_open_owlResourceData.dataAttrMap=new_simplifyModelInfo.getDataAttrMap();
            new_open_owlResourceData.objMap=new_simplifyModelInfo.getObjMap();

            origin_open_owlResourceData.classMap=origin_simplifyModelInfo.getClassMap();
            origin_open_owlResourceData.objAttrMap=origin_simplifyModelInfo.getObjAttrMap();
            origin_open_owlResourceData.dataAttrMap=origin_simplifyModelInfo.getDataAttrMap();
            origin_open_owlResourceData.objMap=origin_simplifyModelInfo.getObjMap();

            main_objMap.clear();
            main_objMap = new_simplifyModelInfo.getMain_objMap();
            vertex_objMap.clear();
            vertex_objMap = new_simplifyModelInfo.getVertex_objMap() ;
            vertex_main_objMap.clear();
            vertex_main_objMap = new_simplifyModelInfo.getVertex_main_objMap();
            edge_objMap.clear();
            edge_objMap = new_simplifyModelInfo.getEdge_objMap();

            new_owlResourceData=null;
            new_owlResourceData=new_open_owlResourceData;
            new_objMap.clear();
            new_objMap=new_owlResourceData.objMap;

            origin_owlResourceData=null;
            origin_owlResourceData=origin_open_owlResourceData;
            origin_objMap.clear();
            origin_objMap=origin_owlResourceData.objMap;

            editor.setNew_owlResourceData(new_owlResourceData);
            editor.setOrigin_owlResourceData(origin_owlResourceData);
            editor.setResourceFile(filepath);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private OwlResourceData readtoGetOwlResourceData(String file_xmi){
        OwlResourceData open_owlResourceData=new OwlResourceData();
        open_owlResourceData.model=null;
        FileInputStream freader;

        try {
            freader = new FileInputStream(file_xmi);
            ObjectInputStream objectInputStream = new ObjectInputStream(freader);
            SimplifyModelInfo simplifyModelInfo=(SimplifyModelInfo) objectInputStream.readObject();

            filepath = simplifyModelInfo.getFilePath();
            open_owlResourceData.classMap=simplifyModelInfo.getClassMap();
            open_owlResourceData.objAttrMap=simplifyModelInfo.getObjAttrMap();
            open_owlResourceData.dataAttrMap=simplifyModelInfo.getDataAttrMap();
            open_owlResourceData.objMap=simplifyModelInfo.getObjMap();

            return open_owlResourceData;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
