package com.mxgraph.examples.swing.graph;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.findKind;
import static com.mxgraph.examples.swing.owl.OwlResourceUtil.parseResourceFile;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:04 2018/8/7
 * @Modify By:
 */
public class showGraph {


    /*将解析数据转化为图结构存储*/
    public static GraphInterface<String> createGraph(OwlResourceData owlResourceData){
        //声明一个图结构对象
        GraphInterface<String> graph=new DirectedGraph<>();
        //System.out.println("Graph is empty:"+graph.isEmpty());

        //循环找出设备顶点，存入graph
        //System.out.println("Adding vertexs...");
        System.out.println("---------------------------- graph info ---------------------------");
        saveResourceVerties(graph,owlResourceData);
        //System.out.println("Number of graph's vertex="+graph.getNumberOfVertices());

        //循环找出设备的边，存入graph
        //System.out.println("Adding edges......");
        saveResourceEdges(graph,owlResourceData);
        //System.out.println("Number of graph's edge="+graph.getNumberOfEdges());
        System.out.println("--------------------------- graph info end ---------------------------");
        return  graph;
    }

    /*将图结构转化为图元：顶点，边，节点&&绘制*/
    public static Map<String, mxCell> drawMxCell(GraphInterface<String> graph,BasicGraphEditor editor,String filePath){
        if (editor == null) {
            return null;
        }
        //得到画布mxgraph
        mxGraph mxgraph = editor.getGraphComponent().getGraph();
        //用来存储绘图时新建的图元
        Map<String, mxCell> createCell=new HashMap<>();
        //开始更新
        mxgraph.getModel().beginUpdate();
        //得到图的顶点和边
        Map<String, VertexInterface<String>> vertexs=graph.getVertices();
        Map<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> edges=graph.getEdges();
        //把顶点和边再输出一遍
        graph.printVertices(vertexs);
        graph.printEdges(edges);
        //绘制--通过edges
        Iterator<Map.Entry<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>>>
                e_entries1 = edges.entrySet().iterator();

        while(e_entries1.hasNext()){

            Map.Entry<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> entry = e_entries1.next();
            MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>> e=entry.getValue();

            VertexInterface<String> beginVertex=e.getLeft();
            VertexInterface<String> endVertex=e.getRight();
            EdgeLink edgelink=e.getMiddle();

            mxCell begin_mx=new mxCell() ;
            mxCell end_mx=new mxCell() ;
            mxCell edgelink_mx=new mxCell();


            if(!createCell.containsKey(beginVertex.getLabel())){
                mxCell root = (mxCell) mxgraph.getModel().getRoot();

                Object[] cloneCells=mxgraph.cloneCells(new mxCell[]{editor.AllCellMap.get(beginVertex.getName())});
                System.out.println("beginVertex.getName():"+beginVertex.getName()+"  "+beginVertex.getId());
                System.out.println("cloneCells:"+cloneCells);
                begin_mx= (mxCell) cloneCells[0];
                begin_mx.setBindResourceFile(filePath);
                begin_mx.setOriginalResourceFile(filePath);
                begin_mx.setV(beginVertex);
                begin_mx.setVertex(true);
                mxGeometry geometry=new mxGeometry();
                if(createCell.isEmpty())
                {geometry = new mxGeometry(getMaxX(root)+ 250, getMaxY(root) + 200, 100, 100);}
                else{
                    {geometry = new mxGeometry(getMaxX(root), getMaxY(root) + 200, 100, 100);}
                }
                begin_mx.setGeometry(geometry);
                begin_mx.setId(getMaxId(root)+1+ "");
                createCell.put(beginVertex.getLabel(),begin_mx);
                root.getChildAt(0).insert(begin_mx);
            }else{
                begin_mx=createCell.get(beginVertex.getLabel());
            }

            System.out.println(begin_mx.getId()+":"+begin_mx.getGeometry().getX()+"--"+begin_mx.getGeometry().getY());

            if(!createCell.containsKey(endVertex.getLabel())){
                mxCell root = (mxCell) mxgraph.getModel().getRoot();
                //end_mx.setDeviceid(endVertex.getLabel());
                Object[] cloneCells1=mxgraph.cloneCells(new mxCell[]{editor.AllCellMap.get(endVertex.getName())});
                end_mx= (mxCell) cloneCells1[0];
                end_mx.setBindResourceFile(filePath);
                end_mx.setOriginalResourceFile(filePath);
                end_mx.setV(endVertex);
                end_mx.setVertex(true);
                mxGeometry geometry = new mxGeometry(begin_mx.getGeometry().getX() , begin_mx.getGeometry().getY() + 200, 100, 100);
                end_mx.setGeometry(geometry);
                end_mx.setId(getMaxId(root)+1+ "");
                createCell.put(endVertex.getLabel(),end_mx);
                root.getChildAt(0).insert(end_mx);
            }else{
                end_mx=createCell.get(endVertex.getLabel());
            }
            System.out.println(end_mx.getValue()+":"+end_mx.getGeometry().getX()+"--"+end_mx.getGeometry().getY());

            if(!createCell.containsKey(edgelink.getId())){
                mxCell root = (mxCell) mxgraph.getModel().getRoot();
                edgelink_mx.setEdge(true);
                edgelink_mx.setEdgeLink(edgelink);

                //edgelink_mx.setValue(edgelink.getId());
                //edgelink_mx.setStyle(editor.AllCellMap.get("fiber_vertical").getStyle());
                //edgelink_mx.setStyle("strokeWidth=6");
                mxGeometry mx_edge_geometry=new mxGeometry(
                        (begin_mx.getGeometry().getX()+end_mx.getGeometry().getX())/2,
                        (begin_mx.getGeometry().getY()+end_mx.getGeometry().getY())/2, 200, 200);

                String res=getRelativeLocation(begin_mx,end_mx);
                System.out.println("res:"+res);
                if(res.equals("directlyDown")){
                    edgelink_mx.setStyle(editor.AllCellMap.get("fiber_vertical").getStyle());
                    mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(getPorts(begin_mx).get("up").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(getPorts(begin_mx).get("up").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(getPorts(end_mx).get("down").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(getPorts(end_mx).get("down").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("directlyUp")){
                    edgelink_mx.setStyle(editor.AllCellMap.get("fiber_vertical").getStyle());
                    mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(getPorts(begin_mx).get("down").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(getPorts(begin_mx).get("down").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(getPorts(end_mx).get("up").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(getPorts(end_mx).get("up").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("directlyRight")){
                    edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                    mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(getPorts(begin_mx).get("left").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(getPorts(begin_mx).get("left").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(getPorts(end_mx).get("right").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(getPorts(end_mx).get("right").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("directlyLeft")){
                    edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                    mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(getPorts(begin_mx).get("right").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(getPorts(begin_mx).get("right").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(getPorts(end_mx).get("left").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(getPorts(end_mx).get("left").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("rightDown")||res.equals("rightUp")){
                    edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                    mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(getPorts(begin_mx).get("left").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(getPorts(begin_mx).get("left").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(getPorts(end_mx).get("right").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(getPorts(end_mx).get("right").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }
                if(res.equals("leftDown")||res.equals("leftUp")){
                    edgelink_mx.setStyle(editor.AllCellMap.get("fiber_horizontal").getStyle());
                    mx_edge_geometry.setTerminalPoint(new mxPoint(begin_mx.getGeometry().getX()+(int)(getPorts(begin_mx).get("right").getGeometry().getX()*begin_mx.getGeometry().getWidth()),
                            begin_mx.getGeometry().getY()+(int)(getPorts(begin_mx).get("right").getGeometry().getY()*begin_mx.getGeometry().getHeight())), true);
                    mx_edge_geometry.setTerminalPoint(new mxPoint(end_mx.getGeometry().getX()+(int)(getPorts(end_mx).get("left").getGeometry().getX()*end_mx.getGeometry().getWidth()),
                            end_mx.getGeometry().getY()+(int)(getPorts(end_mx).get("left").getGeometry().getY()*end_mx.getGeometry().getHeight())), false);
                }

                mx_edge_geometry.setRelative(true);
                edgelink_mx.setGeometry(mx_edge_geometry);
                edgelink_mx.setId(getMaxId(root)+1+ "");
                createCell.put(edgelink.getId(),edgelink_mx);
                root.getChildAt(0).insert(edgelink_mx);

            }else{
                edgelink_mx=createCell.get(edgelink.getId());
            }
        }

        mxgraph.refresh();
        mxgraph.getModel().endUpdate();
        System.out.println("num:"+createCell.size());
        return createCell;
    }

    private static int getMaxId(mxCell root) {
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

    private static double getMaxX(mxCell cell) {
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

    private static double getMaxY(mxCell cell) {
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

    //判断两个图元的相对位置
    public static String getRelativeLocation(mxCell cell1,mxCell cell2){
        double x1=cell1.getGeometry().getX();
        double y1=cell1.getGeometry().getY();
        double x2=cell2.getGeometry().getX();
        double y2=cell2.getGeometry().getY();

        if(x1==x2&&y1>y2){
            //cell1在cell2的正下方
            return "directlyDown";
        }
        else if(x1==x2&&y1<y2){
            //cell1在cell2的正上方
            return "directlyUp";
        }else if(x1>x2&&y1==y2){
            //cell1在cell2的正右方
            return "directlyRight";
        }else if(x1<x2&&y1==y2){
            //cell1在cell2的正左方
            return "directlyLeft";
        }
        else if(x1>x2&&y1>y2){
            //cell1在cell2的右下方
            return "rightDown";
        }
        else if(x1<x2&&y1>y2){
            //cell1在cell2的左下方
            return "leftDown";
        }
        else if(x1>x2&&y1<y2){
            //cell1在cell2的右上方
            return "rightUp";
        }
        else
            //cell1在cell2的左上方
            return "leftUp";

    }

    public static String readFile(String path) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder buff = new StringBuilder(1024);
        String line = br.readLine();
        while (line != null) {
            buff.append(line);
            buff.append('\n');
            line = br.readLine();
        }
        return buff.toString();
    }

    public static Map<String,mxCell> getPorts(mxCell cell){
        Map<String,mxCell> map=new HashMap<String,mxCell>();
        int count=cell.getChildCount();
        for(int i=0;i<count;i++){
            mxCell mx= (mxCell) cell.getChildAt(i);
            if(!map.containsKey(mx.getLocation())){
            map.put(mx.getLocation(),mx);}
        }
        return map;
    }

    //循环找出设备顶点，存入graph
    public static void saveResourceVerties(GraphInterface<String> graph,OwlResourceData owlResourceData){
        owlResourceData.objMap.forEach((uri, obj) -> {
            if(( findKind(obj.type).equals("FeatureOfInterest"))
                    &&obj.visible==true){
                //graph.addVertex(obj.id);  //添加顶点
                VertexInterface<String> v=new Vertex<>(obj.id);
                v.setName(obj.type.id);
                v.setId(obj.id);
                v.setLabel(obj.id);
                v.setUrl(obj.uri);
                //解析它的基本信息，对象信息和属性信息
                Map<String,String> link_info_map=v.getLink_info();
                link_info_map.clear();
                obj.objAttrs.forEach((objAttr, objSet) -> {
                    objSet.forEach(obj2 -> {

                        if(obj2.visible==true){
                            System.out.println(obj.id+"->" + objAttr.id + "->"+obj2.id);
                            if(link_info_map.get(objAttr.id )!=null){
                                String str=link_info_map.get(objAttr.id );
                                str=str+"; "+obj2.id;
                                link_info_map.put(objAttr.id ,str);
                            }else{
                                link_info_map.put(objAttr.id ,obj2.id);
                            }
                        }

                    });
                });

                Map<String,String> data_info_map=v.getData_info();
                data_info_map.clear();
                obj.dataAttrs.forEach((objAttr, objSet) -> {
                    objSet.forEach(obj2 -> {
                        System.out.println(obj.id+"->" + objAttr.id + "->"+obj2);
                        if(data_info_map.get(objAttr.id )!=null){
                            String str=data_info_map.get(objAttr.id );
                            str=str+"; "+obj2.toString();
                            data_info_map.put(objAttr.id ,str);
                        }else{
                            data_info_map.put(objAttr.id ,obj2.toString());
                        }
                    });
                });
                //System.out.println("-----------------------------------------------");

                graph.addV(v);
            }
        });
    }
    //循环找出连接边，存入graph
    public static void saveResourceEdges(GraphInterface<String> graph,OwlResourceData owlResourceData){
        owlResourceData.objMap.forEach((uri, obj) -> {
            String id=obj.id;
            if(obj.visible==true&&(findKind(obj.type).equals("FeatureOfInterest"))
            ){
            obj.objAttrs.forEach((objAttr, objSet) -> {
                if(objAttr.id.equals("connect")||objAttr.id.equals("data_trans")){
                    objSet.forEach(obj2 -> {
                        if(obj2.visible==true){
                            EdgeLink edgelink=new EdgeLink();
                            edgelink.setName(objAttr.id);
                            edgelink.setId(id+"--"+objAttr.id+"--"+obj2.id);
                            //System.out.println(id+"->" + objAttr.id + "->"+obj2.id);
                            graph.addEdge(id,obj2.id,edgelink);
                        }
                    });
                }
            });
            }
        });
    }

}
