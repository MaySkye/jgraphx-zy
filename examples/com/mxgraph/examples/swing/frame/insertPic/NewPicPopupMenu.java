package com.mxgraph.examples.swing.frame.insertPic;


import com.mxgraph.examples.swing.editor.EditorPalette;

import javax.swing.*;

/**
 * ���Ҽ��˵�ֻ�������ͼƬ���ɱ༭��������Ҫ�����������Ҽ��˵����ܣ����Լ����µ�JMenuItem
 * �������µ�Action��֮���д���
 * 
 * @author Cheer
 *
 */

public class NewPicPopupMenu extends JPopupMenu {

	final JMenuItem newPic = new JMenuItem("������ͼƬ");
	
	/**
	 * 
	 * @param ep   ��Ҫ����Ҽ����ͼƬ���ܵ�EditorPalette
	 */
	public NewPicPopupMenu(EditorPalette ep){
		this.add(newPic);
		ep.setComponentPopupMenu(this);
		
		//
		newPic.addActionListener(new addPicAction(ep));
	}
}
