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

    //private JTabbedPane tabPane = new JTabbedPane();//ѡ�����
    private Font font = new Font("����", Font.PLAIN, 16);

    private Container con = this.getContentPane();//����1
    //private Container con1 = new Container();//����2
    private JLabel label1 = new JLabel("��·��");
    private JLabel label2 = new JLabel("�û���");
    private JTextField text1 = new JTextField();
    private JTextField text2 = new JTextField();
    private JButton button1 = new JButton("...");
    private JButton button2 = new JButton("ok");
    private JFileChooser jfc = new JFileChooser();//�ļ�ѡ����
    private LoginAction loginAction = new LoginAction();

    private JLabel labelBack = new JLabel(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/login_back.jpg")));

    public LoginFrame() {

        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation(new Point((int) (lx / 2) - 300, (int) (ly / 2) - 200));//�趨���ڳ���λ��
        this.setSize(600, 400);//�趨���ڴ�С
        // this.setContentPane(tabPane);//���ò���
        //�����趨��ǩ�ȵĳ���λ�ú͸߿�
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
        button1.addActionListener(loginAction);//����¼�����
        button2.addActionListener(loginAction);//����¼�����
        con.add(label1);
        con.add(label2);
        con.add(text1);
        con.add(text2);
        con.add(button1);
        con.add(button2);
        con.add(jfc);
        con.add(labelBack);
        //tabPane.add("��¼��Ϣ", con);//��Ӳ���1
        //tabPane.add("��������",con1);//��Ӳ���2
        //this.add(con);
        //�Ż�һ�½���
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setIconImage(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/icon.png")).getImage());
        this.setVisible(true);//���ڿɼ�
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ʹ�ܹرմ��ڣ���������
    }

    private class LoginAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(button1)) {//�жϴ��������İ�ť���ĸ�
                jfc.setFileSelectionMode(1);//�趨ֻ��ѡ���ļ���
                int state = jfc.showOpenDialog(null);//�˾��Ǵ��ļ�ѡ��������Ĵ������
                if (state == 1) {
                    return;//�����򷵻�
                } else {
                    File f = jfc.getSelectedFile();//fΪѡ�񵽵�Ŀ¼
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
                        //��ʾ���󵯳���
                        JOptionPane.showMessageDialog(null,"��½ʧ�ܣ������µ�½");
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
