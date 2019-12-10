package com.mxgraph.examples.swing.frame;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.alibaba.fastjson.JSONObject;
import com.mxgraph.examples.swing.db.DBConfig;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.util.FileUtil;
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
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URLDecoder;
import java.util.Properties;

public class UploadServiceFileFrame extends JFrame {
    {
        try {
            Properties pps = new Properties();
            pps.load(UploadServiceFileFrame.class.getResourceAsStream("/config/http_url.properties"));
            uploadFileUrl = pps.getProperty("baseUrl") + pps.getProperty("uploadServiceFile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 站点配置文件
    private Wini ini = null;
    private static String uploadFileUrl;
    private int levelNumber = 0;
    //JFrame组件
    private JPanel contentPane;
    private JTextField fileNameFiled = new JTextField();
    private JComboBox<String> siteNameCombox = new JComboBox<String>();
    private JTextField siteTypeField = new JTextField();
    private JComboBox<String> siteLevelComboBox = new JComboBox<String>();
    //Editor
    private BasicGraphEditor editor;

    /**
     * Launch the application.
     */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					UploadServiceFileFrame frame = new UploadServiceFileFrame();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

    //调用该函数，可实现居中
    public void toCenter() {
        //获取屏幕参数
        Dimension screen = getToolkit().getScreenSize();
        int screenH = screen.height;
        int screenW = screen.width;
        //获取窗口参数
        int windowH = this.getSize().width;
        int windowW = this.getSize().height;
        //居中
        this.setLocation((screenW - windowW) / 2, (screenH - windowH) / 2);
    }

    // 根据站级获取站点名
    private void getSiteNamesBySiteLevel(String levelData) {
        siteNameCombox.removeAllItems();
        ini.entrySet().forEach((entry) -> {
            Section sec = entry.getValue();
            if (levelData.equals(sec.get("site_level"))) {
                siteNameCombox.addItem(sec.get("chinese_name"));
            }
        });
    }

    //根据站点名获取等级和类型
    private void getSiteInfoByChineseName(String siteName) {
        ini.entrySet().forEach((entry) -> {
            Section sec = entry.getValue();
            if (siteName.equals(sec.get("chinese_name"))) {
            	levelNumber = Integer.parseInt(sec.get("site_level"));
                int index = levelNumber - 1;
                siteLevelComboBox.setSelectedIndex(index);
                siteTypeField.setText(sec.get("site_type"));
            }
        });
    }

    //Http发送文件
    //通过PostMethod发送请求，可以为JSON字符串、或者MultiPartFile（可通过addParameter添加参数）
    public void uploadFile(String url, File file) {
        if (file.exists()) {
            HttpClient client = new HttpClient();
            PostMethod postMethod = new PostMethod(url);
            postMethod.setQueryString(new NameValuePair[]{
            		new NameValuePair("site_name",siteNameCombox.getSelectedItem().toString()),
					new NameValuePair("site_level",String.valueOf(levelNumber)),
					new NameValuePair("latest","true")  //默认为最新
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
					if(jsonObject.getString("type").equals("success"))
					{
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


    /**
     * Create the frame.
     */
    public UploadServiceFileFrame(BasicGraphEditor editor) {
        this.editor = editor;

        try {
            String pathname= FileUtil.getRootPath();
            ini = new Wini(new File(pathname+"\\resources\\config\\site_info.ini"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setResizable(false);
        setTitle("发布组态图");
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 430);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel siteNamePane = new JPanel();
        siteNamePane.setBounds(125, 130, 350, 24);
        contentPane.add(siteNamePane);
        siteNamePane.setLayout(null);

        JLabel siteNameLabel = new JLabel("站点：");
        siteNameLabel.setBounds(0, 3, 180, 18);
        siteNamePane.add(siteNameLabel);

        JPanel siteLevelPane = new JPanel();
        siteLevelPane.setLayout(null);
        siteLevelPane.setBounds(125, 70, 350, 24);
        contentPane.add(siteLevelPane);

        siteLevelComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                // 添加选中事件
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String level = e.getItem().toString();
                    switch (level) {
                        case "一级":
                            getSiteNamesBySiteLevel("1");
                            break;
                        case "二级":
                            getSiteNamesBySiteLevel("2");
                            break;
                        case "三级":
                            getSiteNamesBySiteLevel("3");
                            break;
                    }
                    getSiteInfoByChineseName(siteNameCombox.getSelectedItem().toString());
                }
            }
        });
        siteLevelComboBox.setModel(new DefaultComboBoxModel(new String[]{"一级", "二级", "三级"}));
        siteLevelComboBox.setBounds(86, 0, 184, 24);
        siteLevelPane.add(siteLevelComboBox);

        siteNameCombox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                //添加选中事件
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String siteName = e.getItem().toString();
                    getSiteInfoByChineseName(siteName);
                }
            }
        });
        siteNameCombox.setBounds(86, 0, 184, 24);
        siteNamePane.add(siteNameCombox);
        //默认获取一级站点名
        getSiteNamesBySiteLevel("1");

        JLabel siteLevelLabel = new JLabel("级别：");
        siteLevelLabel.setBounds(0, 3, 180, 18);
        siteLevelPane.add(siteLevelLabel);

        JPanel fileNamePane = new JPanel();
        fileNamePane.setLayout(null);
        fileNamePane.setBounds(125, 250, 350, 24);
        contentPane.add(fileNamePane);

        JLabel fileNameLabel = new JLabel("资源命名：");
        fileNameLabel.setBounds(0, 3, 180, 18);
        fileNamePane.add(fileNameLabel);

        fileNameFiled.setBounds(86, 0, 184, 24);
        fileNamePane.add(fileNameFiled);
        fileNameFiled.setColumns(10);

        JLabel suffixLabel = new JLabel(".service");
        suffixLabel.setBounds(280, 3, 200, 18);
        fileNamePane.add(suffixLabel);

        JPanel btnPane = new JPanel();
        btnPane.setBounds(130, 325, 340, 27);
        contentPane.add(btnPane);
        btnPane.setLayout(null);

        JButton btnSubmit = new JButton("发布");
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get GraphComponent
                mxGraphComponent graphComponent = editor.getGraphComponent();
                mxGraph graph = graphComponent.getGraph();
                PortStyleUpdate.switchToTransparency((mxCell) graph.getDefaultParent());

                // save as mxe file
                mxCodec codec = new mxCodec();
                String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));

