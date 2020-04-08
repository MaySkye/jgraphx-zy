package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.GraphEditor;
import com.mxgraph.examples.swing.decode.*;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import com.mxgraph.examples.swing.frame.CellEditorFrame.CustomGraphComponent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 20:45 2019/12/25
 * @Modify By:
 */
public class CellManagerFrame extends JFrame {
    private Toolkit kit = Toolkit.getDefaultToolkit();
    //获取屏幕的宽度
    public int getWidth() {
        return kit.getScreenSize().width;
    }
    //获取屏幕的高度
    public int getHeight() {
        return kit.getScreenSize().height;
    }
    //设置界面的大小
    private static final int width = 800;
    private static final int height = 600;
    private static final int DEFAULT_SIZE = 24;
    private static String appPath = FileUtil.getRootPath().substring(1);
    private static String opticalDeviceTemplate =appPath+"/examples/com/mxgraph/examples/swing/config_files/cell_template_optical_device";
    private static String edge_template=appPath+"/examples/com/mxgraph/examples/swing/config_files/edge_template";
    private static String noimagePic=appPath+"/examples/com/mxgraph/examples/swing/images/cell_manage/backpic.png";
    private static String imagedir=appPath+"/examples/com/mxgraph/examples/swing/images/cell_manage";
    private JPanel btnPanel;
    private JPanel showPanel;
    private JPanel kindPanel;
    private JPanel cellPanel;
    private JPanel linkPanel;
    private JFrame cellLeadInFrame;
    private JFrame cellAddFrame;
    private JFrame linkAddFrame;

    private ArrayList<String> device_names=new ArrayList<>();
    private ArrayList<String> device_icons=new ArrayList<>();
    private Map<String,JLabel> device_map=new HashMap<>();

    private ArrayList<String> link_names=new ArrayList<>();
    private ArrayList<String> link_icons=new ArrayList<>();
    private Map<String,JLabel> link_map=new HashMap<>();

    private BasicGraphEditor editor;
    private JTabbedPane libraryPane;
    private  Map<String, mxCell> AllCellMap = new HashMap<>();
    private mxGraph graph ;

    public CellManagerFrame(BasicGraphEditor editor)  {
        this.editor=editor;
        libraryPane = editor.getLibraryPane();
        AllCellMap = editor.getAllCellMap();
        graph = editor.getGraphComponent().getGraph();
        //设置界面大小不可拉伸
        setResizable(false);
        setSize(width, height);
        //设置中心位置
        setLocation(getWidth() / 2 - width / 2, getHeight() / 2 - height / 2);
        setTitle("领域图元管理");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
       /* try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            System.exit(1);
        }*/
        init();
        showpage();
        kindpage();
        cellpage();
        linkpage();
    }

    public void init(){
        btnPanel = new JPanel();
        showPanel = new JPanel();
        kindPanel = new JPanel();
        cellPanel = new JPanel();
        linkPanel = new JPanel();
        cellLeadInFrame = new JFrame();
        cellAddFrame = new JFrame();
        linkAddFrame = new JFrame();

        initFrame(cellLeadInFrame,"图元引入");
        initFrame(cellAddFrame,"引入设备图元");
        initFrame(linkAddFrame,"引入连线图元");
    }

    public void initFrame(JFrame jFrame,String title){
        jFrame.setResizable(false);
        jFrame.setSize(width-50, height-50);
        //设置中心位置
        jFrame.setLocation(getWidth() / 2 - width / 2+50, getHeight() / 2 - height / 2+50);
        jFrame.setTitle(title);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setVisible(false);
    }

