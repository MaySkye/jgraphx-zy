package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.GraphEditor;
import com.mxgraph.examples.swing.decode.*;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 13:22 2020/2/28
 * @Modify By:
 */
public class CellEditorFrame extends BasicGraphEditor {
    public CellEditorFrame() {
        this("组态图模板编辑器", new CustomGraphComponent(new CustomGraph()));
    }

    public CellEditorFrame(String appTitle, mxGraphComponent component) {
        super(appTitle, component);
        // TODO Auto-generated constructor stub
        initialise();
        final mxGraph graph = graphComponent.getGraph();

        //	graph.setAllowDanglingEdges(false);
        //	graph.setAllowNegativeCoordinates(false);
        //	graph.setCellsBendable(false);
        //	graph.setCellsDisconnectable(false);
        graph.setCloneInvalidEdges(false);
        graph.setConnectableEdges(false);
        graph.setDisconnectOnMove(true);
        // Creates the shapes palette
        EditorPalette shapesPalette = insertPalette("基础图形");
        EditorPalette imagesPalette = insertPalette("领域图形");
        EditorPalette linkPalette = insertPalette("设备连线");

        //new NewPicPopupMenu(imagesPalette);

        shapesPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                Object tmp = evt.getProperty("transferable");

                if (tmp instanceof mxGraphTransferable) {
                    mxGraphTransferable t = (mxGraphTransferable) tmp;
                    Object cell = t.getCells()[0];

                    if (graph.getModel().isEdge(cell)) {
                        ((CustomGraph) graph).setEdgeTemplate(cell);
                    }
                }
            }

        });

        shapesPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                Object tmp = evt.getProperty("transferable");

                if (tmp instanceof mxGraphTransferable) {
                    mxGraphTransferable t = (mxGraphTransferable) tmp;
                    Object cell = t.getCells()[0];

                    if (graph.getModel().isEdge(cell)) {
                        ((CustomGraph) graph).setEdgeTemplate(cell);
                    }
                }
            }

        });

        // Adds some template cells for dropping into the graph
        shapesPalette
                .addTemplate(
                        "Container",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/swimlane.png")),
                        "swimlane", 280, 280, "Container");
        shapesPalette
                .addTemplate(
                        "Icon",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                        "icon;image=/com/mxgraph/examples/swing/images/wrench.png",
                        70, 70, "Icon");
        shapesPalette
                .addTemplate(
                        "Label",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                        "label;image=/com/mxgraph/examples/swing/images/gear.png",
                        130, 50, "Label");
        shapesPalette
                .addTemplate(
                        "Rectangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rectangle.png")),
                        null, 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Rounded Rectangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                        "rounded=1", 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Double Rectangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/doublerectangle.png")),
                        "rectangle;shape=doubleRectangle", 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Ellipse",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/ellipse.png")),
                        "ellipse", 160, 160, "");
        shapesPalette
                .addTemplate(
                        "Double Ellipse",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/doubleellipse.png")),
                        "ellipse;shape=doubleEllipse", 160, 160, "");
        shapesPalette
                .addTemplate(
                        "Triangle",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/triangle.png")),
                        "triangle", 120, 160, "");
        shapesPalette
                .addTemplate(
                        "Rhombus",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/rhombus.png")),
                        "rhombus", 160, 160, "");
        shapesPalette
                .addTemplate(
                        "Horizontal Line",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/hline.png")),
                        "line", 160, 10, "");
        shapesPalette
                .addTemplate(
                        "Hexagon",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/hexagon.png")),
                        "shape=hexagon", 160, 120, "");
        shapesPalette
                .addTemplate(
                        "Cylinder",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/cylinder.png")),
                        "shape=cylinder", 120, 160, "");
        shapesPalette
                .addTemplate(
                        "Actor",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/actor.png")),
                        "shape=actor", 120, 160, "");
        shapesPalette
                .addTemplate(
                        "Cloud",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/cloud.png")),
                        "ellipse;shape=cloud", 160, 120, "");

        shapesPalette
                .addEdgeTemplate(
                        "Straight",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/straight.png")),
                        "straight", 120, 120, "");
        shapesPalette
                .addEdgeTemplate(
                        "Horizontal Connector",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/connect.png")),
                        null, 100, 100, "");
        shapesPalette
                .addEdgeTemplate(
                        "Vertical Connector",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/vertical.png")),
                        "vertical", 100, 100, "");
        shapesPalette
                .addEdgeTemplate(
                        "Entity Relation",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/entity.png")),
                        "entity", 100, 100, "");
        shapesPalette
                .addEdgeTemplate(
                        "Arrow",
                        new ImageIcon(
                                GraphEditor.class
                                        .getResource("/com/mxgraph/examples/swing/images/arrow.png")),
                        "arrow", 120, 120, "");

        List<CellEle> fiberCellList = CellDecoder.opticalDeviceCellList;
        addCellsToPalette(imagesPalette, fiberCellList);

        List<EdgeEle> edgeList = EdgeDecoder.egdeList;
        for (EdgeEle edgeEle : edgeList) {
            mxCell cell = linkPalette.addEdgeTemplate(edgeEle.getName(),
                    new ImageIcon(GraphEditor.class.getResource(edgeEle.getIcon())),
                    edgeEle.getStyle(),
                    //"straight;strokeWidth=20;endArrow=none;verticalLabelPosition=middle;verticalAlign=middle;fontFamily=微软雅黑 Light;fontSize=24;fontColor=#FF0000;labelBackgroundColor=#FFFFFF;labelBorderColor=#000000;strokeColor=#66FFFF",
                    edgeEle.getWidth(), edgeEle.getHeight(), edgeEle.getName(), edgeEle.getType(), ""
            );
            AllCellMap.put(cell.getName(), cell);
        }


        component.addComponentListener(new ComponentListener() {

            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

        });
    }


    public void initialise() {
        graphOutline = new mxGraphOutline(graphComponent);
        libraryPane = new JTabbedPane();
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(libraryPane);
        JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel,
                graphComponent);
        outer.setOneTouchExpandable(true);
        outer.setDividerLocation(140);
        outer.setDividerSize(4);
        outer.setBorder(null);
        statusBar = createStatusBar();
        installRepaintListener();
        setLayout(new BorderLayout());
        add(outer, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        installToolBar();
        installHandlers();
        installListeners();

    }

    protected void installToolBar() {
        add(new CellEditorToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
    }
    private void addCellsToPalette(EditorPalette palette, List<CellEle> cellList) {
        for (CellEle cellEle : cellList) {
            mxCell cell = new mxCell();
            cell.setGeometry(new mxGeometry(0, 0, cellEle.getWidth(), cellEle.getHeight()));
            cell.setStyle(cellEle.getStyle());
            cell.setType(cellEle.getType());
            cell.setName(cellEle.getName());
            cell.setVertex(true);
            cell.setMultiValue(cellEle.getMultiValue());

            if (cellEle.getPorts() != null && !cellEle.getPorts().isEmpty()) {
                // has connect point, handle port cell
                cell.setConnectable(true);
                //遍历图元的磁力点
                for (CellPort cellPort : cellEle.getPorts()) {
                    mxGeometry geo = new mxGeometry(cellPort.getX(), cellPort.getY(), cellPort.getWidth(), cellPort.getHeight());
                    geo.setOffset(new mxPoint(-geo.getWidth() / 2, -geo.getHeight() / 2));
                    geo.setRelative(true);

                    mxCell port = new mxCell();
                    port.setStyle(cellPort.getStyle());
                    port.setGeometry(geo);
                    port.setName(cellPort.getName());
                    port.setVertex(true);
                    port.setType("Port");
                    port.setAttr(cellPort.getAttr());
                    port.setLocation(cellPort.getLocation());
                    port.setDirection(cellPort.getDirection());
                    cell.insert(port);
                }
            }
            AllCellMap.put(cell.getName(), cell);  //添加的图元、连接点
            palette.addTemplate(cellEle.getName(),
                    new ImageIcon(GraphEditor.class.getResource(cellEle.getIcon())), cell);
        }
    }
    /**
     *
     */
    private static final long serialVersionUID = 9147985264485929576L;


    public static class CustomGraphComponent extends mxGraphComponent {

        private JFrame frame;

        public JFrame getFrame() {
            return frame;
        }

        public void setFrame(JFrame frame) {
            this.frame = frame;
        }

        /**
         *
         */
        private static final long serialVersionUID = -6833603133512882012L;


        /**
         *
         * @param graph
         */
        public CustomGraphComponent(mxGraph graph) {
            super(graph);
            graph.setKeepEdgesInBackground(false);
            // Sets switches typically used in an editor
            setPageVisible(false);
            setGridVisible(false);
            setToolTips(false);
            getConnectionHandler().setCreateTarget(false);
            getConnectionHandler().setEnabled(false);
            //	getGraphHandler().setEnabled(false);

            this.getPanningHandler().setEnabled(false);
            //	this.getSubHandler().setEnabled(false);//
            //this.getTransferHandler()
            // Loads the defalt stylesheet from an external file
            mxCodec codec = new mxCodec();
            Document doc = mxUtils.loadDocument(FileUtil.getRootPath().substring(1) + "/examples/com/mxgraph/examples/swing/resources/basic-style.xml");
            codec.decode(doc.getDocumentElement(), graph.getStylesheet());


            // Sets the background to white
            getViewport().setOpaque(false);
            setBackground(Color.WHITE);
        }

        /**
         * Overrides drop behaviour to set the cell style if the target
         * is not a valid drop target and the cells are of the same
         * type (eg. both vertices or both edges).
         */
        public Object[] importCells(Object[] cells, double dx, double dy,
                                    Object target, Point location) {
            if (target == null && cells.length == 1 && location != null) {
                target = getCellAt(location.x, location.y);

                if (target instanceof mxICell && cells[0] instanceof mxICell) {
                    mxICell targetCell = (mxICell) target;
                    mxICell dropCell = (mxICell) cells[0];

                    if (targetCell.isVertex() == dropCell.isVertex()
                            || targetCell.isEdge() == dropCell.isEdge()) {
                        mxIGraphModel model = graph.getModel();
                        model.setStyle(target, model.getStyle(cells[0]));
                        graph.setSelectionCell(target);

                        return null;
                    }
                }
            }

            return super.importCells(cells, dx, dy, target, location);
        }

    }

    /**
     * A graph that creates new edges from a given template edge.
     */
    public static class CustomGraph extends mxGraph {
        /**
         * Holds the edge to be used as a template for inserting new edges.
         */
        protected Object edgeTemplate;

        /**
         * Custom graph that defines the alternate edge style to be used when
         * the middle control point of edges is double clicked (flipped).
         */
        public CustomGraph() {
            setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
        }

        /**
         * Sets the edge template to be used to inserting edges.
         */
        public void setEdgeTemplate(Object template) {
            edgeTemplate = template;
        }


        /**
         * Overrides the method to use the currently selected edge template for
         * new edges.
         *
         * @param parent
         * @param id
         * @param value
         * @param source
         * @param target
         * @param style
         * @return
         */
        public Object createEdge(Object parent, String id, Object value,
                                 Object source, Object target, String style) {
            if (edgeTemplate != null) {
                mxCell edge = (mxCell) cloneCells(new Object[]{edgeTemplate})[0];
                edge.setId(id);

                return edge;
            }

            return super.createEdge(parent, id, value, source, target, style);
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        CellEditorFrame cellEditorFrame = new CellEditorFrame();
        cellEditorFrame.start();
    }

    public void start() {
       // mxConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        CellEditorFrame celleditorFrame = new CellEditorFrame();
        celleditorFrame.createCellFrame().setVisible(true);
    }

}
