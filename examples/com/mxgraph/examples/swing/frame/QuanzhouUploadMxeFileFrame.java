package com.mxgraph.examples.swing.frame;

import com.alibaba.fastjson.JSONObject;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Component;
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
    static
    {
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
	private JTextField areaFiled;
	private JTextField nameField;

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
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 430);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		Box areaBox = Box.createHorizontalBox();
		areaBox.setBounds(120, 72, 317, 29);
		contentPane.add(areaBox);

		JLabel areaLabel = new JLabel("区域：    ");
		areaBox.add(areaLabel);

		areaFiled = new JTextField();
		areaBox.add(areaFiled);
		areaFiled.setColumns(10);

		Box nameBox = Box.createHorizontalBox();
		nameBox.setBounds(120, 129, 317, 29);
		contentPane.add(nameBox);

		JLabel nameLabel = new JLabel("命名：    ");
		nameBox.add(nameLabel);

		nameField = new JTextField();
		nameField.setColumns(10);
		nameBox.add(nameField);

		JLabel suffixLabel = new JLabel(".mxe");
		nameBox.add(suffixLabel);

		JButton btnNewButton = new JButton("发布");
		btnNewButton.addMouseListener(new MouseAdapter() {
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
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(126, 312, 113, 27);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("关闭");
		JFrame that = this;
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});
		btnNewButton_1.setBounds(347, 312, 113, 27);
		contentPane.add(btnNewButton_1);
		toCenter();
	}
	
	//Http发送文件
    //通过PostMethod发送请求，可以为JSON字符串、或者MultiPartFile（可通过addParameter添加参数）
    public void uploadFile(String url, File file) {
        if (file.exists()) {
            HttpClient client = new HttpClient();
            PostMethod postMethod = new PostMethod(url);
            postMethod.setQueryString(new NameValuePair[]{
            		new NameValuePair("site_name",areaFiled.getText()),
					new NameValuePair("site_level","default"),
					new NameValuePair("latest","true"),  //默认为最新
                    new NameValuePair("filename",file.getName())
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
}
