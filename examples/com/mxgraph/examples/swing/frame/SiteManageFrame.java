package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.owl.Site;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 19:24 2019/11/21
 * @Modify By:
 */
public class SiteManageFrame extends Frame {

    private static Vector<Site> v = new Vector<Site>();// 可实现自动增长对象数组
    private JFrame frame = new JFrame("站点管理");
    private Container cont = frame.getContentPane();
    private static JList vectorList = null; //组件

    private JPanel buttonJPanel = new JPanel(new GridLayout(10, 1));// new
    // BorderLayout()
    private JButton allSelectButton = new JButton("全选");
    private JButton noSelectButton = new JButton("全不选");
    private JButton reverseSelectButton = new JButton("反选");
    private JButton modifyButton = new JButton("修改");
    private JButton syncButton = new JButton("同步到文件");
    private JButton deleteButton = new JButton("删除");
    private JButton addButton = new JButton("添加站点");
    private JButton addlinkButton = new JButton("添加站点关系");
    private JButton reNewButton = new JButton("初始化数据");


    public static void main(String[] args) {
        SiteManageFrame demo3 = new SiteManageFrame();

        // 初始化数据按钮
        demo3.reNewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                v.clear();

                vectorList.setListData(v);
            }
        });
        // 删除按钮事件
        demo3.deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 如果vectorList有选中的，则通过vector删除，并重新显示到vectorList,否则提示未选中；
                if (vectorList.getSelectedValues().length > 0) {
                    Object[] objArr = vectorList.getSelectedValues();
                    for (int i = 0; i < objArr.length; i++) {
                        v.remove(objArr[i]);
                    }
                    vectorList.setListData(v);
                    // vectorList = null;
                    // vectorList = new JList(v);
                } else {
                    JOptionPane.showMessageDialog(null, "请至少选中一列");
                }
            }
        });
        // 添加按钮事件
        demo3.addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String addStr = JOptionPane.showInputDialog("请输入添加的数据！");
                //v.add(addStr);
                vectorList.setListData(v);
            }
        });
        // 添加修改按钮事件，双击JList列表时也会弹出修改框
        demo3.modifyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 如果没有选中一列或者选中多列都提示让其只能选择一列
                Object[] objArr = vectorList.getSelectedValues();
                if (objArr.length < 0 || objArr.length > 1) {
                    JOptionPane.showMessageDialog(null, "只能选择一列");
                    return;
                } else {
                    v.remove(objArr[0]);
                    String modifyStr = JOptionPane.showInputDialog("请修改值",
                            objArr[0]);
                     //v.add(modifyStr);
                    vectorList.setListData(v);
                }
            }
        });

        // 增加全选按钮事件，点击全选时，JLIst列表全部选中
        demo3.allSelectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ListModel model = vectorList.getModel();
                vectorList
                        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                // vectorList.setsele
                int[] allSelect = new int[v.size()];
                for (int i = 0; i < v.size(); i++) {
                    allSelect[i] = i;
                    // vectorList.setSelectionModel(selectionModel)
                    // vectorList.setSelectionMode(v);
                }
                vectorList.setSelectedIndices(allSelect);
                // vectorList.setSelectedIndex(i);
            }
        });

        // 增加全部选按钮时间，点击全不选时，JList都不选中；
        demo3.noSelectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int[] noSelectArr = new int[v.size()];
                for (int i = 0; i < noSelectArr.length; i++) {
                    noSelectArr[i] = -1;
                }
                vectorList.setSelectedIndices(noSelectArr);
            }
        });

        // 反选按钮
        demo3.reverseSelectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int[] allArr = new int[v.size()];
                for (int i = 0; i < allArr.length; i++) {
                    allArr[i] = i;
                }
                int[] selectArr = vectorList.getSelectedIndices();
                for (int i = 0; i < selectArr.length; i++) {
                    int value = selectArr[i];
                    for (int j = 0; j < allArr.length; j++) {
                        if (value == allArr[j]) {
                            allArr[j] = -1;
                        }
                    }
                }
                vectorList.setSelectedIndices(allArr);

                // for (int i = 0; i < selectArr.length; i++) {
                // System.out.println(selectArr[i]);
                //
                // }
                // System.out.println(selectArr.toString());

            }
        });

        demo3.initUI();
    }

    private void initUI() {

        //this.frame.setLayout(new GridLayout(1, 2));
       // buttonJPanel.setLayout(new GridLayout(10, 1));
        buttonJPanel.setSize(200,this.frame.getHeight());
        // 初始化按钮
        buttonJPanel.add(allSelectButton);
        buttonJPanel.add(noSelectButton);
        buttonJPanel.add(reverseSelectButton);
        buttonJPanel.add(modifyButton);
        buttonJPanel.add(syncButton);
        buttonJPanel.add(deleteButton);
        buttonJPanel.add(addButton);
        buttonJPanel.add(reNewButton);

        // 初始化JList
        Site s1=new Site("001","001","001","001","001");
        Site s2=new Site("002","002","002","002","002");
        Site s3=new Site("003","003","003","003","003");
        Site s4=new Site("004","004","004","004","004");
        Site s5=new Site("005","005","005","005","005");
        v.add(s1);
        v.add(s2);
        v.add(s3);
        v.add(s4);
        vectorList = new JList(v);
        vectorList.setBorder(BorderFactory.createTitledBorder("站点信息列表"));

        this.cont.add(buttonJPanel);
        this.cont.add(new JScrollPane(vectorList));

        this.frame.setSize(1400, 800);
        this.frame.setLocation(new Point(500, 200));
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