    public void showpage(){
        showPanel.setLayout(null);
        JLabel label1= new JLabel("设备图元",JLabel.CENTER);
        JLabel label2= new JLabel("连线图元",JLabel.CENTER);
        label1.setBounds(20, 24, 80, 30);
        label2.setBounds(100, 24, 80, 30);
        label1.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        label2.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
        label1.revalidate();
        label2.revalidate();

        JButton jbt1=new JButton("设备图元");
        JButton jbt2=new JButton("连线图元");
        jbt1.setBorderPainted(false);
        jbt2.setBorderPainted(false);
        JButton jbt3=new JButton("图元引入");
        JButton jbt4=new JButton("图元制作");
        jbt3.setBorder(new RoundBorder());
        jbt4.setBorder(new RoundBorder());

        jbt1.setBounds(20,  24, 80, 30);
        jbt2.setBounds(100, 24, 80, 30);
        //jbt3.setBounds(600, 20, 70, 30);
        jbt3.setBounds(580, 20, 100, 30);
        jbt4.setBounds(690, 20, 100, 30);
        JPanel imagePanel=new JPanel();
        imagePanel.setBounds(20, 55, 740, 480);
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        imagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane sp=new JScrollPane(imagePanel);
        /*--------------------------------------------------*/
        label1.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        label2.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
        try {
            showDeviceCell(imagePanel, opticalDeviceTemplate);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        revalidate();
        /*--------------------------------------------------*/
        label1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label1.setBorder(BorderFactory.createLineBorder(Color.black, 2));
                label2.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
                try {
                    showDeviceCell(imagePanel, opticalDeviceTemplate);
                } catch (ParserConfigurationException e1) {
                    e1.printStackTrace();
                } catch (DocumentException e1) {
                    e1.printStackTrace();
                }
                revalidate();
            }
        });
        label2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label1.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
                label2.setBorder(BorderFactory.createLineBorder(Color.black, 2));
                showLinkCell(imagePanel,edge_template);
                revalidate();
            }
        });
        //图元引入
        jbt3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellLeadInFrame.setVisible(true);
            }
        });
        //图元制作
        jbt4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("图元制作");
                CustomGraphComponent c = new CustomGraphComponent(new CellEditorFrame.CustomGraph());
                CellEditorFrame editor = new CellEditorFrame("图元编辑器", c);
                JFrame cellMakeframe = editor.createCellFrame();
                c.setFrame(cellMakeframe);
                cellMakeframe.setVisible(true);
            }
        });
        //showPanel.add(jbt1);
        //showPanel.add(jbt2);
        showPanel.add(label1);
        showPanel.add(label2);
        showPanel.add(jbt3);
        showPanel.add(jbt4);
        showPanel.add(sp);
        showPanel.add(imagePanel);
        this.add(showPanel);
        revalidate();
    }

    public void kindpage(){
        //kindPanel.setLayout(new BoxLayout(kindPanel, BoxLayout.Y_AXIS));
        //kindPanel.add(Box.createVerticalStrut(15));
        kindPanel.setLayout(new BorderLayout(5,50));
        JPanel j1=new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel j2=new JPanel(new FlowLayout(FlowLayout.CENTER,50,30));
        j1.setPreferredSize(new Dimension(500, 150));
        j2.setPreferredSize(new Dimension(500, 350));

        JLabel label= new JLabel("请选择要添加的图元种类：");
        label.setFont(new Font("Serif", Font.PLAIN, 22));
        label.setPreferredSize(new Dimension(500, 250));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);

        j1.add(label);

        JRadioButton b1 = new JRadioButton("设备");
        JRadioButton b2 = new JRadioButton("连线");
        b1.setFont(new Font("Serif", Font.PLAIN, 18));
        b2.setFont(new Font("Serif", Font.PLAIN, 18));
        ButtonGroup g = new ButtonGroup();  //单选按钮组
        g.add(b1);
        g.add(b2);
        j2.add(b1);
        j2.add(b2);

        kindPanel.add(j1,BorderLayout.NORTH);
        kindPanel.add(j2,BorderLayout.CENTER);
        cellLeadInFrame.add(kindPanel);
        cellLeadInFrame.revalidate();
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellLeadInFrame.setVisible(false);
                cellAddFrame.setVisible(true);
            }
        });
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellLeadInFrame.setVisible(false);
                linkAddFrame.setVisible(true);
            }
        });
    }

    public void cellpage(){
        cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.Y_AXIS));
        cellPanel.add(Box.createVerticalStrut(15));
        JPanel j1=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j2=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j3=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j4=new JPanel(new FlowLayout(FlowLayout.CENTER,50,30));
        JPanel j5=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j6=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j7=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j8=new JPanel(new FlowLayout(FlowLayout.LEFT));
        //j1
        JLabel label1= new JLabel("图片：");
        label1.setFont(new Font("Serif", Font.PLAIN,16));
        JLabel label11=new JLabel();
        label11.setPreferredSize(new Dimension(250, 30));
        JPanel jPanel13=new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel13.add(label1);
        jPanel13.add(label11);
        //按钮
        JPanel jPanel11=new JPanel(new FlowLayout(FlowLayout.CENTER,30,0));
        JButton jbt1=new JButton("选择");
        JButton jbt2=new JButton("上传");
        jPanel11.add(jbt1);
        jPanel11.add(jbt2);
        //按钮和路径
        JPanel jPanel12=new JPanel(new BorderLayout(5,10));
        jPanel12.add(jPanel13,BorderLayout.CENTER);
        jPanel12.add(jPanel11,BorderLayout.SOUTH);

        JLabel jLabel1=new JLabel();
        ImageIcon icon=new ImageIcon(noimagePic);
        jLabel1.setIcon(icon);    //设置标签要显示的图标
        jLabel1.setPreferredSize(new Dimension(80, 80));
        final File[] file = new File[1];
        jbt1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 file[0] =fileChooser();
                 label11.setText(file[0].getAbsolutePath());
            }
        });
        /*
        * 上传：将图片添加到项目中
        * */
        jbt2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("file[0].getAbsolutePath():"+file[0].getAbsolutePath());
                ImageIcon icon1=new ImageIcon(file[0].getAbsolutePath());
                jLabel1.setIcon(icon1);
                File startFile=file[0];
                File endDirection=new File(imagedir);
                File endFile=new File(endDirection+ File.separator+ startFile.getName());
                if (!endFile.exists()) {
                    try {
                        Files.copy(startFile.toPath(), endFile.toPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    endFile.delete();
                    try {
                        Files.copy(startFile.toPath(), endFile.toPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        //j1.add(label1);
        //j1.add(jTextField1);
        //j1.add(jbt1);
        //j1.add(jbt2);
        j1.add(jPanel12);
        j1.add(jLabel1);
        //j2
        JLabel label2= new JLabel("名称（类型）：");
        label2.setFont(new Font("Serif", Font.PLAIN,16));
        JTextField jTextField2=new JTextField();
        jTextField2.setPreferredSize(new Dimension(300, 30));
        j2.add(label2);
        j2.add(jTextField2);
        //j3
        JLabel label3= new JLabel("宽度：");
        label3.setFont(new Font("Serif", Font.PLAIN,16));
        JTextField jTextField3=new JTextField();
        jTextField3.setPreferredSize(new Dimension(100, 30));
        JLabel label4= new JLabel("      高度：");
        label4.setFont(new Font("Serif", Font.PLAIN,16));
        JTextField jTextField4=new JTextField();
        jTextField4.setPreferredSize(new Dimension(100, 30));
        j3.add(label3);
        j3.add(jTextField3);
        j3.add(label4);
        j3.add(jTextField4);

        //j5
        JLabel label5= new JLabel("port1：");
        label5.setFont(new Font("Serif", Font.PLAIN,18));
        JLabel label51= new JLabel("  attr：");
        label51.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox51=new JComboBox();
        comboBox51.addItem("fiber");
        comboBox51.addItem("data");
        JLabel label52= new JLabel("  location：");
        label52.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox52=new JComboBox();
        comboBox52.addItem("up");
        comboBox52.addItem("down");
        comboBox52.addItem("left");
        comboBox52.addItem("right");
        JLabel label53= new JLabel("  direction：");
        label53.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox53=new JComboBox();
        comboBox53.addItem("in");
        comboBox53.addItem("out");
        JLabel label54= new JLabel("  x：");
        label54.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField54=new JTextField();
        jTextField54.setPreferredSize(new Dimension(50, 30));
        JLabel label55= new JLabel("  y：");
        label55.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField55=new JTextField();
        jTextField55.setPreferredSize(new Dimension(50, 30));
        j5.add(label5);
        j5.add(label51);
        j5.add(comboBox51);
        j5.add(label52);
        j5.add(comboBox52);
        j5.add(label53);
        j5.add(comboBox53);
        j5.add(label54);
        j5.add(jTextField54);
        j5.add(label55);
        j5.add(jTextField55);
        //j6
        JLabel label6= new JLabel("port2：");
        label6.setFont(new Font("Serif", Font.PLAIN,18));
        JLabel label61= new JLabel("  attr：");
        label61.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox61=new JComboBox();
        comboBox61.addItem("fiber");
        comboBox61.addItem("data");
        JLabel label62= new JLabel("  location：");
        label62.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox62=new JComboBox();
        comboBox62.addItem("up");
        comboBox62.addItem("down");
        comboBox62.addItem("left");
        comboBox62.addItem("right");
        JLabel label63= new JLabel("  direction：");
        label63.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox63=new JComboBox();
        comboBox63.addItem("in");
        comboBox63.addItem("out");
        JLabel label64= new JLabel("  x：");
        label64.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField64=new JTextField();
        jTextField64.setPreferredSize(new Dimension(50, 30));
        JLabel label65= new JLabel("  y：");
        label65.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField65=new JTextField();
        jTextField65.setPreferredSize(new Dimension(50, 30));
        j6.add(label6);
        j6.add(label61);
        j6.add(comboBox61);
        j6.add(label62);
        j6.add(comboBox62);
        j6.add(label63);
        j6.add(comboBox63);
        j6.add(label64);
        j6.add(jTextField64);
        j6.add(label65);
        j6.add(jTextField65);
        //j7
        JLabel label7= new JLabel("port3：");
        label7.setFont(new Font("Serif", Font.PLAIN,18));
        JLabel label71= new JLabel("  attr：");
        label71.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox71=new JComboBox();
        comboBox71.addItem("fiber");
        comboBox71.addItem("data");
        JLabel label72= new JLabel("  location：");
        label72.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox72=new JComboBox();
        comboBox72.addItem("up");
        comboBox72.addItem("down");
        comboBox72.addItem("left");
        comboBox72.addItem("right");
        JLabel label73= new JLabel("  direction：");
        label73.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox73=new JComboBox();
        comboBox73.addItem("in");
        comboBox73.addItem("out");
        JLabel label74= new JLabel("  x：");
        label74.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField74=new JTextField();
        jTextField74.setPreferredSize(new Dimension(50, 30));
        JLabel label75= new JLabel("  y：");
        label75.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField75=new JTextField();
        jTextField75.setPreferredSize(new Dimension(50, 30));
        j7.add(label7);
        j7.add(label71);
        j7.add(comboBox71);
        j7.add(label72);
        j7.add(comboBox72);
        j7.add(label73);
        j7.add(comboBox73);
        j7.add(label74);
        j7.add(jTextField74);
        j7.add(label75);
        j7.add(jTextField75);
        //j8
        JLabel label8= new JLabel("port4：");
        label8.setFont(new Font("Serif", Font.PLAIN,18));
        JLabel label81= new JLabel("  attr：");
        label81.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox81=new JComboBox();
        comboBox81.addItem("fiber");
        comboBox81.addItem("data");
        JLabel label82= new JLabel("  location：");
        label82.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox82=new JComboBox();
        comboBox82.addItem("up");
        comboBox82.addItem("down");
        comboBox82.addItem("left");
        comboBox82.addItem("right");
        JLabel label83= new JLabel("  direction：");
        label83.setFont(new Font("Serif", Font.PLAIN,18));
        JComboBox comboBox83=new JComboBox();
        comboBox83.addItem("in");
        comboBox83.addItem("out");
        JLabel label84= new JLabel("  x：");
        label84.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField84=new JTextField();
        jTextField84.setPreferredSize(new Dimension(50, 30));
        JLabel label85= new JLabel("  y：");
        label85.setFont(new Font("Serif", Font.PLAIN,18));
        JTextField jTextField85=new JTextField();
        jTextField85.setPreferredSize(new Dimension(50, 30));
        j8.add(label8);
        j8.add(label81);
        j8.add(comboBox81);
        j8.add(label82);
        j8.add(comboBox82);
        j8.add(label83);
        j8.add(comboBox83);
        j8.add(label84);
        j8.add(jTextField84);
        j8.add(label85);
        j8.add(jTextField85);
        //j4
        JButton jbt3=new JButton("取消");
        JButton jbt4=new JButton("确定");
        j4.add(jbt3);
        j4.add(jbt4);
        //取消
        jbt3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellAddFrame.setVisible(false);
            }
        });
        //确定：添加到xml中
        jbt4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File file1 = new File(opticalDeviceTemplate);
                SAXReader reader=new SAXReader();
                Document doc = null;
                try {
                    doc = reader.read(opticalDeviceTemplate);
                } catch (DocumentException e1) {
                    e1.printStackTrace();
                }
                //通过文档对象获得该文档对象的根节点
                Element root=doc.getRootElement();

                //创建一个新的cell节点
                Element cell = root.addElement("cell");
                //创建cell的几个子节点
                Element port1 = cell.addElement("port");
                Element port2 = cell.addElement("port");
                Element port3 = cell.addElement("port");
                Element port4 = cell.addElement("port");
                //给子节点设置值
                port1.addAttribute("attr",(String) comboBox51.getSelectedItem());
                port1.addAttribute("location",(String) comboBox52.getSelectedItem());
                port1.addAttribute("direction",(String) comboBox53.getSelectedItem());
                port1.addAttribute("x",jTextField54.getText());
                port1.addAttribute("y",jTextField55.getText());

                port2.addAttribute("attr",(String) comboBox61.getSelectedItem());
                port2.addAttribute("location",(String) comboBox62.getSelectedItem());
                port2.addAttribute("direction",(String) comboBox63.getSelectedItem());
                port2.addAttribute("x",jTextField64.getText());
                port2.addAttribute("y",jTextField65.getText());

                port3.addAttribute("attr",(String) comboBox71.getSelectedItem());
                port3.addAttribute("location",(String) comboBox72.getSelectedItem());
                port3.addAttribute("direction",(String) comboBox73.getSelectedItem());
                port3.addAttribute("x",jTextField74.getText());
                port3.addAttribute("y",jTextField75.getText());

                port4.addAttribute("attr",(String) comboBox81.getSelectedItem());
                port4.addAttribute("location",(String) comboBox82.getSelectedItem());
                port4.addAttribute("direction",(String) comboBox83.getSelectedItem());
                port4.addAttribute("x",jTextField84.getText());
                port4.addAttribute("y",jTextField85.getText());
                //给person的id设置值
                cell.addAttribute("name", jTextField2.getText());
                cell.addAttribute("type", jTextField2.getText());
                cell.addAttribute("width", jTextField3.getText());
                cell.addAttribute("height", jTextField4.getText());
                cell.addAttribute("icon", "/com/mxgraph/examples/swing/images/cell_manage/"+file[0].getName());
                cell.addAttribute("style", "roundImage;image=/com/mxgraph/examples/swing/images/cell_manage/"+file[0].getName());

                OutputFormat outputFormat= OutputFormat.createPrettyPrint();
                outputFormat.setEncoding("UTF-8");
                XMLWriter xmlWriter= null;
                try {
                    xmlWriter = new XMLWriter(new FileWriter(file1),outputFormat);
                    xmlWriter.write(doc);//开始写入XML文件   写入Document对象
                    xmlWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showConfirmDialog(null, "添加成功", "成功", JOptionPane.PLAIN_MESSAGE);
                cellAddFrame.setVisible(false);
                showPanel.removeAll();
                showPanel.revalidate();
                showpage();
                refresh_libraryPane();
            }
        });
        cellPanel.add(j1);
        cellPanel.add(j2);
        cellPanel.add(j3);
        cellPanel.add(j5);
        cellPanel.add(j6);
        cellPanel.add(j7);
        cellPanel.add(j8);
        cellPanel.add(j4);
        cellAddFrame.add(cellPanel);
        cellAddFrame.revalidate();
    }

    public void linkpage(){
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));
        linkPanel.add(Box.createVerticalStrut(15));
        JPanel j1=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j2=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j3=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j4=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel j5=new JPanel(new FlowLayout(FlowLayout.CENTER,50,30));
        //j1
        JLabel label1= new JLabel("图片：");
        label1.setFont(new Font("Serif", Font.PLAIN,16));
        JLabel jLabel11=new JLabel();
        jLabel11.setPreferredSize(new Dimension(250, 30));
        JPanel jPanel13=new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel13.add(label1);
        jPanel13.add(jLabel11);
        //按钮
        JPanel jPanel11=new JPanel(new FlowLayout(FlowLayout.CENTER,30,0));
        JButton jbt1=new JButton("选择");
        JButton jbt2=new JButton("上传");
        jPanel11.add(jbt1);
        jPanel11.add(jbt2);
        //按钮和路径
        JPanel jPanel12=new JPanel(new BorderLayout(5,10));
        jPanel12.add(jPanel13,BorderLayout.CENTER);
        jPanel12.add(jPanel11,BorderLayout.SOUTH);

        JLabel jLabel1=new JLabel();
        ImageIcon icon=new ImageIcon(noimagePic);
        jLabel1.setIcon(icon);    //设置标签要显示的图标
        jLabel1.setPreferredSize(new Dimension(80, 80));
        final File[] file = new File[1];

        jbt1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                file[0] =fileChooser();
                jLabel11.setText(file[0].getAbsolutePath());
            }
        });
        /*
         * 上传：将图片添加到项目中
         * */
        jbt2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("file[0].getAbsolutePath():"+file[0].getAbsolutePath());
                ImageIcon icon1=new ImageIcon(file[0].getAbsolutePath());
                jLabel1.setIcon(icon1);
                File startFile=file[0];
                File endDirection=new File(imagedir);
                File endFile=new File(endDirection+ File.separator+ startFile.getName());
                if (!endFile.exists()) {
                    try {
                        Files.copy(startFile.toPath(), endFile.toPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    endFile.delete();
                    try {
                        Files.copy(startFile.toPath(), endFile.toPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        /*j1.add(label1);
        j1.add(jTextField1);
        j1.add(jbt1);
        j1.add(jbt2);*/
        j1.add(jPanel12);
        j1.add(jLabel1);
        //j2
        JLabel label2= new JLabel("名称：");
        label2.setFont(new Font("Serif", Font.PLAIN,16));
        JTextField jTextField2=new JTextField();
        jTextField2.setPreferredSize(new Dimension(300, 30));
        j2.add(label2);
        j2.add(jTextField2);
        //j3
        JLabel label3= new JLabel("类型：");
        label3.setFont(new Font("Serif", Font.PLAIN,16));
        JComboBox comboBox=new JComboBox();
        comboBox.setFont(new Font("Serif", Font.PLAIN,16));
        comboBox.addItem("FiberEdge");
        comboBox.addItem("DataEdge");
        j3.add(label3);
        j3.add(comboBox);
         //j4
        JLabel label5= new JLabel("宽度：");
        label5.setFont(new Font("Serif", Font.PLAIN,16));
        JTextField jTextField3=new JTextField();
        jTextField3.setPreferredSize(new Dimension(100, 30));
        JLabel label4= new JLabel("     高度：");
        label4.setFont(new Font("Serif", Font.PLAIN,16));
        JTextField jTextField4=new JTextField();
        jTextField4.setPreferredSize(new Dimension(100, 30));
        j4.add(label5);
        j4.add(jTextField3);
        j4.add(label4);
        j4.add(jTextField4);
        //j5
        JButton jbt3=new JButton("取消");
        JButton jbt4=new JButton("确定");
        //取消
        jbt3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkAddFrame.setVisible(false);
            }
        });
        //确定：添加到xml中
        jbt4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File file1 = new File(edge_template);
                SAXReader reader=new SAXReader();
                Document doc = null;
                try {
                    doc = reader.read(edge_template);
                } catch (DocumentException e1) {
                    e1.printStackTrace();
                }
                //通过文档对象获得该文档对象的根节点
                Element root=doc.getRootElement();
                //创建一个新的edge节点
                Element cell = root.addElement("edge");
                //创建cell的几个子节点
                Element name = cell.addElement("name");
                Element type = cell.addElement("type");
                Element width = cell.addElement("width");
                Element height = cell.addElement("height");
                Element icon = cell.addElement("icon");
                Element style = cell.addElement("style");
                //给子节点设置值
                //name.setData(jTextField2.getText());
                name.setText(jTextField2.getText());
                type.setText((String)comboBox.getSelectedItem());
                width.setText(jTextField3.getText());
                height.setText(jTextField4.getText());
                icon.setText("/com/mxgraph/examples/swing/images/cell_manage/"+file[0].getName());
                style.setText("edgeStyle=elbowEdgeStyle;strokeWidth=5;endArrow=none;verticalLabelPosition=middle;verticalAlign=middle;fontFamily=微软雅黑 Light;fontSize=24;fontColor=#FF0000;labelBackgroundColor=#FFFFFF;labelBorderColor=#000000;strokeColor=#488FAD");

                OutputFormat outputFormat= OutputFormat.createPrettyPrint();
                outputFormat.setEncoding("UTF-8");
                XMLWriter xmlWriter= null;
                try {
                    xmlWriter = new XMLWriter(new FileWriter(file1),outputFormat);
                    xmlWriter.write(doc);//开始写入XML文件   写入Document对象
                    xmlWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "添加成功", "成功", JOptionPane.PLAIN_MESSAGE);
                linkAddFrame.setVisible(false);
                showPanel.removeAll();
                showPanel.revalidate();
                showpage();
                refresh_libraryPane();
            }
        });
        j5.add(jbt3);
        j5.add(jbt4);

        linkPanel.add(j1);
        linkPanel.add(j2);
        linkPanel.add(j3);
        linkPanel.add(j4);
        linkPanel.add(j5);
        linkAddFrame.add(linkPanel);
        linkAddFrame.revalidate();
    }

    public File fileChooser(){
        // 打开文件选择窗口
        String lastDir = null;
        String wd = (lastDir != null) ? lastDir : FileUtil.getAppPath();

        JFileChooser fc = new JFileChooser(wd);

        // Adds file filter for supported file format
        /*DefaultFileFilter defaultFilter = new DefaultFileFilter(".png",
                mxResources.get("allSupportedFormats") + " (.png)"+"(.jpg)") {

            public boolean accept(File file) {
                String lcase = file.getName().toLowerCase();
                return super.accept(file) || lcase.endsWith(".png")||lcase.endsWith(".jpg");
            }
        };
        fc.addChoosableFileFilter(defaultFilter);
        fc.setFileFilter(defaultFilter);*/
        fc.setDialogTitle("打开文件");

        int rc = fc.showDialog(null, AliasName.getAlias("open_file"));

        String filePath="";
        File sFile=null;
        if (rc == JFileChooser.APPROVE_OPTION) {
            sFile = fc.getSelectedFile();
            filePath=sFile.getAbsolutePath();
            System.out.println("filePath:"+filePath);
        }
        return sFile;
    }

    public class RoundBorder implements Border {
        private Color color;

        public RoundBorder(Color color) {// 有参数的构造方法
            this.color = color;
        }

        public RoundBorder() {// 无参构造方法
            this.color = Color.BLACK;
            // 如果实例化时，没有传值
            // 默认是黑色边框
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        // 实现Border（父类）方法
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width,
                                int height) {
            g.setColor(color);
            g.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
        }
    }

    public void showDeviceCell(JPanel imagePanel,String cell_template) throws ParserConfigurationException, DocumentException {
        imagePanel.removeAll();
        imagePanel.updateUI();
        //创建DOM4J解析器对象
        SAXReader saxReader = new SAXReader();
        File file1 = new File(cell_template);
        //读取xml文件，并生成document对象 现可通过document来操作文档
        Document document = saxReader.read(cell_template);
        //获取到文档的根节点
        Element rootElement = document.getRootElement();
        //System.out.println("根节点的名字是:" + rootElement.getName());
        //获取子节点列表
        Iterator it = rootElement.elementIterator();
        while (it.hasNext()) {
            Element fistChild = (Element) it.next();
            //获取节点的属性值
            device_names.add(fistChild.attribute("name").getValue());
            device_icons.add(fistChild.attribute("icon").getValue());
            //System.out.println(fistChild.attribute("icon").getValue());
            JPanel jp=new JPanel();
            //jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
           // jp.add(Box.createVerticalStrut(5));
            jp.setLayout(new BorderLayout(5,10));
            jp.setPreferredSize(new Dimension(100, 100));
            //jp.setBorder(BorderFactory.createLineBorder(Color.gray, 2));

            JLabel jj=new JLabel();
            ImageIcon icon=new ImageIcon(appPath+"/examples"+fistChild.attribute("icon").getValue()); //根据所给图片，生成指定大小的图标
            if (icon != null)
            {
                if (icon.getIconWidth() > 50 || icon.getIconHeight() > 50)
                {
                    icon = new ImageIcon(icon.getImage().getScaledInstance(50, 50,
                            0));
                }
            }
            jj.setPreferredSize(new Dimension(100, 80));
            jj.setVerticalAlignment(JLabel.CENTER);
            jj.setHorizontalAlignment(JLabel.CENTER);
            jj.setIcon(icon);    //设置标签要显示的图标

            JLabel jname=new JLabel(fistChild.attribute("name").getValue());
            jname.setPreferredSize(new Dimension(100, 20));
            jname.setHorizontalAlignment(JLabel.CENTER);
            jname.setVerticalAlignment(JLabel.BOTTOM);

            jp.add(jj,BorderLayout.CENTER);
            jp.add(jname,BorderLayout.SOUTH);

            device_map.put(fistChild.attribute("name").getValue(),jj);
            imagePanel.add(jp);
            //弹出详细信息，表格

            jp.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if(e.getButton()== e.BUTTON1)
                    {
                        System.out.println("左击");
                        ArrayList<String> elementList=new ArrayList<>();
                        elementList=getportString(fistChild);
                        popCellFrame(fistChild,elementList);
                    }
                    if(e.getButton()== e.BUTTON2)
                    {
                        System.out.println("中击");
                    }
                    if(e.getButton()==e.BUTTON3)
                    {
                        System.out.println("右击");
                        //弹出式菜单
                        JPopupMenu popup=new JPopupMenu();
                        //popup.add( createMenuItem("delete.png", "delete","删除"));
                        JMenuItem item=new JMenuItem("删除");
                        item.setActionCommand("delete");
                        //item.addActionListener(actionListener);
                        item.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String action=e.getActionCommand();
                                System.out.println("执行命令："+action);
                                //当是打开命令时，再弹出一个窗口
                                if(action.equals("delete"))
                                {
                                    //JOptionPane.showMessageDialog(CellManagerFrame.this, action);
                                    int res=JOptionPane.showConfirmDialog(null, "确认删除此图元吗", "删除", JOptionPane.YES_NO_OPTION);
                                    if(res==JOptionPane.YES_OPTION){
                                        //删除图片
                                       File file = new File(appPath+"/examples"+fistChild.attribute("icon").getValue());
                                        if (file.exists()) {
                                            file.delete();
                                            System.out.println("图片已经被成功删除");
                                        }
                                        //删除xml
                                        rootElement.remove(fistChild);
                                        System.out.println(rootElement.remove(fistChild));
                                        OutputFormat outputFormat= OutputFormat.createPrettyPrint();
                                        outputFormat.setEncoding("UTF-8");
                                        XMLWriter xmlWriter= null;
                                        try {
                                            xmlWriter = new XMLWriter(new FileWriter(file1),outputFormat);
                                            xmlWriter.write(document);//开始写入XML文件   写入Document对象
                                            xmlWriter.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        //刷新frame
                                        showPanel.removeAll();
                                        showPanel.revalidate();
                                        showpage();
                                        refresh_libraryPane();
                                    }else{

                                    }
                                }
                            }
                        });
                        String imagePath=imagedir+"/delete.png";
                        System.out.println("imagePath:"+imagePath);
                        //RL imageURL=getClass().getResource(imagePath);
                        //System.out.println("imageURL:"+imageURL);
                        ImageIcon icon=new ImageIcon(imagePath);
                        icon = new ImageIcon(icon.getImage().getScaledInstance(26, 26,
                                0));
                        item.setIcon(icon);
                        popup.add(item);
                        popup.show(e.getComponent(), e.getX()+10, e.getY()+10);
                        revalidate();
                    }
                }
            });
        }
    }

    public void showLinkCell(JPanel imagePanel,String edge_template){
        imagePanel.removeAll();
        imagePanel.updateUI();
        //创建DOM4J解析器对象
        SAXReader saxReader = new SAXReader();
        File file1 = new File(edge_template);
        try {
            //读取xml文件，并生成document对象 现可通过document来操作文档
            Document document = saxReader.read(edge_template);
            //获取到文档的根节点
            Element rootElement = document.getRootElement();
            //System.out.println("根节点的名字是:" + rootElement.getName());
            //获取子节点列表
            Iterator it = rootElement.elementIterator();
            while (it.hasNext()) {
                Element fistChild = (Element) it.next();
                String sname="";
                String sicon="";
                //获取子节点的下一级节点
                Iterator iterator = fistChild.elementIterator();
                while (iterator.hasNext()) {
                    Element element = (Element) iterator.next();
                    if(element.getName().equals("name")){
                        sname=element.getStringValue();
                        link_names.add(sname);
                    }
                    if(element.getName().equals("icon")){
                        sicon=element.getStringValue();
                        link_icons.add(sicon);
                    }
                }
                JPanel jp=new JPanel();
                jp.setLayout(new BorderLayout(5,10));
                jp.setPreferredSize(new Dimension(100, 100));

                JLabel jj=new JLabel();
                ImageIcon icon=new ImageIcon(appPath+"/examples"+sicon); //根据所给图片，生成指定大小的图标
                if (icon != null)
                {
                    if (icon.getIconWidth() > 50 || icon.getIconHeight() > 50)
                    {
                        icon = new ImageIcon(icon.getImage().getScaledInstance(50, 50,
                                0));
                    }
                }
                jj.setIcon(icon);    //设置标签要显示的图标
                jj.setPreferredSize(new Dimension(80, 80));
                jj.setHorizontalAlignment(JLabel.CENTER);
                jj.setVerticalAlignment(JLabel.CENTER);
                JLabel jname=new JLabel(sname);
                jname.setPreferredSize(new Dimension(100, 20));
                jname.setVerticalAlignment(JLabel.BOTTOM);
                jname.setHorizontalAlignment(JLabel.CENTER);
                link_map.put(sname,jj);
                jp.add(jj,BorderLayout.CENTER);
                jp.add(jname,BorderLayout.SOUTH);
                imagePanel.add(jp);
                String finalSicon = sicon;
                jp.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if(e.getButton()== e.BUTTON1)
                        {
                            System.out.println("左击");
                            popLinkFrame(fistChild);
                        }
                        if(e.getButton()== e.BUTTON2)
                        {
                            System.out.println("中击");
                        }
                        if(e.getButton()==e.BUTTON3)
                        {
                            System.out.println("右击");
                            //弹出式菜单
                            JPopupMenu popup=new JPopupMenu();
                            //popup.add( createMenuItem("delete.png", "delete","删除"));
                            JMenuItem item=new JMenuItem("删除");
                            item.setActionCommand("delete");
                            //item.addActionListener(actionListener);
                            item.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String action=e.getActionCommand();
                                    //System.out.println("执行命令："+action);
                                    //当是打开命令时，再弹出一个窗口
                                    if(action.equals("delete"))
                                    {
                                        //JOptionPane.showMessageDialog(CellManagerFrame.this, action);
                                        int res=JOptionPane.showConfirmDialog(null, "确认删除此图元吗", "删除", JOptionPane.YES_NO_OPTION);
                                        if(res==JOptionPane.YES_OPTION){
                                            //删除图片
                                            File file = new File(appPath+"/examples"+ finalSicon);
                                            if (file.exists()) {
                                                file.delete();
                                                System.out.println("图片已经被成功删除");
                                            }
                                            //删除xml
                                            rootElement.remove(fistChild);
                                            System.out.println(rootElement.remove(fistChild));
                                            OutputFormat outputFormat= OutputFormat.createPrettyPrint();
                                            outputFormat.setEncoding("UTF-8");
                                            XMLWriter xmlWriter= null;
                                            try {
                                                xmlWriter = new XMLWriter(new FileWriter(file1),outputFormat);
                                                xmlWriter.write(document);//开始写入XML文件   写入Document对象
                                                xmlWriter.close();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            //刷新frame
                                            showPanel.removeAll();
                                            showPanel.revalidate();
                                            showpage();
                                            refresh_libraryPane();
                                        }else{

                                        }
                                    }
                                }
                            });
                            String imagePath=imagedir+"/delete.png";
                            //System.out.println("imagePath:"+imagePath);
                            //RL imageURL=getClass().getResource(imagePath);
                            //System.out.println("imageURL:"+imageURL);
                            ImageIcon icon=new ImageIcon(imagePath);
                            icon = new ImageIcon(icon.getImage().getScaledInstance(26, 26,
                                    0));
                            item.setIcon(icon);
                            popup.add(item);
                            popup.show(e.getComponent(), e.getX()+10, e.getY()+10);
                            revalidate();
                        }
                    }
                });
            }

        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
    }

    public void popCellFrame(Element fistChild,ArrayList<String> elementList){
        JFrame tableFrame = new JFrame();
        tableFrame.setResizable(false);
        tableFrame.setSize(500, 300);
        //设置中心位置
        tableFrame.setLocation(getWidth() / 2 - width / 2+200, getHeight() / 2 - height / 2+200);
        tableFrame.setTitle(fistChild.attribute("name").getValue());
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tableFrame.setVisible(true);

        JPanel j1=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j11=new JLabel("name:",JLabel.LEFT);
        j11.setFont(new Font("Serif", Font.PLAIN, 18));
        j11.setPreferredSize(new Dimension(50, 30));
        JLabel j12=new JLabel(fistChild.attribute("name").getValue(),JLabel.LEFT);
        j12.setFont(new Font("Serif", Font.PLAIN, 18));
        j1.add(j11);
        j1.add(j12);

        JPanel j2=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j21=new JLabel("type:",JLabel.LEFT);
        j21.setFont(new Font("Serif", Font.PLAIN, 18));
        j21.setPreferredSize(new Dimension(50, 30));
        JLabel j22=new JLabel(fistChild.attribute("type").getValue(),JLabel.LEFT);
        j22.setFont(new Font("Serif", Font.PLAIN, 18));
        j2.add(j21);
        j2.add(j22);

        JPanel j3=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j31=new JLabel("width:",JLabel.LEFT);
        j31.setFont(new Font("Serif", Font.PLAIN, 18));
        j31.setPreferredSize(new Dimension(50, 30));
        JLabel j32=new JLabel(fistChild.attribute("width").getValue(),JLabel.LEFT);
        j32.setFont(new Font("Serif", Font.PLAIN, 18));
        j3.add(j31);
        j3.add(j32);

        JPanel j4=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j41=new JLabel("height:",JLabel.LEFT);
        j41.setFont(new Font("Serif", Font.PLAIN, 18));
        j41.setPreferredSize(new Dimension(50, 30));
        JLabel j42=new JLabel(fistChild.attribute("height").getValue(),JLabel.LEFT);
        j42.setFont(new Font("Serif", Font.PLAIN, 18));
        j4.add(j41);
        j4.add(j42);

        JPanel j5=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j51=new JLabel("icon:",JLabel.LEFT);
        j51.setFont(new Font("Serif", Font.PLAIN, 18));
        j51.setPreferredSize(new Dimension(50, 30));
        JLabel j52=new JLabel(fistChild.attribute("icon").getValue(),JLabel.LEFT);
        j52.setFont(new Font("Serif", Font.PLAIN, 18));
        j5.add(j51);
        j5.add(j52);

        JPanel j6=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j61=new JLabel("port:",JLabel.LEFT);
        j61.setFont(new Font("Serif", Font.PLAIN, 18));
        j61.setPreferredSize(new Dimension(50, 30));
        JLabel j62=new JLabel(elementList.get(0),JLabel.LEFT);
        j62.setFont(new Font("Serif", Font.PLAIN, 18));
        j6.add(j61);
        j6.add(j62);

        JPanel j7=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j71=new JLabel("port:",JLabel.LEFT);
        j71.setFont(new Font("Serif", Font.PLAIN, 18));
        j71.setPreferredSize(new Dimension(50, 30));
        JLabel j72=new JLabel(elementList.get(1),JLabel.LEFT);
        j72.setFont(new Font("Serif", Font.PLAIN, 18));
        j7.add(j71);
        j7.add(j72);

        JPanel j8=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j81=new JLabel("port:",JLabel.LEFT);
        j81.setFont(new Font("Serif", Font.PLAIN, 18));
        j81.setPreferredSize(new Dimension(50, 30));
        JLabel j82=new JLabel(elementList.get(2),JLabel.LEFT);
        j82.setFont(new Font("Serif", Font.PLAIN, 18));
        j8.add(j81);
        j8.add(j82);

        JPanel j9=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j91=new JLabel("port:",JLabel.LEFT);
        j91.setFont(new Font("Serif", Font.PLAIN, 18));
        j91.setPreferredSize(new Dimension(50, 30));
        JLabel j92=new JLabel(elementList.get(3),JLabel.LEFT);
        j92.setFont(new Font("Serif", Font.PLAIN, 18));
        j9.add(j91);
        j9.add(j92);


        JPanel jPanel=new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(Box.createVerticalStrut(15));
        JScrollPane  jtablePanel=new JScrollPane (jPanel);
        jPanel.add(j1);
        jPanel.add(j2);
        jPanel.add(j3);
        jPanel.add(j4);
        jPanel.add(j5);
        jPanel.add(j6);
        jPanel.add(j7);
        jPanel.add(j8);
        jPanel.add(j9);
        tableFrame.add(jtablePanel);


    }

    public void popLinkFrame(Element fistChild){
        JFrame tableFrame = new JFrame();
        tableFrame.setResizable(false);
        tableFrame.setSize(500, 300);
        //设置中心位置
        tableFrame.setLocation(getWidth() / 2 - width / 2+200, getHeight() / 2 - height / 2+200);

        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tableFrame.setVisible(true);

        String name="";
        String type="";
        String width="";
        String height="";
        String icon="";
        String style="";
        Iterator iterator = fistChild.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if(element.getName().equals("name")){
                name=element.getStringValue();
            }
            if(element.getName().equals("type")){
                type=element.getStringValue();
            }
            if(element.getName().equals("width")){
                width=element.getStringValue();
            }
            if(element.getName().equals("height")){
                height=element.getStringValue();
            }
            if(element.getName().equals("icon")){
                icon=element.getStringValue();
            }
            if(element.getName().equals("style")){
                style=element.getStringValue();
            }
        }
        tableFrame.setTitle(name);

        JPanel j1=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j11=new JLabel("name:",JLabel.LEFT);
        j11.setFont(new Font("Serif", Font.PLAIN, 18));
        j11.setPreferredSize(new Dimension(50, 30));
        JLabel j12=new JLabel(name,JLabel.LEFT);
        j12.setFont(new Font("Serif", Font.PLAIN, 18));
        j1.add(j11);
        j1.add(j12);

        JPanel j2=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j21=new JLabel("type:",JLabel.LEFT);
        j21.setFont(new Font("Serif", Font.PLAIN, 18));
        j21.setPreferredSize(new Dimension(50, 30));
        JLabel j22=new JLabel(type,JLabel.LEFT);
        j22.setFont(new Font("Serif", Font.PLAIN, 18));
        j2.add(j21);
        j2.add(j22);

        JPanel j3=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j31=new JLabel("width:",JLabel.LEFT);
        j31.setFont(new Font("Serif", Font.PLAIN, 18));
        j31.setPreferredSize(new Dimension(50, 30));
        JLabel j32=new JLabel(width,JLabel.LEFT);
        j32.setFont(new Font("Serif", Font.PLAIN, 18));
        j3.add(j31);
        j3.add(j32);

        JPanel j4=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j41=new JLabel("height:",JLabel.LEFT);
        j41.setFont(new Font("Serif", Font.PLAIN, 18));
        j41.setPreferredSize(new Dimension(50, 30));
        JLabel j42=new JLabel(height,JLabel.LEFT);
        j42.setFont(new Font("Serif", Font.PLAIN, 18));
        j4.add(j41);
        j4.add(j42);

        JPanel j5=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j51=new JLabel("icon:",JLabel.LEFT);
        j51.setFont(new Font("Serif", Font.PLAIN, 18));
        j51.setPreferredSize(new Dimension(50, 30));
        JLabel j52=new JLabel(icon,JLabel.LEFT);
        j52.setFont(new Font("Serif", Font.PLAIN, 18));
        j5.add(j51);
        j5.add(j52);

        JPanel j6=new JPanel(new FlowLayout(FlowLayout.LEFT,15,0));
        JLabel j61=new JLabel("style:",JLabel.LEFT);
        j61.setFont(new Font("Serif", Font.PLAIN, 18));
        j61.setPreferredSize(new Dimension(50, 30));
        JLabel j62=new JLabel(style,JLabel.LEFT);
        j62.setFont(new Font("Serif", Font.PLAIN, 18));
        j6.add(j61);
        j6.add(j62);


        JPanel jPanel=new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(Box.createVerticalStrut(15));
        JScrollPane  jtablePanel=new JScrollPane (jPanel);
        jPanel.add(j1);
        jPanel.add(j2);
        jPanel.add(j3);
        jPanel.add(j4);
        jPanel.add(j5);
        jPanel.add(j6);
        tableFrame.add(jtablePanel);
    }

    public ArrayList<String> getportString(Element fistChild){
        //获取子节点的下一级节点
        Iterator iterator = fistChild.elementIterator();
        ArrayList<String> elementList=new ArrayList<>();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if(element.getName().equals("port")){
                StringBuilder s=new StringBuilder();
                /*<port attr="fiber" location="up" direction="in" x="0.5" y="0.1"/>*/
                s.append(" attr:"+element.attribute("attr").getValue());
                s.append(" location:"+element.attribute("location").getValue());
                s.append(" direction:"+element.attribute("direction").getValue());
                s.append(" x:"+element.attribute("x").getValue());
                s.append(" y:"+element.attribute("y").getValue());
                elementList.add(s.toString());
                System.out.println("s:"+s);
            }
        }
        return elementList;
    }

    public void refresh_libraryPane(){
        libraryPane.removeAll();
        libraryPane.revalidate();
        // Creates the shapes palette
        EditorPalette shapesPalette = editor.insertPalette(mxResources.get("shapes"));
        EditorPalette imagesPalette = editor.insertPalette(mxResources.get("images"));
        EditorPalette symbolsPalette = editor.insertPalette(mxResources.get("symbols"));
        EditorPalette fiber_devicesPalette = editor.insertPalette("光频设备");
        EditorPalette linksPalette = editor.insertPalette(mxResources.get("links"));

        shapesPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener()
        {
            public void invoke(Object sender, mxEventObject evt)
            {
                Object tmp = evt.getProperty("transferable");

                if (tmp instanceof mxGraphTransferable)
                {
                    mxGraphTransferable t = (mxGraphTransferable) tmp;
                    Object cell = t.getCells()[0];

                    if (graph.getModel().isEdge(cell))
                    {
                        ((GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
                    }
                }
            }

        });

        // Adds some template cells for dropping into the graph
        shapesPalette
                .addTemplate(
                        "Container",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/swimlane.png")),
                        "swimlane", 280, 280, "Container");
        shapesPalette
                .addTemplate(
                        "Icon",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                        "icon;image=/com/mxgraph/examples/swing/images/wrench.png",
                        70, 70, "Icon");
        shapesPalette
                .addTemplate(
                        "Label",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                        "label;image=/com/mxgraph/examples/swing/images/gear.png",
                        130, 50, "Label");
        shapesPalette
                .addTemplate(
                        "Rectangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rectangle.png")),
                        null, 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Rounded Rectangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                        "rounded=1", 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Double Rectangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/doublerectangle.png")),
                        "rectangle;shape=doubleRectangle", 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Ellipse",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/ellipse.png")),
                        "ellipse", 160, 160, "");
        shapesPalette
                .addTemplate(
                        "Double Ellipse",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/doubleellipse.png")),
                        "ellipse;shape=doubleEllipse", 160, 160, "");
        shapesPalette
                .addTemplate(
                        "Triangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/triangle.png")),
                        "triangle", 120, 160, "");
        shapesPalette
                .addTemplate(
                        "Rhombus",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rhombus.png")),
                        "rhombus", 160, 160, "");
        shapesPalette
                .addTemplate(
                        "Horizontal Line",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/hline.png")),
                        "line", 160, 10, "");
        shapesPalette
                .addTemplate(
                        "Hexagon",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/hexagon.png")),
                        "shape=hexagon", 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Cylinder",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/cylinder.png")),
                        "shape=cylinder", 120, 160, "");
        shapesPalette
                .addTemplate(
                        "Actor",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/actor.png")),
                        "shape=actor", 120, 160, "");
        shapesPalette
                .addTemplate(
                        "Cloud",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/cloud.png")),
                        "ellipse;shape=cloud", 160, 120, "");

        shapesPalette
                .addEdgeTemplate(
                        "Straight",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/straight.png")),
                        "straight", 120, 120, "");
        shapesPalette
                .addEdgeTemplate(
                        "Horizontal Connector",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/connect.png")),
                        null, 100, 100, "");
        shapesPalette
                .addEdgeTemplate(
                        "Vertical Connector",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/vertical.png")),
                        "vertical", 100, 100, "");
        shapesPalette
                .addEdgeTemplate(
                        "Entity Relation",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/entity.png")),
                        "entity", 100, 100, "");
        shapesPalette
                .addEdgeTemplate(
                        "Arrow",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/arrow.png")),
                        "arrow", 120, 120, "");

        imagesPalette
                .addTemplate(
                        "Bell",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/bell.png")),
                        "image;image=/com/mxgraph/examples/swing/images/bell.png",
                        50, 50, "Bell");
        imagesPalette
                .addTemplate(
                        "Box",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/box.png")),
                        "image;image=/com/mxgraph/examples/swing/images/box.png",
                        50, 50, "Box");
        imagesPalette
                .addTemplate(
                        "Cube",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/cube_green.png")),
                        "image;image=/com/mxgraph/examples/swing/images/cube_green.png",
                        50, 50, "Cube");
        imagesPalette
                .addTemplate(
                        "User",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/dude3.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/dude3.png",
                        50, 50, "User");
        imagesPalette
                .addTemplate(
                        "Earth",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/earth.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/earth.png",
                        50, 50, "Earth");
        imagesPalette
                .addTemplate(
                        "Gear",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/gear.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/gear.png",
                        50, 50, "Gear");
        imagesPalette
                .addTemplate(
                        "Home",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/house.png")),
                        "image;image=/com/mxgraph/examples/swing/images/house.png",
                        50, 50, "Home");
        imagesPalette
                .addTemplate(
                        "Package",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/package.png")),
                        "image;image=/com/mxgraph/examples/swing/images/package.png",
                        50, 50, "Package");
        imagesPalette
                .addTemplate(
                        "Printer",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/printer.png")),
                        "image;image=/com/mxgraph/examples/swing/images/printer.png",
                        50, 50, "Printer");
        imagesPalette
                .addTemplate(
                        "Server",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/server.png")),
                        "image;image=/com/mxgraph/examples/swing/images/server.png",
                        50, 50, "Server");
        imagesPalette
                .addTemplate(
                        "Workplace",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/workplace.png")),
                        "image;image=/com/mxgraph/examples/swing/images/workplace.png",
                        50, 50, "Workplace");
        imagesPalette
                .addTemplate(
                        "Wrench",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/wrench.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/wrench.png",
                        50, 50, "Wrench");

        symbolsPalette
                .addTemplate(
                        "Cancel",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/cancel_end.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/cancel_end.png",
                        80, 80, "Cancel");
        symbolsPalette
                .addTemplate(
                        "Error",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/error.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/error.png",
                        80, 80, "Error");
        symbolsPalette
                .addTemplate(
                        "Event",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/event.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/event.png",
                        80, 80, "Event");
        symbolsPalette
                .addTemplate(
                        "Fork",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/fork.png")),
                        "rhombusImage;image=/com/mxgraph/examples/swing/images/fork.png",
                        80, 80, "Fork");
        symbolsPalette
                .addTemplate(
                        "Inclusive",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/inclusive.png")),
                        "rhombusImage;image=/com/mxgraph/examples/swing/images/inclusive.png",
                        80, 80, "Inclusive");
        symbolsPalette
                .addTemplate(
                        "Link",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/link.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/link.png",
                        80, 80, "Link");
        symbolsPalette
                .addTemplate(
                        "Merge",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/merge.png")),
                        "rhombusImage;image=/com/mxgraph/examples/swing/images/merge.png",
                        80, 80, "Merge");
        symbolsPalette
                .addTemplate(
                        "Message",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/message.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/message.png",
                        80, 80, "Message");
        symbolsPalette
                .addTemplate(
                        "Multiple",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/multiple.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/multiple.png",
                        80, 80, "Multiple");
        symbolsPalette
                .addTemplate(
                        "Rule",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rule.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/rule.png",
                        80, 80, "Rule");
        symbolsPalette
                .addTemplate(
                        "Terminate",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/terminate.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/terminate.png",
                        80, 80, "Terminate");
        symbolsPalette
                .addTemplate(
                        "Timer",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/timer.png")),
                        "roundImage;image=/com/mxgraph/examples/swing/images/timer.png",
                        80, 80, "Timer");
        // Adds some template cells for dropping into the graph

        AllCellMap.clear();
        //updatecells(fiber_devicesPalette,linksPalette);
		// add cell and pipe node
        List<CellEle> cellList = null;
        try {
            cellList = new CellNewDecoder(opticalDeviceTemplate).decodeDoc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (CellEle cellEle : cellList) {
			mxCell cell = new mxCell();
			//cell.setValue(cellEle.getName());
			cell.setGeometry(new mxGeometry(0, 0, cellEle.getWidth(), cellEle.getHeight()));
			cell.setStyle(cellEle.getStyle());
			cell.setType(cellEle.getType());
			cell.setName(cellEle.getName());
			cell.setVertex(true);
			cell.setMultiValue(cellEle.getMultiValue());

			if (cellEle.getPorts() != null && !cellEle.getPorts().isEmpty()) {
				// has connect point, handle port cell
				cell.setConnectable(true);
				//遍历图元的磁力点
				for (CellPort cellPort : cellEle.getPorts()) {
					mxGeometry geo = new mxGeometry(cellPort.getX(), cellPort.getY(), cellPort.getWidth(), cellPort.getHeight());
					geo.setOffset(new mxPoint(-geo.getWidth() / 2, -geo.getHeight() / 2));
					geo.setRelative(true);

					mxCell port = new mxCell();
					port.setStyle(cellPort.getStyle());
					port.setGeometry(geo);
					port.setName(cellPort.getName());
					port.setVertex(true);
					port.setType("Port");
					port.setAttr(cellPort.getAttr());
					port.setLocation(cellPort.getLocation());
					port.setDirection(cellPort.getDirection());
					cell.insert(port);
				}
			}
			System.out.println("cell.getName():"+cell.getName());
			AllCellMap.put(cell.getName(), cell);  //添加的图元、连接点
			fiber_devicesPalette.addTemplate(cellEle.getName(),
					new ImageIcon(GraphEditor.class.getResource(cellEle.getIcon())), cell);
		}
        List<EdgeEle> edgeList = null;
        try {
            edgeList = new EdgeNewDecoder(edge_template).decodeDoc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (EdgeEle edgeEle : edgeList) {
			mxCell cell =linksPalette.addEdgeTemplate(edgeEle.getName(),
					new ImageIcon(GraphEditor.class.getResource(edgeEle.getIcon())),
					edgeEle.getStyle(),
					//"straight;strokeWidth=20;endArrow=none;verticalLabelPosition=middle;verticalAlign=middle;fontFamily=微软雅黑 Light;fontSize=24;fontColor=#FF0000;labelBackgroundColor=#FFFFFF;labelBorderColor=#000000;strokeColor=#66FFFF",
					edgeEle.getWidth(),edgeEle.getHeight(),edgeEle.getName(),edgeEle.getType(),""
			);
			AllCellMap.put(cell.getName(), cell);
		}
		libraryPane.revalidate();
    }

    public static void main(String[] args) {
       // new CellManagerFrame();
    }
}
