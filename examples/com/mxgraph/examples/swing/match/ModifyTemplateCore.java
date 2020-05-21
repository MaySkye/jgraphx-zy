package com.mxgraph.examples.swing.match;

import com.alibaba.fastjson.JSON;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.graph.EdgeLink;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.owl.OwlDataAttribute;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlObjectAttribute;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutableTriple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.findKind;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:36 2018/11/9
 * @Modify By:
 */
public class ModifyTemplateCore {
    private String templatePath; //对应的模板路径
    private String filePath;
    private Set<mxCell> newCreateCell;
    private static Map<String, String> ConnectorToEdgeMap = new HashMap<>();
    private BasicGraphEditor editor;
    private mxGraph graph;
    private mxCell root;
    private OwlResourceData new_owlResourceData;

    //赵艺：关于添加监控图元涉及到的变量
    private Map<String,OwlObject> cell_property=new HashMap<>();
    //用来记录包括哪些记录数据的图元
    private Map<String,mxCell> mxcell_property=new HashMap<>();
    private Map<String,mxCell> mxcell_device=new HashMap<>();
    private static int MonitorHeight = 40;
    private static int MonitorNameWidth = 180;
    private static int MonitorDataWidth = 160;
    private static int MonitorUnitWidth = 40;
    private static int DeviceWidth=380;
    private static int DeviceHeight=30;

    static {
        /*
        * key:边的名称name  value:边的类型type
        * name是唯一的，type不一定是唯一的
        * */
        /*ConnectorToEdgeMap.put("fiber_vertical", "FiberEdge");
        ConnectorToEdgeMap.put("fiber_horizontal", "FiberEdge");
        ConnectorToEdgeMap.put("fiber", "FiberEdge");
        ConnectorToEdgeMap.put("data_horizontal", "DataEdge");
        ConnectorToEdgeMap.put("data_vertical", "DataEdge");
        ConnectorToEdgeMap.put("data", "DataEdge");*/
    }

    public ModifyTemplateCore(String templatePath) {
        this.templatePath=templatePath;
    }

    public  void postProcess(GraphInterface<String> res_graph, BasicGraphEditor editor, String filePath) {
        System.out.println("开始执行postProcess......");
        this.filePath=filePath;
        this.editor=editor;
        //对组态图进行调整
        graph = editor.getGraphComponent().getGraph();
        root = (mxCell) graph.getModel().getRoot();
        new_owlResourceData = editor.getNew_owlResourceData();
        newCreateCell = new HashSet<>();
        /*---------------------------------------------------------------------------------------------------*/
        // 第一步：去除绑定信息,图元的v对象设为null
        clearBindResource(root);
        /*---------------------------------------------------------------------------------------------------*/
        /*
          第二步：
        * 删除多余的单值图元,多值图元和监控图元
        * 目前只用到了单值图元，默认multiValue = "0"
        * 单值图元、多值图元的函数已实现，，监控图元的函数未实现
        * */
        List<mxCell> removeCells = new ArrayList<>();//removeCells存储将被删除的图元
        ResourceStaticData resourceStaticData = ResMatchCore.calcStaticMap(res_graph);
        //比较root和resourceStaticData.cellMap，将要删除的图元存储在removeCells
        removeRedundantCell(root, resourceStaticData.cellMap, removeCells,editor);
        removeRedundantMultiCell(root, resourceStaticData.cellMap, removeCells,editor);
        //removeRedundantMonitor(root, removeCells);

        graph.removeCells(removeCells.toArray()); //removeCells为自带的函数
        /*---------------------------------------------------------------------------------------------------*/
        // 第三步：删除多余Connector，连线
        removeCells.clear();
        removeRedundantConnector(root, removeCells);
        graph.removeCells(removeCells.toArray());
        /*---------------------------------------------------------------------------------------------------*/
        // 第四步：根据情况，删除没必要存在的Container图元
        removeCells.clear();
        //removeRestContainer(root, removeCells);
        graph.removeCells(removeCells.toArray());
        /*---------------------------------------------------------------------------------------------------*/
        // 第五步：根据资源文件，补充剩余图元
        fillingCell(resourceStaticData,editor);
        /*---------------------------------------------------------------------------------------------------*/
        // 第六步：重新进行自动化资源绑定(包括资源文件和资源ID以及设备ID)
        autoBindResource(res_graph, resourceStaticData.cellMap, root, new HashSet<>());
        /*---------------------------------------------------------------------------------------------------*/
        // 第七步：根据资源文件，对剩余的监控图元进行删减（暂不实现）
        removeCells.clear();
        //removeRestMonitor(root, removeCells);
        graph.removeCells(removeCells.toArray());
        // 根据情况，删除没必要存在的Container图元（暂不实现）
        removeCells.clear();
        //removeRestContainer(root, removeCells);
        graph.removeCells(removeCells.toArray());

        /*---------------------------------------------------------------------------------------------------*/
        // 第八步：根据图元绑定的资源信息，补充剩余图元连接关系
         fillingEdge(editor, res_graph);
        /*---------------------------------------------------------------------------------------------------*/
        // 第九步：自动化资源绑定，针对连接资源
         autoBindEdge(res_graph, root,new HashSet<>());
        /*---------------------------------------------------------------------------------------------------*/
         //自动调整图元，补充监控图元，补充monitor的资源信息（file & id）
        // autoAdjustCellAndEdge(root, root);
        // autoFillMonitorCell(root, root);
        //fillRedundantEdge(root, root);
        //autoBindMonitorResource(root, root);
        // 将新创建图元调整到后面，防止monitor图元被遮挡
        // List<mxCell> monitorCells = new ArrayList<>();
        // collectMonitor(root, monitorCells);
        /*---------------------------------------------------------------------------------------------------*/
        // 第十步：显示监控项图元
        fillingMonitorCells();
        /*---------------------------------------------------------------------------------------------------*/
        // 将新创建图元调整到后面，防止monitor图元被遮挡
        graph.orderCells(true, newCreateCell.toArray());
        graph.refresh();
    }

