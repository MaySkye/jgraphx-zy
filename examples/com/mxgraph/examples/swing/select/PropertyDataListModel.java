package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.owl.OwlDataAttribute;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.model.mxCell;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.parseResourceFile;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 16:49 2018/10/30
 * @Modify By:
 */
public class PropertyDataListModel extends AbstractTableModel {

    /*当dataAttrs不为空时用到的变量*/
    private String[] columnNames = {"属性名称","数据值"};
    private Object[][] objdatas;
    private int rowCount;
    private ArrayList<JLabel> property_name;
    private ArrayList<JTextField> property_data;

    public PropertyDataListModel(ArrayList<JLabel> property_name,ArrayList<JTextField> property_data) {
        this.property_name=property_name;
        this.property_data=property_data;
        rowCount=property_data.size();
        objdatas=new Object[rowCount][columnNames.length];

        loadData(this.property_name,this.property_data);//加载objdatas
    }

    public void loadData(ArrayList<JLabel> property_name,ArrayList<JTextField> property_data){

        if(property_name==null&&property_data==null&&property_name.size()!=property_data.size()){
            return;
        }
        for(int i=0;i<property_name.size();i++){
            objdatas[i][0]="";
        }
        int i=0;

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
