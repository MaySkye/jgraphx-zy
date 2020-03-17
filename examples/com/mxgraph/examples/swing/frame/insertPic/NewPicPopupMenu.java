package com.mxgraph.examples.swing.frame.insertPic;


import com.mxgraph.examples.swing.editor.EditorPalette;

import javax.swing.*;

/**
 * 本右键菜单只用于添加图片到可编辑区，如需要其他的新增右键菜单功能，可以加入新的JMenuItem
 * 并增加新的Action对之进行处理
 * 
 * @author Cheer
 *
 */

public class NewPicPopupMenu extends JPopupMenu {

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
