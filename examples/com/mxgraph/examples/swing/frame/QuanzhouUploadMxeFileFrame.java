package com.mxgraph.examples.swing.frame;

import com.alibaba.fastjson.JSONObject;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.util.UTF8FileWriter;
import com.mxgraph.examples.swing.util.PortStyleUpdate;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

public class QuanzhouUploadMxeFileFrame extends JFrame {

    // 静态代码块
    static {
        try {
            Properties pps = new Properties();
            pps.load(QuanzhouUploadMxeFileFrame.class.getResourceAsStream("/config/http_url.properties"));
            uploadFileUrl = pps.getProperty("quanzhouBaseUrl") + pps.getProperty("quanzhouUploadMxeFileUrl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 站点配置文件
    //private Wini ini = null;
    private static String uploadFileUrl;
    //JFrame组件
    //Editor
    private BasicGraphEditor editor;

    private JPanel contentPane;
    private JTextField userAddressField;
    private JTextField nameField;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton timeSyncRadio;
    private JRadioButton docAuthRadio;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    QuanzhouUploadMxeFileFrame frame = new QuanzhouUploadMxeFileFrame(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 窗体居中
     */
    public void toCenter() {
        // 获取屏幕参数
        Dimension screen = getToolkit().getScreenSize();
        int screenH = screen.height;
        int screenW = screen.width;
        // 获取窗口参数
        int windowH = this.getSize().width;
        int windowW = this.getSize().height;
        // 居中
        this.setLocation((screenW - windowW) / 2, (screenH - windowH) / 2);
    }

    /**
     * Create the frame.
     */
    public QuanzhouUploadMxeFileFrame(BasicGraphEditor editor) {
        setTitle("发布组态图——泉州项目");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //设置图标
        this.setIconImage(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/icon.png")).getImage());
        setBounds(100, 100, 600, 430);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        Box areaBox = Box.createHorizontalBox();
        areaBox.setBounds(126, 96, 317, 29);
        contentPane.add(areaBox);

        JLabel areaLabel = new JLabel("区域：    ");
        areaBox.add(areaLabel);

        userAddressField = new JTextField();
        areaBox.add(userAddressField);
        userAddressField.setColumns(10);

        Box nameBox = Box.createHorizontalBox();
        nameBox.setBounds(126, 156, 317, 29);
        contentPane.add(nameBox);

        JLabel nameLabel = new JLabel("命名：    ");
        nameBox.add(nameLabel);

        nameField = new JTextField();
        nameField.setColumns(10);
        nameBox.add(nameField);

        JLabel suffixLabel = new JLabel("   .mxe");
        nameBox.add(suffixLabel);

        JButton submitBtn = new JButton("发布");
        submitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mxGraphComponent graphComponent = editor.getGraphComponent();
                mxGraph graph = graphComponent.getGraph();
                PortStyleUpdate.switchToTransparency((mxCell) graph.getDefaultParent());

                // save as mxe file
                mxCodec codec = new mxCodec();
                String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));

                // image=/com/mxgraph/examples/swing/images/ -> image=stencils/clipart/
                xml = xml.replace("image=/com/mxgraph/examples/swing/images/",
                        "image=images/");


                String filename = System.getProperty("java.io.tmpdir") + "/" + nameField.getText() + ".mxe";

                try {
                    filename = URLDecoder.decode(filename, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                File file = new File(filename);
                UTF8FileWriter.write(xml,file);
                System.out.println("mongodb  uploadFileUrl:" + uploadFileUrl);
                System.out.println("mongodb  filename:" + filename);
                uploadFile(uploadFileUrl, file);

                editor.setModified(false);
                editor.setCurrentFile(file);
                dispose();
            }
        });
        submitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        submitBtn.setBounds(126, 312, 113, 27);
        contentPane.add(submitBtn);

        JButton closeBtn = new JButton("关闭");
        JFrame that = this;
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                dispose();
            }
        });
        closeBtn.setBounds(347, 312, 113, 27);
        contentPane.add(closeBtn);

        JLabel label = new JLabel("类型：    ");
        label.setBounds(126, 217, 77, 18);
        contentPane.add(label);

        timeSyncRadio = new JRadioButton("时间同步");
        timeSyncRadio.setSelected(true);
        timeSyncRadio.setBounds(196, 213, 113, 27);
        contentPane.add(timeSyncRadio);
        buttonGroup.add(timeSyncRadio);

        docAuthRadio = new JRadioButton("文档认证");
        docAuthRadio.setBounds(315, 213, 129, 27);
        contentPane.add(docAuthRadio);
        buttonGroup.add(docAuthRadio);
        setVisible(true);
        toCenter();
    }

    //Http发送文件
    //通过PostMethod发送请求，可以为JSON字符串、或者MultiPartFile（可通过addParameter添加参数）
    public void uploadFile(String url, File file) {
        if (file.exists()) {
            HttpClient client = new HttpClient();
            PostMethod postMethod = new PostMethod(url);
            postMethod.setQueryString(new NameValuePair[]{
                    new NameValuePair("user_address", userAddressField.getText()),
                    new NameValuePair("type", timeSyncRadio.isSelected() ? "时间同步" : "文档认证"),
                    new NameValuePair("latest", "true"),  //默认为最新
                    new NameValuePair("filename", file.getName())
            });

            try {
                // FilePart：用来上传文件的类,file即要上传的文件
                FilePart fp = new FilePart("multipartFile", file);
                Part[] parts = {fp};

                // 对于MIME类型的请求，httpclient建议全用MulitPartRequestEntity进行包装
                MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
                postMethod.setRequestEntity(mre);
                // 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
                client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);

                int status = client.executeMethod(postMethod);
                if (status == HttpStatus.SC_OK) {
                    JSONObject jsonObject = JSONObject.parseObject(new String(postMethod.getResponseBody()));
                    if (jsonObject.getString("type").equals("success")) {
                        JOptionPane.showMessageDialog(null, "成功", "发布完成", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                // 释放连接
                postMethod.releaseConnection();
            }
        }
    }

    public JRadioButton getTimeSyncRadio() {
        return timeSyncRadio;
    }

    public JRadioButton getDocAuthRadio() {
        return docAuthRadio;
    }
}
