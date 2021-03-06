package com.mxgraph.examples.swing.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;


import com.mxgraph.examples.swing.owl.OwlDataAttribute;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlObjectAttribute;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.findKind;

public class BasicGraphEditor extends JPanel
{
	private static final long serialVersionUID = -6561623072112577140L;

	//在加载图元时，保存系统涉及的图元信息，初始化在GraphEditor.java中完成
	public static Map<String, mxCell> AllCellMap = new HashMap<>();

	public  Map<String, mxCell> getAllCellMap() {
		return AllCellMap;
	}

	//资源文件路径
	private String resourceFile = null;

	public void setResourceFile(String resourceFile) {
		this.resourceFile = resourceFile;
	}

	public String getResourceFile() {
		return resourceFile;
	}

	//origin_owlResourceData表示选择之前的图元
	private OwlResourceData origin_owlResourceData;

	public OwlResourceData getOrigin_owlResourceData() {
		return origin_owlResourceData;
	}

	public void setOrigin_owlResourceData(OwlResourceData origin_owlResourceData) {
		this.origin_owlResourceData = origin_owlResourceData;
	}

	//new_owlResourceData表示经过选择后的资源，根据new_owlResourceData来绘制组态图
	private OwlResourceData new_owlResourceData;

	public OwlResourceData getNew_owlResourceData() {
		return new_owlResourceData;
	}

	public void setNew_owlResourceData(OwlResourceData new_owlResourceData) {
		this.new_owlResourceData = new_owlResourceData;
	}

	/**
	 * 对于切换编辑模式和视图模式时使用
	 *
	 * @param editable
	 */
	private boolean editable = true;
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public boolean isEditable() {
		return editable;
	}

	/**
	 * left pane, include cell and outline
	 */
	private JSplitPane inner = null;
	/**
	 * right pane, paint
	 */
	private JSplitPane outer = null;
	/*领域图元管理*/
	protected JPanel panel;
	/**
	 * edit tool bar
	 */
	private EditorToolBar toolBar = null;


    private JSplitPane dataPanel;
    private JPanel propertyPanel = new JPanel();
    private JScrollPane jScrollPane = new JScrollPane(propertyPanel);
    private Map<String,JLabel> property_name = new HashMap<>();
    private Map<String,JTextField> property_data = new HashMap<>();
	private Map<String,JLabel> property_unit = new HashMap<>();
    private Map<String,JPanel> device_property=new HashMap<>();
    private Map<String,OwlObject> cell_property=new HashMap<>();
    //用来记录包括哪些记录数据的图元
    private Map<String,mxCell> mxcell_property=new HashMap<>();
	private Map<String,mxCell> mxcell_device=new HashMap<>();

	private static int MonitorHeight = 40;
	private static int MonitorNameWidth = 180;
	private static int MonitorDataWidth = 60;
	private static int MonitorUnitWidth = 40;
	private static int DeviceWidth=280;
	private static int DeviceHeight=30;


	public Map<String, JTextField> getProperty_data() {
		return property_data;
	}

	public void setProperty_data(Map<String, JTextField> property_data) {
		this.property_data = property_data;
	}

	/**
	 * Adds required resources for i18n
	 */
	static
	{
		try
		{
			mxResources.add("com/mxgraph/examples/swing/resources/editor");
		}
		catch (Exception e)
		{
			// ignore
		}
	}


	protected mxGraphComponent graphComponent;

	/**
	 * 
	 */
	protected mxGraphOutline graphOutline;

	/**
	 * 
	 */
	protected JTabbedPane libraryPane;

	/**
	 * 
	 */
	protected mxUndoManager undoManager;

	/**
	 * 
	 */
	protected String appTitle;

	/**
	 * 
	 */
	protected JLabel statusBar;

	/**
	 * 
	 */
	protected File currentFile;

	/**
	 * Flag indicating whether the current graph has been modified 
	 */
	protected boolean modified = false;

	/**
	 * 
	 */
	protected mxRubberband rubberband;

