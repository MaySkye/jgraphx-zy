package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.GraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import org.apache.jena.rdf.model.ModelFactory;

import javax.swing.*;
import java.awt.*;


public class StartUI extends JFrame {


    private JProgressBar progress; //进度条
    private JFrame workFrame = null;
    private String username = null;

    public StartUI(String username) {
        this.username = username;
        //loadStartFrame();
        // System.out.println("loading resource");
        Container container = getContentPane(); //得到容器
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));  //设置光标
        this.setIconImage(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/others/icon.png")).getImage());
        this.setTitle("欢迎使用");
        container.add(new JLabel(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/others/back.jpg"))), BorderLayout.CENTER);  //增加图片
        progress = new JProgressBar(1, 100); //实例化进度条
        progress.setStringPainted(true); //描绘文字
        progress.setString("加载配置文件");  //设置显示文字
        progress.setBackground(Color.white);  //设置背景色
        container.add(progress, BorderLayout.SOUTH);  //增加进度条到容器上

        Dimension screen = getToolkit().getScreenSize();  //得到屏幕尺寸
        setSize(900,680);
        //pack(); //窗口适应组件尺寸
        setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2); //设置窗口位置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使能关闭窗口，结束程序
        setVisible(true);
        new Thread(() -> loadStartFrame()).start();
        new Thread(() -> loadWorkFrame()).start();
    }

    public void loadStartFrame() {

        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress.setValue(Math.min(100, progress.getValue() + 1));
        }
        this.setVisible(false);
        this.dispose();
    }

    public void loadWorkFrame() {
        try {
            //优化界面
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        GraphEditor editor = new GraphEditor("授时态势监控系统");
        workFrame = editor.createFrame(new EditorMenuBar(editor, username));
        ModelFactory.createDefaultModel();
        workFrame.setVisible(true);
    }

//    public static void main(String[] args) {
//        StartUI ui = new StartUI();
//        ui.loadStartFrame();
//    }

}
