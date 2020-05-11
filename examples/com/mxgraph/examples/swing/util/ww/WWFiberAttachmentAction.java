package com.mxgraph.examples.swing.util.ww;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxStyleUtils;
import com.mxgraph.view.mxGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class WWFiberAttachmentAction {

    // 默认光纤参数
    private final static double fiberWidth = 15;  // 光纤宽度
    private final static double turningWidth = fiberWidth;  // 拐角宽度度
    private final static double turningHeight = fiberWidth;  // 拐角高度
    private final static double halfDiffer = fiberWidth / 2;  // 插图像素差值

    // 默认图元类型样式
    private final static String commonStyle = "shape=image;imageAspect=0;movable=0;editable=0;resizable=0;";
    private final static String VERTICAL = commonStyle + "image=/com/mxgraph/examples/swing/images/fiber/vertical_line.png";
    private final static String HORIZONTAL = commonStyle + "image=/com/mxgraph/examples/swing/images/fiber/horizontal_line.png";
    private final static String TOP_LEFT = commonStyle + "image=/com/mxgraph/examples/swing/images/fiber/top_left.png";
    private final static String TOP_RIGHT = commonStyle + "image=/com/mxgraph/examples/swing/images/fiber/top_right.png";
    private final static String BOTTOM_LEFT = commonStyle + "image=/com/mxgraph/examples/swing/images/fiber/bottom_left.png";
    private final static String BOTTOM_RIGHT = commonStyle + "image=/com/mxgraph/examples/swing/images/fiber/bottom_right.png";
    private final static String POINT = commonStyle + "image=/com/mxgraph/examples/swing/images/fiber/point.png";

    // 所有转折点
    private static HashSet<String> turningPoints = new HashSet<>();

    // 类成员
    private mxCell fiberCell;  // 目标原始光纤图元
    private mxGraph graph;  // 编辑器图形

    // 联系map
    public static HashMap<String, HashSet<mxCell>> imageCellsOfFiberEdge = new HashMap<>();  // 原始光纤id--附加光纤图片，键值对

    /**
     * 构造函数
     *
     * @param graph     编辑器图形
     * @param fiberCell 原始光纤图元
     */
    public WWFiberAttachmentAction(mxGraph graph, mxCell fiberCell) {
        this.graph = graph;
        this.fiberCell = fiberCell;
    }


    /**
     * 添加图元（无历史）
     *
     * @param x      x坐标
     * @param y      y坐标
     * @param width  宽度
     * @param height 高度
     * @param style  样式
     * @return mxCell实例
     */
    private mxCell addCell(double x, double y, double width, double height, String style) {
        /* 不会记录在history中的方法 */
        // 向图中添加图元
        mxGeometry geometry = new mxGeometry(x, y, width, height);
        mxCell cell = new mxCell(null, geometry, style);
        cell.setVertex(true);
        mxCell parentCell = (mxCell) graph.getDefaultParent();
        parentCell.insert(cell);
        cell.setType("fiber_image");
        // 检测光纤图片集合是否空
        HashSet<mxCell> fiberImageSet = imageCellsOfFiberEdge.get(fiberCell.getId());
        if (fiberImageSet == null) {
            fiberImageSet = new HashSet<mxCell>();
            imageCellsOfFiberEdge.put(fiberCell.getId(), fiberImageSet);
        }
        fiberImageSet.add(cell);
        return cell;
    }

    /**
     * 添加图元（追加历史）
     *
     * @param x      x坐标
     * @param y      y坐标
     * @param width  宽度
     * @param height 高度
     * @param style  样式
     * @return mxCell实例
     */
    @Deprecated()
    private mxCell addCellWithHistory(double x, double y, double width, double height, String style) {
        /* 会记录在history中的方法 */
        mxCell cell = null;
        mxCell defaultParent = (mxCell) graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            cell = (mxCell) graph.insertVertex(defaultParent, null, "", x, y, width, height, style);
        } finally {
            graph.getModel().endUpdate();
            graph.refresh();
        }
        return cell;
    }

    /**
     * 添加垂直型直线型美化光纤图元
     *
     * @param x      x坐标
     * @param y      y坐标
     * @param length 长度度
     * @return
     */
    private mxCell addVerticalFiberLine(double x, double y, double length) {
        if ("fiber_horizontal".equals(fiberCell.getName())) {
            x -= halfDiffer;
        } else if ("fiber_vertical".equals(fiberCell.getName())) {
            x -= halfDiffer;
        }
        return addCell(x, y, fiberWidth, length, VERTICAL);
    }


    /**
     * 添加水平型直线型美化光纤图元
     *
     * @param x      x坐标
     * @param y      y坐标
     * @param length 长度度
     * @return
     */
    private mxCell addHorizontalFiberLine(double x, double y, double length) {
        if ("fiber_horizontal".equals(fiberCell.getName())) {
            y -= halfDiffer;
        } else if ("fiber_vertical".equals(fiberCell.getName())) {
            y -= halfDiffer;
        }
        return addCell(x, y, length, fiberWidth, HORIZONTAL);
    }

    /**
     * 添加拐角型美化光纤图元
     *
     * @param x
     * @param y
     * @param style
     * @return
     */
    private mxCell addFiberTurning(double x, double y, String style) {
        x -= halfDiffer;
        y -= halfDiffer;
        return addCell(x, y, turningWidth, turningHeight, style);
    }


    /**
     * 添加拐角型美化光纤相交点
     *
     * @param x
     * @param y
     * @return
     */
    private mxCell addFiberPoint(double x, double y) {
        WWLogger.debug("YES YES");
        x -= halfDiffer;
        y -= halfDiffer;
        mxCell mxCell = addCell(x, y, turningWidth, turningHeight, POINT);
        mxCell.setName("fiber_point");
        fiberCell.getParent().insert(mxCell);
        return mxCell;
    }

    /**
     * 根据左上起点添加旋转直线
     *
     * @param x
     * @param y
     * @param length
     * @param rotation
     * @return
     */
    private mxCell addRotationLine(double x, double y, double length, double rotation) {
        WWLogger.error(Math.toRadians(rotation));
        // 转换为旋转中心
        double radians = Math.toRadians(rotation);
        x = x + Math.cos(radians) * length / 2 - (length / 2);
        y = y + Math.sin(radians) * length / 2;
        // 计算偏移
        double xOffsetForFiberWidth = 0;
        double yOffsetForFiberWidth = - fiberWidth / 2;
        // 向图中添加图元
        mxGeometry geometry = new mxGeometry(x+xOffsetForFiberWidth, y + yOffsetForFiberWidth, length, fiberWidth);
        mxCell cell = new mxCell(null, geometry, HORIZONTAL);
        cell.setVertex(true);
        mxCell parentCell = (mxCell) graph.getDefaultParent();
        parentCell.insert(cell);
        cell.setType("fiber_image");
        String newStyle = mxStyleUtils.setStyle(cell.getStyle(), "rotation", String.valueOf(rotation));
        cell.setStyle(newStyle);
        // 检测光纤图片集合是否空
        HashSet<mxCell> fiberImageSet = imageCellsOfFiberEdge.get(fiberCell.getId());
        if (fiberImageSet == null) {
            fiberImageSet = new HashSet<mxCell>();
            imageCellsOfFiberEdge.put(fiberCell.getId(), fiberImageSet);
        }
        fiberImageSet.add(cell);
        return cell;
    }

    /**
     * 为原始光纤图元添加美化光纤
     *
     * @return
     */
    public void doHandleForFiber() {
        // 删除原有附加图元，并清空map
        HashSet<mxCell> mxCells = imageCellsOfFiberEdge.get(fiberCell.getId());
        if (mxCells != null) {
            for (mxCell fiberImageCell : mxCells) {
                // 不计入历史的删除光纤图片图元
                if (fiberImageCell.getParent() == null) {
                    mxCell defaultParent = (mxCell) graph.getDefaultParent();
                    fiberImageCell.setParent(defaultParent);
                }
                fiberImageCell.getParent().remove(fiberImageCell);
            }
        }
        // 清空集合
        imageCellsOfFiberEdge.remove(fiberCell.getId());

        // 获取几何元素
        mxGeometry geometry = fiberCell.getGeometry();
        // 获取端点
        mxPoint sourcePoint = fiberCell.getTerminal(true) == null ? geometry.getSourcePoint() : getConnectPoint(true);
        mxPoint targetPoint = fiberCell.getTerminal(false) == null ? geometry.getTargetPoint() : getConnectPoint(false);
        // 判断是否为直线型光纤
        if ("fiber".equals(fiberCell.getName())) {
            WWLogger.warn("有起点终端: ", fiberCell.getTerminal(true) != null, "有终点终端: ", fiberCell.getTerminal(false) != null);
            WWLogger.warn("计算终端起点：", sourcePoint, "计算终端终点", targetPoint);
            // 计算线长
            double length = Math.sqrt(Math.pow(sourcePoint.getX() - targetPoint.getX(), 2) + Math.pow(sourcePoint.getY() - targetPoint.getY(), 2));
            // 根据反正切，计算旋转角
            double tan = targetPoint.getX() > sourcePoint.getX() ? (targetPoint.getY() - sourcePoint.getY()) / (targetPoint.getX() - sourcePoint.getX()) : (sourcePoint.getY() - targetPoint.getY()) / (sourcePoint.getX() - targetPoint.getX());
            double arcTan = Math.atan(tan);
            double deg = Math.toDegrees(arcTan);
            WWLogger.error(tan, arcTan, deg);
            // sourcePoint为起点，画直线
            if (sourcePoint.getX() < targetPoint.getX()) {
                addRotationLine(sourcePoint.getX(), sourcePoint.getY(), length, deg);
            }
            // targetPoint为起点，画直线
            else {
                addRotationLine(targetPoint.getX(), targetPoint.getY(), length, deg);
            }
        }
        // 若是肘型光纤
        else {
            // 拐点
            mxPoint sourceTurningPoint = null, targetTurningPoint = null;
            // 计算关键终点
            List<mxPoint> changePoints = fiberCell.getGeometry().getPoints();
            mxPoint changePoint = changePoints == null ? new mxPoint((sourcePoint.getX() + targetPoint.getX()) / 2, (sourcePoint.getY() + targetPoint.getY()) / 2) : changePoints.get(0);
            mxCell line1 = null, line2 = null, mline = null;
            if ("fiber_vertical".equals(fiberCell.getName())) {
                WWLogger.debug("fiber_vertical");
                // 两条垂直线长变量
                double sourceLineLength, targetLineLength = 0;
                // 计算中线长
                double middleLineLength = Math.abs(sourcePoint.getX() - targetPoint.getX());
                if (changePoints == null) {
                    // 计算垂直线长
                    sourceLineLength = targetLineLength = Math.abs(sourcePoint.getY() - targetPoint.getY()) / 2;
                }
                // 非对称性
                else {
                    // 计算垂直线长
                    sourceLineLength = Math.abs(sourcePoint.getY() - changePoint.getY());
                    targetLineLength = Math.abs(targetPoint.getY() - changePoint.getY());
                }
                // 创建起点线条、终点线条（fiber_vertical，需要画两条垂线，且x坐标分别为起点、终点横坐标，y坐标为min(changePoint, xxPoint)
                line1 = addVerticalFiberLine(sourcePoint.getX(), sourcePoint.getY() < changePoint.getY() ? sourcePoint.getY() : changePoint.getY(), sourceLineLength);
                line2 = addVerticalFiberLine(targetPoint.getX(), targetPoint.getY() < changePoint.getY() ? targetPoint.getY() : changePoint.getY(), targetLineLength);
                // 添加中线
                mline = addHorizontalFiberLine(sourcePoint.getX() < targetPoint.getX() ? sourcePoint.getX() : targetPoint.getX(), changePoint.getY(), middleLineLength > 0 ? middleLineLength : 0);
                // 计算拐点
                sourceTurningPoint = new mxPoint(sourcePoint.getX(), changePoint.getY());
                targetTurningPoint = new mxPoint(targetPoint.getX(), changePoint.getY());
                getTurningImageCell(sourcePoint, targetPoint, sourceTurningPoint, targetTurningPoint, changePoint);
            } else if ("fiber_horizontal".equals(fiberCell.getName())) {
                WWLogger.debug("fiber_horizontal");
                // 两条水平线长变量
                double sourceLineLength, targetLineLength = 0;
                // 计算垂直线长
                double middleLineLength = Math.abs(sourcePoint.getY() - targetPoint.getY());
                if (changePoints == null) {
                    // 计算水平线长
                    sourceLineLength = targetLineLength = Math.abs(sourcePoint.getX() - targetPoint.getX()) / 2;
                } else {
                    // 计算水平线长
                    sourceLineLength = Math.abs(sourcePoint.getX() - changePoint.getX());
                    targetLineLength = Math.abs(targetPoint.getX() - changePoint.getX());
                }
                // 创建起点线条、终点线条
                line1 = addHorizontalFiberLine(sourcePoint.getX() < changePoint.getX() ? sourcePoint.getX() : changePoint.getX(), sourcePoint.getY(), sourceLineLength);
                line2 = addHorizontalFiberLine(targetPoint.getX() < changePoint.getX() ? targetPoint.getX() : changePoint.getX(), targetPoint.getY(), targetLineLength);
                // 添加中线
                mline = addVerticalFiberLine(changePoint.getX(), sourcePoint.getY() < targetPoint.getY() ? sourcePoint.getY() : targetPoint.getY(), middleLineLength > 0 ? middleLineLength : 0);
                // 计算拐点
                sourceTurningPoint = new mxPoint(changePoint.getX(), sourcePoint.getY());
                targetTurningPoint = new mxPoint(changePoint.getX(), targetPoint.getY());
                getTurningImageCell(sourcePoint, targetPoint, sourceTurningPoint, targetTurningPoint, changePoint);
            }
        }
        // 将原始光纤设置透明
        String newFiberStyle = mxStyleUtils.setStyle(fiberCell.getStyle(), "opacity", "0");
        fiberCell.setStyle(newFiberStyle);
        // 原始光纤上移，使用graoh.orderCells()会导致录入历史
        fiberCell.getParent().insert(fiberCell);
//        graph.cellsOrdered(new mxCell[]{line1, line2, mline}, true);
        graph.refresh();
    }

    /**
     * 计算连接点
     *
     * @param source 是否为起始端连接点
     * @return
     */
    private mxPoint getConnectPoint(boolean source) {
        // 直线型光纤
        List<mxPoint> absolutePoints = graph.getView().getState(fiberCell).getAbsolutePoints();
        if ("fiber".equals(fiberCell.getName())) {
            // 获取两点的绝对坐标
            mxPoint p1 = absolutePoints.get(0);
            mxPoint p2 = absolutePoints.get(1);
            // 计算斜率
            double k = p2.getX() == p1.getX() ? (p2.getY() >= p1.getY() ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY) : (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
            double x, y, length;
            // WWLogger.warn("k is ", k);
            // 获取几何信息
            mxGeometry tg = (mxGeometry) fiberCell.getTerminal(source).getGeometry();
            // 判断是否为相对坐标系
            if (tg.isRelative()) {
                WWLogger.warn("相对坐标系");
                mxGeometry pg = fiberCell.getTerminal(source).getParent().getGeometry();
                tg = new mxGeometry(pg.getX() + pg.getWidth() * tg.getX() + tg.getOffset().getX(), pg.getY() + pg.getHeight() * tg.getY() + tg.getOffset().getY(), tg.getWidth(), tg.getHeight());
            }
            // 获取终端设备中心坐标
            mxPoint basePoint = new mxPoint(tg.getCenterX(), tg.getCenterY());
            // 当前直线是kx + h/2 - kw/2
            mxPoint[] pointers = getLinePointerOnTerminalcBorder(k, tg, basePoint);
            WWLogger.error("待选点", pointers[0], pointers[1]);
            if (source) {
                for (mxPoint pointer : pointers) {
                    if (p2.getX() > p1.getX() && pointer.getX() > basePoint.getX()) return pointer;
                    if (p2.getX() < p1.getX() && pointer.getX() < basePoint.getX()) return pointer;
                    if (k == Double.POSITIVE_INFINITY && pointer.getY() < basePoint.getY()) return pointer;
                    if (k == Double.NEGATIVE_INFINITY && pointer.getY() > basePoint.getY()) return pointer;
                }
            } else {
                for (mxPoint pointer : pointers) {
                    if (p2.getX() > p1.getX() && pointer.getX() < basePoint.getX()) return pointer;
                    if (p2.getX() < p1.getX() && pointer.getX() > basePoint.getX()) return pointer;
                    if (k == Double.POSITIVE_INFINITY && pointer.getY() > basePoint.getY()) return pointer;
                    if (k == Double.NEGATIVE_INFINITY && pointer.getY() < basePoint.getY()) return pointer;
                }
            }
            return null;
        }
        // 肘型光纤
        else {
            mxPoint p1 = absolutePoints.get(0);
            mxPoint p2 = absolutePoints.get(1);
            mxPoint p3 = absolutePoints.get(2);
            mxPoint p4 = null;
            if (absolutePoints.size() < 4) {
                p4 = p3;
            } else {
                p4 = absolutePoints.get(3);
            }
            // 终端设备
            mxCell terminal = (mxCell) fiberCell.getTerminal(source);
            mxGeometry tg = terminal.getGeometry();
            // 判断坐标系是否为relative型
            if (tg.isRelative()) {
                mxGeometry pg = terminal.getParent().getGeometry();
                tg = new mxGeometry(pg.getX() + pg.getWidth() * tg.getX() + tg.getOffset().getX(), pg.getY() + pg.getHeight() * tg.getY() + tg.getOffset().getY(), tg.getWidth(), tg.getHeight());
            }
            // 垂直型肘型光纤
            if ("fiber_vertical".equals(fiberCell.getName())) {
                double x = tg.getX() + tg.getWidth() / 2;
                double y = 0;
                if (source) {
                    y = p2.getY() - p1.getY() >= 0 ? tg.getY() + tg.getHeight() : tg.getY();
                } else {
                    y = p4.getY() - p3.getY() >= 0 ? tg.getY() : tg.getY() + tg.getHeight();
                }
                return new mxPoint(x, y);
            }
            // 水平型肘型光纤
            else {
                double x = 0;
                double y = tg.getY() + tg.getHeight() / 2;
                if (source) {
                    x = p2.getX() - p1.getX() >= 0 ? tg.getX() + tg.getWidth() : tg.getX();
                } else {
                    x = p4.getX() - p3.getX() >= 0 ? tg.getX() : tg.getX() + tg.getWidth();
                }
                return new mxPoint(x, y);
            }
        }
    }

    /**
     * 以终端设备中心坐标、直线斜率，求出直线与终端设备边框的交点
     *
     * @param k                斜率
     * @param terminalGeometry 终端设备mxGeometry
     * @param basePoint        终端设备中心坐标
     * @return
     */
    private mxPoint[] getLinePointerOnTerminalcBorder(double k, mxGeometry terminalGeometry, mxPoint basePoint) {
        double w = terminalGeometry.getWidth();
        double h = terminalGeometry.getHeight();
        // 无斜率
        if (k == Double.POSITIVE_INFINITY || k == Double.NEGATIVE_INFINITY) {
            mxPoint[] mxPoints = {new mxPoint(basePoint.getX(), basePoint.getY() - h / 2), new mxPoint(basePoint.getX(), basePoint.getY() + h / 2)};
            return mxPoints;
        }
        // 有斜率
        else {
            LinkedList<mxPoint> list = new LinkedList();
            double limitK = terminalGeometry.getHeight() / terminalGeometry.getWidth();
            if (k <= limitK && k >= -limitK) {
                list.add(new mxPoint(basePoint.getX() - w / 2, basePoint.getY() - k * w / 2));
                list.add(new mxPoint(basePoint.getX() + w / 2, basePoint.getY() + k * w / 2));
//                WWLogger.log("-1 ~ 1");
            } else {
                list.add(new mxPoint(basePoint.getX() - h / 2 / k, basePoint.getY() - h / 2));
                list.add(new mxPoint(basePoint.getX() + h / 2 / k, basePoint.getY() + h / 2));
//                WWLogger.log("<=-1    >=1");
            }
            mxPoint[] mxPoints = new mxPoint[list.size()];
            mxPoints = list.toArray(mxPoints);
            return mxPoints;
        }
    }

    /**
     * 删除所有光纤美化图图元
     *
     * @param graph
     */
    public static void deleteAllFiberImage(mxGraph graph) {
        Object[] childCells = graph.getChildCells(graph.getDefaultParent());
        mxCell defaultParent = (mxCell) graph.getDefaultParent();
        for (Object childCell : childCells) {
            mxCell cell = (mxCell) childCell;
            if ("fiber_image".equals(cell.getType())) {
                cell.setParent(defaultParent);
                defaultParent.remove(cell);
            }
        }

    }

    private mxCell[] getTurningImageCell(mxPoint sourcePoint, mxPoint targetPoint, mxPoint sourceTurningPoint, mxPoint targetTurningPoint, mxPoint changePoint) {
        // 辅助数据结构
        HashMap<String, String> helper = new HashMap<>();
        helper.put("topleft", TOP_LEFT);
        helper.put("topright", TOP_RIGHT);
        helper.put("bottomleft", BOTTOM_LEFT);
        helper.put("bottomright", BOTTOM_RIGHT);
        mxCell[] turnings = null;
        // 方向
        String sourceChoice = "", targetChoice = "";
        if ("fiber_horizontal".equals(fiberCell.getName())) {
            if (changePoint.getY() > sourcePoint.getY()) {
                sourceChoice += "top";
            } else if (changePoint.getY() < sourcePoint.getY()) {
                sourceChoice += "bottom";
            }
            if (changePoint.getX() > sourcePoint.getX()) {
                sourceChoice += "right";
            } else if (changePoint.getX() < sourcePoint.getX()) {
                sourceChoice += "left";
            }

            if (changePoint.getY() > targetPoint.getY()) {
                targetChoice += "top";
            } else if (changePoint.getY() < targetPoint.getY()) {
                targetChoice += "bottom";
            }
            if (changePoint.getX() > targetPoint.getX()) {
                targetChoice += "right";
            } else if (changePoint.getX() < targetPoint.getX()) {
                targetChoice += "left";
            }
        } else if ("fiber_vertical".equals(fiberCell.getName())) {
            if (changePoint.getY() > sourcePoint.getY()) {
                sourceChoice += "bottom";
            } else if (changePoint.getY() < sourcePoint.getY()) {
                sourceChoice += "top";
            }
            if (changePoint.getX() > sourcePoint.getX()) {
                sourceChoice += "left";
            } else if (changePoint.getX() < sourcePoint.getX()) {
                sourceChoice += "right";
            }

            if (changePoint.getY() > targetPoint.getY()) {
                targetChoice += "bottom";
            } else if (changePoint.getY() < targetPoint.getY()) {
                targetChoice += "top";
            }
            if (changePoint.getX() > targetPoint.getX()) {
                targetChoice += "left";
            } else if (changePoint.getX() < targetPoint.getX()) {
                targetChoice += "right";
            }
        }
        mxCell sourceTurning = addFiberTurning(sourceTurningPoint.getX(), sourceTurningPoint.getY(), helper.get(sourceChoice));
        mxCell targetTurning = addFiberTurning(targetTurningPoint.getX(), targetTurningPoint.getY(), helper.get(targetChoice));
        return new mxCell[]{sourceTurning, targetTurning};
    }


    @Deprecated
    private static String getPureXY(mxPoint point) {
        String pureXY = point.getX() + "," + point.getY();
        return pureXY;
    }

    @Deprecated
    public static void findAndAddPoint(mxGraph graph) {
        if (true)
            return;
        // 删除唯一坐标集合
        turningPoints = new HashSet<>();
        mxCell defaultParent = (mxCell) (graph.getDefaultParent());
        // 删除所有相交点图元
        for (Object childCell : graph.getChildCells(defaultParent)) {
            mxCell cell = (mxCell) childCell;
            if ("fiber_point".equals(cell.getName())) {
                cell.setParent(defaultParent);
                defaultParent.remove(cell);
            }
        }
        for (Object childCell : graph.getChildCells(defaultParent)) {
            mxCell cell = (mxCell) childCell;
            if ("fiber_vertical".equals(cell.getName()) || "fiber_horizontal".equals(cell.getName())) {
                // 获取几何元素
                mxGeometry geometry = cell.getGeometry();
                // 获取关键点
                mxPoint sourcePoint = geometry.getSourcePoint();
                mxPoint targetPoint = geometry.getTargetPoint();
                // 计算关键终点
                List<mxPoint> changePoints = cell.getGeometry().getPoints();
                mxPoint changePoint = changePoints == null ? new mxPoint((sourcePoint.getX() + targetPoint.getX()) / 2, (sourcePoint.getY() + targetPoint.getY()) / 2) : changePoints.get(0);
                mxCell line1 = null, line2 = null, mline = null;
                // 计算拐点
                mxPoint sourceTurningPoint = new mxPoint(changePoint.getX(), sourcePoint.getY());
                mxPoint targetTurningPoint = new mxPoint(changePoint.getX(), targetPoint.getY());
                // 转折点加圆点
                String stPure = getPureXY(sourceTurningPoint);
                String ttPure = getPureXY(targetTurningPoint);
                WWFiberAttachmentAction wwFiberAttachmentAction = new WWFiberAttachmentAction(graph, cell);
                if (turningPoints.contains(stPure)) {
                    wwFiberAttachmentAction.addFiberPoint(sourceTurningPoint.getX(), sourceTurningPoint.getY());
                }
                if (turningPoints.contains(ttPure)) {
                    wwFiberAttachmentAction.addFiberPoint(targetTurningPoint.getX(), targetTurningPoint.getY());
                }
                turningPoints.add(stPure);
                turningPoints.add(ttPure);
                WWLogger.debug("add pure xy:", stPure, ttPure, "All: ", turningPoints);
            }
        }
        allPointToFront(graph);
    }

    @Deprecated
    private static void allPointToFront(mxGraph graph) {
        mxCell defaultParent = (mxCell) (graph.getDefaultParent());
        for (Object childCell : graph.getChildCells(defaultParent)) {
            mxCell cell = (mxCell) childCell;
            if ("fiber_point".equals(cell.getName())) {
                cell.setParent(defaultParent);
                cell.getParent().insert(cell);
            }
            WWLogger.warn(cell, "UP UP");
        }
    }
}
