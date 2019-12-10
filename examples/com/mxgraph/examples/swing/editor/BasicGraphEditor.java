package com.mxgraph.examples.swing.editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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

	//private static DBDataCollector2 insertThread = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6561623072112577140L;
	/**
	 * 在加载图元时，保存候选图元
	 */
	public static Map<String, mxCell> AllCellMap = new HashMap<>();

	private String resourceFile = null;

	/**
	 * 加载资源文件时使用
	 * @param resourceFile
	 */
	public void setResourceFile(String resourceFile) {
		this.resourceFile = resourceFile;
	}

	public String getResourceFile() {
		return resourceFile;
	}

	/**/
	private OwlResourceData origin_owlResourceData;

	public OwlResourceData getOrigin_owlResourceData() {
		return origin_owlResourceData;
	}

	public void setOrigin_owlResourceData(OwlResourceData origin_owlResourceData) {
		this.origin_owlResourceData = origin_owlResourceData;
	}

	/*
	* new_owlResourceData表示经过选择后的资源，根据new_owlResourceData来绘制组态图
	* */
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
	/**
	 * edit tool bar
	 */
	private EditorToolBar toolBar = null;
    private JSplitPane dataPanel = null;
    private JButton button = new JButton("test");
    private JPanel propertyPanel = new JPanel();
    private JScrollPane jScrollPane = new JScrollPane(propertyPanel);
    private Map<String,JLabel> property_name = new HashMap<>();
    private Map<String,JTextField> property_data = new HashMap<>();
    private Map<String,JPanel> device_property=new HashMap<>();
    private Map<String,OwlObject> cell_property=new HashMap<>();
    //用来记录包括哪些记录数据的图元
    private Map<String,mxCell> mxcell_property=new HashMap<>();
	private static int MonitorHeight = 40;
	private static int MonitorWidth = 240;
	private static int MonitorNameWidth = 150;
	private static int MonitorDataWidth = 60;
	private static int MonitorUnitWidth = 40;

	private JTable jTable;
	private AbstractTableModel attrListModel;
	private JScrollPane scrollPanel;


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

	/**
	 * 
	 */
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

		//jScrollPane.setBounds(10, 10, 550,700 );
		dataPanel=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,outer,jScrollPane);
		button.setVisible(false);
		propertyPanel.setVisible(false);
		jScrollPane.setVisible(false);
		dataPanel.setOneTouchExpandable(true);
		dataPanel.setDividerLocation(900);
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
						- scrollPane.getVerticalScrollBar().getWidth();
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
				getResource("/com/mxgraph/examples/swing/images/icon.png")).getImage());
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
		dataPanel.setDividerLocation(600);
		//graphComponent代表画图的组件
		mxGraph graph = graphComponent.getGraph();
		//先清空再重新加载
		initPropertyPanel();
		inner.setVisible(false);
		//button.setVisible(true);
		Dimension preferredSize = new Dimension(400,2000);//设置尺寸
		propertyPanel.setPreferredSize(preferredSize);
		int len=property_name.size();
		propertyPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
		propertyPanel.removeAll();
		for (Map.Entry<String, JPanel> entry : device_property.entrySet()) {
			entry.getValue().setBorder(BorderFactory.createTitledBorder(entry.getKey()));
			propertyPanel.add(entry.getValue());
		}
		propertyPanel.setVisible(true);
		jScrollPane.setVisible(true);

		//保留原先的，查漏补缺
		insertPropertyCell();

		//insertThread = new DBDataCollector2(mxcell_property);
		//insertThread.start();

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
		dataPanel.setDividerLocation(900);
		installToolBar();
		jScrollPane.setVisible(false);

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
		//graph.setEnabled(true);
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
		device_property.clear();
		cell_property.clear();
        //初始化property_name，property_data
		if(new_owlResourceData==null){
			System.out.println("new_owlResourceData is null!");
			return;
		}
		for (Map.Entry<String, OwlObject> entry : new_owlResourceData.objMap.entrySet()) {
			if((findKind(entry.getValue().type).equals("FeatureOfInterest")
					//||findKind(entry.getValue().type).equals("Site")
					||findKind(entry.getValue().type).equals("ControlRoom"))
					&&entry.getValue().visible){

				for (Map.Entry<OwlObjectAttribute, Set<OwlObject>> entrys : entry.getValue().objAttrs.entrySet()) {

					if (entrys.getKey().id.equals("has_property")&&entrys.getValue().size()!=0) {
						//System.out.println("entrys.getValue():"+entrys.getValue());
						JPanel jPanel=new JPanel();
						device_property.put(entry.getValue().type.id,jPanel);
						int m=entrys.getValue().size();
						jPanel.setLayout(new GridLayout(m,2,10,10));
						for (OwlObject obj : entrys.getValue()) {
							JLabel jLabel=new JLabel(obj.type.id);
							//Dimension preferredSize1 = new Dimension(100,50);//设置尺寸
							//jLabel.setPreferredSize(preferredSize1);
							String name=obj.id;
							property_name.put(name,jLabel);
							JTextField jTextField=new JTextField(6);
							//Dimension preferredSize2 = new Dimension(100,50);//设置尺寸
							//jTextField.setPreferredSize(preferredSize2);
							property_data.put(name,jTextField);
							jPanel.add(jLabel);
							jPanel.add(jTextField);

							//System.out.println("name:"+name);
							cell_property.put(name,obj);
						}
					}
				}
			}
		}
	}

	//应该遍历graph,获取图元的位置，设计图元的摆放问题
	//每一个属性是一个obj
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
					String nameStyle = AliasName.getAlias("monitor_name_style");
					// nameCell UI坐标偏移量是相对于父图元，而不再是相对于UI原点
					mxCell nameCell = new mxCell(child.getV().getName(), child.getV().getName(),
							new mxGeometry(x-45, y, 250, 30), nameStyle);
					nameCell.setType("DeviceName");
					nameCell.setAttr("device_name");
					nameCell.setVertex(true);
					nameCell.setConnectable(false);
					((mxCell) graph.getDefaultParent()).insert(nameCell);
				}


				if(child.getV().getLink_info().get("has_property")!=null){
					//读cell_property的信息，得到属性信息  cell_property
					String propertys=child.getV().getLink_info().get("has_property");
					String [] arr = propertys.split(";");
					String [] arr1=new String[arr.length];
					for(int r=0;r<arr.length;r++){

						//arr[r].trim()是属性名称  unit是属性单位
						OwlObject owlObject=cell_property.get(arr[r].trim());
						arr1[r]=owlObject.type.id;
						String unit="";
						if(owlObject.dataAttrs!=null){
							for (Map.Entry<OwlDataAttribute, Set<Object>> entry : owlObject.dataAttrs.entrySet()) {
								if(entry.getKey().id.equals("资源单位")){
									for(Object obj:entry.getValue()){
										unit=obj.toString();
									}
								}
							}
						}else{
							unit="***";
						}

						//System.out.println("property_name:"+arr[r].trim()+"   property_unit:"+unit);
						if(!mxcell_property.containsKey(arr[r].trim())){
							//创建图元
							// 创建监控器图元三元组--Name，插入到cell中
							String nameStyle = AliasName.getAlias("monitor_name_style");
							// monitorCell UI坐标偏移量是相对于父图元，而不再是相对于UI原点
							mxCell nameMonitorCell = new mxCell(arr1[r].trim(), arr1[r].trim(),
									new mxGeometry(x-45, (y+MonitorHeight*r+30), MonitorNameWidth, MonitorHeight), nameStyle);
							nameMonitorCell.setType("Property");
							nameMonitorCell.setAttr("property_name");
							nameMonitorCell.setVertex(true);
							//nameMonitorCell.setOriginParentId(cell.getId());
							nameMonitorCell.setConnectable(false);
							((mxCell) graph.getDefaultParent()).insert(nameMonitorCell);
							mxcell_property.put(arr1[r].trim(),nameMonitorCell);


							// 创建监控器图元三元组--Data，插入到cell中
							String dataStyle = AliasName.getAlias("monitor_data_style");
							// monitorCell UI坐标偏移量是相对于父图元，而不再是相对于UI原点
							mxCell dataMonitorCell = new mxCell(arr[r].trim(), "",
									new mxGeometry(x + MonitorNameWidth-45, (y+MonitorHeight*r+30), MonitorDataWidth, MonitorHeight), dataStyle);
							dataMonitorCell.setType("Property");
							dataMonitorCell.setAttr("property_data");
							dataMonitorCell.setVertex(true);
							dataMonitorCell.setMonitor_device_name(child.getDeviceid());
							dataMonitorCell.setMonitor_name(arr[r].trim());
							dataMonitorCell.setMonitor_unit(unit);
							//dataMonitorCell.setOriginParentId(cell.getId());
							dataMonitorCell.setConnectable(false);
							((mxCell) graph.getDefaultParent()).insert(dataMonitorCell);


							// 创建监控器图元三元组--Unit，插入到cell中
							String unitStyle = AliasName.getAlias("monitor_unit_style");
							// monitorCell UI坐标偏移量是相对于父图元，而不再是相对于UI原点
							mxCell unitMonitorCell = new mxCell(arr[r].trim(), unit,
									new mxGeometry(x + MonitorNameWidth + MonitorDataWidth-45, (y+MonitorHeight*r+30),
											MonitorUnitWidth, MonitorHeight), unitStyle);
							unitMonitorCell.setType("Property");
							unitMonitorCell.setAttr("property_unit");
							unitMonitorCell.setVertex(true);
							//unitMonitorCell.setOriginParentId(cell.getId());
							unitMonitorCell.setConnectable(false);
							((mxCell) graph.getDefaultParent()).insert(unitMonitorCell);
						}


					}

				}

			}
			repaint();
			addPropertyCell(child,graph);
		}
	}

}