                // image=/com/mxgraph/examples/swing/images/ -> image=stencils/clipart/
                xml = xml.replace("image=/com/mxgraph/examples/swing/images/",
                        "image=images/");


                String filename = System.getProperty("java.io.tmpdir") + "/" + fileNameFiled.getText() + ".service";


                try {
                    mxUtils.writeFile(xml, filename); //把xml写到filename
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    filename = URLDecoder.decode(filename, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                File file = new File(filename);
                System.out.println("mongodb  uploadFileUrl:"+uploadFileUrl);
                System.out.println("mongodb  filename:"+filename);
                uploadFile(uploadFileUrl, file);

                editor.setModified(false);
                editor.setCurrentFile(file);

                dispose();
                // save end
                //PortStyleUpdate.resetStyle((mxCell) graph.getModel().getRoot());
            }
        });
        btnSubmit.setBounds(0, 0, 113, 27);
        btnPane.add(btnSubmit);

        JButton btnCancel = new JButton("取消");
        JFrame that = this;
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                that.dispose();
            }
        });
        btnCancel.setBounds(227, 0, 113, 27);
        btnPane.add(btnCancel);

        JPanel siteTypePane = new JPanel();
        siteTypePane.setLayout(null);
        siteTypePane.setBounds(125, 190, 350, 24);
        contentPane.add(siteTypePane);

        JLabel siteTypeLabel = new JLabel("站点类型：");
        siteTypeLabel.setBounds(0, 3, 180, 18);
        siteTypePane.add(siteTypeLabel);

        siteTypeField = new JTextField();
        siteTypeField.setEditable(false);
        siteTypeField.setColumns(10);
        siteTypeField.setBounds(86, 0, 184, 24);
        siteTypePane.add(siteTypeField);

        getSiteInfoByChineseName(siteNameCombox.getSelectedItem().toString());
        btnSubmit.requestFocus();

        //设置图标
        this.setIconImage(new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/icon.png")).getImage());
        //居中
        this.toCenter();
        this.setVisible(true);
    }

    public JComboBox getSiteNameCombox() {
        return siteNameCombox;
    }

    public JComboBox getSiteLevelComboBox() {
        return siteLevelComboBox;
    }

    public JTextField getFileNameTextFiled() {
        return fileNameFiled;
    }
}
