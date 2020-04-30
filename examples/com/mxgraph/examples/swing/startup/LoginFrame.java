package com.mxgraph.examples.swing.startup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mxgraph.examples.swing.auth.Check;
import com.mxgraph.examples.swing.auth.Md5;
import com.mxgraph.examples.swing.auth.VerifyIdentity;
import com.mxgraph.examples.swing.frame.StartUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.swing.JFileChooser.FILES_AND_DIRECTORIES;


public class LoginFrame extends JFrame {

    private String path;
    private String username;
    private String password;
    //private JTabbedPane tabPane = new JTabbedPane();//选项卡布局
    private Font font = new Font("宋体", Font.PLAIN, 16);
    private Container con = this.getContentPane();//布局1
    //private Container con1 = new Container();//布局2
    private JLabel label1 = new JLabel("卡路径");
    private JLabel label2 = new JLabel("用户名");
    private JLabel label3 = new JLabel("密码");
    private JTextField text1 = new JTextField();
    private JTextField text2 = new JTextField();
    private JTextField text3 = new JTextField();
    private JButton button1 = new JButton("选择");
    private JButton button2 = new JButton("登录");
    private JButton button3 = new JButton("取消");
    private JFileChooser jfc = new JFileChooser();//文件选择器
    private ButtonAction btnAction = new ButtonAction();

    private JLabel labelBack = new JLabel(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/others/login_back.jpg")));

    public LoginFrame() {

        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation(new Point((int) (lx / 2) - 300, (int) (ly / 2) - 200));//设定窗口出现位置
        this.setSize(600, 400);//设定窗口大小
        // this.setContentPane(tabPane);//设置布局
        //下面设定标签等的出现位置和高宽
        label1.setBounds(60, 160, 200, 30);
        label1.setFont(font);
        label2.setBounds(60, 60, 200, 30);
        label2.setFont(font);
        label3.setBounds(60, 110, 200, 30);
        label3.setFont(font);
        text1.setBounds(160, 160, 240, 30);
        text1.setFont(font);
        text2.setBounds(160, 60, 240, 30);
        text2.setFont(font);
        text3.setBounds(160, 110, 240, 30);
        text3.setFont(font);
        button1.setBounds(450, 160, 80, 30);
        button2.setBounds(180, 240, 80, 30);
        button3.setBounds(280, 240, 80, 30);
        button1.setFont(font);
        button2.setFont(font);
        button3.setFont(font);
        button1.addActionListener(btnAction);//添加事件处理
        button2.addActionListener(btnAction);//添加事件处理
        button3.addActionListener(btnAction);//添加事件处理
        con.add(label1);
        con.add(label2);
        con.add(label3);
        con.add(text1);
        con.add(text2);
        con.add(text3);
        con.add(button1);
        con.add(button2);
        con.add(button3);
        con.add(jfc);
        con.add(labelBack);

        //默认目录
        String defaultDirectory = "d:/Graduation/kong";
        //设置默认目录
        jfc.setCurrentDirectory(new File(defaultDirectory));
        //优化一下界面
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setTitle("用户登录");
        this.setIconImage(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/others/icon.png")).getImage());
        this.setVisible(true);//窗口可见
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使能关闭窗口，结束程序
    }

    private class ButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(button1)) {//判断触发方法的按钮是哪个
                jfc.setFileSelectionMode(FILES_AND_DIRECTORIES);//设定只能选择到文件夹
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
                password = text3.getText();
                /*
                * 不进行身份验证时
                * */
                setVisible(false);
                dispose();
                new StartUI(username);

                /*
                * 进行身份验证时
                * */
                /*try {
                    //TODO: verifyIdentity
                    String resp = VerifyIdentity.VerifyIdentity(username, path);
                    JSONObject jsonRead = JSONObject.parseObject(resp);
                    Integer code = (Integer)jsonRead.get("code");
                    System.out.println(code);
                    if (code == 0) {
                        Check.SubAttr sa = new Check.SubAttr();
                        sa.setName(username);
                        //sa.setRole();

                        Check.ObjAttr oa = new Check.ObjAttr();
                        oa.setName("组态图设计工具");
                        //oa.setOwner();

                        Check.ActAttr aa = new Check.ActAttr();
                        aa.setName("修改");

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Check.EnvAttr ea = new Check.EnvAttr();
                        ea.setTime(df.format(new Date()));

                        String sastr = JSON.toJSONString(sa);
                        String oastr = JSON.toJSONString(oa);
                        String aastr = JSON.toJSONString(aa);
                        String eastr = JSON.toJSONString(ea);
                        String md5Sum = Md5.md5Map.get(username);
                        String resp2 = Check.Check("运控分系统访问控制策略集合", sastr, oastr, aastr, eastr,username, md5Sum);
                        jsonRead = JSONObject.parseObject(resp2);
                        code = (Integer)jsonRead.get("code");
                        if (code == 0) {
                            setVisible(false);
                            dispose();
                            new StartUI(username);
                        }
                    } else {
                        //显示错误弹出框
                        JOptionPane.showMessageDialog(null,"登陆失败，请重新登陆");
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/
            }

            if(e.getSource().equals(button3)){
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