    /********************************************************************************************/

    //第一步：
    private void clearBindResource(mxCell cell) {
        if (cell == null) {
            return;
        }
        cell.setV(null);
        cell.setHasconnect(null);
        cell.setHasproperty(null);
        cell.setDeviceid(null);
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            clearBindResource(child);
        }
    }

    /********************************************************************************************/

    //第二步：删除多余的单值图元
    private void removeRedundantCell(mxCell cell, Map<String, Integer> cellMap, List<mxCell> removeCells, BasicGraphEditor editor) {
        //如果包括这个类型的，说明这个类型的被处理完了
        removeCells.forEach(mxCell -> {
            if ( Objects.equals(mxCell.getType(),cell.getType())) {
                return;
            }
        });

        if (cell.isVertex() && cell.getType() != null && !"Port".equals(cell.getType())
                && !"Monitor".equals(cell.getType()) && !"1".equals(cell.getMultiValue())
                && !"Container".equals(cell.getType())
                ) {
            //如果资源文件里不包括此种类型的图元，全部找出，存储在removeCells里
            if (!cellMap.containsKey(cell.getType())) {

                Set<mxCell> resSet = getCellsByType((mxCell) editor.getGraphComponent().getGraph().getDefaultParent(),
                        cell.getType());
                for (mxCell rmCell : resSet) {
                    if(!removeCells.contains(rmCell)){
                        removeCells.add(rmCell);
                        System.out.println("will remove: " + rmCell.getName());
                    }
                }
                return;
            }
            //如果资源文件里包括此种类型的图元，则比较数量
            else if (cellMap.get(cell.getType()) <
                    countCellType((mxCell) editor.getGraphComponent().getGraph().getDefaultParent(), cell.getType())) {
                List<mxCell> redundantList = new ArrayList<>();
                ListCellType((mxCell) editor.getGraphComponent().getGraph().getDefaultParent(), cell.getType(), redundantList);

                //定义了一个排序规则，先保留左上角的，将右下角的存入removeCells
                Collections.sort(redundantList, (o1, o2) -> {
                    mxCell cell1 = (mxCell) o1;
                    mxCell cell2 = (mxCell) o2;
                    mxGeometry geo1 = cell1.getGeometry();
                    mxGeometry geo2 = cell2.getGeometry();
                    return (int) ((geo1.getX() + geo1.getY() - geo2.getX() - geo2.getY()) * 1000);
                });
                for (int i = cellMap.get(cell.getType()); i < redundantList.size(); ++i) {
                    if (!removeCells.contains(redundantList.get(i))) {
                        removeCells.add(redundantList.get(i));
                    }
                }
                return;
            }
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            removeRedundantCell(child, cellMap, removeCells,editor);
        }
    }
     //删除多余的多值图元
    private void removeRedundantMultiCell(mxCell cell, Map<String, Integer> cellMap, List<mxCell> removeCells, BasicGraphEditor editor) {
        //如果包括这个类型的，说明这个类型的被处理完了
        removeCells.forEach(mxCell -> {
            if ( Objects.equals(mxCell.getType(),cell.getType())) {
                return;
            }
        });

        if (cell.isVertex() && cell.getType() != null && !"Port".equals(cell.getType())
                && !"Monitor".equals(cell.getType()) && !"0".equals(cell.getMultiValue())
                && !"Container".equals(cell.getType())
                ) {
            //如果资源文件里不包括此种类型的图元，全部找出，存储在removeCells里
            if (!cellMap.containsKey(cell.getType())) {
                Set<mxCell> resSet = getCellsByType((mxCell) editor.getGraphComponent().getGraph().getDefaultParent(),
                        cell.getType());
                for (mxCell rmCell : resSet) {
                    if(!removeCells.contains(rmCell)){
                        removeCells.add(rmCell);
                        System.out.println("will remove: " + rmCell.getName());
                    }
                }
                return;
            }
            //如果资源文件里包括此种类型的图元，则比较数量
            else if (cellMap.get(cell.getType()) <
                    countCellType((mxCell) editor.getGraphComponent().getGraph().getDefaultParent(), cell.getType())) {
                List<mxCell> redundantList = new ArrayList<>();
                ListCellType((mxCell) editor.getGraphComponent().getGraph().getDefaultParent(), cell.getType(), redundantList);

                //定义了一个排序规则，先保留左上角的，将右下角的存入removeCells
                Collections.sort(redundantList, (o1, o2) -> {
                    mxCell cell1 = (mxCell) o1;
                    mxCell cell2 = (mxCell) o2;
                    mxGeometry geo1 = cell1.getGeometry();
                    mxGeometry geo2 = cell2.getGeometry();
                    return (int) ((geo1.getX() + geo1.getY() - geo2.getX() - geo2.getY()) * 1000);
                });
                for (int i = cellMap.get(cell.getType()); i < redundantList.size(); ++i) {
                    if (!removeCells.contains(redundantList.get(i))) {
                        removeCells.add(redundantList.get(i));
                    }
                }
                return;
            }
        }

        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            removeRedundantMultiCell(child, cellMap, removeCells,editor);
        }
    }
     //将type类型的所有图元都存储在了Set<mxCell>
    private Set<mxCell> getCellsByType(mxCell root, String type) {
        if (root == null || type == null) {
            return null;
        }
        Set<mxCell> res = new HashSet<>();
        if (type.equals(root.getType())) {
            res.add(root);
        }
        for (int i = 0; i < root.getChildCount(); ++i) {
            mxCell child = (mxCell) root.getChildAt(i);
            Set<mxCell> tmp = getCellsByType(child, type);
            for (mxCell cell : tmp) {
                res.add(cell);
            }
        }
        return res;
    }
    private int countCellType(mxCell cell, String type) {
        int res = 0;
        if (type == null) {
            return res;
        }
        if (type.equals(cell.getType())) {
            ++res;
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            res += countCellType(child, type);
        }
        return res;
    }
    private void ListCellType(mxCell cell, String type, List<mxCell> list) {
        if (type == null || list == null) {
            return;
        }
        if (type.equals(cell.getType())) {
            list.add(cell);
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            ListCellType(child, type, list);
        }
    }

    /********************************************************************************************/

    //第三步：删除多余的连接(无两个连接的图元)
    private void removeRedundantConnector(mxCell cell, List<mxCell> removeCells) {
        if (cell == null || removeCells == null || removeCells.contains(cell)) {
            return;
        }

        if (cell.isEdge()&& (cell.getTarget()==null||cell.getSource()==null)) {
            System.out.println("will remove: " + cell.getName());
            removeCells.add(cell);
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            removeRedundantConnector(child, removeCells);
        }
    }
    //判断是不是孤立的，有没有与之相连接的图元(暂时未使用)
    private boolean isIsolated(mxCell cell) {
        int connectCells = 0;
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            if (!"Port".equals(child.getType()) || child.getEdgeCount() == 0) {
                continue;
            }
            for (int k = 0; k < child.getEdgeCount(); ++k) {
                mxCell edge = (mxCell) child.getEdgeAt(k);
                if (child.equals(edge.getSource())) {
                    if (edge.getTarget() != null) {
                        ++connectCells;
                    }
                } else {
                    if (edge.getSource() != null) {
                        ++connectCells;
                    }
                }
            }
        }
        return connectCells < 2;
    }

    /********************************************************************************************/

    //第四步：
    private  void removeRestContainer(mxCell cell, List<mxCell> removeCells) {
        if (cell == null || removeCells == null) {
            return;
        }
        if (cell.isVertex() && "Container".equals(cell.getType())) {
            if (cell.getChildCount() == 0) {
                removeCells.add(cell);
                return;
            }/* else if (cell.getChildCount() == 1
                    //&& Objects.equals(cell.getChildAt(0).getType(), "Port")
                    ) {
                removeCells.add(cell);
                return;
            }*/
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            removeRestContainer(child, removeCells);
        }
    }

    /********************************************************************************************/

    //第五步：
    private void fillingCell(ResourceStaticData resourceStaticData, BasicGraphEditor editor) {
        mxGraph graph = editor.getGraphComponent().getGraph();
        mxCell root = (mxCell) graph.getModel().getRoot();
        double[] baseX = {getMaxX(root)};
        double[] baseY = {getMaxY(root)};
        if (baseX[0] + 400 > 850) {
            baseX[0] = 0;
        } else {
            baseY[0] -= 300;
        }
        Map<String, Integer> staticMap = calcMap(root);
        resourceStaticData.cellMap.forEach((s, integer) -> {
            int cnt = 0;
            if (!staticMap.containsKey(s)) {
                cnt = integer;
            } else if (staticMap.get(s) < integer) {
                cnt = integer - staticMap.get(s);
            }
            System.out.println("need fill: " + s + ": " + cnt);
            for (int i = 0; i < cnt; ++i) {
                Pair<Double, Double> par = insertCell(s, baseX[0], baseY[0],editor);
                double width = par.getKey();
                double height = par.getValue();
                baseX[0] += width;
                if (baseX[0] + 200 > 850) {
                    baseX[0] = 0;
                    baseY[0] += 300;
                }
            }
        });
    }

    private  double getMaxX(mxCell cell) {
        if (cell == null) {
            return 0;
        }
        mxGeometry geo = cell.getGeometry();
        double res = 0;
        if (geo != null) {
            if (cell.isVertex()) {
                // vertex cell
                res = Math.max(geo.getX() + geo.getWidth(), res);
            } else {
                // edge cell
                if (cell.getSource() == null) {
                    res = Math.max(geo.getSourcePoint().getX(), res);
                }
                if (cell.getTarget() == null) {
                    res = Math.max(geo.getTargetPoint().getX(), res);
                }
            }
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            double tmpX = getMaxX(child);
            if (res < tmpX) {
                res = tmpX;
            }
        }
        return res;
    }
    private  double getMaxY(mxCell cell) {
        mxGeometry geo = cell.getGeometry();
        double res = 0;
        if (geo != null) {
            if (cell.isVertex()) {
                // vertex cell
                res = Math.max(geo.getY() + geo.getHeight(), res);
            } else {
                // edge cell
                if (cell.getSource() == null) {
                    res = Math.max(geo.getSourcePoint().getY(), res);
                }
                if (cell.getTarget() == null) {
                    res = Math.max(geo.getTargetPoint().getY(), res);
                }
            }
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            double tmpY = getMaxY(child);
            if (res < tmpY) {
                res = tmpY;
            }
        }
        return res;
    }
    public  int  getMaxId(mxCell root) {
        int maxId = 0;
        if (root == null) {
            return maxId;
        }
        if (root.getId() != null) {
            try {
                int tmpId = Integer.parseInt(root.getId());
                if (maxId < tmpId) {
                    maxId = tmpId;
                }
            } catch (NumberFormatException e) {
            }
        }
        for (int i = 0; i < root.getChildCount(); ++i) {
            int tmpId = getMaxId((mxCell) root.getChildAt(i));
            if (maxId < tmpId) {
                maxId = tmpId;
            }
        }
        return maxId;
    }
    private  Pair<Double, Double> insertCell(String type, double baseX, double baseY, BasicGraphEditor editor) {
        System.out.println("type: "+type);
        Pair<Double, Double> res = new Pair<>(0.0, 0.0);
        List<String> cells = CellTypeUtil.getCells(type);
        if (cells == null || cells.size() == 0) {
            return res;
        }
        String cellName = cells.get(0);
        mxCell originCell = editor.AllCellMap.get(cellName);
        mxGraph graph = editor.getGraphComponent().getGraph();
        Object[] cloneCells = graph.cloneCells(new mxCell[]{originCell});
        if (cloneCells == null || cloneCells.length == 0 || !(cloneCells[0] instanceof mxCell)) {
            return res;
        }
        mxCell cell = (mxCell) cloneCells[0];
        if (cell == null) {
            System.out.println("cell clone fail: " + cellName);
            return res;
        }

        mxGeometry geo = cell.getGeometry();
        geo.setX(baseX + 150);
        geo.setY(baseY + 100);
        if(geo.getY()<150) {
            geo.setY(150);
        }
        mxCell root = (mxCell) graph.getModel().getRoot();
        cell.setId((getMaxId(root) + 1) + "");
        System.out.println("getMaxId(root): "+getMaxId(root));
        root.getChildAt(0).insert(cell);
        newCreateCell.add(cell);

        res = new Pair<>(geo.getWidth() + 150, geo.getHeight() + 100);
        return res;
    }

    /********************************************************************************************/

    // 第六步：重新进行自动化资源绑定(包括资源文件和资源ID以及设备ID)
    // 若有两个相同的设备，则应该根据连接关系来绑定
    private void autoBindResource(GraphInterface<String> res_graph, Map<String, Integer> cellMap,
                                  mxCell cell, Set<String> hasBind) {
        if (cell.isVertex() && cell.getType() != null && cellMap.containsKey(cell.getType())) {
            cell.setOriginalResourceFile(filePath);
            cell.setBindResourceFile(filePath);
            Map<String, VertexInterface<String>> vertexs=res_graph.getVertices();
            Iterator<Map.Entry<String, VertexInterface<String>>> v_entry = vertexs.entrySet().iterator();
            while(v_entry.hasNext()){
                Map.Entry<String, VertexInterface<String>> entry = v_entry.next();
                String type = entry.getValue().getName();
                if(cell.getV()==null&&cell.getType().equals(type)&&!hasBind.contains(entry.getValue().getId())){
                    bindCellResource(cell,entry.getValue());
                    //editor.getGraphComponent().startEditingAtCell(cell, eventObject);
                    hasBind.add(entry.getValue().getId());
                }
            }
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            autoBindResource(res_graph, cellMap, child, hasBind);
        }
    }

    public static void bindCellResource(mxCell cell, VertexInterface<String> resource) {
        cell.setV(resource);
        cell.setVertextInfo(JSON.toJSONString(resource));
        System.out.println("setVertextInfo: "+cell.getVertextInfo());
        String hasproperty=null;
        String hasconnect=null;
        if(resource.getLink_info()!=null){
            for (Map.Entry<String, String> entry1 : resource.getLink_info().entrySet()) {
                if(entry1.getKey().equals("has_property")){
                    hasproperty=entry1.getValue();
                    //System.out.println("hasproperty:"+hasproperty);
                }
                if(entry1.getKey().equals("connect")){
                    hasconnect=entry1.getValue();
                    //System.out.println("hasconnect:"+hasconnect);
                }
            }
        }

        cell.setHasproperty(hasproperty);
        cell.setHasconnect(hasconnect);
        cell.setDeviceid(resource.getId());
        cell.setName(resource.getId());
        //cell.setHas_property(resource.getLink_info());
    }
    private  Map<String, Integer> calcMap(mxCell cell) {
        if (cell.isEdge()) {
            return null;
        }
        Map<String, Integer> res = new HashMap<>();
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            String type = child.getType();
            if (child.isVertex()) {
                res.put(type, res.containsKey(type) ? res.get(type) + 1 : 1);
            }
            Map<String, Integer> tmpMap = calcMap(child);
            if (tmpMap != null) {
                tmpMap.forEach((s, integer) -> {
                    res.put(s, res.containsKey(s) ? res.get(s) + integer : integer);
                });
            }
        }
        return res;
    }

    /********************************************************************************************/

    //第八步：补充图元之间的连线
    private void fillingEdge(BasicGraphEditor editor, GraphInterface<String> res_graph) {

        mxCell root=(mxCell) editor.getGraphComponent().getGraph().getModel().getRoot();

        Map<String, MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>>> edges=res_graph.getEdges();
        Iterator<Map.Entry<String, MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>>>>
                e_entry = edges.entrySet().iterator();

        while(e_entry.hasNext()) {

            Map.Entry<String, MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>>> entry = e_entry.next();
            MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>> e = entry.getValue();

            VertexInterface<String> beginVertex = e.getLeft();
            VertexInterface<String> endVertex = e.getRight();
            EdgeLink edgelink = e.getMiddle();
            String edge_name=edgelink.getName();
            //判断两个顶点中间是否有edge,如果有，则不用连了，如果没有，就连接
            mxCell begin_mx = getCellByV(root, beginVertex);
            mxCell end_mx = getCellByV(root, endVertex);
            mxCell edgelink_mx = new mxCell();

            if(!hasedge(begin_mx,end_mx,root)&&begin_mx!=null&&end_mx!=null){
                edgelink_mx.setEdge(true);
                System.out.println("need fill: " + begin_mx.getName() + "-" + end_mx.getName());
                mxGeometry mx_edge_geometry=new mxGeometry(
                        (begin_mx.getGeometry().getX()+end_mx.getGeometry().getX())/2,
                        (begin_mx.getGeometry().getY()+end_mx.getGeometry().getY())/2, 200, 200);

                String res= showGraph.getRelativeLocation(begin_mx,end_mx); //得到两个图元的相对位置
                //System.out.println("res:"+res);
                if(res.equals("directlyDown")){
                    if(edge_name.equals("data_trans")){
                        edgelink_mx.setStyle(editor.AllCellMap.get("data_vertical").getStyle());
                        edgelink_mx.setName("data_vertical");
                    }else{
                        edgelink_mx.setStyle(editor.AllCellMap.get("fiber_vertical").getStyle());
                        edgelink_mx.setName("fiber_vertical");
                    }
                    edgelink_mx.setSource(showGraph.getPorts(begin_mx).get("up"));
                    edgelink_mx.setTarget(showGraph.getPorts(end_mx).get("down"));
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(showGraph.getPorts(begin_mx).get("up").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                    //        begin_mx.getGeometry().getY()+(int)(showGraph.getPorts(begin_mx).get("up").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(showGraph.getPorts(end_mx).get("down").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                    //        end_mx.getGeometry().getY()+(int)(showGraph.getPorts(end_mx).get("down").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("directlyUp")){
                    if(edge_name.equals("data_trans")){
                        edgelink_mx.setStyle(editor.AllCellMap.get("data_vertical").getStyle());
                        edgelink_mx.setName("data_vertical");
                    }else{
                        edgelink_mx.setStyle(editor.AllCellMap.get("fiber_vertical").getStyle());
                        edgelink_mx.setName("fiber_vertical");
                    }
                    edgelink_mx.setSource(showGraph.getPorts(begin_mx).get("down"));
                    edgelink_mx.setTarget(showGraph.getPorts(end_mx).get("up"));
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(showGraph.getPorts(begin_mx).get("down").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                    //        begin_mx.getGeometry().getY()+(int)(showGraph.getPorts(begin_mx).get("down").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(showGraph.getPorts(end_mx).get("up").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                    //        end_mx.getGeometry().getY()+(int)(showGraph.getPorts(end_mx).get("up").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("directlyRight")){
                    if(edge_name.equals("data_trans")){
                        edgelink_mx.setStyle(editor.AllCellMap.get("data_horizontal").getStyle());
                        edgelink_mx.setName("data_horizontal");
                    }else{
                        edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                        edgelink_mx.setName("fiber_horizontal");
                    }
                    edgelink_mx.setSource(showGraph.getPorts(begin_mx).get("left"));
                    edgelink_mx.setTarget(showGraph.getPorts(end_mx).get("right"));
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(showGraph.getPorts(begin_mx).get("left").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                    //        begin_mx.getGeometry().getY()+(int)(showGraph.getPorts(begin_mx).get("left").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(showGraph.getPorts(end_mx).get("right").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                    //        end_mx.getGeometry().getY()+(int)(showGraph.getPorts(end_mx).get("right").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("directlyLeft")){
                    if(edge_name.equals("data_trans")){
                        edgelink_mx.setStyle(editor.AllCellMap.get("data_horizontal").getStyle());
                        edgelink_mx.setName("data_horizontal");
                    }else{
                        edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                        edgelink_mx.setName("fiber_horizontal");
                    }
                    edgelink_mx.setSource(showGraph.getPorts(begin_mx).get("right"));
                    edgelink_mx.setTarget(showGraph.getPorts(end_mx).get("left"));
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(showGraph.getPorts(begin_mx).get("right").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                    //        begin_mx.getGeometry().getY()+(int)(showGraph.getPorts(begin_mx).get("right").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    //mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(showGraph.getPorts(end_mx).get("left").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                    //        end_mx.getGeometry().getY()+(int)(showGraph.getPorts(end_mx).get("left").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("rightDown")||res.equals("rightUp")){
                    if(edge_name.equals("data_trans")){
                        edgelink_mx.setStyle(editor.AllCellMap.get("data_horizontal").getStyle());
                        edgelink_mx.setName("data_horizontal");
                    }else{
                        edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                        edgelink_mx.setName("fiber_horizontal");
                    }
                    edgelink_mx.setSource(showGraph.getPorts(begin_mx).get("left"));
                    edgelink_mx.setTarget(showGraph.getPorts(end_mx).get("right"));
                    /*mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(showGraph.getPorts(begin_mx).get("left").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(showGraph.getPorts(begin_mx).get("left").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(showGraph.getPorts(end_mx).get("right").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(showGraph.getPorts(end_mx).get("right").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);*/
                }
                if(res.equals("leftDown")||res.equals("leftUp")){
                    if(edge_name.equals("data_trans")){
                        edgelink_mx.setStyle(editor.AllCellMap.get("data_horizontal").getStyle());
                        edgelink_mx.setName("data_horizontal");
                    }else{
                        edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                        edgelink_mx.setName("fiber_horizontal");
                    }
                    edgelink_mx.setSource(showGraph.getPorts(begin_mx).get("right"));
                    edgelink_mx.setTarget(showGraph.getPorts(end_mx).get("left"));
                   /* mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(showGraph.getPorts(begin_mx).get("right").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(showGraph.getPorts(begin_mx).get("right").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(showGraph.getPorts(end_mx).get("left").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(showGraph.getPorts(end_mx).get("left").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);*/
                }

                mx_edge_geometry.setRelative(true);
                edgelink_mx.setGeometry(mx_edge_geometry);
                edgelink_mx.setId(getMaxId(root)+1+ "");
                if(edge_name.equals("data_trans")){
                    edgelink_mx.setType("DataEdge");
                }else{
                    edgelink_mx.setType("FiberEdge");
                }
                root.getChildAt(0).insert(edgelink_mx);
            }
        }
    }

    public boolean hasedge(mxCell begin, mxCell end, mxCell root){
        boolean result=false;
        if (root == null||begin==null||end==null) {
            return false;
        }
        if(root.isEdge()){
            if(root.getSource().getId()==begin.getId()&&root.getTarget().getId()==end.getId()){
                return true;
            }
            if(root.getSource().getId()==end.getId()&&root.getTarget().getId()==begin.getId()){
                return true;
            }
            if(root.getSource().getParent().getId()!=null&&root.getTarget().getParent().getId()!=null&&
                    ((root.getSource().getParent().getId()==begin.getId()&&root.getTarget().getParent().getId()==end.getId())||
                            (root.getSource().getParent().getId()==end.getId()&&root.getTarget().getParent().getId()==begin.getId()))){
                return true;
            }
            if(root.getTarget().getParent().getId()!=null&&((root.getSource().getId()==begin.getId()&&root.getTarget().getParent().getId()==end.getId())||
                    (root.getSource().getId()==end.getId()&&root.getTarget().getParent().getId()==begin.getId()))){
                return true;
            }
            if(root.getSource().getParent().getId()!=null&&((root.getSource().getParent().getId()==begin.getId()&&root.getTarget().getId()==end.getId())||
                    (root.getSource().getParent().getId()==end.getId()&&root.getTarget().getId()==begin.getId()))){
                return true;
            }
        }
        for (int i = 0; i < root.getChildCount(); ++i) {
            result=hasedge(begin,end,(mxCell) root.getChildAt(i));
            if(result){
                return true;
            }
        }
        return result;
    }

    public mxCell getCellByV(mxCell root, VertexInterface<String> v){
        if (root == null || v == null) {
            return null;
        }
        if (root.getV()==v) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); ++i) {
            mxCell child = (mxCell) root.getChildAt(i);
            mxCell res = getCellByV(child, v);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    /********************************************************************************************/

    //第九步：并绑定连线的资源信息
    private void autoBindEdge(GraphInterface<String> res_graph, mxCell cell, Set<String> hasBind) {

        if (cell == null) {
            return;
        }
        if (cell.isEdge() && cell.getSource() != null && cell.getTarget() != null) {

            cell.setOriginalResourceFile(filePath);
            cell.setBindResourceFile(filePath);

            Map<String, MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>>> edges=res_graph.getEdges();
            Iterator<Map.Entry<String, MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>>>>
                    e_entry= edges.entrySet().iterator();
            while(e_entry.hasNext()){

                Map.Entry<String, MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>>> entry = e_entry.next();
                MutableTriple<VertexInterface<String>, EdgeLink, VertexInterface<String>> e=entry.getValue();

                mxCell targetCell = getCellById(root,cell.getTarget().getId());
                mxCell sourceCell = getCellById(root,cell.getSource().getId());
                if(targetCell == null || sourceCell == null) continue;
                if("port".equals(targetCell.getName())){
                    targetCell = getCellById(root,targetCell.getParent().getId());
                }
                if("port".equals(sourceCell.getName())){
                    sourceCell = getCellById(root,sourceCell.getParent().getId());
                }
                if(cell.getV()==null&&targetCell.getName().equals(e.getRight().getId())&&sourceCell.getName().equals(e.getLeft().getId())
                        &&!hasBind.contains(e.getMiddle().getId())){
                    bindEdgeResource(cell,e.getMiddle());
                    hasBind.add(e.getMiddle().getId());
                }
                if(cell.getV()==null&&targetCell.getName().equals(e.getLeft().getId())&&sourceCell.getName().equals(e.getRight().getId())
                        &&!hasBind.contains(e.getMiddle().getId())){
                    bindEdgeResource(cell,e.getMiddle());
                    hasBind.add(e.getMiddle().getId());
                }
            }
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            autoBindEdge(res_graph, child, hasBind);
        }
    }

    public mxCell getCellById(mxCell root, String id){
        if (root == null || id == null) {
            return null;
        }
        if (root.getId()==id) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); ++i) {
            mxCell child = (mxCell) root.getChildAt(i);
            mxCell res = getCellById(child, id);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public static void bindEdgeResource(mxCell cell, EdgeLink resource) {
        cell.setEdgeLink(resource);
        cell.setEdgeLinkInfo(JSON.toJSONString(resource));
        System.out.println("setEdgeLinkInfo: "+cell.getEdgeLinkInfo());
    }

    /********************************************************************************************/

    //第十步：显示监控项图元
    public void fillingMonitorCells(){
        initCell_property();
        insertPropertyCell();
    }
    public void initCell_property(){
        cell_property.clear();
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
                        for (OwlObject obj : entrys.getValue()) {
                            if(obj.visible){
                                String name=obj.id;
                                cell_property.put(name,obj);
                            }
                        }
                    }
                }
            }
        }
    }

    //应该遍历graph,获取图元的位置，设计监控项图元的摆放问题，每一个监控属性是一个obj
    public  void insertPropertyCell(){
        mxCell root = (mxCell) graph.getModel().getRoot();
        addPropertyCell(root,graph);
        editor.revalidate();
    }

    private  void addPropertyCell(mxCell cell,mxGraph graph) {
        if (cell == null||cell.getChildCount()==0) {
            return;
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            if(child.isVertex()&&child.getV()!=null){
                //如果是顶点，看有没有属性,得到图元的位置
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
                    String [] sortedArr = sortStringArray(arr);
                    for(int r=0;r<sortedArr.length;r++){
                        //arr[r].trim()是属性名称id  owlObject.type.id是属性类型  unit是属性单位
                        OwlObject owlObject=cell_property.get(sortedArr[r].trim());
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
                        //System.out.println("owlObject.type.parentClass.id: "+owlObject.type.parentClass.id);
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
                            nameMonitorCell.setMonitor_device_name(child.getDeviceid());
                            nameMonitorCell.setMonitor_property_name(arr[r].trim());
                            nameMonitorCell.setMonitor_property_type(owlObject.type.id);
                            nameMonitorCell.setMonitor_property_kind(owlObject.type.parentClass.id);
                            nameMonitorCell.setMonitor_unit(unit);
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
                            dataMonitorCell.setMonitor_property_kind(owlObject.type.parentClass.id);
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
            graph.repaint();
            addPropertyCell(child,graph);
        }
    }

    private static String[] sortStringArray(String[] keys){
         for (int i = 0; i < keys.length - 1; i++) {
                 for (int j = 0; j < keys.length - i -1; j++) {
                         String pre = keys[j];
                         String next = keys[j + 1];
                         if(isMoreThan(pre, next)){
                                 String temp = pre;
                                 keys[j] = next;
                                 keys[j+1] = temp;
                             }
                     }
             }
         return keys;
     }

    private static boolean isMoreThan(String pre, String next){
         if(null == pre || null == next || "".equals(pre) || "".equals(next)){
                return false;
             }
         char[] c_pre = pre.toCharArray();
         char[] c_next = next.toCharArray();
         int minSize = Math.min(c_pre.length, c_next.length);
         for (int i = 0; i < minSize; i++) {
                 if((int)c_pre[i] > (int)c_next[i]){
                         return true;
                     }else if((int)c_pre[i] < (int)c_next[i]){
                         return false;
                    }
             }
         if(c_pre.length > c_next.length){
                 return true;
             }
         return false;
     }
}
