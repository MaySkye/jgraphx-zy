package com.mxgraph.examples.swing.frame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:46 2019/11/27
 * @Modify By:
 */
public class SyncApplets {
    public static void main(String[] args) {
        // 面板组件
        JPanel taskPanel = new JPanel();
        JPanel dbPanel = new JPanel();
        JTabbedPane tabbedPane = buildJTabbedPane(taskPanel, dbPanel);
        buildFrame(tabbedPane);
    }

    private static JTabbedPane buildJTabbedPane(JPanel taskPanel, JPanel dbPanel) {
        // 选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();
        // 通过BorderFactory来设置边框的特性
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabbedPane.add("执行任务", taskPanel);
        tabbedPane.add("数据源配置", dbPanel);

        // 创建面板容器
        JPanel tablePanel = new JPanel();
        // 设置BorderLayout布局方式
        tablePanel.setLayout(new BorderLayout());
        // 创建表格
        JTable table = configTable();
        // 使用普通的中间容器添加表格时，表头 和 内容 需要分开添加
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);
        // 将容器放入taskPanel面板
        taskPanel.add(tablePanel);

        return tabbedPane;
    }

    public static JTable configTable() {
        // 创建 table
        JTable table = getTable();
        // 获取 model
        DefaultTableModel model = getTableModel();
        table.setModel(model);
        for (int i = 0; i < model.getRowCount(); i++) {
            // 获得表格的值
            String taskName = (String) model.getValueAt(i, 0);
            // 设置表格的值
           // model.setValueAt(taskName + "执行时间:" + i + "分钟后", i, 2);
        }
        /*
        * "ID","等级","X坐标","Y坐标","城市名","描述信息","类型","编址", "相连站点","操作"
        * */
        int[] columnWidth = {60, 60, 60, 60, 60, 200,60,150,200,100};
        for (int i = 0; i < columnWidth.length; i++) {
            // 设置表格各栏各行的尺寸
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
        }
        return table;
    }

    private static JTable getTable() {
        JTable table = new JTable() {
            @Override
            public void updateUI() {
                // 刷新
                super.updateUI();
                // 表格行高
                setRowHeight(36);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // 第3列、第4列设置可以编辑，其余列不可编辑
                if (column != 4 && column != 5) {
                    return false;
                }
                return true;
            }
        };
        // 设置表头不可移动
        table.getTableHeader().setReorderingAllowed(false);
        // 一次只能选择一项
        table.setSelectionMode(SINGLE_SELECTION);
        return table;
    }


    /*连接数据库，获取最新的数据信息*/
    //level=0表示全部显示
    private static DefaultTableModel getTableModel() {
        int rowCount = 10;
        Object[] columnNames = {"ID","等级","X坐标",
                "Y坐标","城市名","描述信息","类型","编址", "相连站点","操作"};
        Object[][] rowData = new Object[rowCount][columnNames.length];
        String[] s1={"1","1","1","1","1","2","2","2","2","3"};
        String[] s2={"109","116","117","114","114","121","125","110","86","117"};
        String[] s3={"34","40","32","31","35","31","43","18","42","39"};
        String[] s4={"西安","北京","合肥","武汉","郑州","上海","长春","三亚","库尔勒","天津"};
        String[] s6={"正式","正式","正式","正式","正式","正式","正式","正式","正式","普通"};
        String[] s8={"武汉；沈阳","","","","","北京；合肥","","","",""};
        // 向表格中填充数据
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnNames.length; j++) {
                switch (j) {
                    case 0:
                        rowData[i][j] =  ""+i;
                        break;
                    case 1:
                        rowData[i][j] = s1[i];
                        break;
                    case 2:
                        rowData[i][j] = s2[i];
                        break;
                    case 3:
                        rowData[i][j] = s3[i];
                        break;
                    case 4:
                        rowData[i][j] = s4[i];
                        break;
                    case 6:
                        rowData[i][j] = s6[i];
                        break;
                    case 8:
                        rowData[i][j] = s8[i];
                        break;
                    default:
                        rowData[i][j] = null;
                }
            }
        }
        return new DefaultTableModel(rowData, columnNames);
    }

    private static void buildFrame(JComponent component) {
        // 窗体容器
        JFrame frame = new JFrame("数据同步工具");
        frame.add(component);
        //  JFrame.EXIT_ON_CLOSE  退出
        //  JFrame.HIDE_ON_CLOSE  最小化隐藏
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // 设置布局
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(BorderLayout.CENTER, component);
        // 设置窗口最小尺寸
        frame.setMinimumSize(new Dimension(1060, 560));
        // 调整此窗口的大小，以适合其子组件的首选大小和布局
        frame.pack();
        // 设置窗口相对于指定组件的位置
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // 设置窗口尺寸是否固定不变
        frame.setResizable(true);
    }

}
