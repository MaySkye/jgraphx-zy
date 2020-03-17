package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.GraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import org.apache.jena.rdf.model.ModelFactory;

import javax.swing.*;
import java.awt.*;


public class StartUI extends JFrame {


    private JProgressBar progress; //������
    private JFrame workFrame = null;
    private String username = null;

    public StartUI(String username) {
        this.username = username;
        //loadStartFrame();
        // System.out.println("loading resource");
        Container container = getContentPane(); //�õ�����
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));  //���ù��
        this.setIconImage(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/icon.png")).getImage());
        this.setTitle("��ӭʹ��");
        container.add(new JLabel(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/back.jpg"))), BorderLayout.CENTER);  //����ͼƬ
        progress = new JProgressBar(1, 100); //ʵ����������
        progress.setStringPainted(true); //�������
        progress.setString("���������ļ�");  //������ʾ����
        progress.setBackground(Color.white);  //���ñ���ɫ
        container.add(progress, BorderLayout.SOUTH);  //���ӽ�������������

        Dimension screen = getToolkit().getScreenSize();  //�õ���Ļ�ߴ�
        setSize(900,680);
        //pack(); //������Ӧ����ߴ�
        setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2); //���ô���λ��
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ʹ�ܹرմ��ڣ���������
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
            //�Ż�����
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        GraphEditor editor = new GraphEditor("��ʱ̬�Ƽ��ϵͳ");
        workFrame = editor.createFrame(new EditorMenuBar(editor, username));
        ModelFactory.createDefaultModel();
        workFrame.setVisible(true);
    }

//    public static void main(String[] args) {
//        StartUI ui = new StartUI();
//        ui.loadStartFrame();
//    }

}
