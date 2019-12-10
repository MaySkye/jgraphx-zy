package com.mxgraph.examples.swing.frame;

import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.model.mxCell;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.parseResourceFile;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 16:49 2018/10/30
 * @Modify By:
 */

/*先按照cell不为空，资源信息可以直接从cell获取设计，此时资源信息只有一种*/
/*table为一列*/
public class ResourceListModel extends AbstractTableModel {

    private String cellName;
    private boolean isshowCell;//是否输出选中图元的信息，当为true时，直接查看，直接输出图元存储的顶点信息即可
                                 // 当为false时，重新选择文件，显示加载解析文件的数据
    private String RecentResourcePath;
    private mxCell cell;

    /*当isshowCell=true时用到的变量*/
    private String[] columnNames = {AliasName.getAlias("resource_name"),AliasName.getAlias("resource_info")};
    private Object[][] objdatas;
    private String[] resource_name_list={AliasName.getAlias("resource_id"),AliasName.getAlias("resource_label"),
            AliasName.getAlias("resource_name(type)"),AliasName.getAlias("resource_url"),
            AliasName.getAlias("resource_data"),AliasName.getAlias("resource_link")};
    private String[] resource_info_list;
    private VertexInterface<String> vertex ;

    /*当isshowCell=false时用到的变量*/
    private String[] update_columnNames = {AliasName.getAlias("resource_info")};
    private List<OwlObject> objList = new ArrayList();
    private OwlResourceData owlResourceData;
    private Map<String, OwlObject> objMap;

    public ResourceListModel(String cellName, mxCell cell,boolean isshowCell,String RecentResourcePath) {

        this.cellName = cellName;
        this.cell=cell;
        this.isshowCell=isshowCell;
        this.RecentResourcePath=RecentResourcePath;
        if(isshowCell){

            objdatas=new Object[resource_name_list.length][columnNames.length];
            resource_info_list=new String[resource_name_list.length];

            loadData(cell);//初始化objdatas和resource_info_list

        }else{
            owlResourceData=parseResourceFile(RecentResourcePath);
            //重新加载资源文件的资源信息

            updateData(owlResourceData);//初始化objList
        }
    }

    public void loadData(mxCell cell){
        if(cell==null){
            return;
        }
        vertex=cell.getV();
        if(vertex!=null){
            StringBuilder data_stringBuilder = new StringBuilder();
            StringBuilder link_stringBuilder = new StringBuilder();

            data_stringBuilder.append("<html>");
            link_stringBuilder.append("<html>");

            System.out.println("vertex:"+vertex);
            vertex.getData_info().forEach((key, value) -> {
                data_stringBuilder.append(key + ": " + value+"\n"+"<br/>" );
            });

            vertex.getLink_info().forEach((key, value) -> {
                link_stringBuilder.append(key + ": " + value+"\n\r"+"<br/>");
            });

            data_stringBuilder.append("<html/>");
            link_stringBuilder.append("<html/>");

            resource_info_list[0]=vertex.getId();
            resource_info_list[1]=vertex.getLabel();
            resource_info_list[2]=vertex.getName();
            resource_info_list[3]=vertex.getUrl();
            resource_info_list[4]=data_stringBuilder.toString();
            resource_info_list[5]=link_stringBuilder.toString();

        }else { //表示图元此时还没有绑定资源，显示基本信息即可
            resource_info_list[0]=cell.getDeviceid();//resource_id
            resource_info_list[1]=cell.getDeviceid();//resource_label
            resource_info_list[2]=cell.getType();//resource_name(type)
            resource_info_list[3]=null;//resource_url
            resource_info_list[4]=null;//resource_data
            resource_info_list[5]=null;//resource_link
        }
        for(int j=0;j<resource_name_list.length;j++){
            objdatas[j][0]=resource_name_list[j];
        }
        for(int j=0;j<resource_info_list.length;j++){
            objdatas[j][1]=resource_info_list[j];
        }
    }

    public void updateData(OwlResourceData owlResourceData){
        if (owlResourceData == null) {
            return;
        }
        objMap = owlResourceData.objMap;
        objList.clear();
        objMap.forEach((uri, owlObject) -> {
            if (cellName.equals(owlObject.type.id)) {
                objList.add(owlObject);
            }
        });

    }

    @Override
    public int getRowCount() {
        if(isshowCell){
            return resource_name_list.length;
        }else{
            return objList.size();
        }
    }
    @Override
    public int getColumnCount() {
        if(isshowCell){
            return columnNames.length;
        }else{
            return update_columnNames.length;
        }
    }
    @Override
    public String getColumnName(int col) {
        if(isshowCell){
            return columnNames[col];
        }else{
            return update_columnNames[col];
        }
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(!isshowCell){
            OwlObject owlObject = objList.get(rowIndex);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<html>");
            stringBuilder.append(AliasName.getAlias("resource_id") + ": " + owlObject.id + "<br/>");
            stringBuilder.append(AliasName.getAlias("resource_type") + ": " + owlObject.type.id + "<br/>");
            stringBuilder.append(AliasName.getAlias("resource_url") + ": " + owlObject.type.uri + "<br/>");
            stringBuilder.append(AliasName.getAlias("resource_data") + ": " + "<br/>");
            owlObject.dataAttrs.forEach((owlDataAttribute, objects) -> {
                String[] val = new String[]{new String()};
                objects.forEach(obj -> {
                    val[0] = val[0] + obj + ", ";
                });
                if (val[0].length() > 0) {
                    val[0] = val[0].substring(0, val[0].length() - 2);
                }
                stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+AliasName.getAlias(owlDataAttribute.id) + ": " + val[0] + "<br/>");
            });
            stringBuilder.append(AliasName.getAlias("resource_link") + ":"  + "<br/>");
            owlObject.objAttrs.forEach((owlDataAttribute, objects) -> {
                String[] val = new String[]{new String()};
                objects.forEach(obj -> {
                    val[0] = val[0] + obj.id + ", ";
                });
                if (val[0].length() > 0) {
                    val[0] = val[0].substring(0, val[0].length() - 2);
                }
                stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+AliasName.getAlias(owlDataAttribute.id) + ":        " + val[0] + "<br/>");
            });
            stringBuilder.append("<html/>");

            return stringBuilder.toString();
        }else {
            return  objdatas[rowIndex][columnIndex];
        }
    }

    public List<OwlObject> getObjList() {
        return objList;
    }
}
