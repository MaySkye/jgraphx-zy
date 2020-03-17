/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package com.mxgraph.examples.swing.frame.insertPic;

import com.mxgraph.examples.swing.editor.EditorPalette;

import javax.swing.*;
import java.io.File;

//import org.h2.java.lang.System;

/**
 * ��jdk��FileChooserDemo2�Ļ����ϣ��Զ�ȡ���ļ����д�����ӵ���Ӧ�ı�ǩҳ�С�
 * 
 * @author Cheer
 *
 */
public class AddPicFileChooser{
	
    private JFileChooser fc;

    public AddPicFileChooser(EditorPalette ep) {
    	if (fc == null) {
            fc = new JFileChooser();

	    //Add a custom file filter and disable the default
	    //(Accept All) file filter.
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);


	    //Add the preview pane.
            fc.setAccessory(new ImagePreview(fc));
        }

        fc.setSelectedFile(null);
        int ret = fc.showOpenDialog(null);
        
        if(ret == JFileChooser.APPROVE_OPTION){
        	File f = fc.getSelectedFile();
        	String path = f.getAbsolutePath();
     //   	System.out.println("file:///" + f.getAbsolutePath());
        	ep.addTemplate(
    				f.getName(),
    				new ImageIcon(f.getAbsolutePath()),
    				"image;image=file:///" + path,
    				50, 50, f.getName());
        	ep.revalidate();
        	ep.repaint();

        }
    }

}
