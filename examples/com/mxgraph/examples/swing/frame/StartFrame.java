package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.GraphEditor;
import com.mxgraph.examples.swing.util.AliasNameDecoder;
import com.mxgraph.examples.swing.util.NameAliasEle;
import org.apache.jena.rdf.model.ModelFactory;


import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StartFrame {

    private static List<NameAliasEle> aliasList = AliasNameDecoder.decodeDoc();
    private static Map<String, String> aliasMap = new HashMap<>();


    public StartFrame() {

        aliasList.forEach(nameAliasEle -> {
            aliasMap.put(nameAliasEle.getName(), nameAliasEle.getAlias());
        });

        try {
            //优化界面
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean loginflag = true;
        GraphEditor editor = new GraphEditor(aliasMap.get("monitor_system"));

        //mxConstants.SHADOW_COLOR = Color.LIGHT_GRAY;

        JFrame frame = null;
        if (loginflag)     //根据flag信息，调整菜单栏
        {
           // frame = editor.createFrame(new EditorMenuBar(editor));
            //editor.setOldMenuBar(new EditorSimpleMenuBar(editor));
        } else {
            //frame = editor.createFrame(new EditorSimpleMenuBar(editor));
            //editor.setOldMenuBar(new EditorMenuBar(editor));
        }

        // pre load
        ModelFactory.createDefaultModel();

        //StartUI.isAllOk = true;

        try {
            //StartUI.isAllOk = true;
            //System.out.println("done!!!!");
            Thread.sleep(20); //线程休眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //frame.setVisible(true);
    }

//    public static void main(String[] args) {
//        try {
//            new Thread(new StartUI()).start();
//            new StartFrame();
//            //设置外观
//           /* UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
//            JFrame.setDefaultLookAndFeelDecorated(true);
//            SubstanceSaharaLookAndFeel.setSkin(new CremeSkin());*/
//
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }

}
