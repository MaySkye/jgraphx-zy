package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.map.OpenMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;

import static com.mxgraph.examples.swing.frame.SyncApplets.configTable;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 9:41 2019/11/27
 * @Modify By:
 */
public class SiteFrame extends Frame {

    private JButton j1;
    private JButton j2;
    private JButton j3;
    private JPanel j4;
    private JComboBox j5;
    private JTextField j6;
    private JButton j7;
    private JList j8;
    private JScrollPane j9;

    public static void main(String args[]) {
        SiteFrame siteFrame = new SiteFrame();
    }

    public SiteFrame() {
        init();
        this.setTitle("站点管理");
        Dimension screen = getToolkit().getScreenSize();  //得到屏幕尺寸
        this.setSize(1200, 600);
        this.setLocation((screen.width - getSize().width) / 2,
                (screen.height - getSize().height) / 2); //设置窗口位置
        this.setVisible(true);
        this.addWindowListener (new WindowAdapter()
        {
            @Override
            public void windowClosing ( WindowEvent e )
            {
                dispose();
            }
        });


    }

    public void init() {
        j1 = new JButton("预览");
        j1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Properties properties = System.getProperties();
                String osName = properties.getProperty("os.name");
                System.out.println (osName);

                if (osName.indexOf("Linux") != -1) {
                    try {
                        Runtime.getRuntime().exec("htmlview");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else if (osName.indexOf("Windows") != -1){
                    try {
                        Runtime.getRuntime().exec("explorer http://localhost:8080/javascript/examples/grapheditor/www/sitemap.html");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    throw new RuntimeException("Unknown OS.");
                }
            }
        });

        j2 = new JButton("刷新");
        j3 = new JButton("添加站点");
        j4 = new JPanel();
        String[] str = { "一级站", "二级站", "三级普通站","三级测试站","四级站" };
        j5 = new JComboBox(str);
        j6 = new JTextField();
        j7 = new JButton("搜索");
        j8 = new JList(str);
        j8.addListSelectionListener(new ListSelectionListener() {
             public void valueChanged(ListSelectionEvent e) {
                 //刷新表格的值

                 //do_list_valueChanged(e);
             }
         });

        JTable table = configTable();
        j9 = new JScrollPane(table);//改为表格
        j9.setBackground(Color.PINK);// 为了看出效果，设置了颜色

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        this.add(j1);// 把组件添加进jframe
        this.add(j2);
        this.add(j3);
        this.add(j4);
        this.add(j5);
        this.add(j6);
        this.add(j7);
        this.add(j8);
        this.add(j9);
        GridBagConstraints s = new GridBagConstraints();// 定义一个GridBagConstraints，
        // 是用来控制添加进的组件的显示位置
        s.fill = GridBagConstraints.BOTH;
        // 该方法是为了设置如果组件所在的区域比组件本身要大时的显示情况
        // NONE：不调整组件大小。
        // HORIZONTAL：加宽组件，使它在水平方向上填满其显示区域，但是不改变高度。
        // VERTICAL：加高组件，使它在垂直方向上填满其显示区域，但是不改变宽度。
        // BOTH：使组件完全填满其显示区域。
        s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(j1, s);// 设置组件
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(j2, s);
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(j3, s);
        s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0;// 不能为1，j4是占了4个格，并且可以横向拉伸，
        // 但是如果为1，后面行的列的格也会跟着拉伸,导致j7所在的列也可以拉伸
        // 所以应该是跟着j6进行拉伸
        s.weighty = 0;
        layout.setConstraints(j4, s);
        s.gridwidth = 2;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(j5, s);
        ;
        s.gridwidth = 4;
        s.weightx = 1;
        s.weighty = 0;
        layout.setConstraints(j6, s);
        ;
        s.gridwidth = 0;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(j7, s);
        ;
        s.gridwidth = 2;
        s.weightx = 0;
        s.weighty = 1;
        layout.setConstraints(j8, s);
        ;
        s.gridwidth = 5;
        s.weightx = 0;
        s.weighty = 1;
        layout.setConstraints(j9, s);
    }
;
}
