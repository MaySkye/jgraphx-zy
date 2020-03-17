package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.examples.swing.frame.insertPic.addPicAction;

import javax.swing.*;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 14:21 2020/2/28
 * @Modify By:
 */
public class NewPicPopupMenu extends JPopupMenu{
    final JMenuItem newPic = new JMenuItem("导入新图片");

    /**
     *
     * @param ep   需要添加右键添加图片功能的EditorPalette
     */
    public NewPicPopupMenu(EditorPalette ep){
        this.add(newPic);
        ep.setComponentPopupMenu(this);

        //
        newPic.addActionListener(new addPicAction(ep));
    }
}
