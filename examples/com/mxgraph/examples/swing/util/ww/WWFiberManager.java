package com.mxgraph.examples.swing.util.ww;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class WWFiberManager {
    /**
     * 为mxCell实例处理
     *
     * @param targetCellObjs 选中图元
     * @param graph
     */
    public static void doHandleForMxCells(Object[] targetCellObjs, mxGraph graph) {
        // 遍历处理
        for (Object targetCellObj : targetCellObjs) {
            // mxCell类型转换
            mxCell targetCell = (mxCell)targetCellObj;
            // 空指针检测
            if (targetCell == null)
                return;
            // 如果图元类型为光纤
            if (targetCell.getType() != null && targetCell.getType().indexOf("FiberEdge") != -1) {
                WWFiberAttachmentAction factory = new WWFiberAttachmentAction(graph, targetCell);
                factory.doHandleForFiber();
            } else {
                WWLogger.infoF("图元类型为设备，有{0}条光纤连接", targetCell.getEdgeCount());
                // 遍历与设备相连的连线
                for (int i = 0; i < targetCell.getEdgeCount(); i++) {
                    mxCell fiberEdge = (mxCell) targetCell.getEdgeAt(i);
                    WWLogger.infoF(fiberEdge.getName());
                    // 若连线为光纤
                    if (fiberEdge.getType() != null && fiberEdge.getType().indexOf("FiberEdge") != -1) {
                        WWFiberAttachmentAction factory = new WWFiberAttachmentAction(graph, fiberEdge);
                        factory.doHandleForFiber();
                    }
                }
                // 为四个磁力端口处理
                doHandleForMxCells(graph.getChildCells(targetCell), graph);
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
        Object[] allCellObjs = graph.getChildCells(parentCell);
        doHandleForMxCells(allCellObjs, graph);
        graph.refresh();
        WWFiberAttachmentAction.findAndAddPoint(graph);
    }
}


