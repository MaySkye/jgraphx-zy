package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorActions.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CellEditorToolBar extends JToolBar {

    /**
     *
     */
    private static final long serialVersionUID = -1638858423933741524L;


    /**
     * @param frame
     * @param orientation
     */
    private boolean ignoreZoomChange = false;

    /**
     *
     */
    public CellEditorToolBar(final BasicGraphEditor editor, int orientation) {
        super(orientation);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);

        add(editor.bind("New", new NewAction(),
                "/com/mxgraph/examples/swing/images/new.gif"));
        add(editor.bind("Open", new OpenAction(),
                "/com/mxgraph/examples/swing/images/open.gif"));
       /* add(editor.bind("Save", new CellSaveAction(false),
                "/com/mxgraph/examples/swing/images/save.gif"));*/
        add(editor.bind("Save", new OpenAction(),
                "/com/mxgraph/examples/swing/images/save.gif"));
        addSeparator();

        add(editor.bind("Print", new PrintAction(),
                "/com/mxgraph/examples/swing/images/print.gif"));

        addSeparator();

        add(editor.bind("Cut", TransferHandler.getCutAction(),
                "/com/mxgraph/examples/swing/images/cut.gif"));
        add(editor.bind("Copy", TransferHandler.getCopyAction(),
                "/com/mxgraph/examples/swing/images/copy.gif"));
        add(editor.bind("Paste", TransferHandler.getPasteAction(),
                "/com/mxgraph/examples/swing/images/paste.gif"));

        addSeparator();

        add(editor.bind("Delete", mxGraphActions.getDeleteAction(),
                "/com/mxgraph/examples/swing/images/delete.gif"));

        addSeparator();

        add(editor.bind("Undo", new HistoryAction(true),
                "/com/mxgraph/examples/swing/images/undo.gif"));
        add(editor.bind("Redo", new HistoryAction(false),
                "/com/mxgraph/examples/swing/images/redo.gif"));

        addSeparator();

        add(editor.bind("Stroke", new ColorAction("Stroke",
                        mxConstants.STYLE_STROKECOLOR),
                "/com/mxgraph/examples/swing/images/linecolor.gif"));
        add(editor.bind("Fill", new ColorAction("Fill",
                        mxConstants.STYLE_FILLCOLOR),
                "/com/mxgraph/examples/swing/images/fillcolor.gif"));

        addSeparator();

        final mxGraphView view = editor.getGraphComponent().getGraph()
                .getView();
        final JComboBox zoomCombo = new JComboBox(new Object[]{"400%",
                "200%", "150%", "100%", "75%", "50%", mxResources.get("page"),
                mxResources.get("width"), mxResources.get("actualSize")});
        zoomCombo.setEditable(true);
        zoomCombo.setMinimumSize(new Dimension(75, 0));
        zoomCombo.setPreferredSize(new Dimension(75, 0));
        zoomCombo.setMaximumSize(new Dimension(75, 100));
        zoomCombo.setMaximumRowCount(9);
        add(zoomCombo);

        // Sets the zoom in the zoom combo the current value
        mxIEventListener scaleTracker = new mxIEventListener() {
            /**
             *
             */
            public void invoke(Object sender, mxEventObject evt) {
                ignoreZoomChange = true;

                try {
                    zoomCombo.setSelectedItem((int) Math.round(100 * view
                            .getScale())
                            + "%");
                } finally {
                    ignoreZoomChange = false;
                }
            }
        };

        // Installs the scale tracker to update the value in the combo box
        // if the zoom is changed from outside the combo box
        view.getGraph().getView().addListener(mxEvent.SCALE,
                scaleTracker);
        view.getGraph().getView().addListener(
                mxEvent.SCALE_AND_TRANSLATE, scaleTracker);

        // Invokes once to sync with the actual zoom value
        scaleTracker.invoke(null, null);

        zoomCombo.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                mxGraphComponent graphComponent = editor.getGraphComponent();

                // Zoomcombo is changed when the scale is changed in the diagram
                // but the change is ignored here
                if (!ignoreZoomChange) {
                    String zoom = zoomCombo.getSelectedItem().toString();

                    if (zoom.equals(mxResources.get("page"))) {
                        graphComponent.setPageVisible(true);
                        graphComponent
                                .setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
                    } else if (zoom.equals(mxResources.get("width"))) {
                        graphComponent.setPageVisible(true);
                        graphComponent
                                .setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
                    } else if (zoom.equals(mxResources.get("actualSize"))) {
                        graphComponent.zoomActual();
                    } else {
                        try {
                            zoom = zoom.replace("%", "");
                            graphComponent.zoomTo(
                                    Double.parseDouble(zoom) / 100,
                                    graphComponent.isCenterZoom());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(editor, ex
                                    .getMessage());
                        }
                    }
                }
            }
        });
        //chen

        addSeparator();

        /*连接点*/
        /*final ImageIcon rect = new ImageIcon(this.getClass().getResource("/com/mxgraph/examples/swing/images/connectpoint.GIF"));
        //默认的在jdk或者jcreator下运行，用此语句导入图片
        JButton rectButton = new JButton("", rect);
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mxConstants.paint = 2;
                mxGraph graph = editor.getGraphComponent().getGraph();
                graph.setCellsSelectable(false);
                graph.setCellsMovable(false);
                graph.setCellsLocked(true);
                graph.setAllowDanglingEdges(false);
            }
        });
        add(rectButton);*/

    }


}
