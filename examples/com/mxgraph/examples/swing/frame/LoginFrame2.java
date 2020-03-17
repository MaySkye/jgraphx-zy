package com.mxgraph.examples.swing.frame;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

public class LoginFrame2 extends JFrame {

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //面板
    private JPanel topPanel;
    private JPanel middlePanel;
    private JPanel middlePanel_name;
    private JPanel middlePanel_password;
    private JPanel bottomPanel;

    //标签
    private JLabel nameLabel;
    private JLabel passwordLabel;

    private JLabel enrollLabel;
    private JLabel findLable;
    //顶部标签信息
    private JLabel messageLabel;

    //文本框
    private JTextField nameText;
    private JPasswordField passwordText;

    //按钮
    private JButton loginButton;
    private JButton cancelButton;

    public LoginFrame2(){

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
        nameLabel = new JLabel("账号:",SwingConstants.RIGHT);
        nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(200,30));
        enrollLabel = new JLabel("注册账号",SwingConstants.LEFT);

        middlePanel_name.add(nameLabel);
        middlePanel_name.add(nameText);
        middlePanel_name.add(enrollLabel);

        //第二行
        middlePanel_password = new JPanel();
        passwordLabel = new JLabel("密码:",SwingConstants.RIGHT);
        passwordText = new JPasswordField();
        passwordText.setPreferredSize(new Dimension(200,30));
        findLable = new JLabel("忘记密码",SwingConstants.LEFT);
        findLable.setEnabled(false);


        middlePanel_password.add(passwordLabel);
        middlePanel_password.add(passwordText);
        middlePanel_password.add(findLable);


        middlePanel.add(middlePanel_name);
        middlePanel.add(middlePanel_password);



        this.add(middlePanel, BorderLayout.CENTER);

        //底部面板
        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(600,100));
        loginButton = new JButton("登陆");
        cancelButton = new JButton("取消");
        bottomPanel.add(loginButton);
        bottomPanel.add(cancelButton);

        this.add(bottomPanel, BorderLayout.SOUTH);

    }

    //登陆
    private class loginAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("正在执行登陆操作！");
            boolean loginFlag = false;
            if(loginFlag){
                //登陆成功的操作
//                try {
//                    //new Thread(new StartUI()).start();
//                } catch (UnsupportedEncodingException ex) {
//                    ex.printStackTrace();
//                }
                new StartFrame();
            }else{
                //登陆失败的操作

            }
        }
    }

    //取消
    private class cancelAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //关闭登陆窗口
        }
    }

    //点击注册跳转
    private class registerAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //关闭当前窗口

            //打开新的注册窗口
            RegisterFrame registerFrame = new RegisterFrame();
            registerFrame.setVisible(true);
        }
    }


}
