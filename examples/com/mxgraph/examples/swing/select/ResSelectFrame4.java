package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.match.ModifyTemplateCore;
import com.mxgraph.examples.swing.match.ResMatchCore;
import com.mxgraph.examples.swing.owl.*;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxXmlUtils;
import org.jfree.ui.action.ActionButton;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.*;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 22:16 2018/12/13
 * @Modify By:
 */
public class ResSelectFrame4  extends Frame{
    private OwlResourceData origin_owlResourceData;
    private OwlResourceData new_owlResourceData;
    private BasicGraphEditor editor;
    private Map<String, OwlObject> origin_objMap;
    private Map<String, OwlObject> new_objMap;

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel contentPanel;
    private JPanel objPanel;
    private JPanel objinfoPanel;
    private JPanel introductionPanel;
    private JPanel monitorPanel;
    private JPanel coninfoPanel;

    private JButton before_btn;
    private JButton after_btn;

    private Map<String,DefaultMutableTreeNode> tree_map;
    private DefaultMutableTreeNode top;
    private final JTree tree ;

    private JTextField jTextField;//显示introductionPanel的信息
    private String info;
    private JRadioButton jRadioButton;//是否添加到组态图中的单选框
    private String selected_obj_name="";

    private Set<JRadioButton> jrbtn_list=new HashSet<>();
    private Map<String,JRadioButton> conn_list=new HashMap<>();

    private ActionListener before_btn_listener;
    private ActionListener after_btn_listener;

    public ResSelectFrame4(BasicGraphEditor editor,OwlResourceData origin_owlResourceData) throws HeadlessException {
        this.editor=editor;
        this.origin_owlResourceData=origin_owlResourceData;
        this.new_owlResourceData=editor.getNew_owlResourceData();

        this.origin_objMap=this.origin_owlResourceData.objMap;
        this.new_objMap=new_owlResourceData.objMap;

        tree_map=new HashMap<>();
        top = new DefaultMutableTreeNode("owl:Thing");
        tree = new JTree(top);

        initPanel();
        initFrame();
        init_objPanel_data();

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null)
                    return;
                Object object = node.getUserObject();

