/*
 * Copyright (c) 2001-2012, JGraph Ltd
 */
package com.mxgraph.examples.swing.editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mxgraph.examples.swing.browser.BrowserFrame2;
import com.mxgraph.examples.swing.db.DBDataAdaptor;
import com.mxgraph.examples.swing.frame.UploadServiceFileFrame;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.Vertex;
import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.map.Liulanqi;
import com.mxgraph.examples.swing.map.OpenMap;
import com.mxgraph.examples.swing.match.ModifyTemplateCore;
import com.mxgraph.examples.swing.match.ResMatchCore;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.select.ResSelectFrame4;
import com.mxgraph.examples.swing.select.ResSelectFrame5;
import com.mxgraph.examples.swing.util.*;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.*;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.ba;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.w3c.dom.Document;

import com.mxgraph.analysis.mxDistanceCostFunction;
import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.*;
import static com.mxgraph.examples.swing.util.HttpUtil.sendHttpPost;
import static com.sun.deploy.uitoolkit.ToolkitStore.dispose;

/**
 *
 */
public class EditorActions
{

	private static DBDataAdaptor updateThread = null;

	/**
	 * 
	 * @param e
	 * @return Returns the graph for the given action event.
	 */
	public static final BasicGraphEditor getEditor(ActionEvent e)
	{
		if (e.getSource() instanceof Component)
		{
			Component component = (Component) e.getSource();

			while (component != null
					&& !(component instanceof BasicGraphEditor))
			{
				component = component.getParent();
			}

			return (BasicGraphEditor) component;
		}

		return null;
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleRulersItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleRulersItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(editor.getGraphComponent().getColumnHeader() != null);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					mxGraphComponent graphComponent = editor
							.getGraphComponent();

					if (graphComponent.getColumnHeader() != null)
					{
						graphComponent.setColumnHeader(null);
						graphComponent.setRowHeader(null);
					}
					else
					{
						graphComponent.setColumnHeaderView(new EditorRuler(
								graphComponent,
								EditorRuler.ORIENTATION_HORIZONTAL));
						graphComponent.setRowHeaderView(new EditorRuler(
								graphComponent,
								EditorRuler.ORIENTATION_VERTICAL));
					}
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleGridItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleGridItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(true);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					mxGraphComponent graphComponent = editor
							.getGraphComponent();
					mxGraph graph = graphComponent.getGraph();
					boolean enabled = !graph.isGridEnabled();

					graph.setGridEnabled(enabled);
					graphComponent.setGridVisible(enabled);
					graphComponent.repaint();
					setSelected(enabled);
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleOutlineItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleOutlineItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(true);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					final mxGraphOutline outline = editor.getGraphOutline();
					outline.setVisible(!outline.isVisible());
					outline.revalidate();

					SwingUtilities.invokeLater(new Runnable()
					{
						/*
						 * (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run()
						{
							if (outline.getParent() instanceof JSplitPane)
							{
								if (outline.isVisible())
								{
									((JSplitPane) outline.getParent())
											.setDividerLocation(editor
													.getHeight() - 300);
									((JSplitPane) outline.getParent())
											.setDividerSize(6);
								}
								else
								{
									((JSplitPane) outline.getParent())
											.setDividerSize(0);
								}
							}
						}
					});
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ExitAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				editor.exit();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class StylesheetAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String stylesheet;

		/**
		 * 
		 */
		public StylesheetAction(String stylesheet)
		{
			this.stylesheet = stylesheet;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxCodec codec = new mxCodec();
				Document doc = mxUtils.loadDocument(EditorActions.class
						.getResource(stylesheet).toString());

				if (doc != null)
				{
					codec.decode(doc.getDocumentElement(),
							graph.getStylesheet());
					graph.refresh();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ZoomPolicyAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected int zoomPolicy;

		/**
		 * 
		 */
		public ZoomPolicyAction(int zoomPolicy)
		{
			this.zoomPolicy = zoomPolicy;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.setPageVisible(true);
				graphComponent.setZoomPolicy(zoomPolicy);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class GridStyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected int style;

		/**
		 * 
		 */
		public GridStyleAction(int style)
		{
			this.style = style;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.setGridStyle(style);
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class GridColorAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent,
						mxResources.get("gridColor"),
						graphComponent.getGridColor());

				if (newColor != null)
				{
					graphComponent.setGridColor(newColor);
					graphComponent.repaint();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ScaleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected double scale;

		/**
		 * 
		 */
		public ScaleAction(double scale)
		{
			this.scale = scale;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				double scale = this.scale;

				if (scale == 0)
				{
					String value = (String) JOptionPane.showInputDialog(
							graphComponent, mxResources.get("value"),
							mxResources.get("scale") + " (%)",
							JOptionPane.PLAIN_MESSAGE, null, null, "");

					if (value != null)
					{
						scale = Double.parseDouble(value.replace("%", "")) / 100;
					}
				}

				if (scale > 0)
				{
					graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PageSetupAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				PrinterJob pj = PrinterJob.getPrinterJob();
				PageFormat format = pj.pageDialog(graphComponent
						.getPageFormat());

				if (format != null)
				{
					graphComponent.setPageFormat(format);
					graphComponent.zoomAndCenter();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PrintAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				PrinterJob pj = PrinterJob.getPrinterJob();

				if (pj.printDialog())
				{
					PageFormat pf = graphComponent.getPageFormat();
					Paper paper = new Paper();
					double margin = 36;
					paper.setImageableArea(margin, margin, paper.getWidth()
							- margin * 2, paper.getHeight() - margin * 2);
					pf.setPaper(paper);
					pj.setPrintable(graphComponent, pf);

					try
					{
						pj.print();
					}
					catch (PrinterException e2)
					{
						System.out.println(e2);
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean showDialog;

		/**
		 * 
		 */
		protected String lastDir = null;

		/**
		 * 
		 */
		public SaveAction(boolean showDialog)
		{
			this.showDialog = showDialog;
		}

		/**
		 * Saves XML+PNG format.
		 */
		protected void saveXmlPng(BasicGraphEditor editor, String filename,
				Color bg) throws IOException
		{
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();

			// Creates the image for the PNG file
			BufferedImage image = mxCellRenderer.createBufferedImage(graph,
					null, 1, bg, graphComponent.isAntiAlias(), null,
					graphComponent.getCanvas());

			// Creates the URL-encoded XML data
			mxCodec codec = new mxCodec();
			String xml = URLEncoder.encode(
					mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
			mxPngEncodeParam param = mxPngEncodeParam
					.getDefaultEncodeParam(image);
			param.setCompressedText(new String[] { "mxGraphModel", xml });

			// Saves as a PNG file
			FileOutputStream outputStream = new FileOutputStream(new File(
					filename));
			try
			{
				mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
						param);

				if (image != null)
				{
					encoder.encode(image);

					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noImageData"));
				}
			}
			finally
			{
				outputStream.close();
			}
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				FileFilter selectedFilter = null;
				DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
						"PNG+XML " + mxResources.get("file") + " (.png)");
				FileFilter vmlFileFilter = new DefaultFileFilter(".html",
						"VML " + mxResources.get("file") + " (.html)");
				String filename = null;
				boolean dialogShown = false;

				if (showDialog || editor.getCurrentFile() == null)
				{
					String wd;

					if (lastDir != null)
					{
						wd = lastDir;
					}
					else if (editor.getCurrentFile() != null)
					{
						wd = editor.getCurrentFile().getParent();
					}
					else
					{
						wd = FileUtil.getAppPath();
					}

					JFileChooser fc = new JFileChooser(wd);

					// Adds the default file format
					FileFilter defaultFilter = xmlPngFilter;
					fc.addChoosableFileFilter(defaultFilter);

					// Adds special vector graphics formats and HTML
					fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
							"mxGraph Editor " + mxResources.get("file")
									+ " (.mxe)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
							"Graph Drawing " + mxResources.get("file")
									+ " (.txt)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
							"SVG " + mxResources.get("file") + " (.svg)"));
					fc.addChoosableFileFilter(vmlFileFilter);
					fc.addChoosableFileFilter(new DefaultFileFilter(".html",
							"HTML " + mxResources.get("file") + " (.html)"));

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();

					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<String>();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString().toLowerCase();
						formats.add(ext);
					}

					imageFormats = formats.toArray();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("."
								+ ext, ext.toUpperCase() + " "
								+ mxResources.get("file") + " (." + ext + ")"));
					}

					// Adds filter that accepts all supported image formats
					fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
							mxResources.get("allImages")));
					fc.setFileFilter(defaultFilter);
					int rc = fc.showDialog(null, mxResources.get("save"));
					dialogShown = true;

					if (rc != JFileChooser.APPROVE_OPTION)
					{
						return;
					}
					else
					{
						lastDir = fc.getSelectedFile().getParent();
					}

					filename = fc.getSelectedFile().getAbsolutePath();
					selectedFilter = fc.getFileFilter();

					if (selectedFilter instanceof DefaultFileFilter)
					{
						String ext = ((DefaultFileFilter) selectedFilter)
								.getExtension();

						if (!filename.toLowerCase().endsWith(ext))
						{
							filename += ext;
						}
					}

					if (new File(filename).exists()
							&& JOptionPane.showConfirmDialog(graphComponent,
									mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
				else
				{
					filename = editor.getCurrentFile().getAbsolutePath();
				}

				try
				{
					String ext = filename
							.substring(filename.lastIndexOf('.') + 1);

					if (ext.equalsIgnoreCase("svg"))
					{
						mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
								.drawCells(graph, null, 1, null,
										new CanvasFactory()
										{
											public mxICanvas createCanvas(
													int width, int height)
											{
												mxSvgCanvas canvas = new mxSvgCanvas(
														mxDomUtils.createSvgDocument(
																width, height));
												canvas.setEmbedded(true);

												return canvas;
											}

										});

						mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()),
								filename);
					}
					else if (selectedFilter == vmlFileFilter)
					{
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("html"))
					{
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("mxe")
							|| ext.equalsIgnoreCase("xml"))
					{
						mxCodec codec = new mxCodec();
						String xml = mxXmlUtils.getXml(codec.encode(graph
								.getModel()));

						mxUtils.writeFile(xml, filename);

						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
					else if (ext.equalsIgnoreCase("txt"))
					{
						String content = mxGdCodec.encode(graph);

						mxUtils.writeFile(content, filename);
					}
					else
					{
						Color bg = null;

						if ((!ext.equalsIgnoreCase("gif") && !ext
								.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
												.get("transparentBackground")) != JOptionPane.YES_OPTION)
						{
							bg = graphComponent.getBackground();
						}

						if (selectedFilter == xmlPngFilter
								|| (editor.getCurrentFile() != null
										&& ext.equalsIgnoreCase("png") && !dialogShown))
						{
							saveXmlPng(editor, filename, bg);
						}
						else
						{
							BufferedImage image = mxCellRenderer
									.createBufferedImage(graph, null, 1, bg,
											graphComponent.isAntiAlias(), null,
											graphComponent.getCanvas());

							if (image != null)
							{
								ImageIO.write(image, ext, new File(filename));
							}
							else
							{
								JOptionPane.showMessageDialog(graphComponent,
										mxResources.get("noImageData"));
							}
						}
					}
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SelectShortestPathAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean directed;

		/**
		 * 
		 */
		public SelectShortestPathAction(boolean directed)
		{
			this.directed = directed;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxIGraphModel model = graph.getModel();

				Object source = null;
				Object target = null;

				Object[] cells = graph.getSelectionCells();

				for (int i = 0; i < cells.length; i++)
				{
					if (model.isVertex(cells[i]))
					{
						if (source == null)
						{
							source = cells[i];
						}
						else if (target == null)
						{
							target = cells[i];
						}
					}

					if (source != null && target != null)
					{
						break;
					}
				}

				if (source != null && target != null)
				{
					int steps = graph.getChildEdges(graph.getDefaultParent()).length;
					Object[] path = mxGraphAnalysis.getInstance()
							.getShortestPath(graph, source, target,
									new mxDistanceCostFunction(), steps,
									directed);
					graph.setSelectionCells(path);
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noSourceAndTargetSelected"));
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SelectSpanningTreeAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean directed;

		/**
		 * 
		 */
		public SelectSpanningTreeAction(boolean directed)
		{
			this.directed = directed;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				mxIGraphModel model = graph.getModel();

				Object parent = graph.getDefaultParent();
				Object[] cells = graph.getSelectionCells();

				for (int i = 0; i < cells.length; i++)
				{
					if (model.getChildCount(cells[i]) > 0)
					{
						parent = cells[i];
						break;
					}
				}

				Object[] v = graph.getChildVertices(parent);
				Object[] mst = mxGraphAnalysis.getInstance()
						.getMinimumSpanningTree(graph, v,
								new mxDistanceCostFunction(), directed);
				graph.setSelectionCells(mst);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleDirtyAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.showDirtyRectangle = !graphComponent.showDirtyRectangle;
			}
		}

	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleConnectModeAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxConnectionHandler handler = graphComponent
						.getConnectionHandler();
				handler.setHandleEnabled(!handler.isHandleEnabled());
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleCreateTargetItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public ToggleCreateTargetItem(final BasicGraphEditor editor, String name)
		{
			super(name);
			setSelected(true);

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					mxGraphComponent graphComponent = editor
							.getGraphComponent();

					if (graphComponent != null)
					{
						mxConnectionHandler handler = graphComponent
								.getConnectionHandler();
						handler.setCreateTarget(!handler.isCreateTarget());
						setSelected(handler.isCreateTarget());
					}
				}
			});
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PromptPropertyAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected Object target;

		/**
		 * 
		 */
		protected String fieldname, message;

		/**
		 * 
		 */
		public PromptPropertyAction(Object target, String message)
		{
			this(target, message, message);
		}

		/**
		 * 
		 */
		public PromptPropertyAction(Object target, String message,
				String fieldname)
		{
			this.target = target;
			this.message = message;
			this.fieldname = fieldname;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof Component)
			{
				try
				{
					Method getter = target.getClass().getMethod(
							"get" + fieldname);
					Object current = getter.invoke(target);

					// TODO: Support other atomic types
					if (current instanceof Integer)
					{
						Method setter = target.getClass().getMethod(
								"set" + fieldname, new Class[] { int.class });

						String value = (String) JOptionPane.showInputDialog(
								(Component) e.getSource(), "Value", message,
								JOptionPane.PLAIN_MESSAGE, null, null, current);

						if (value != null)
						{
							setter.invoke(target, Integer.parseInt(value));
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			// Repaints the graph component
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class TogglePropertyItem extends JCheckBoxMenuItem
	{
		/**
		 * 
		 */
		public TogglePropertyItem(Object target, String name, String fieldname)
		{
			this(target, name, fieldname, false);
		}

		/**
		 * 
		 */
		public TogglePropertyItem(Object target, String name, String fieldname,
				boolean refresh)
		{
			this(target, name, fieldname, refresh, null);
		}

		/**
		 * 
		 */
		public TogglePropertyItem(final Object target, String name,
				final String fieldname, final boolean refresh,
				ActionListener listener)
		{
			super(name);

			// Since action listeners are processed last to first we add the given
			// listener here which means it will be processed after the one below
			if (listener != null)
			{
				addActionListener(listener);
			}

			addActionListener(new ActionListener()
			{
				/**
				 * 
				 */
				public void actionPerformed(ActionEvent e)
				{
					execute(target, fieldname, refresh);
				}
			});

			PropertyChangeListener propertyChangeListener = new PropertyChangeListener()
			{

				/*
				 * (non-Javadoc)
				 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
				 */
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (evt.getPropertyName().equalsIgnoreCase(fieldname))
					{
						update(target, fieldname);
					}
				}
			};

			if (target instanceof mxGraphComponent)
			{
				((mxGraphComponent) target)
						.addPropertyChangeListener(propertyChangeListener);
			}
			else if (target instanceof mxGraph)
			{
				((mxGraph) target)
						.addPropertyChangeListener(propertyChangeListener);
			}

			update(target, fieldname);
		}

		/**
		 * 
		 */
		public void update(Object target, String fieldname)
		{
			if (target != null && fieldname != null)
			{
				try
				{
					Method getter = target.getClass().getMethod(
							"is" + fieldname);

					if (getter != null)
					{
						Object current = getter.invoke(target);

						if (current instanceof Boolean)
						{
							setSelected(((Boolean) current).booleanValue());
						}
					}
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}

		/**
		 * 
		 */
		public void execute(Object target, String fieldname, boolean refresh)
		{
			if (target != null && fieldname != null)
			{
				try
				{
					Method getter = target.getClass().getMethod(
							"is" + fieldname);
					Method setter = target.getClass().getMethod(
							"set" + fieldname, new Class[] { boolean.class });

					Object current = getter.invoke(target);

					if (current instanceof Boolean)
					{
						boolean value = !((Boolean) current).booleanValue();
						setter.invoke(target, value);
						setSelected(value);
					}

					if (refresh)
					{
						mxGraph graph = null;

						if (target instanceof mxGraph)
						{
							graph = (mxGraph) target;
						}
						else if (target instanceof mxGraphComponent)
						{
							graph = ((mxGraphComponent) target).getGraph();
						}

						graph.refresh();
					}
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class HistoryAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean undo;

		/**
		 * 
		 */
		public HistoryAction(boolean undo)
		{
			this.undo = undo;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				if (undo)
				{
					editor.getUndoManager().undo();
				}
				else
				{
					editor.getUndoManager().redo();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class FontStyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean bold;

		/**
		 * 
		 */
		public FontStyleAction(boolean bold)
		{
			this.bold = bold;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Component editorComponent = null;

				if (graphComponent.getCellEditor() instanceof mxCellEditor)
				{
					editorComponent = ((mxCellEditor) graphComponent
							.getCellEditor()).getEditor();
				}

				if (editorComponent instanceof JEditorPane)
				{
					JEditorPane editorPane = (JEditorPane) editorComponent;
					int start = editorPane.getSelectionStart();
					int ende = editorPane.getSelectionEnd();
					String text = editorPane.getSelectedText();

					if (text == null)
					{
						text = "";
					}

					try
					{
						HTMLEditorKit editorKit = new HTMLEditorKit();
						HTMLDocument document = (HTMLDocument) editorPane
								.getDocument();
						document.remove(start, (ende - start));
						editorKit.insertHTML(document, start, ((bold) ? "<b>"
								: "<i>") + text + ((bold) ? "</b>" : "</i>"),
								0, 0, (bold) ? HTML.Tag.B : HTML.Tag.I);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					editorPane.requestFocus();
					editorPane.select(start, ende);
				}
				else
				{
					mxIGraphModel model = graphComponent.getGraph().getModel();
					model.beginUpdate();
					try
					{
						graphComponent.stopEditing(false);
						graphComponent.getGraph().toggleCellStyleFlags(
								mxConstants.STYLE_FONTSTYLE,
								(bold) ? mxConstants.FONT_BOLD
										: mxConstants.FONT_ITALIC);
					}
					finally
					{
						model.endUpdate();
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class WarningAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Object[] cells = graphComponent.getGraph().getSelectionCells();

				if (cells != null && cells.length > 0)
				{
					String warning = JOptionPane.showInputDialog(mxResources
							.get("enterWarningMessage"));

					for (int i = 0; i < cells.length; i++)
					{
						graphComponent.setCellWarning(cells[i], warning);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noCellSelected"));
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class NewAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				if (!editor.isModified()
						|| JOptionPane.showConfirmDialog(editor,
								mxResources.get("loseChanges")) == JOptionPane.YES_OPTION)
				{
					mxGraph graph = editor.getGraphComponent().getGraph();

					// Check modified flag and display save dialog
					mxCell root = new mxCell();
					root.insert(new mxCell());
					graph.getModel().setRoot(root);

					editor.setModified(false);
					editor.setCurrentFile(null);
					editor.getGraphComponent().zoomAndCenter();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ImportAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String lastDir;

		/**
		 * Loads and registers the shape as a new shape in mxGraphics2DCanvas and
		 * adds a new entry to use that shape in the specified palette
		 * @param palette The palette to add the shape to.
		 * @param nodeXml The raw XML of the shape
		 * @param path The path to the directory the shape exists in
		 * @return the string name of the shape
		 */
		public static String addStencilShape(EditorPalette palette,
				String nodeXml, String path)
		{

			// Some editors place a 3 byte BOM at the start of files
			// Ensure the first char is a "<"
			int lessthanIndex = nodeXml.indexOf("<");
			nodeXml = nodeXml.substring(lessthanIndex);
			mxStencilShape newShape = new mxStencilShape(nodeXml);
			String name = newShape.getName();
			ImageIcon icon = null;

			if (path != null)
			{
				String iconPath = path + newShape.getIconPath();
				icon = new ImageIcon(iconPath);
			}

			// Registers the shape in the canvas shape registry
			mxGraphics2DCanvas.putShape(name, newShape);

			if (palette != null && icon != null)
			{
				palette.addTemplate(name, icon, "shape=" + name, 80, 80, "");
			}

			return name;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				String wd = (lastDir != null) ? lastDir : FileUtil.getAppPath();

				JFileChooser fc = new JFileChooser(wd);

				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				// Adds file filter for Dia shape import
				fc.addChoosableFileFilter(new DefaultFileFilter(".shape",
						"Dia Shape " + mxResources.get("file") + " (.shape)"));

				int rc = fc.showDialog(null, mxResources.get("importStencil"));

				if (rc == JFileChooser.APPROVE_OPTION)
				{
					lastDir = fc.getSelectedFile().getParent();

					try
					{
						if (fc.getSelectedFile().isDirectory())
						{
							EditorPalette palette = editor.insertPalette(fc
									.getSelectedFile().getName());

							for (File f : fc.getSelectedFile().listFiles(
									new FilenameFilter()
									{
										public boolean accept(File dir,
												String name)
										{
											return name.toLowerCase().endsWith(
													".shape");
										}
									}))
							{
								String nodeXml = mxUtils.readFile(f
										.getAbsolutePath());
								addStencilShape(palette, nodeXml, f.getParent()
										+ File.separator);
							}

							JComponent scrollPane = (JComponent) palette
									.getParent().getParent();
							editor.getLibraryPane().setSelectedComponent(
									scrollPane);

							// FIXME: Need to update the size of the palette to force a layout
							// update. Re/in/validate of palette or parent does not work.
							//editor.getLibraryPane().revalidate();
						}
						else
						{
							String nodeXml = mxUtils.readFile(fc
									.getSelectedFile().getAbsolutePath());
							String name = addStencilShape(null, nodeXml, null);

							JOptionPane.showMessageDialog(editor, mxResources
									.get("stencilImported",
											new String[] { name }));
						}
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class OpenAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String lastDir;

		/**
		 * 
		 */
		protected void resetEditor(BasicGraphEditor editor)
		{
			editor.setModified(false);
			editor.getUndoManager().clear();
			editor.getGraphComponent().zoomAndCenter();
		}

		/**
		 * Reads XML+PNG format.
		 */
		protected void openXmlPng(BasicGraphEditor editor, File file)
				throws IOException
		{
			Map<String, String> text = mxPngTextDecoder
					.decodeCompressedText(new FileInputStream(file));

			if (text != null)
			{
				String value = text.get("mxGraphModel");

				if (value != null)
				{
					Document document = mxXmlUtils.parseXml(URLDecoder.decode(
							value, "UTF-8"));
					mxCodec codec = new mxCodec(document);
					codec.decode(document.getDocumentElement(), editor
							.getGraphComponent().getGraph().getModel());
					editor.setCurrentFile(file);
					resetEditor(editor);

					return;
				}
			}

			JOptionPane.showMessageDialog(editor,
					mxResources.get("imageContainsNoDiagramData"));
		}

		/**
		 * @throws IOException
		 *
		 */
		protected void openGD(BasicGraphEditor editor, File file,
				String gdText)
		{
			mxGraph graph = editor.getGraphComponent().getGraph();

			// Replaces file extension with .mxe
			String filename = file.getName();
			filename = filename.substring(0, filename.length() - 4) + ".mxe";

			if (new File(filename).exists()
					&& JOptionPane.showConfirmDialog(editor,
							mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
			{
				return;
			}

			((mxGraphModel) graph.getModel()).clear();
			mxGdCodec.decode(gdText, graph);
			editor.getGraphComponent().zoomAndCenter();
			editor.setCurrentFile(new File(lastDir + "/" + filename));
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				if (!editor.isModified()
						|| JOptionPane.showConfirmDialog(editor,
								mxResources.get("loseChanges")) == JOptionPane.YES_OPTION)
				{
					mxGraph graph = editor.getGraphComponent().getGraph();

					if (graph != null)
					{
						String wd = (lastDir != null) ? lastDir : FileUtil.getAppPath();

						JFileChooser fc = new JFileChooser(wd);

						// Adds file filter for supported file format
						DefaultFileFilter defaultFilter = new DefaultFileFilter(
								".mxe", mxResources.get("allSupportedFormats")
										+ " (.mxe, .png, .vdx)")
						{

							public boolean accept(File file)
							{
								String lcase = file.getName().toLowerCase();

								return super.accept(file)
										|| lcase.endsWith(".png")
										|| lcase.endsWith(".vdx");
							}
						};
						fc.addChoosableFileFilter(defaultFilter);

						fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
								"mxGraph Editor " + mxResources.get("file")
										+ " (.mxe)"));
						fc.addChoosableFileFilter(new DefaultFileFilter(".png",
								"PNG+XML  " + mxResources.get("file")
										+ " (.png)"));

						// Adds file filter for VDX import
						fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
								"XML Drawing  " + mxResources.get("file")
										+ " (.vdx)"));

						// Adds file filter for GD import
						fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
								"Graph Drawing  " + mxResources.get("file")
										+ " (.txt)"));

						fc.setFileFilter(defaultFilter);

						int rc = fc.showDialog(null,
								mxResources.get("openFile"));

						if (rc == JFileChooser.APPROVE_OPTION)
						{
							lastDir = fc.getSelectedFile().getParent();

							try
							{
								if (fc.getSelectedFile().getAbsolutePath()
										.toLowerCase().endsWith(".png"))
								{
									openXmlPng(editor, fc.getSelectedFile());
								}
								else if (fc.getSelectedFile().getAbsolutePath()
										.toLowerCase().endsWith(".txt"))
								{
									openGD(editor, fc.getSelectedFile(),
											mxUtils.readFile(fc
													.getSelectedFile()
													.getAbsolutePath()));
								}
								else
								{
									Document document = mxXmlUtils
											.parseXml(mxUtils.readFile(fc
													.getSelectedFile()
													.getAbsolutePath()));

									mxCodec codec = new mxCodec(document);
									codec.decode(
											document.getDocumentElement(),
											graph.getModel());
									editor.setCurrentFile(fc
											.getSelectedFile());

									resetEditor(editor);
								}
							}
							catch (IOException ex)
							{
								ex.printStackTrace();
								JOptionPane.showMessageDialog(
										editor.getGraphComponent(),
										ex.toString(),
										mxResources.get("error"),
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ToggleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key;

		/**
		 * 
		 */
		protected boolean defaultValue;

		/**
		 * 
		 * @param key
		 */
		public ToggleAction(String key)
		{
			this(key, false);
		}

		/**
		 * 
		 * @param key
		 */
		public ToggleAction(String key, boolean defaultValue)
		{
			this.key = key;
			this.defaultValue = defaultValue;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null)
			{
				graph.toggleCellStyles(key, defaultValue);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SetLabelPositionAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String labelPosition, alignment;

		/**
		 * 
		 * @param
		 */
		public SetLabelPositionAction(String labelPosition, String alignment)
		{
			this.labelPosition = labelPosition;
			this.alignment = alignment;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				graph.getModel().beginUpdate();
				try
				{
					// Checks the orientation of the alignment to use the correct constants
					if (labelPosition.equals(mxConstants.ALIGN_LEFT)
							|| labelPosition.equals(mxConstants.ALIGN_CENTER)
							|| labelPosition.equals(mxConstants.ALIGN_RIGHT))
					{
						graph.setCellStyles(mxConstants.STYLE_LABEL_POSITION,
								labelPosition);
						graph.setCellStyles(mxConstants.STYLE_ALIGN, alignment);
					}
					else
					{
						graph.setCellStyles(
								mxConstants.STYLE_VERTICAL_LABEL_POSITION,
								labelPosition);
						graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN,
								alignment);
					}
				}
				finally
				{
					graph.getModel().endUpdate();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SetStyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String value;

		/**
		 * 
		 * @param value
		 */
		public SetStyleAction(String value)
		{
			this.value = value;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				graph.setCellStyle(value);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class KeyValueAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key, value;

		/**
		 * 
		 * @param key
		 */
		public KeyValueAction(String key)
		{
			this(key, null);
		}

		/**
		 * 
		 * @param key
		 */
		public KeyValueAction(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				graph.setCellStyles(key, value);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PromptValueAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String key, message;

		/**
		 * 
		 * @param key
		 */
		public PromptValueAction(String key, String message)
		{
			this.key = key;
			this.message = message;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof Component)
			{
				mxGraph graph = mxGraphActions.getGraph(e);

				if (graph != null && !graph.isSelectionEmpty())
				{
					String value = (String) JOptionPane.showInputDialog(
							(Component) e.getSource(),
							mxResources.get("value"), message,
							JOptionPane.PLAIN_MESSAGE, null, null, "");

					if (value != null)
					{
						if (value.equals(mxConstants.NONE))
						{
							value = null;
						}

						graph.setCellStyles(key, value);
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class AlignCellsAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String align;

		/**
		 * 
		 * @param align
		 */
		public AlignCellsAction(String align)
		{
			this.align = align;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				graph.alignCells(align);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class AutosizeAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = mxGraphActions.getGraph(e);

			if (graph != null && !graph.isSelectionEmpty())
			{
				Object[] cells = graph.getSelectionCells();
				mxIGraphModel model = graph.getModel();

				model.beginUpdate();
				try
				{
					for (int i = 0; i < cells.length; i++)
					{
						graph.updateCellSize(cells[i]);
					}
				}
				finally
				{
					model.endUpdate();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class ColorAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String name, key;

		/**
		 * 
		 * @param key
		 */
		public ColorAction(String name, String key)
		{
			this.name = name;
			this.key = key;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();

				if (!graph.isSelectionEmpty())
				{
					Color newColor = JColorChooser.showDialog(graphComponent,
							name, null);

					if (newColor != null)
					{
						graph.setCellStyles(key, mxUtils.hexString(newColor));
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class BackgroundImageAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				String value = (String) JOptionPane.showInputDialog(
						graphComponent, mxResources.get("backgroundImage"),
						"URL", JOptionPane.PLAIN_MESSAGE, null, null,
						"http://www.callatecs.com/images/background2.JPG");

				if (value != null)
				{
					if (value.length() == 0)
					{
						graphComponent.setBackgroundImage(null);
					}
					else
					{
						Image background = mxUtils.loadImage(value);
						// Incorrect URLs will result in no image.
						// TODO provide feedback that the URL is not correct
						if (background != null)
						{
							graphComponent.setBackgroundImage(new ImageIcon(
									background));
						}
					}

					// Forces a repaint of the outline
					graphComponent.getGraph().repaint();
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class BackgroundAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent,
						mxResources.get("background"), null);

				if (newColor != null)
				{
					graphComponent.getViewport().setOpaque(true);
					graphComponent.getViewport().setBackground(newColor);
				}

				// Forces a repaint of the outline
				graphComponent.getGraph().repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class PageBackgroundAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent,
						mxResources.get("pageBackground"), null);

				if (newColor != null)
				{
					graphComponent.setPageBackgroundColor(newColor);
				}

				// Forces a repaint of the component
				graphComponent.repaint();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class StyleAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				mxGraph graph = graphComponent.getGraph();
				String initial = graph.getModel().getStyle(
						graph.getSelectionCell());
				String value = (String) JOptionPane.showInputDialog(
						graphComponent, mxResources.get("style"),
						mxResources.get("style"), JOptionPane.PLAIN_MESSAGE,
						null, null, initial);

				if (value != null)
				{
					graph.setCellStyle(value);
				}
			}
		}
	}

	public static class MapChartAction extends AbstractAction
	{
		/**
		 * @Author:zhoayi
		 * @Description：show map chart
		 * @Date：11:04 2018/7/27
		 * @Modify：
		 */
		public void actionPerformed(ActionEvent e)
		{
                //System.out.println("lllll");
			try {
				OpenMap.open("http://localhost:8080/baidumap2/index.html");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static class TestAction extends AbstractAction
	{
		/**
		 * @Author:zhoayi
		 * @Description：
		 * @Date：11:04 2018/7/27
		 * @Modify：
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
			String filePath="C:/Users/ASUS/Desktop/new.owl";
			editor.setResourceFile(filePath);
			OwlResourceData owlResourceData = parseResourceFile(filePath);
            //这两句不匹配模板图时使用
			GraphInterface<String> graph=showGraph.createGraph(owlResourceData);
			Map<String, mxCell> createCell= showGraph.drawMxCell(graph,editor,filePath);
		}
	}

	public static class GenerateDiagramAction extends AbstractAction
	{
		/**
		 * @Author:zhoayi
		 * @Description：
		 * @Date：11:04 2018/7/27
		 * @Modify：
		 */
		public void actionPerformed(ActionEvent e)
		{

			// 打开文件选择窗口
			String lastDir = null;
			String wd = (lastDir != null) ? lastDir : FileUtil.getAppPath();

			JFileChooser fc = new JFileChooser(wd);

			// Adds file filter for supported file format
			DefaultFileFilter defaultFilter = new DefaultFileFilter(".owl",
					mxResources.get("allSupportedFormats") + " (.owl)") {

				public boolean accept(File file) {
					String lcase = file.getName().toLowerCase();
					return super.accept(file) || lcase.endsWith(".owl");
				}
			};
			fc.addChoosableFileFilter(defaultFilter);
			fc.setFileFilter(defaultFilter);
			fc.setDialogTitle("打开文件");

			int rc = fc.showDialog(null, AliasName.getAlias("open_file"));

			if (rc == JFileChooser.APPROVE_OPTION) {
				File sFile = fc.getSelectedFile();
				lastDir = sFile.getParent();

				if (sFile.getAbsolutePath().toLowerCase().endsWith(".owl")) {
					BasicGraphEditor editor = getEditor(e);
					String filePath=sFile.getAbsolutePath();
					editor.setResourceFile(filePath);
					OwlResourceData origin_owlResourceData = parseResourceFile(filePath);
					OwlResourceData new_owlResourceData = parseResourceFile(filePath);
					editor.setNew_owlResourceData(new_owlResourceData);
					editor.setOrigin_owlResourceData(origin_owlResourceData);

					//匹配模板图时使用如下方法
					//为graph匹配相似度最高的match_graph,得到对应模板图的文件名
					String TemplatePath= ResMatchCore.getTemplatePath(origin_owlResourceData);
					//System.out.println("TemplatePath:"+TemplatePath);

					/*此段代码可以显示出模板mxe文件*/
					if (TemplatePath == null) {
						return;
					}
					Document document = null;
					try {
						document = mxXmlUtils.parseXml(FileUtil.readFile(this.getClass().getResourceAsStream(TemplatePath)));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					mxCodec codec = new mxCodec(document);
					codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());

					// 根据资源模型文件，对模板组态图进行调整,
					// 则可以显示出调整好的组态图，图元没有绑定资源信息
					GraphInterface<String> graph=showGraph.createGraph(origin_owlResourceData);
					new ModifyTemplateCore(TemplatePath).postProcess(graph,editor,filePath);

					//添加标题
					showTitle(sFile.getName(),editor);
					editor.getGraphComponent().getGraph().refresh();
					editor.getOrigin_owlResourceData().title= sFile.getName().substring(0,sFile.getName().length()-4);
					editor.getNew_owlResourceData().title=sFile.getName().substring(0,sFile.getName().length()-4);

					//editor.getOrigin_owlResourceData().title=EncodeUtil.GBKTOUTF8(sFile.getName().substring(0,sFile.getName().length()-4));
					//editor.getNew_owlResourceData().title=EncodeUtil.GBKTOUTF8(sFile.getName().substring(0,sFile.getName().length()-4));
				}
			}
		}
	}

	public static void showTitle(String sitename, BasicGraphEditor editor){
		String title=sitename.substring(0,sitename.length()-4)+"站点监控图";
		String titleStyle = AliasName.getAlias("monitor_title_style");
		mxCell titleCell = new mxCell(title, title,
				new mxGeometry(100, 50, 400, 60), titleStyle);
		//System.out.println("--------x:"+titleCell.getGeometry().getX()+"  y:"+titleCell.getGeometry().getY());
		titleCell.setType("title");
		titleCell.setVertex(true);
		titleCell.setConnectable(false);
		((mxCell) editor.getGraphComponent().getGraph().getDefaultParent()).insert(titleCell);
	}
	public static class SelectResAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
			String filePath="C:/Users/ASUS/Desktop/new.owl";
			editor.setResourceFile(filePath);
			OwlResourceData origin_owlResourceData = parseResourceFile(filePath);
			OwlResourceData new_owlResourceData = parseResourceFile(filePath);
			editor.setNew_owlResourceData(new_owlResourceData);

			System.out.println("改变前：-----------------------------------------------------------------------------");
			printOwlobject(editor.getNew_owlResourceData().objMap);

			new ResSelectFrame4(editor,origin_owlResourceData);


		}
	}

	public static class ConfigureFileResAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			// 打开文件选择窗口
			String lastDir = null;
			String wd = (lastDir != null) ? lastDir : FileUtil.getAppPath();

			JFileChooser fc = new JFileChooser(wd);

			// Adds file filter for supported file format
			DefaultFileFilter defaultFilter = new DefaultFileFilter(".owl",
					mxResources.get("allSupportedFormats") + " (.owl)") {

				public boolean accept(File file) {
					String lcase = file.getName().toLowerCase();
					return super.accept(file) || lcase.endsWith(".owl");
				}
			};
			fc.addChoosableFileFilter(defaultFilter);
			fc.setFileFilter(defaultFilter);
			fc.setDialogTitle("打开文件");

			int rc = fc.showDialog(null, AliasName.getAlias("open_file"));

			if (rc == JFileChooser.APPROVE_OPTION) {
				File sFile = fc.getSelectedFile();
				lastDir = sFile.getParent();

				if (sFile.getAbsolutePath().toLowerCase().endsWith(".owl")) {
					BasicGraphEditor editor = getEditor(e);
					String filePath=sFile.getAbsolutePath();
					editor.setResourceFile(filePath);
					OwlResourceData origin_owlResourceData = parseResourceFile(filePath);
					OwlResourceData new_owlResourceData = parseResourceFile(filePath);
					editor.setNew_owlResourceData(new_owlResourceData);
					editor.setOrigin_owlResourceData(origin_owlResourceData);
					ResSelectFrame5 resSelectFrame5=new ResSelectFrame5(editor,sFile.getName());


				}
			}


			/*BasicGraphEditor editor = getEditor(e);
			String filePath="C:/Users/ASUS/Desktop/putong.owl";
			editor.setResourceFile(filePath);
			OwlResourceData origin_owlResourceData = parseResourceFile(filePath);
			OwlResourceData new_owlResourceData = parseResourceFile(filePath);
			editor.setNew_owlResourceData(new_owlResourceData);
			editor.setOrigin_owlResourceData(origin_owlResourceData);

			ResSelectFrame5 resSelectFrame5=new ResSelectFrame5(editor);*/
			/*selectHandler frame = new selectHandler(editor,origin_owlResourceData);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1000, 800);
			frame.setVisible(true);*/


		}
	}

	// 保存到mondodb,保存.service文件
	public static class SaveServiceFileAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			// get BasicGraphEditor
			BasicGraphEditor editor = getEditor(e);

			if (editor == null) {
				return;
			}

			new UploadServiceFileFrame(editor);
		}
	}

	//选择文件路径，保存.service文件
	public static class SaveServiceFile1Action extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor == null) {
				return;
			}
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();

			FileFilter selectedFilter = null;
			String filename = null;

			String wd;
			String lastDir=null;

			if (lastDir != null) {
				wd = lastDir;
			} else {
				wd = FileUtil.getRootPath();
			}

			JFileChooser fc = new JFileChooser(wd);

			// Adds the default file format
			DefaultFileFilter defaultFilter = new DefaultFileFilter(".service",
					"service file " + mxResources.get("file") + " (.service)");
			// Adds special vector graphics formats and HTML
			fc.addChoosableFileFilter(defaultFilter);

			// Adds filter that accepts all supported image formats
			fc.setFileFilter(defaultFilter);
			int rc = fc.showDialog(null, mxResources.get("save"));

			if (rc != JFileChooser.APPROVE_OPTION) {
				return;
			} else {
				lastDir = fc.getSelectedFile().getParent();
			}
			filename = fc.getSelectedFile().getAbsolutePath();
			selectedFilter = fc.getFileFilter();
			if (selectedFilter == null || !selectedFilter.equals(defaultFilter)) {
				return;
			}
			String ext = ((DefaultFileFilter) selectedFilter).getExtension();
			if (!filename.toLowerCase().endsWith(ext)) {
				filename += ext;
			}

			if (new File(filename).exists()
					&& JOptionPane.showConfirmDialog(graphComponent,
					mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
				return;
			}
			PortStyleUpdate.switchToTransparency((mxCell) graph.getModel().getRoot());
			// save as mxe file
			mxCodec codec = new mxCodec();
			String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));

			// image=/com/mxgraph/examples/swing/images/ -> image=stencils/clipart/
			xml = xml.replace("image=/com/mxgraph/examples/swing/images/",
					"image=images/");

			try {
				mxUtils.writeFile(xml, filename); //把xml写到filename
				JOptionPane.showMessageDialog(null, "成功", "导出完成", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			editor.setModified(false);
			editor.setCurrentFile(new File(filename));
		}
	}

	public static class OpenServiceFileAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);



		}
	}

	public static class OpenxModelFileAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			BasicGraphEditor editor = getEditor(e);
			if (editor == null) {
				return;
			}
		}
	}

	public static class RunDiagramAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			BasicGraphEditor editor = getEditor(e);
			if (editor == null) {
				return;
			}
			System.out.println("open diagram running...");
			//运行
			if (!editor.isEditable()) {
				return;
			}
			editor.setEditable(false);
			editor.switchToView();

			mxGraph graph = editor.getGraphComponent().getGraph();
			PortStyleUpdate.switchToTransparency((mxCell) graph.getDefaultParent());//去掉连接点
			graph.refresh();

			Map<String, JTextField> property_data=editor.getProperty_data();
			//显示数据库数据
			updateThread = new DBDataAdaptor(editor,graph,property_data);
			updateThread.start();
		}

	}

	public static class SumbitDiagramAction extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null,"组态图数据已提交！");
		}
	}

	public static class QuitRunStateAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			BasicGraphEditor editor = getEditor(e);
			if (editor.isEditable()) {
				return;
			}
			editor.setEditable(true);
			editor.switchToEdit();

			PortStyleUpdate.resetStyle((mxCell) editor.getGraphComponent().getGraph().getDefaultParent());

			editor.getGraphComponent().getGraph().refresh();

			 if(updateThread != null) {
				updateThread.cancel();
			}
		}
	}


	public static class ViewSchemaAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			// 打开文件选择窗口
			String lastDir = null;
			String wd = (lastDir != null) ? lastDir : FileUtil.getAppPath();

			JFileChooser fc = new JFileChooser(wd);

			// Adds file filter for supported file format
			DefaultFileFilter defaultFilter = new DefaultFileFilter(".owl",
					mxResources.get("allSupportedFormats") + " (.owl)") {

				public boolean accept(File file) {
					String lcase = file.getName().toLowerCase();
					return super.accept(file) || lcase.endsWith(".owl");
				}
			};
			fc.addChoosableFileFilter(defaultFilter);
			fc.setFileFilter(defaultFilter);
			fc.setDialogTitle("打开文件");

			int rc = fc.showDialog(null, AliasName.getAlias("open_file"));

			if (rc == JFileChooser.APPROVE_OPTION) {
				File sFile = fc.getSelectedFile();
				lastDir = sFile.getParent();

				if (sFile.getAbsolutePath().toLowerCase().endsWith(".owl")) {
					BasicGraphEditor editor = getEditor(e);
					String filePath=sFile.getAbsolutePath();
					System.out.println("查看关系图filepath:"+filePath);

					BrowserFrame2.viewschema(filePath,sFile.getName());
				}
			}
		}
	}

	/*同步数据库数据*/
	public static class SynDatabaseAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			BasicGraphEditor editor = getEditor(e);
			OwlResourceData ord=editor.getNew_owlResourceData();
			Map<String, OwlObject> objmap=ord.objMap;
			JSONArray array = new JSONArray();
			JSONArray res_array = new JSONArray();
			final String[] site_name = {""};
			//把objmap的信息同步到数据库
			objmap.forEach((uri, obj) -> {
				if((findKind(obj.type).equals("Site"))){
					site_name[0] =obj.id;
					System.out.println("site_name[0]:"+site_name[0]);
				}
			});
			JSONObject res_jsonObject  = new JSONObject();
			res_jsonObject.put("site_name", site_name[0]);
			objmap.forEach((uri, obj) -> {
				if((findKind(obj.type).equals("FeatureOfInterest"))&&obj.visible==true){
					String device_name=obj.id;
					//解析它的属性信息
					obj.objAttrs.forEach((objAttr, objSet) -> {
						objSet.forEach(obj2 -> {
							System.out.println(obj.id+"->" + objAttr.id + "->"+obj2.id);
						    if(objAttr.id.equals("has_property")){
								JSONObject jsonObject  = new JSONObject();
								jsonObject.put("site_name", site_name[0]);
								jsonObject.put("device_name", device_name);
								jsonObject.put("data_name", obj2.id);

								jsonObject.put("base", "");
								jsonObject.put("offset", "");
								jsonObject.put("ratio", "");
								jsonObject.put("upper_limit", "");
								jsonObject.put("lower_limit", "");
								jsonObject.put("alarm_upper_limit", "");
								jsonObject.put("alarm_lower_limit", "");
								jsonObject.put("state", "");
								jsonObject.put("blocked", "");
								jsonObject.put("alarm_state", "");

								OwlObject temp=objmap.get(obj2.uri);
								temp.dataAttrs.forEach((temp_objAttr, temp_objSet) -> {
									temp_objSet.forEach(temp_obj2 -> {
										System.out.println(temp.id+"->" + temp_objAttr.id + "->"+temp_obj2);
										if(temp_objAttr.id.equals("基值")){
											jsonObject.put("base",temp_obj2);
										}else if(temp_objAttr.id.equals("偏移")){
											jsonObject.put("offset",temp_obj2);
										}else if(temp_objAttr.id.equals("系数")){
											jsonObject.put("ratio",temp_obj2);
										}else if(temp_objAttr.id.equals("上限")){
											jsonObject.put("upper_limit",temp_obj2);
										}else if(temp_objAttr.id.equals("下限")){
											jsonObject.put("lower_limit",temp_obj2);
										}else if(temp_objAttr.id.equals("上上限")){
											jsonObject.put("alarm_upper_limit",temp_obj2);
										}else if(temp_objAttr.id.equals("下下限")){
											jsonObject.put("alarm_lower_limit",temp_obj2);
										}else if(temp_objAttr.id.equals("状态")){
											jsonObject.put("state",temp_obj2);
										}else if(temp_objAttr.id.equals("封锁状态")){
											jsonObject.put("blocked",temp_obj2);
										}else if(temp_objAttr.id.equals("告警状态")){
											jsonObject.put("alarm_state",temp_obj2);
										}
									});
								});
								array.add(jsonObject);
							}
						});
					});
					System.out.println("-----------------------------------------------");
				}
			});
			res_jsonObject.put("telemetry_json",array.toString());
			res_array.add(res_jsonObject);
			System.out.println("res_array.toString():"+res_array.toString());
			String url="http://localhost:8888/telemetry/addtelemetry";
			try {
				sendHttpPost(url,res_array.toJSONString());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
