package com.mxgraph.examples.swing.frame.insertPic;

import com.mxgraph.examples.swing.GraphEditor;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;

import javax.swing.*;

public class Cheer_EditorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		mxGraph g = new mxGraph();
		mxGraphComponent mgc = new mxGraphComponent(g);
		BasicGraphEditor bge = new BasicGraphEditor("test", mgc);;
		
		EditorPalette shapesPalette = bge.insertPalette("test");
		EditorPalette imagesPalette = bge.insertPalette(mxResources.get("images"));
		EditorPalette symbolsPalette = bge.insertPalette(mxResources.get("symbols"));
		
		//imagesPalette创建可添加新图片的功能
		NewPicPopupMenu nppm = new NewPicPopupMenu(imagesPalette);
		
		
		
		imagesPalette
		.addTemplate(
				"Bell",
				new ImageIcon(
						GraphEditor.class
								.getResource("/com/mxgraph/examples/swing/images/bell.png")),
				"image;image=/com/mxgraph/examples/swing/images/bell.png",
				50, 50, "Bell");
imagesPalette
		.addTemplate(
				"Box",
				new ImageIcon("D:\\guolu_01.png")
						,
				"image;image=file:///D:\\guolu_01.png",
				50, 50, "Box");
imagesPalette
		.addTemplate(
				"Cube",
				new ImageIcon(
						GraphEditor.class
								.getResource("/com/mxgraph/examples/swing/images/cube_green.png")),
				"image;image=/com/mxgraph/examples/swing/images/cube_green.png",
				50, 50, "Cube");


		mxCodec codec = new mxCodec();
		Document doc = mxUtils.loadDocument(FileUtil.getRootPath().substring(1) + "/examples/com/mxgraph/examples/swing/resources/basic-style.xml");
		codec.decode(doc.getDocumentElement(), g.getStylesheet());
		
		//bge.createFrame(new EditorMenuBar(bge)).setVisible(true);
	}

}