                if (node.isLeaf()) {
                    OwlObject owlObj = (OwlObject) object;
                    selected_obj_name=owlObj.id;
                    //重新显示面板的内容
                    owlObject_data_Change_Fun(owlObj);
                    update_introductionPanel_data(info);
                    update_monitorPanel_data();
                    update_coninfoPanel_data();
                }else{
                    if(!object.toString().equals("owl:Thing")){
                        OwlClass owlClass = (OwlClass) object;
                        //重新显示面板的内容
                        owlClass_data_Change_Fun(owlClass);
                        update_introductionPanel_data(info);
                        update_monitorPanel_data();
                        update_coninfoPanel_data();
                    }
                }
            }
        });
        jRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        before_btn_listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                  System.out.println("改动前......");
                  drawDiagram(origin_objMap,"改动前示意图");
            }
        };
        after_btn_listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                  System.out.println("改动后......");
                  drawDiagram(new_objMap,"改动后示意图");
            }
        };

        before_btn.addActionListener(before_btn_listener);
        after_btn.addActionListener(after_btn_listener);
    }

    /*初始化各个面板*/
    private void initPanel(){
        //先初始化子Panel,再初始化父Panel
        //初始化introductionPanel，monitorPanel， coninfoPanel
        introductionPanel=new JPanel();
        introductionPanel.setBackground(Color.WHITE);
        introductionPanel.setBorder(new TitledBorder("introduction："));
        introductionPanel.setLayout(new FlowLayout());

        jTextField=new JTextField();
        introductionPanel.add(jTextField);
        jTextField.setVisible(false);
        jTextField.setBackground(Color.WHITE);

        monitorPanel=new JPanel();
        monitorPanel.setBackground(Color.WHITE);
        monitorPanel.setBorder(new TitledBorder("monitor_property："));
        monitorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Dimension preferredSize = new Dimension(130,35);//设置尺寸
        monitorPanel.setPreferredSize(preferredSize);

        jRadioButton=new JRadioButton("是否添加到组态图中",true);
        jRadioButton.setVisible(false);

        coninfoPanel=new JPanel();
        coninfoPanel.setBorder(new TitledBorder("coninfo："));
        coninfoPanel.setLayout(new FlowLayout());
        coninfoPanel.setBackground(Color.WHITE);

        //初始化objPanel
        objPanel=new JPanel();
        objPanel.setBackground(Color.WHITE);
        objPanel.setBorder(new TitledBorder("object："));
        objPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        objPanel.add(tree);

        //初始化objinfoPanel
        objinfoPanel=new JPanel();
        objinfoPanel.setBackground(Color.WHITE);
        objinfoPanel.setLayout(new GridLayout(3,1));
        objinfoPanel.add(introductionPanel);
        objinfoPanel.add(monitorPanel);
        objinfoPanel.add(coninfoPanel);


        //初始化contentPanel
        contentPanel=new JPanel();
        coninfoPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        Dimension preferredSize2 = new Dimension(400,35);//设置尺寸
        objPanel.setPreferredSize(preferredSize2);
        contentPanel.add(objPanel,BorderLayout.WEST);
        contentPanel.add(objinfoPanel,BorderLayout.CENTER);

        //初始化topPanel
        topPanel=new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        before_btn=new JButton("改动前的图");
        after_btn=new JButton("改动后的图");
        before_btn.setFocusable(false);
        after_btn.setFocusable(false);
        topPanel.add(before_btn);
        topPanel.add(after_btn);

        //初始化mainPanel
        mainPanel=new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel,BorderLayout.NORTH);
        mainPanel.add(contentPanel,BorderLayout.CENTER);

    }
    /*初始化主frame*/
    private void initFrame(){
        this.setTitle("-文件资源模板选择框-");//设置窗口标题内容
        this.setSize(1500, 800);//设置窗口大小,宽度500，高度400
        this.setLocation(200, 100);//设置窗口位置为距离屏幕左边水平方向300，上方垂直方向200
        this.setBackground(Color.WHITE);
        this.setVisible(true);//设置窗体可见
        this.setLayout(new BorderLayout());//设置窗体布局为流式布局。
        this.setResizable(true); //禁止改变大小在调用显示之前

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

    private void init_objPanel_data(){
        //应该先找到树的次根节点都有哪些
        init_root_data();

        for (Map.Entry<String, OwlObject> entry : new_objMap.entrySet()) {

            //遍历，把class都加到节点中
            OwlClass owlClass=entry.getValue().type;
            OwlClass parentClass=owlClass.parentClass;

            while(owlClass!=null&&parentClass!=null){
                DefaultMutableTreeNode child_node;
                DefaultMutableTreeNode node;
                if(!tree_map.containsKey(owlClass.id)){
                    child_node = new DefaultMutableTreeNode(owlClass);
                    tree_map.put(owlClass.id,child_node);
                }else{
                    child_node=tree_map.get(owlClass.id);
                }

                if(!tree_map.containsKey(parentClass.id)){
                    node = new DefaultMutableTreeNode(parentClass);
                    tree_map.put(parentClass.id,node);
                }else{
                    node=tree_map.get(parentClass.id);
                }
                node.add(child_node);
                owlClass=owlClass.parentClass;
                parentClass=owlClass.parentClass;
            }

            //把叶节点(obj)加入
            if(!tree_map.containsKey(entry.getValue().id)){
                DefaultMutableTreeNode left_node = new DefaultMutableTreeNode(entry.getValue());
                tree_map.put(entry.getValue().id,left_node);
                DefaultMutableTreeNode parent_node = tree_map.get(entry.getValue().type.id);
                if(parent_node!=null){
                    parent_node.add(left_node);
                }

            }
        }

    }

    private void init_root_data(){
        for (Map.Entry<String, OwlObject> entry : new_objMap.entrySet()) {
            OwlClass root_class=findKindClass(entry.getValue().type);
            if(!tree_map.containsKey(root_class.id)){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(root_class);
                tree_map.put(root_class.id,node);
                top.add(node);
            }
        }
    }

    private void owlObject_data_Change_Fun(OwlObject owlObject){

        //为info(introduction面板赋值)
        info="Name:"+owlObject.id+"  Type:"+owlObject.type.id;

        jrbtn_list.clear();
        if(findKind(owlObject.type).equals("FeatureOfInterest")){
            jRadioButton.setVisible(true);
            jRadioButton.setSelected(owlObject.visible);
        }else{
            jRadioButton.setVisible(false);
        }

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

            if(entry.getKey().id.equals("connect")){
                for (OwlObject obj : entry.getValue()) {
                    String s=owlObject.id+"-"+obj.id;
                    JRadioButton jrb=new JRadioButton(s);
                    jrb.setSelected(true);
                    jrb.setVisible(true);
                    conn_list.put(s,jrb);
                }
            }

            origin_objMap.forEach((uri, obj) -> {
                if(obj.id.equals(owlObject.id)){
                    obj.objAttrs.forEach((objAttr, objSet) -> {
                        if(objAttr.id.equals("connect")){
                            objSet.forEach(obj2 -> {
                                String s=obj.id+"-"+obj2.id;
                                if(conn_list.get(s)==null){
                                    JRadioButton jrb=new JRadioButton(s);
                                    jrb.setVisible(true);
                                    jrb.setSelected(false);
                                    conn_list.put(s,jrb);
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
                                    if(objAttr.id.equals("connect")){
                                        if(objSet.contains(last_obj)){
                                            if(!entry1.getValue().isSelected()){
                                                objSet.remove(last_obj);
                                            }
                                        }else{
                                            if(entry1.getValue().isSelected()){
                                                objSet.add(last_obj);
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

    private void owlClass_data_Change_Fun(OwlClass owlClass){

        //为info(introduction面板赋值)
        info="Name:"+owlClass.id;
        if(owlClass.parentClass!=null){
            info=info+"  Type:"+owlClass.parentClass.id;
        }
        //清空monitor_property
        jRadioButton.setVisible(false);
        jrbtn_list.clear();

        //清空coninfo
        conn_list.clear();
    }

    private void update_introductionPanel_data(String info){
        jTextField.setText(info);
        if (info == null || info == "") {
            jTextField.setVisible(false);
        }else {
            jTextField.setVisible(true);
        }
    }

    private void update_monitorPanel_data(){
        monitorPanel.removeAll();
        monitorPanel.repaint();
        jRadioButton.setBackground(Color.WHITE);
        monitorPanel.add(jRadioButton);

        if(jrbtn_list.size()==0){
            return;
        }else{
            for (JRadioButton jrb : jrbtn_list) {
                jrb.setBackground(Color.WHITE);
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
                entry.getValue().setBackground(Color.WHITE);
                coninfoPanel.add(entry.getValue());
            }
        }

        coninfoPanel.revalidate();
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

    private void drawDiagram(Map<String, OwlObject> objMap,String title){
        //弹出一个新的窗口示意
        JFrame jFrame=new JFrame(title);
        jFrame.setSize(800, 600);//设置窗口大小,宽度500，高度400
        jFrame.setLocation(300, 200);//设置窗口位置为距离屏幕左边水平方向300，上方垂直方向200
        jFrame.setBackground(Color.WHITE);
        jFrame.setVisible(true);//设置窗体可见
        jFrame.setLayout(new FlowLayout());//设置窗体布局为流式布局。
        jFrame.setResizable(true); //禁止改变大小在调用显示之前
        jFrame.addWindowListener (new WindowAdapter()
        {
            @Override
            public void windowClosing ( WindowEvent e )
            {
                jFrame.dispose();
            }
        });

        GraphPanel graphPanel=new GraphPanel(objMap);
        Dimension preferredSize1 = new Dimension(800,600);//设置尺寸
        graphPanel.setPreferredSize(preferredSize1);
        jFrame.add(graphPanel);

    }
}
