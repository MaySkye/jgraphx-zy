package com.mxgraph.examples.swing.select;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 21:44 2018/12/13
 * @Modify By:
 */
public class Test {
    public static void main(String[] args) {
        DefaultPieDataset dpd = new DefaultPieDataset();
        dpd.setValue("管理人员", 25);
        dpd.setValue("市场人员", 10);
        dpd.setValue("开发人员", 50);
        dpd.setValue("其它人员", 15);
        JFreeChart chart = ChartFactory.createPieChart("某公司人员组织结构图", dpd, true,
                false, false);
        // JFreeChart chart=ChartFactory.createPieChart3D("某公司人员组织结构图", dpd,
        // true, false, false);
        ChartFrame chartFrame = new ChartFrame("某公司人员组织结构图", chart);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }
}
