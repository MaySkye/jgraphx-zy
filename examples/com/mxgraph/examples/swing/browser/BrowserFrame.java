package com.mxgraph.examples.swing.browser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Properties;

import com.mxgraph.examples.swing.frame.UploadMxeFileFrame;
import com.mxgraph.examples.swing.util.FileUtil;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.ba;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import static com.mxgraph.examples.swing.browser.FileCharsetConverter.convert;
import static com.mxgraph.examples.swing.browser.FileUtil.*;
import static com.sun.deploy.uitoolkit.ToolkitStore.dispose;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:11 2019/6/19
 * @Modify By:
 */
public class BrowserFrame {

    static {
        try {
            Field e = ba.class.getDeclaredField("e");
            e.setAccessible(true);
            Field f = ba.class.getDeclaredField("f");
            f.setAccessible(true);
            Field modifersField = Field.class.getDeclaredField("modifiers");
            modifersField.setAccessible(true);
            modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
            modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            e.set(null, new BigInteger("1"));
            f.set(null, new BigInteger("1"));
            modifersField.setAccessible(false);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    private static String webvOwlUrl;
    private static String webvDataPath;

    public static void viewschema(String filepath, String filename) {
        try {
            Properties pps = new Properties();
            pps.load(UploadMxeFileFrame.class.getResourceAsStream("/config/http_url.properties"));
            webvOwlUrl = pps.getProperty("mxbaseUrl") + pps.getProperty("webvOwlHtml");
            System.out.println("webvOwlUrl:"+webvOwlUrl);
            webvDataPath=FileUtil.getDirPath()+pps.getProperty("webvDataPath");
            System.out.println("webvDataPath:"+webvDataPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateJson(filepath, filename);
        final String title = filename.substring(0, filename.length() - 4) + "资源文件关系图";
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        //禁用close功能
       // frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //不显示标题栏,最大化,最小化,退出按钮
        //frame.setUndecorated(true);
        frame.setTitle(title);
        frame.setSize(1500, 900);
        frame.add(view, BorderLayout.CENTER);
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setLocationByPlatform(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    dispose();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        browser.loadURL(webvOwlUrl);
    }

    /*
    * D:\Graduation\mxgraph-master\javascript\examples\grapheditor\www\data\foaf.json*/
    public static void updateJson(String filepath, String filename) {

        String jarPath = FileUtil.getAppPath() ;
        jarPath=jarPath.substring(1);
        jarPath=jarPath+"/";
        System.out.println("jarPath:"+jarPath);

        //存储临时的json文件
        //String jsonPath = BrowserFrame2.class.getResource("/").getPath();
        String tomcatPath = System.getenv("CATALINA_HOME");
        /*//第一步  切换到D文件夹下
        String command1="D:";
        System.out.println("command1:"+command1);
        Process process = null;
        try {
            runProcess("cmd.exe /c "+command1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //第二步  切换到owl2vowl.jar所在目录  D:\software\apache-tomcat-7.0.90\webapps\ROOT\OwlView\owl2vowl.jar
        String command2="cd D:\\software\\apache-tomcat-7.0.90\\webapps\\ROOT\\OwlView";
        System.out.println("command2:"+command2);
        try {
            runProcess("cmd.exe /c "+command2);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //第三步
        //java -jar owl2vowl.jar -file ontologies/foaf.rdf
        String command3 = "java -jar " + jarPath+"owl/owl2vowl.jar" + " -file " + filepath;
        System.out.println("command3:" + command3);
        try {
            runProcess("cmd.exe /c "+command3);
           // runProcess(command3);
        } catch (Exception e) {
            e.printStackTrace();
        }

       //第四步
        //删文件
        //deleteFile(jsonPath + "foaf.json");
        deleteFile(webvDataPath+"foaf.json");
        //给文件改名  D:\LearnedProject\jgraphx-master\jgraphx-master
        String oldName = filename.substring(0, filename.length() - 4) + ".json";
       // System.out.println("oldName:" + oldName);
        String newName = "foaf.json";
        String absolutePath = jarPath;
        renameFile(oldName, newName, absolutePath);

        String sourceFile = absolutePath + newName;
       // System.out.println("sourceFile:" + sourceFile);
        String targetFile = webvDataPath+"foaf.json";

        //给文件移位置
        File file1 = new File(sourceFile);
        File file2 = new File(targetFile);
        copyFile(file1, file2);
        deleteFile(sourceFile);

       try {
            convert(webvDataPath+"foaf.json",
                    "GB2312", "UTF-8", new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith("json");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command,null,new File(FileUtil.getAppPath()));
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
    }

    private static void printLines(String cmd, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(cmd + " " + line);
        }
    }

    public static void main(String[] args) {
        viewschema("C:\\Users\\ASUS\\Desktop\\郑州.owl","郑州.owl");

    }

}
