package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlResourceUtil;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.findKind;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 16:06 2018/12/12
 * @Modify By:
 */
public class GraphPanel extends JPanel {

    private Map<String, OwlObject> objMap;
    private Map<String, OwlObject> main_objMap=new HashMap<>();
    private Map<String,coordinateClass> data=new HashMap<>();
    private int radius;

    public GraphPanel(Map<String, OwlObject> objMap) {

        radius=80;
        this.objMap=objMap;
        init_main_objMap(this.objMap,main_objMap);
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int height=this.getHeight();
        int width=this.getWidth();
        //先判断个数
        int num= OwlResourceUtil.getFOINum(main_objMap);
        System.out.println("num:"+num);
        //分网格
        int part_num=getPart_num(num);
        int part_height=height/part_num;
        int part_width=width/part_num;

        Graphics2D g2d = (Graphics2D)g;
        //放置每个obj的位置
        int i=0;
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            //矩形左上角的坐标
            int x=part_width*(i%part_num)+part_width/3;
            int y=part_height*(i/part_num)+part_height/5;
            //绘制圆
            g2d.setColor(Color.darkGray);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x,y,radius,radius);
            data.put(entry.getValue().id,new coordinateClass(x,y));
            //绘制字符串
            g.setFont(new Font("楷体", Font.PLAIN, 15));
            g2d.drawString(entry.getValue().id,x-20,y+radius+15);

            i++;
        }

        //将连接关系连接起来
        //根据坐标来找绘制的图形
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            entry.getValue().objAttrs.forEach((objAttr, objSet) -> {
                if(objAttr.id.equals("connect")){
                    objSet.forEach(obj2 -> {
                        if(obj2.visible) {
                            coordinateClass obj1_coordinate = data.get(entry.getValue().id);
                            coordinateClass obj2_coordinate = data.get(obj2.id);
                            g2d.drawLine(obj1_coordinate.getX() + radius/2, obj1_coordinate.getY() + radius/2,
                                    obj2_coordinate.getX() + radius/2, obj2_coordinate.getY() + radius/2);
                        }
                    });
                }
            });
        }

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
