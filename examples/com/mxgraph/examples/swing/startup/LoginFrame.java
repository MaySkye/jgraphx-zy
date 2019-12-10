package com.mxgraph.examples.swing.startup;

import com.mxgraph.examples.swing.frame.StartUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class LoginFrame extends JFrame {

    private String path;
    private String username;

    //private JTabbedPane tabPane = new JTabbedPane();//选项卡布局
    private Font font = new Font("宋体", Font.PLAIN, 16);

    private Container con = this.getContentPane();//布局1
    //private Container con1 = new Container();//布局2
    private JLabel label1 = new JLabel("卡路径");
    private JLabel label2 = new JLabel("用户名");
    private JTextField text1 = new JTextField();
    private JTextField text2 = new JTextField();
    private JButton button1 = new JButton("...");
    private JButton button2 = new JButton("ok");
    private JFileChooser jfc = new JFileChooser();//文件选择器
    private LoginAction loginAction = new LoginAction();

    private JLabel labelBack = new JLabel(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/login_back.jpg")));

    public LoginFrame() {

        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation(new Point((int) (lx / 2) - 300, (int) (ly / 2) - 200));//设定窗口出现位置
        this.setSize(600, 400);//设定窗口大小
        // this.setContentPane(tabPane);//设置布局
        //下面设定标签等的出现位置和高宽
        label1.setBounds(40, 120, 200, 25);
        label1.setFont(font);
        label2.setBounds(40, 40, 200, 25);
        label2.setFont(font);
        text1.setBounds(150, 120, 240, 25);
        text1.setFont(font);
        text2.setBounds(150, 40, 240, 25);
        text2.setFont(font);
        button1.setBounds(450, 120, 100, 25);
        button2.setBounds(220, 220, 80, 30);
        button1.setFont(font);
        button2.setFont(font);
        button1.addActionListener(loginAction);//添加事件处理
        button2.addActionListener(loginAction);//添加事件处理
        con.add(label1);
        con.add(label2);
        con.add(text1);
        con.add(text2);
        con.add(button1);
        con.add(button2);
        con.add(jfc);
        con.add(labelBack);
        //tabPane.add("登录信息", con);//添加布局1
        //tabPane.add("暂无内容",con1);//添加布局2
        //this.add(con);
        //优化一下界面
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setIconImage(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/icon.png")).getImage());
        this.setVisible(true);//窗口可见
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使能关闭窗口，结束程序
    }

    private class LoginAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(button1)) {//判断触发方法的按钮是哪个
                jfc.setFileSelectionMode(1);//设定只能选择到文件夹
                int state = jfc.showOpenDialog(null);//此句是打开文件选择器界面的触发语句
                if (state == 1) {
                    return;//撤销则返回
                } else {
                    File f = jfc.getSelectedFile();//f为选择到的目录
                    text1.setText(f.getAbsolutePath());
                }
            }
            if (e.getSource().equals(button2)) {
                path = text1.getText();
                username = text2.getText();
                try {
                    if (FabricAuth.loginVerify(path, username)) {
                        setVisible(false);
                        dispose();
                        new StartUI(username);
                    } else {
                        //显示错误弹出框
                        JOptionPane.showMessageDialog(null,"登陆失败，请重新登陆");
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
