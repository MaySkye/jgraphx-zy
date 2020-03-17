package com.mxgraph.examples.swing.frame;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

public class LoginFrame2 extends JFrame {

    private Toolkit kit = Toolkit.getDefaultToolkit();

    //��ȡ��Ļ�Ŀ��
    public int getWidth() {
        return kit.getScreenSize().width;
    }

    //��ȡ��Ļ�ĸ߶�
    public int getHeight() {
        return kit.getScreenSize().height;
    }

    //���ý���Ĵ�С
    private static final int width = 500;
    private static final int height = 400;

    //���ý����ͼ��
    private Image img = new ImageIcon("com/mxgraph/examples/swing/images/icon.png").getImage();


    //��ʼ�������
    public void init() {
        //���ý����С��������
        setResizable(false);
        setSize(width, height);
        //��������λ��
        setLocation(getWidth() / 2 - width / 2, getHeight() / 2 - height / 2);
        //setLocationByPlatform(true);
        setIconImage(img);
        setTitle("��ʱ���ĵ�½����");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //���
    private JPanel topPanel;
    private JPanel middlePanel;
    private JPanel middlePanel_name;
    private JPanel middlePanel_password;
    private JPanel bottomPanel;

    //��ǩ
    private JLabel nameLabel;
    private JLabel passwordLabel;

    private JLabel enrollLabel;
    private JLabel findLable;
    //������ǩ��Ϣ
    private JLabel messageLabel;

    //�ı���
    private JTextField nameText;
    private JPasswordField passwordText;

    //��ť
    private JButton loginButton;
    private JButton cancelButton;

    public LoginFrame2(){

        init();

        //�������
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(600,100));
        messageLabel = new JLabel("������ʾ��Ϣ");
        topPanel.add(messageLabel);
        this.add(topPanel, BorderLayout.NORTH);

        //�м����
        middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        //��һ��
        middlePanel_name = new JPanel();
        nameLabel = new JLabel("�˺�:",SwingConstants.RIGHT);
        nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(200,30));
        enrollLabel = new JLabel("ע���˺�",SwingConstants.LEFT);

        middlePanel_name.add(nameLabel);
        middlePanel_name.add(nameText);
        middlePanel_name.add(enrollLabel);

        //�ڶ���
        middlePanel_password = new JPanel();
        passwordLabel = new JLabel("����:",SwingConstants.RIGHT);
        passwordText = new JPasswordField();
        passwordText.setPreferredSize(new Dimension(200,30));
        findLable = new JLabel("��������",SwingConstants.LEFT);
        findLable.setEnabled(false);


        middlePanel_password.add(passwordLabel);
        middlePanel_password.add(passwordText);
        middlePanel_password.add(findLable);


        middlePanel.add(middlePanel_name);
        middlePanel.add(middlePanel_password);



        this.add(middlePanel, BorderLayout.CENTER);

        //�ײ����
        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(600,100));
        loginButton = new JButton("��½");
        cancelButton = new JButton("ȡ��");
        bottomPanel.add(loginButton);
        bottomPanel.add(cancelButton);

        this.add(bottomPanel, BorderLayout.SOUTH);

    }

    //��½
    private class loginAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("����ִ�е�½������");
            boolean loginFlag = false;
            if(loginFlag){
                //��½�ɹ��Ĳ���
//                try {
//                    //new Thread(new StartUI()).start();
//                } catch (UnsupportedEncodingException ex) {
//                    ex.printStackTrace();
//                }
                new StartFrame();
            }else{
                //��½ʧ�ܵĲ���

            }
        }
    }

    //ȡ��
    private class cancelAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //�رյ�½����
        }
    }

    //���ע����ת
    private class registerAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //�رյ�ǰ����

            //���µ�ע�ᴰ��
            RegisterFrame registerFrame = new RegisterFrame();
            registerFrame.setVisible(true);
        }
    }


}
