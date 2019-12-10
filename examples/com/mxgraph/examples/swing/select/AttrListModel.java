package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.owl.OwlDataAttribute;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.model.mxCell;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.parseResourceFile;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 16:49 2018/10/30
 * @Modify By:
 */
public class AttrListModel extends AbstractTableModel {

    /*当dataAttrs不为空时用到的变量*/
    private OwlObject owlObject;
    private String[] columnNames = {"数据属性","值"};
    private Object[][] objdatas;
    private Map<OwlDataAttribute, Set<Object>> dataAttrs;
    private int rowCount;

    public AttrListModel(OwlObject owlObject) {
            this.owlObject=owlObject;
            this.dataAttrs=owlObject.dataAttrs;
            rowCount=dataAttrs.size()+2;
            objdatas=new Object[rowCount][columnNames.length];

            objdatas[0][0]="Name";
            objdatas[0][1]=owlObject.id;
            objdatas[1][0]="Type";
            objdatas[1][1]=owlObject.type.id;

            loadData(this.dataAttrs);//加载objdatas
    }

    public void loadData(Map<OwlDataAttribute, Set<Object>> dataAttrs){

        if(dataAttrs==null){
            return;
        }
        int i=2;
        for (Map.Entry<OwlDataAttribute, Set<Object>> entry : dataAttrs.entrySet()) {
            objdatas[i][0]=entry.getKey().id;
            for(Object obj:entry.getValue()){
                if(obj.toString().length()>1&&obj.toString().subSequence(0,2).equals("^^")){
                    objdatas[i][1]="";
                }else{
                    objdatas[i][1]=obj.toString();
                }
                System.out.println("obj.toString():"+obj.toString());
            }
            i++;
        }
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return objdatas[rowIndex][columnIndex];
    }

}
