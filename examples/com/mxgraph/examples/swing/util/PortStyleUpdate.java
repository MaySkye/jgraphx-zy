package com.mxgraph.examples.swing.util;

import com.mxgraph.model.mxCell;


public class PortStyleUpdate {

    private static String transparencyStyle = "image;image=/com/mxgraph/examples/swing/images/transparent.png";


    public static void switchToTransparency(mxCell cell) {
        if (cell == null) {
            return;
        }

        if (cell.isVertex() && "Port".equals(cell.getType())) {
            cell.setOriginStyle(cell.getStyle());
            //cell.setStyle(null);
            cell.setStyle(transparencyStyle);
            cell.setVisible(false);

        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            switchToTransparency(child);
        }
    }

    public static void resetStyle(mxCell cell) {
        if (cell == null) {
            return;
        }
        if (cell.isVertex() && "Port".equals(cell.getType())) {
            cell.setStyle(cell.getOriginStyle());
            cell.setOriginStyle(null);
            cell.setVisible(true);
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            resetStyle(child);
        }
    }
}
