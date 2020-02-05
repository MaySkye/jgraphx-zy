package com.mxgraph.examples.swing.match;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.graph.EdgeLink;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlObjectAttribute;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.util.*;

import static com.mxgraph.examples.swing.match.ResMatchCore.calcStaticMap;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:36 2018/11/9
 * @Modify By:
 */
public class ModifyTemplateCore {
    private String filePath; //对应的模板路径
    private String filePath1;
    private Set<mxCell> newCreateCell;
    private static Map<String, String> ConnectorToEdgeMap = new HashMap<>();
    private BasicGraphEditor editor;
    static {
        /*
        * key:边的名称name  value:边的类型type
        * name是唯一的，type不一定是唯一的
        * */
        ConnectorToEdgeMap.put("fiber_vertical", "FiberEdge");
        ConnectorToEdgeMap.put("fiber_horizontal", "FiberEdge");
    }

    public ModifyTemplateCore(String filePath) {

        this.filePath=filePath;

    }

    public  void postProcess(GraphInterface<String> res_graph, BasicGraphEditor editor, String filePath1) {
        System.out.println("开始执行postProcess......");
        this.filePath1=filePath1;
        this.editor=editor;
        //对组态图进行调整
        mxGraph graph = editor.getGraphComponent().getGraph();
        mxCell root = (mxCell) graph.getModel().getRoot();
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
        removeRedundantConnector(root,root, resourceStaticData.connMap,removeCells,editor);
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
    private void removeRedundantCell(mxCell cell, Map<String, Integer> cellMap, List<mxCell> removeCells,BasicGraphEditor editor) {
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
    private void removeRedundantMultiCell(mxCell cell, Map<String, Integer> cellMap, List<mxCell> removeCells,BasicGraphEditor editor) {
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
    private void removeRedundantConnector(mxCell root,mxCell cell, Map<ResourceStaticData.MTriple<String, String, String>, Integer> connMap,List<mxCell> removeCells,BasicGraphEditor editor) {
        if (cell == null || removeCells == null || removeCells.contains(cell)) {
            return;
        }
        /*删除图元的情况*/
        if (cell.isEdge()&&ConnectorToEdgeMap.containsKey(cell.getName()) && (cell.getTarget()==null||cell.getSource()==null)) {
            System.out.println("will remove: " + cell.getName());
            removeCells.add(cell);
        }
        if (cell.isEdge()&&ConnectorToEdgeMap.containsKey(cell.getName()) && (cell.getTarget()!=null&&cell.getSource()!=null)) {
            mxCell firstcell=getCellById(root,cell.getSource().getId());
            mxCell thirdcell=getCellById(root,cell.getTarget().getId());
            if(firstcell.getType().equals("Port")){
                firstcell=getCellById(root,cell.getSource().getParent().getId());
            }
            if(thirdcell.getType().equals("Port")){
                thirdcell=getCellById(root,cell.getTarget().getParent().getId());
            }
            ResourceStaticData.MTriple<String, String, String> temp=new ResourceStaticData.MTriple<>(firstcell.getType(),"connect",thirdcell.getType());
            if(!connMap.containsKey(temp)){
                System.out.println("will remove1: " + cell.getName());
                removeCells.add(cell);
            }
        }
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            removeRedundantConnector(root,child, connMap,removeCells,editor);
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

    //这里的id指的是数字
   private mxCell getCellById(mxCell root,String id){
       if (root == null || id == null) {
           return null;
       }
       if (root.getId().equals(id)) {
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
    private void fillingCell(ResourceStaticData resourceStaticData,BasicGraphEditor editor) {
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
    public    int   getMaxId(mxCell root) {
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
    private  Pair<Double, Double> insertCell(String type, double baseX, double baseY,BasicGraphEditor editor) {
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
            cell.setOriginalResourceFile(filePath1);
            cell.setBindResourceFile(filePath1);
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
        String hasproperty=null;
        String hasconnect=null;
        if(resource.getLink_info()!=null){
            for (Map.Entry<String, String> entry1 : resource.getLink_info().entrySet()) {
                if(entry1.getKey().equals("has_property")){
                    hasproperty=entry1.getValue();
                    System.out.println("hasproperty:"+hasproperty);
                }
                if(entry1.getKey().equals("connect")){
                    hasconnect=entry1.getValue();
                    System.out.println("hasconnect:"+hasconnect);
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

        Map<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> edges=res_graph.getEdges();
        Iterator<Map.Entry<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>>>
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
                mxGeometry mx_edge_geometry=new mxGeometry(
                        (begin_mx.getGeometry().getX()+end_mx.getGeometry().getX())/2,
                        (begin_mx.getGeometry().getY()+end_mx.getGeometry().getY())/2, 200, 200);

                String res=showGraph.getRelativeLocation(begin_mx,end_mx); //得到两个图元的相对位置
                System.out.println("res:"+res);
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
    public boolean hasedge(mxCell begin,mxCell end,mxCell root){
        boolean result=false;
        if (root == null||begin==null||end==null) {
            return false;
        }
        if(root.isEdge()&&
                ((root.getSource().getParent()==begin&&root.getTarget().getParent()==end)||
                (root.getSource().getParent()==end&&root.getTarget().getParent()==begin))){
            return true;
        }

        for (int i = 0; i < root.getChildCount(); ++i) {
            result=hasedge(begin,end,(mxCell) root.getChildAt(i));
            if(result){
                return true;
            }
        }
        return result;
    }

    public mxCell getCellByV(mxCell root,VertexInterface<String> v){
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
    private void autoBindEdge(GraphInterface<String> res_graph, mxCell cell,Set<String> hasBind) {
        if (cell == null) {
            return;
        }
        if (cell.isEdge() && cell.getSource() != null && cell.getTarget() != null) {

            cell.setOriginalResourceFile(filePath1);
            cell.setBindResourceFile(filePath1);

            Map<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> edges=res_graph.getEdges();
            Iterator<Map.Entry<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>>>
                    e_entry= edges.entrySet().iterator();
            while(e_entry.hasNext()){

                Map.Entry<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> entry = e_entry.next();
                MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>> e=entry.getValue();

                if(cell.getV()==null&&cell.getTarget()==e.getLeft()&&cell.getSource()==e.getRight()
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

    public static void bindEdgeResource(mxCell cell, EdgeLink resource) {
        cell.setEdgeLink(resource);
    }
    //继续......
}
