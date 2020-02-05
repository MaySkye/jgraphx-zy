package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import com.mxgraph.examples.swing.graph.Vertex;
import sun.swing.table.DefaultTableCellHeaderRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 20:29 2018/10/23
 * @Modify By:
 */
public class ResourceShowFrame extends JFrame {

    private BasicGraphEditor editor = null;
    private mxGraph graph = null;
    private mxCell cell = null;
    private ResourceListModel resourceListModel = null;
    private ResourceListModel lastresourceListModel=null;
    private List<OwlObject> objList;

    private String RecentResourcePath=null;
    private boolean isAlter=false;      //true点击了确认修改
    private boolean ischooseFile=false;//true表示重新选择了资源文件

    private JPanel tablePanel;
    private JPanel btnPanel;

    private JTable table;
    private JLabel chooseFileLabel;
    private JButton btnAlter;
    private JButton btnreBind;
    private JButton btnBack;

    public ResourceShowFrame(BasicGraphEditor editor, mxGraph graph, mxCell cell) throws HeadlessException {
        this.editor=editor;
        this.graph=graph;
        this.cell=cell;
        RecentResourcePath = cell.getOriginalResourceFile();

        initFrame();
        initTablePanel();
        initBtnPanel();

        //this.pack();//调用pack()适应子控件大小,调用在子控件设计后
    }

    private void initFrame(){
        this.setTitle("-已绑资源信息-");//设置窗口标题内容
        this.setSize(800, 600);//设置窗口大小,宽度500，高度400
        this.setLocation(300, 200);//设置窗口位置为距离屏幕左边水平方向300，上方垂直方向200
        this.setVisible(true);//设置窗体可见。
        this.setLayout(new FlowLayout(FlowLayout.CENTER));//设置窗体布局为流式布局。
        this.setResizable(false); //禁止改变大小在调用显示之前

        this.addWindowListener (new WindowAdapter()
        {
            @Override
            public void windowClosing ( WindowEvent e )
            {
                dispose();
            }
        });
    }

