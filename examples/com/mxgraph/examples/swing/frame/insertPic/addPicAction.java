package com.mxgraph.examples.swing.frame.insertPic;

import com.mxgraph.examples.swing.editor.EditorPalette;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ���ڴ����Ҽ��������ͼƬ�¼�
 * 
 * @author Cheer
 *  
 */

public class addPicAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3424078628421848114L;

	static EditorPalette ep;
	
	/**
	 * 
	 * @param imagesPalette ��Ҫ��Ӳ���ͼƬ���ܵ�EditorPalette
	 * 
	 * ��������{@link cheer.insertPic.NewPicPopupMenu} ���ã���ֱ��ʹ��.
	 */
	public addPicAction(EditorPalette imagesPalette) {
		ep = imagesPalette;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		 SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                //Turn off metal's use of bold fonts
	                UIManager.put("swing.boldMetal", Boolean.FALSE);
	                getPic();
	            }
		 });
	}
		
	 private static void getPic() {
	        //open a FileChooser
		 AddPicFileChooser fc = new AddPicFileChooser(ep);
	    }



}

