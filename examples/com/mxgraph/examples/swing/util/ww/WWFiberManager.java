package com.mxgraph.examples.swing.util.ww;

import com.github.jsonldjava.utils.Obj;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxStyleUtils;
import com.mxgraph.view.mxGraph;

import java.lang.reflect.Array;
import java.util.*;

public class WWFiberManager {
    /**
     * 为mxCell实例处理
     *
     * @param selectionObjs 选中图元
     * @param graph
     */
    public static void doHandleForMxCells(Object[] selectionObjs, mxGraph graph) {
        // 数组类型转换
        mxCell[] selectionCells = new mxCell[selectionObjs.length];
        for (int i = 0;i<selectionCells.length;i++) {
            selectionCells[i] = (mxCell) selectionObjs[i];
        }
        // 遍历处理
        for (mxCell selectionCell : selectionCells) {
            // 空指针检测
            if (selectionCell == null)
                return;
            // 如果图元类型为设备
            if (selectionCell.getType() != null && selectionCell.getType().indexOf("FiberEdge") != -1) {
                WWFiberAttachmentAction factory = new WWFiberAttachmentAction(graph, selectionCell);
                factory.doHandleForFiber();
            } else {
                WWLogger.logF("图元类型为设备，有{0}条光纤连接", selectionCell.getEdgeCount());
                for (int i = 0; i < selectionCell.getEdgeCount(); i++) {
                    mxCell fiberEdge = (mxCell) selectionCell.getEdgeAt(i);
                    WWLogger.log(fiberEdge.getName());
                    if (fiberEdge.getType() != null && fiberEdge.getType().indexOf("FiberEdge") != -1) {
                        WWFiberAttachmentAction factory = new WWFiberAttachmentAction(graph, fiberEdge);
                        factory.doHandleForFiber();
                    }
                }
                // 为四个磁力端口处理
                doHandleForMxCells(graph.getChildCells(selectionCell), graph);
            }
        }
        WWFiberAttachmentAction.findAndAddPoint(graph);
    }

    /**
     * 为整张图处理
     *
     * @param graph
     */
    public static void doHandleForGraph(mxGraph graph) {
        WWFiberAttachmentAction.deleteAllFiberImage(graph);
        mxCell parentCell = (mxCell) graph.getDefaultParent();
        Object[] selectionbjs = graph.getChildCells(parentCell);
        mxCell[] selectionCells = new mxCell[selectionbjs.length];
        for (int i = 0; i < selectionbjs.length; i++) {
            selectionCells[i] = (mxCell) selectionbjs[i];
        }
        doHandleForMxCells(selectionCells, graph);
        graph.refresh();
        WWFiberAttachmentAction.findAndAddPoint(graph);
    }
}