    private void initTablePanel( ) {

        tablePanel = new JPanel();

        lastresourceListModel = new ResourceListModel(cell.getName(),cell,true, RecentResourcePath);
        table=new JTable(lastresourceListModel);

        table.setPreferredScrollableViewportSize(new Dimension(750, 450));
        FitTableColumns(table);
        table.setFillsViewportHeight(false);

        int num1=0;
        int num2=0;
        if(cell.getV()!=null){
            num1=cell.getV().getData_info().size();
            num2=cell.getV().getLink_info().size();
            System.out.println("num1:"+num1+"num2:"+num2);
        }else {
            num1=0;
            num2=0;
        }

        table.getRowCount();
        System.out.println("table.getRowCount():"+table.getRowCount());
        table.setRowHeight(80);
        table.setRowHeight(4,80+num1*20);
        table.setRowHeight(5,80+num2*60);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();//单元格渲染器
        tcr.setHorizontalAlignment(JLabel.CENTER);//居中显示
        table.setDefaultRenderer(Object.class, tcr);//设置渲染器
        table.setSelectionBackground(Color.GRAY);
        DefaultTableCellHeaderRenderer hr = new DefaultTableCellHeaderRenderer();
        hr.setHorizontalAlignment(JLabel.CENTER);

        table.getTableHeader().setDefaultRenderer(hr);

        //为每行添加点击事件

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (ischooseFile) {
                        UpdateResourceListener();
                    }else{
                        int row = table.getSelectedRow();
                        System.out.println("row: " + row);
                    }
                }
            });

        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        // Add the scroll pane to this panel.
        tablePanel.add(scrollPane);
        this.add(tablePanel);
        //this.add(tablePanel,BorderLayout.NORTH);
    }

    private void initBtnPanel( ) {

        btnPanel = new JPanel();

        if (RecentResourcePath == null) {
            RecentResourcePath = "";
        }
        chooseFileLabel = new JLabel(RecentResourcePath);

        //按钮初始化
        btnAlter = new JButton(AliasName.getAlias("resource_alter"));
        btnreBind = new JButton(AliasName.getAlias("resource_rebind"));
        btnAlter.setVisible(false);
        btnreBind.setVisible(false);
        btnBack = new JButton(AliasName.getAlias("确定"));
        //为按钮绑定点击事件
        btnAlter.addActionListener(event -> btnAlterClickListener(event));
        btnreBind.addActionListener(event -> btnChoiceFileListener(event));
        btnBack.addActionListener(event ->  btnBackListener(event));

        //btnPanel.add(chooseFileLabel);
        btnPanel.add(btnAlter);
        btnPanel.add(btnreBind);
        btnPanel.add(btnBack);

        //this.add(btnPanel, BorderLayout.CENTER);
        this.add(btnPanel);
    }

    private void btnChoiceFileListener(ActionEvent e) {
        isAlter=false;
        // 重新选择文件
        String wd = FileUtil.getAppPath();
        if (RecentResourcePath != null) {
            wd = FileUtil.getParentPath(RecentResourcePath);
        }

        JFileChooser fc = new JFileChooser(wd);
        DefaultFileFilter defaultFilter = new DefaultFileFilter(".owl",
                "owl" + AliasName.getAlias("file") + "(.owl)");

        // Adds special vector graphics formats and HTML
        fc.addChoosableFileFilter(defaultFilter);
        // Adds filter that accepts all supported image formats
        fc.setFileFilter(defaultFilter);

        int rc = fc.showDialog(null, AliasName.getAlias("open"));

        if (rc != JFileChooser.APPROVE_OPTION) {
            return;
        }

        //filePath
        String filePath = fc.getSelectedFile().getAbsolutePath();
        ischooseFile=true;
        System.out.println("open file path: " + filePath);
        if (!new File(filePath).exists()) {
            return;
        }
        chooseFileLabel.setText(FileUtil.getFileName(filePath));
        RecentResourcePath = filePath;
        System.out.println("btnChoiceFileListener - RecentResourcePath: " + RecentResourcePath);
       // cell.setResourceFile(filePath);
        //editor.setResourceFile(filePath);
        resourceListModel=new ResourceListModel(cell.getName(),cell,false, RecentResourcePath);
        table.setModel(resourceListModel);
        //动态设置行高
        for(int i=0;i<resourceListModel.getObjList().size();i++){
            int a1=resourceListModel.getObjList().get(i).dataAttrs.size();
            int b1=resourceListModel.getObjList().get(i).objAttrs.size();
            table.setRowHeight(i,300+a1*15+b1*25);
        }

        FitTableColumns(table);

        objList=resourceListModel.getObjList();

        //为每行添加点击事件

       /* table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

            }
        });*/
    }

    private void btnAlterClickListener(ActionEvent e) {
        System.out.println("修改");
       /* if (resource == null) {
            JOptionPane.showMessageDialog(null, AliasName.getAlias("please_select_resource"), AliasName.getAlias("error"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        cell.setResourceFile(RecentResourcePath);
        LoadResourceFile.bindCellResource(cell, resource);

        JFrame frame = new ResourceAttrFrame(editor, graph, cell, resource);
        frame.setVisible(true);
        this.dispose();*/

    }

    private void btnBackListener(ActionEvent e) {
        if(!ischooseFile){
            this.dispose();
        }else{
            ischooseFile=false;
            if(!isAlter){
                table.setModel(lastresourceListModel);
                table.setRowHeight(80);
                FitTableColumns(table);
                int num1=0;
                int num2=0;
                if(cell.getV()!=null){
                    num1=cell.getV().getData_info().size();
                    num2=cell.getV().getLink_info().size();
                    //System.out.println("num1:"+num1+"num2:"+num2);
                }else{
                    num1=0;
                    num2=0;
                }

                table.setRowHeight(4,80+num1*20);
                table.setRowHeight(5,80+num2*60);

            }else{
                this.dispose();
            }
        }
        System.out.println("返回");
    }

    private void FitTableColumns(JTable myTable)
    {
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer() .
                    getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col) .
                    getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col) .
                        getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col) .
                        getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column);
            column.setWidth(width + myTable.getIntercellSpacing().width + 10);
        }
    }

    private void UpdateResourceListener() {
        int row = table.getSelectedRow();
        System.out.println("row1: " + row);
        OwlObject obj = objList.get(row);

        Object[] options = {"确定", "取消"};
        int response = JOptionPane.showOptionDialog(null,
                "确定重新绑定为更改的资源信息吗？",
                "提示", JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (response == 0) {
            System.out.println("重新绑定资源");
            //修改cell的内容
            VertexInterface<String> v = cell.getV();
            if(v==null){
                v=new Vertex<>("");
            }
            v.setName(obj.type.id);
            v.setId(obj.id);
            v.setLabel(obj.id);
            v.setUrl(obj.uri);
            //解析它的基本信息，对象信息和属性信息
            Map<String, String> link_info_map = v.getLink_info();
            link_info_map.clear();
            obj.objAttrs.forEach((objAttr, objSet) -> {
                objSet.forEach(obj2 -> {
                    System.out.println(obj.id + "->" + objAttr.id + "->" + obj2.id);
                    if (link_info_map.get(objAttr.id) != null) {
                        String str = link_info_map.get(objAttr.id);
                        str = str + "; " + obj2.id;
                        link_info_map.put(objAttr.id, str);
                    } else {
                        link_info_map.put(objAttr.id, obj2.id);
                    }
                });
            });

            Map<String, String> data_info_map = v.getData_info();
            data_info_map.clear();
            obj.dataAttrs.forEach((objAttr, objSet) -> {
                objSet.forEach(obj2 -> {
                    System.out.println(obj.id + "->" + objAttr.id + "->" + obj2);
                    if (data_info_map.get(objAttr.id) != null) {
                        String str = data_info_map.get(objAttr.id);
                        str = str + "; " + obj2.toString();
                        data_info_map.put(objAttr.id, str);
                    } else {
                        data_info_map.put(objAttr.id, obj2.toString());
                    }
                });
            });

            cell.setV(v);
            resourceListModel = new ResourceListModel(cell.getName(), cell, true, RecentResourcePath);
            table.setModel(resourceListModel);
            FitTableColumns(table);
            lastresourceListModel = resourceListModel;
            int num1 = cell.getV().getData_info().size();
            int num2 = cell.getV().getLink_info().size();
            System.out.println("num1:" + num1 + "num2:" + num2);
            table.setRowHeight(80);
            table.setRowHeight(4, 80 + num1 * 20);
            table.setRowHeight(5, 80 + num2 * 60);
            isAlter = true;
            ischooseFile=false;
            cell.setBindResourceFile(RecentResourcePath);

        }
    }
}
