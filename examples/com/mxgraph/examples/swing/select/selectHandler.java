package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.owl.OwlResourceUtil;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.findKind;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 17:14 2018/12/26
 * @Modify By:
 */
public class selectHandler extends JPanel {

    private BasicGraphEditor editor;
    private OwlResourceData origin_owlResourceData;
    private OwlResourceData new_owlResourceData;
    private Map<String, OwlObject> origin_objMap;
    private Map<String, OwlObject> new_objMap;
    private Map<String, OwlObject> main_objMap=new HashMap<>();
    private Map<String, Object> vertex_objMap=new HashMap<>();

    private int height=700;
    private int width=1000;

    private final mxGraph graph ;
    private Object parent ;
    private final mxGraphComponent graphComponent;

    public selectHandler(BasicGraphEditor editor){
        //super("资源选择模板框");
        this.editor=editor;
        this.origin_owlResourceData=this.editor.getOrigin_owlResourceData();
        this.new_owlResourceData=this.editor.getNew_owlResourceData();

        this.origin_objMap=this.origin_owlResourceData.objMap;
        this.new_objMap=this.new_owlResourceData.objMap;

        this.graph = new mxGraph();
        this.parent = graph.getDefaultParent();

        graphComponent = new mxGraphComponent(graph);
        graphComponent.setSize(800,600);
        this.height=graphComponent.getHeight();
        this.width=graphComponent.getWidth();
        graphComponent.setBorder(null);

        this.add(graphComponent);


        graph.getModel().beginUpdate();
        try
        {
            paint(new_objMap);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

    }

    public void paint(Map<String, OwlObject> new_objMap){
        init_main_objMap(new_objMap,main_objMap);

        //先判断个数
        int num= OwlResourceUtil.getFOINum(main_objMap);
        System.out.println("num:"+num);
        //分网格
        int part_num=getPart_num(num);
        int part_height=height/part_num;
        int part_width=width/part_num;

        //放置每个obj的位置
        int i=0;
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            //矩形左上角的坐标
            //int x=part_width*(i%part_num)+part_width/3;
            //int y=part_height*(i/part_num)+part_height/5;
            int x=part_width*(i%part_num)+part_width/5;
            int y=part_height*(i/part_num)+part_height/5;
            //绘制圆
            //Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,30);
            System.out.println("x:"+x+"   y:"+y);
            Object obj=graph.insertVertex(parent, null, entry.getValue().id, x, y, 140,80,
                    "shape=ellipse;perimeter=ellipsePerimeter");
            vertex_objMap.put(entry.getValue().id,obj);
            i++;
        }

        //将连接关系连接起来
        //根据坐标来找绘制的图形
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            entry.getValue().objAttrs.forEach((objAttr, objSet) -> {
                if(objAttr.id.equals("connect")){
                    objSet.forEach(obj2 -> {
                        if(obj2.visible) {
                            graph.insertEdge(parent, null, "Edge",
                                    vertex_objMap.get(entry.getValue().id), vertex_objMap.get(obj2.id));

                        }
                    });
                }
            });
        }

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {

            public void mouseReleased(MouseEvent e)
            {
                Object cell = graphComponent.getCellAt(e.getX(), e.getY());

                if (cell != null)
                {
                    System.out.println("cell="+graph.getLabel(cell));
                }
            }
        });
    }

    public Map<String, OwlObject> init_main_objMap(Map<String, OwlObject> objMap,Map<String, OwlObject> main_objMap){
        for (Map.Entry<String, OwlObject> entry : objMap.entrySet()) {
            if(findKind(entry.getValue().type).equals("FeatureOfInterest")
                    &&entry.getValue().visible){
                main_objMap.put(entry.getKey(),entry.getValue());
            }
        }
        return main_objMap;
    }

    public int getPart_num(int num){
        int n;
        double m=Math.sqrt(num);
        System.out.println("m:"+m);
        n=(int)m;
        System.out.println("n:"+n);
        if(n==m){
            return n;
        }else{
            return n+1;
        }
    }
}