	/**
	 * 
	 */
	protected mxKeyboardHandler keyboardHandler;

	/**
	 * 
	 */
	protected mxIEventListener undoHandler = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			undoManager.undoableEditHappened((mxUndoableEdit) evt
					.getProperty("edit"));
		}
	};

	/**
	 * 
	 */
	protected mxIEventListener changeTracker = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			setModified(true);
		}
	};

	/**
	 * 
	 */
	public BasicGraphEditor(String appTitle, mxGraphComponent component)
	{
		// Stores and updates the frame title
		this.appTitle = appTitle;

		// Stores a reference to the graph and creates the command history
		graphComponent = component;
		final mxGraph graph = graphComponent.getGraph();
		undoManager = createUndoManager();

		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

		// Keeps the selection in sync with the command history
		mxIEventListener undoHandler = new mxIEventListener()
		{
			public void invoke(Object source, mxEventObject evt)
			{
				List<mxUndoableChange> changes = ((mxUndoableEdit) evt
						.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph
						.getSelectionCellsForChanges(changes));
			}
		};

		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);

		// Creates the graph outline component
		graphOutline = new mxGraphOutline(graphComponent);

		// Creates the library pane that contains the tabs with the palettes
		libraryPane = new JTabbedPane();

		// Creates the inner split pane that contains the library with the
		// palettes and the graph outline on the left side of the window
		inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				libraryPane, graphOutline);
		inner.setDividerLocation(320);
		inner.setResizeWeight(1);
		inner.setDividerSize(6);
		inner.setBorder(null);

		// Creates the outer split pane that contains the inner split pane and
		// the graph component on the right side of the window
		outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner,
				graphComponent);
		outer.setOneTouchExpandable(true);
		outer.setDividerLocation(200);
		outer.setDividerSize(6);
		outer.setBorder(null);


		//运行预览时，propertyPanel表示右侧的数据
		dataPanel=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,outer,jScrollPane);
		propertyPanel.setVisible(false);
		jScrollPane.setVisible(false);
		dataPanel.setOneTouchExpandable(true);
		dataPanel.setDividerLocation(0.1);
		dataPanel.setDividerSize(6);
		dataPanel.setBorder(null);


		// Creates the status bar
		statusBar = createStatusBar();

		// Display some useful information about repaint events
		installRepaintListener();

		// Puts everything together
		setLayout(new BorderLayout());
		//add(outer, BorderLayout.CENTER);
		add(dataPanel, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		installToolBar();

		// Installs rubberband selection and handling for some special
		// keystrokes such as F2, Control-C, -V, X, A etc.
		installHandlers();
		installListeners();
		updateTitle();
	}

	/**
	 * 
	 */
	protected mxUndoManager createUndoManager()
	{
		return new mxUndoManager();
	}

	/**
	 * 
	 */
	protected void installHandlers()
	{
		rubberband = new mxRubberband(graphComponent);
		keyboardHandler = new EditorKeyboardHandler(graphComponent);
	}

	/**
	 * 
	 */
	protected void installToolBar()
	{
		if (toolBar == null) {
			toolBar = new EditorToolBar(this, JToolBar.HORIZONTAL);
		}
		add(toolBar, BorderLayout.NORTH);
	}

	/**
	 * 
	 */
	protected JLabel createStatusBar()
	{
		JLabel statusBar = new JLabel(mxResources.get("ready"));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		return statusBar;
	}

	/**
	 * 
	 */
	protected void installRepaintListener()
	{
		graphComponent.getGraph().addListener(mxEvent.REPAINT,
				new mxIEventListener()
				{
					public void invoke(Object source, mxEventObject evt)
					{
						String buffer = (graphComponent.getTripleBuffer() != null) ? ""
								: " (unbuffered)";
						mxRectangle dirty = (mxRectangle) evt
								.getProperty("region");

						if (dirty == null)
						{
							status("Repaint all" + buffer);
						}
						else
						{
							status("Repaint: x=" + (int) (dirty.getX()) + " y="
									+ (int) (dirty.getY()) + " w="
									+ (int) (dirty.getWidth()) + " h="
									+ (int) (dirty.getHeight()) + buffer);
						}
					}
				});
	}

	/**
	 * 
	 */
	public EditorPalette insertPalette(String title)
	{
		final EditorPalette palette = new EditorPalette();
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		libraryPane.add(title, scrollPane);

		// Updates the widths of the palettes if the container size changes
		libraryPane.addComponentListener(new ComponentAdapter()
		{
			/**
			 * 
			 */
			public void componentResized(ComponentEvent e)
			{
				int w = scrollPane.getWidth()
						-scrollPane.getVerticalScrollBar().getWidth();
				palette.setPreferredWidth(w);
			}

		});

		return palette;
	}

	/**
	 * 
	 */
	protected void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.getWheelRotation() < 0)
		{
			graphComponent.zoomIn();
		}
		else
		{
			graphComponent.zoomOut();
		}

		status(mxResources.get("scale") + ": "
				+ (int) (100 * graphComponent.getGraph().getView().getScale())
				+ "%");
	}

	/**
	 * 
	 */
	protected void showOutlinePopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(
				mxResources.get("magnifyPage"));
		item.setSelected(graphOutline.isFitPage());

		item.addActionListener(new ActionListener()
		{
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setFitPage(!graphOutline.isFitPage());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(
				mxResources.get("showLabels"));
		item2.setSelected(graphOutline.isDrawLabels());

		item2.addActionListener(new ActionListener()
		{
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(
				mxResources.get("buffering"));
		item3.setSelected(graphOutline.isTripleBuffered());

		item3.addActionListener(new ActionListener()
		{
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
				graphOutline.repaint();
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(item);
		menu.add(item2);
		menu.add(item3);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}

	/**
	 * 
	 */
	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		EditorPopupMenu menu = new EditorPopupMenu(BasicGraphEditor.this);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}

	/**
	 * 
	 */
	protected void mouseLocationChanged(MouseEvent e)
	{
		status(e.getX() + ", " + e.getY());
	}

	/**
	 * 
	 */
	protected void installListeners()
	{
		// Installs mouse wheel listener for zooming
		MouseWheelListener wheelTracker = new MouseWheelListener()
		{
			/**
			 * 
			 */
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getSource() instanceof mxGraphOutline
						|| e.isControlDown())
				{
					BasicGraphEditor.this.mouseWheelMoved(e);
				}
			}

		};

		// Handles mouse wheel events in the outline and graph component
		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		// Installs the popup menu in the outline
		graphOutline.addMouseListener(new MouseAdapter()
		{

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e)
			{
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showOutlinePopupMenu(e);
				}
			}

		});

		// Installs the popup menu in the graph component
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e)
			{
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showGraphPopupMenu(e);
				}
			}

		});

		// Installs a mouse motion listener to display the mouse location
		graphComponent.getGraphControl().addMouseMotionListener(
				new MouseMotionListener()
				{

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
					 */
					public void mouseDragged(MouseEvent e)
					{
						mouseLocationChanged(e);
					}

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
					 */
					public void mouseMoved(MouseEvent e)
					{
						mouseDragged(e);
					}

				});
	}

	/**
	 * 
	 */
	public void setCurrentFile(File file)
	{
		File oldValue = currentFile;
		currentFile = file;

		firePropertyChange("currentFile", oldValue, file);

		if (oldValue != file)
		{
			updateTitle();
		}
	}

	/**
	 * 
	 */
	public File getCurrentFile()
	{
		return currentFile;
	}

	/**
	 * 
	 * @param modified
	 */
	public void setModified(boolean modified)
	{
		boolean oldValue = this.modified;
		this.modified = modified;

		firePropertyChange("modified", oldValue, modified);

		if (oldValue != modified)
		{
			updateTitle();
		}
	}

	/**
	 * 
	 * @return whether or not the current graph has been modified
	 */
	public boolean isModified()
	{
		return modified;
	}

	/**
	 * 
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphComponent;
	}

	/**
	 * 
	 */
	public mxGraphOutline getGraphOutline()
	{
		return graphOutline;
	}
	
	/**
	 * 
	 */
	public JTabbedPane getLibraryPane()
	{
		return libraryPane;
	}

	/**
	 * 
	 */
	public mxUndoManager getUndoManager()
	{
		return undoManager;
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name
	 */
	public Action bind(String name, final Action action)
	{
		return bind(name, action, null);
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name and icon
	 */
	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl)
	{
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				BasicGraphEditor.class.getResource(iconUrl)) : null)
		{
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(new ActionEvent(getGraphComponent(), e
						.getID(), e.getActionCommand()));
			}
		};
		
		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		
		return newAction;
	}

	/**
	 * 
	 * @param msg
	 */
	public void status(String msg)
	{
		statusBar.setText(msg);
	}

	/**
	 * 
	 */
	public void updateTitle()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			/*String title = (currentFile != null) ? currentFile
					.getAbsolutePath() : mxResources.get("newDiagram");*/
            String title="";
			if (modified)
			{
				title += "*";
			}

			//frame.setTitle(title + " - " + appTitle);
			frame.setTitle(title + appTitle);
		}
	}

	/**
	 * 
	 */
	public void about()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			EditorAboutFrame about = new EditorAboutFrame(frame);
			about.setModal(true);

			// Centers inside the application frame
			int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
			int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
			about.setLocation(x, y);

			// Shows the modal dialog and waits
			about.setVisible(true);
		}
	}

	/**
	 * 
	 */
	public void exit()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			frame.dispose();
		}
	}

	/**
	 * 
	 */
	public void setLookAndFeel(String clazz)
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			try
			{
				UIManager.setLookAndFeel(clazz);
				SwingUtilities.updateComponentTreeUI(frame);

				// Needs to assign the key bindings again
				keyboardHandler = new EditorKeyboardHandler(graphComponent);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public JFrame createFrame(JMenuBar menuBar)
	{
		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(this.getClass().
				getResource("/com/mxgraph/examples/swing/images/others/icon.png")).getImage());
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setSize(1200, 680);
		frame.setLocationRelativeTo(null);
		// Updates the frame title
		updateTitle();

		return frame;
	}

	/**
	 * Creates an action that executes the specified layout.
	 * 
	 * @param key Key to be used for getting the label from mxResources and also
	 * to create the layout instance for the commercial graph editor example.
	 * @return an action that executes the specified layout
	 */
	@SuppressWarnings("serial")
	public Action graphLayout(final String key, boolean animate)
	{
		final mxIGraphLayout layout = createLayout(key, animate);

		if (layout != null)
		{
			return new AbstractAction(mxResources.get(key))
			{
				public void actionPerformed(ActionEvent e)
				{
					final mxGraph graph = graphComponent.getGraph();
					Object cell = graph.getSelectionCell();

					if (cell == null
							|| graph.getModel().getChildCount(cell) == 0)
					{
						cell = graph.getDefaultParent();
					}

					graph.getModel().beginUpdate();
					try
					{
						long t0 = System.currentTimeMillis();
						layout.execute(cell);
						status("Layout: " + (System.currentTimeMillis() - t0)
								+ " ms");
					}
					finally
					{
						mxMorphing morph = new mxMorphing(graphComponent, 20,
								1.2, 20);

						morph.addListener(mxEvent.DONE, new mxIEventListener()
						{

							public void invoke(Object sender, mxEventObject evt)
							{
								graph.getModel().endUpdate();
							}

						});

						morph.startAnimation();
					}

				}

			};
		}
		else
		{
			return new AbstractAction(mxResources.get(key))
			{

				public void actionPerformed(ActionEvent e)
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noLayout"));
				}

			};
		}
	}

	/**
	 * Creates a layout instance for the given identifier.
	 */
	protected mxIGraphLayout createLayout(String ident, boolean animate)
	{
		mxIGraphLayout layout = null;

		if (ident != null)
		{
			mxGraph graph = graphComponent.getGraph();

			if (ident.equals("verticalHierarchical"))
			{
				layout = new mxHierarchicalLayout(graph);
			}
			else if (ident.equals("horizontalHierarchical"))
			{
				layout = new mxHierarchicalLayout(graph, JLabel.WEST);
			}
			else if (ident.equals("verticalTree"))
			{
				layout = new mxCompactTreeLayout(graph, false);
			}
			else if (ident.equals("horizontalTree"))
			{
				layout = new mxCompactTreeLayout(graph, true);
			}
			else if (ident.equals("parallelEdges"))
			{
				layout = new mxParallelEdgeLayout(graph);
			}
			else if (ident.equals("placeEdgeLabels"))
			{
				layout = new mxEdgeLabelLayout(graph);
			}
			else if (ident.equals("organicLayout"))
			{
				layout = new mxOrganicLayout(graph);
			}
			if (ident.equals("verticalPartition"))
			{
				layout = new mxPartitionLayout(graph, false)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalPartition"))
			{
				layout = new mxPartitionLayout(graph, true)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("verticalStack"))
			{
				layout = new mxStackLayout(graph, false)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalStack"))
			{
				layout = new mxStackLayout(graph, true)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("circleLayout"))
			{
				layout = new mxCircleLayout(graph);
			}
		}

		return layout;
	}

	public void switchToView() {
		//dataPanel.setDividerLocation(600);
		mxGraph graph = graphComponent.getGraph();
		/*右侧属性面板，先清空再重新加载*/
		/*initPropertyPanel();
		propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.Y_AXIS));
		propertyPanel.add(Box.createVerticalStrut(15));
		propertyPanel.removeAll();
		for (Map.Entry<String, JPanel> entry : device_property.entrySet()) {
			entry.getValue().setBorder(BorderFactory.createTitledBorder(entry.getKey()));
			propertyPanel.add(entry.getValue());
		}
		propertyPanel.setVisible(true);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane.setVisible(true);*/

		//为每个设备添加监控项图元，保留原先的，查漏补缺
		//insertPropertyCell();

		inner.setVisible(false);
		uninstallToolBar();
		graph.setVertexLabelsMovable(false);
		graph.setDisconnectOnMove(false);
		graph.setAllowDanglingEdges(false);
		graph.setCellsBendable(false);
		graph.setCellsCloneable(false);
		graph.setCellsDeletable(false);
		graph.setEdgeLabelsMovable(false);
		graph.setCellsMovable(false);
		graph.setCellsResizable(false);
		graph.setCellsEditable(false);
		graph.setEnabled(false);
		revalidate();
	}

	public void switchToEdit() {
		inner.setVisible(true);
		outer.setDividerLocation(260);
		//dataPanel.setDividerLocation(900);
		installToolBar();
		//jScrollPane.setVisible(false);
		mxGraph graph = graphComponent.getGraph();
		graph.setVertexLabelsMovable(true);
		graph.setDisconnectOnMove(true);
		graph.setAllowDanglingEdges(true);
		graph.setCellsBendable(true);
		graph.setCellsCloneable(true);
		graph.setCellsDeletable(true);
		graph.setEdgeLabelsMovable(true);
		graph.setCellsMovable(true);
		graph.setCellsResizable(true);
		graph.setCellsEditable(true);
		graph.setEnabled(true);
		revalidate();
	}

	protected void uninstallToolBar() {
		if (toolBar == null) {
			return;
		}
		remove(toolBar);
	}

	public void initPropertyPanel(){
		//先清空
		property_name.clear();
		property_data.clear();
		property_unit.clear();
		device_property.clear();
		cell_property.clear();
        //初始化property_name，property_data,property_unit
		if(new_owlResourceData==null){
			System.out.println("new_owlResourceData is null!");
			return;
		}
		for (Map.Entry<String, OwlObject> entry : new_owlResourceData.objMap.entrySet()) {
			if((findKind(entry.getValue().type).equals("FeatureOfInterest")
					||findKind(entry.getValue().type).equals("ControlRoom")) //如果ControlRoom有变量也要监控
					&&entry.getValue().visible){
				for (Map.Entry<OwlObjectAttribute, Set<OwlObject>> entrys : entry.getValue().objAttrs.entrySet()) {
					if (entrys.getKey().id.equals("has_property")&&entrys.getValue().size()!=0) {
						JPanel jPanel=new JPanel();
						device_property.put(entry.getValue().id,jPanel);

						BoxLayout boxl=new BoxLayout(jPanel, BoxLayout.Y_AXIS);
						jPanel.setLayout(boxl);
						for (OwlObject obj : entrys.getValue()) {

                            if(obj.visible){
								JPanel child_jPanel=new JPanel();
								child_jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
								JLabel jLabel=new JLabel(obj.id);
								String name=obj.id;
								property_name.put(name,jLabel);
								jLabel.setPreferredSize(new Dimension(250, 30));
								child_jPanel.add(jLabel);

								JTextField jTextField=new JTextField(4);
								property_data.put(name,jTextField);
								jTextField.setPreferredSize(new Dimension(150, 30));
								child_jPanel.add(jTextField);
								//单位
								obj.dataAttrs.forEach((temp_objAttr, temp_objSet) -> {
									temp_objSet.forEach(temp_obj2 -> {
										System.out.println(obj.id+"->" + temp_objAttr.id + "->"+temp_obj2);
										if(temp_objAttr.id.equals("资源单位")){
											JLabel jLabel1=new JLabel(temp_obj2.toString());
											System.out.println("temp_obj2:"+temp_obj2);
											property_name.put(name,jLabel1);
											jLabel1.setPreferredSize(new Dimension(150, 30));
											child_jPanel.add(jLabel1);
										}
									});
								});
								jPanel.add(child_jPanel);
								cell_property.put(name,obj);
							}
						}
					}
				}
			}
		}
	}
	//应该遍历graph,获取图元的位置，设计图元的摆放问题,每一个属性是一个obj
	public void insertPropertyCell(){
		  mxGraph graph = graphComponent.getGraph();
		  mxCell root = (mxCell) graph.getModel().getRoot();
		  addPropertyCell(root,graph);
		  revalidate();
	}
	private void addPropertyCell(mxCell cell,mxGraph graph) {
		if (cell == null||cell.getChildCount()==0) {
			return;
		}
		for (int i = 0; i < cell.getChildCount(); ++i) {
			mxCell child = (mxCell) cell.getChildAt(i);
			if(child.isVertex()&&child.getV()!=null){
				//如果是顶点，看有没有属性
				//如果是顶点，得到图元的位置
				mxGeometry geo = child.getGeometry();
				double x = geo.getX();
				double y = geo.getY() + geo.getHeight();

				//添加设备名称
				if(child.getV().getName()!=null){
					if(!mxcell_device.containsKey(child.getV().getId())) {
						String nameStyle = AliasName.getAlias("device_name_style");
						// nameCell UI坐标偏移量是相对于父图元，而不再是相对于UI原点
						mxCell nameCell = new mxCell(child.getV().getId(), child.getV().getId(),
								new mxGeometry(x-45, y, DeviceWidth, DeviceHeight), nameStyle);
						nameCell.setType("DeviceName");
						nameCell.setAttr("device_name");
						nameCell.setVertex(true);
						nameCell.setConnectable(false);
						((mxCell) graph.getDefaultParent()).insert(nameCell);
						mxcell_device.put(child.getV().getId(),nameCell);
					}
				}

				if(child.getV().getLink_info().get("has_property")!=null){
					//得到属性信息
					String properties=child.getV().getLink_info().get("has_property");
					String [] arr = properties.split(";");
					for(int r=0;r<arr.length;r++){
						//arr[r].trim()是属性名称id  owlObject.type.id是属性类型  unit是属性单位
						OwlObject owlObject=cell_property.get(arr[r].trim());
						String unit="";
						if(owlObject.dataAttrs!=null){
							for (Map.Entry<OwlDataAttribute, Set<Object>> entry : owlObject.dataAttrs.entrySet()) {
								if(entry.getKey().id.equals("资源单位")){
									for(Object obj:entry.getValue()){
										unit=obj.toString();
									}
								}
							}
						}

						if(!mxcell_property.containsKey(arr[r].trim())) {
							// 创建监控图元三元组--Name，插入到cell中
							String nameStyle = AliasName.getAlias("monitor_name_style");
							// monitorCell UI坐标偏移量是相对于父图元，而不再是相对于UI原点
							//若显示设备属性名称  owlObject.id   若显示设备属性类型名称  owlObject.type.id
							mxCell nameMonitorCell = new mxCell(arr[r].trim(), owlObject.id,
									new mxGeometry(x - 45, (y + MonitorHeight * r + 30), MonitorNameWidth, MonitorHeight), nameStyle);
							nameMonitorCell.setType("Property");
							nameMonitorCell.setAttr("property_name");
							nameMonitorCell.setVertex(true);
							nameMonitorCell.setConnectable(false);
							((mxCell) graph.getDefaultParent()).insert(nameMonitorCell);
							mxcell_property.put(arr[r].trim(), nameMonitorCell);

							// 创建监控图元三元组--Data，插入到cell中
							String dataStyle = AliasName.getAlias("monitor_data_style");
							mxCell dataMonitorCell;
							if (!unit.equals("")) {
								dataMonitorCell = new mxCell(arr[r].trim(), "",
										new mxGeometry(x + MonitorNameWidth - 45, (y + MonitorHeight * r + 30), MonitorDataWidth, MonitorHeight), dataStyle);
							} else {
								dataMonitorCell = new mxCell(arr[r].trim(), "",
										new mxGeometry(x + MonitorNameWidth - 45, (y + MonitorHeight * r + 30), MonitorDataWidth + MonitorUnitWidth, MonitorHeight), dataStyle);
							}
							dataMonitorCell.setType("Property");
							dataMonitorCell.setAttr("property_data");
							dataMonitorCell.setVertex(true);
							dataMonitorCell.setMonitor_device_name(child.getDeviceid());
							dataMonitorCell.setMonitor_property_name(arr[r].trim());
							dataMonitorCell.setMonitor_property_type(owlObject.type.id);
							dataMonitorCell.setMonitor_unit(unit);
							dataMonitorCell.setConnectable(false);
							((mxCell) graph.getDefaultParent()).insert(dataMonitorCell);

							// 创建监控图元三元组--Unit，插入到cell中
							if (!unit.equals("")) {
								String unitStyle = AliasName.getAlias("monitor_unit_style");
								mxCell unitMonitorCell = new mxCell(arr[r].trim(), unit,
										new mxGeometry(x + MonitorNameWidth + MonitorDataWidth - 45, (y + MonitorHeight * r + 30),
												MonitorUnitWidth, MonitorHeight), unitStyle);
								unitMonitorCell.setType("Property");
								unitMonitorCell.setAttr("property_unit");
								unitMonitorCell.setVertex(true);
								unitMonitorCell.setConnectable(false);
								((mxCell) graph.getDefaultParent()).insert(unitMonitorCell);
							}
						}
					}
				}
			}
			repaint();
			addPropertyCell(child,graph);
		}
	}


	public JFrame createCellFrame() {

		final JFrame frame = new JFrame("图元编辑器");
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//System.exit(0);
				//	frame.dispose();
			}
		});
		frame.setSize(550, 450);
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setLocation((int) width / 4,
				(int) height / 4);
		return frame;
	}
}
