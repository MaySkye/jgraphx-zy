package com.mxgraph.examples.swing.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {

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
    private static final int width = 500;
    private static final int height = 400;

    //设置界面的图标
    private Image img = new ImageIcon("com/mxgraph/examples/swing/images/icon.png").getImage();


    //初始化主框架
    public void init() {
        //设置界面大小不可拉伸
        setResizable(false);
        setSize(width, height);
        //设置中心位置
        setLocation(getWidth() / 2 - width / 2, getHeight() / 2 - height / 2);
        //setLocationByPlatform(true);
        setIconImage(img);
        setTitle("授时中心登陆界面");
    }

    //面板
    private JPanel topPanel;
    private JPanel middlePanel;
    private JPanel middlePanel_name;
    private JPanel middlePanel_password;
    private JPanel middlePanel_ackpw;
    private JPanel bottomPanel;

    //标签
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JLabel ackpwLabel;


    //顶部标签信息
    private JLabel messageLabel;

    //文本框
    private JTextField nameText;
    private JPasswordField passwordText;
    private JPasswordField ackpwText;

    //按钮
    private JButton loginButton;
    private JButton cancelButton;

    public RegisterFrame(){

        init();

        //顶部面板
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(600,100));
        messageLabel = new JLabel("顶部显示信息");
        topPanel.add(messageLabel);
        this.add(topPanel, BorderLayout.NORTH);

        //中间面板
        middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        //第一行
        middlePanel_name = new JPanel();
        nameLabel = new JLabel("账    号:",SwingConstants.RIGHT);
        nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(200,30));


        middlePanel_name.add(nameLabel);
        middlePanel_name.add(nameText);


        //第二行
        middlePanel_password = new JPanel();
        passwordLabel = new JLabel("密    码:",SwingConstants.RIGHT);
        passwordText = new JPasswordField();
        passwordText.setPreferredSize(new Dimension(200,30));



        middlePanel_password.add(passwordLabel);
        middlePanel_password.add(passwordText);

        //第三行
        middlePanel_ackpw = new JPanel();
        ackpwLabel = new JLabel("确认密码:");
        ackpwText = new JPasswordField();
        ackpwText.setPreferredSize(new Dimension(200,30));

        middlePanel_ackpw.add(ackpwLabel);
        middlePanel_ackpw.add(ackpwText);

        middlePanel.add(middlePanel_name);
        middlePanel.add(middlePanel_password);
        middlePanel.add(middlePanel_ackpw);


        this.add(middlePanel, BorderLayout.CENTER);

        //底部面板
        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(600,100));
        loginButton = new JButton("确认");
        cancelButton = new JButton("取消");
        bottomPanel.add(loginButton);
        bottomPanel.add(cancelButton);

        this.add(bottomPanel, BorderLayout.SOUTH);

    }

    //确认操作
    private class ackAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //检查两次密码是否一致
        }
    }


}
