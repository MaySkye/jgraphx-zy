package com.mxgraph.examples.swing.util;

import com.mxgraph.examples.swing.select.coordinateClass;
import com.mxgraph.model.mxCell;


/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 9:51 2018/12/26
 * @Modify By:
 */
public class mxcellUtil {
    public static coordinateClass getMidCoor(mxCell cell){
        int height= (int) cell.getGeometry().getHeight();
        int width= (int) cell.getGeometry().getWidth();
        int x= (int) cell.getGeometry().getX();
        int y= (int) cell.getGeometry().getY();

        int coor_x=x+width/2;
        int coor_y=y+height/2;

        coordinateClass coor=new coordinateClass(coor_x,coor_y);
        return coor;
    }

    public static coordinateClass getPortCoor(mxCell cell,String location,String direction){
        coordinateClass coor=new coordinateClass(0,0);
        for (int i = 0; i < cell.getChildCount(); ++i) {
            mxCell child = (mxCell) cell.getChildAt(i);
            if(child.getLocation().equals(location)){
                int x= (int) cell.getGeometry().getX();
                int y= (int) cell.getGeometry().getY();
                int child_x=(int) child.getGeometry().getX();
                int child_y= (int) child.getGeometry().getY();
                int port_x=x*child_x;
                int port_y=y*child_y;
                coor.setX(port_x);
                coor.setY(port_y);
            }
        }
        return coor;
    }
}
