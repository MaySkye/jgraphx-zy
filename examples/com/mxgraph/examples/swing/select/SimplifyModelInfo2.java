package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.owl.OwlClass;
import com.mxgraph.examples.swing.owl.OwlDataAttribute;
import com.mxgraph.examples.swing.owl.OwlObject;
import com.mxgraph.examples.swing.owl.OwlObjectAttribute;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:37 2019/1/14
 * @Modify By:
 */
public class SimplifyModelInfo2 implements Serializable {
    private String filePath ; //文件路径
    private Map<String, OwlClass> classMap ;
    private Map<String, OwlObjectAttribute> objAttrMap ;
    private Map<String, OwlDataAttribute> dataAttrMap ;
    private Map<String, OwlObject> objMap ;

    private Map<String, OwlObject> main_objMap ;
    private Map<String, Object> vertex_objMap ;
    private Map<String, OwlObject> vertex_main_objMap ;
    private Map<Object, String> edge_objMap ;

    public SimplifyModelInfo2(String filePath, Map<String, OwlClass> classMap, Map<String, OwlObjectAttribute> objAttrMap, Map<String, OwlDataAttribute> dataAttrMap, Map<String, OwlObject> objMap, Map<String, OwlObject> main_objMap, Map<String, Object> vertex_objMap, Map<String, OwlObject> vertex_main_objMap, Map<Object, String> edge_objMap) {
        this.filePath = filePath;
        this.classMap = classMap;
        this.objAttrMap = objAttrMap;
        this.dataAttrMap = dataAttrMap;
        this.objMap = objMap;
        this.main_objMap = main_objMap;
        this.vertex_objMap = vertex_objMap;
        this.vertex_main_objMap = vertex_main_objMap;
        this.edge_objMap = edge_objMap;
    }

    public SimplifyModelInfo2() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, OwlClass> getClassMap() {
        return classMap;
    }

    public void setClassMap(Map<String, OwlClass> classMap) {
        this.classMap = classMap;
    }

    public Map<String, OwlObjectAttribute> getObjAttrMap() {
        return objAttrMap;
    }

    public void setObjAttrMap(Map<String, OwlObjectAttribute> objAttrMap) {
        this.objAttrMap = objAttrMap;
    }

    public Map<String, OwlDataAttribute> getDataAttrMap() {
        return dataAttrMap;
    }

    public void setDataAttrMap(Map<String, OwlDataAttribute> dataAttrMap) {
        this.dataAttrMap = dataAttrMap;
    }

    public Map<String, OwlObject> getObjMap() {
        return objMap;
    }

    public void setObjMap(Map<String, OwlObject> objMap) {
        this.objMap = objMap;
    }

    public Map<String, OwlObject> getMain_objMap() {
        return main_objMap;
    }

    public void setMain_objMap(Map<String, OwlObject> main_objMap) {
        this.main_objMap = main_objMap;
    }

    public Map<String, Object> getVertex_objMap() {
        return vertex_objMap;
    }

    public void setVertex_objMap(Map<String, Object> vertex_objMap) {
        this.vertex_objMap = vertex_objMap;
    }

    public Map<String, OwlObject> getVertex_main_objMap() {
        return vertex_main_objMap;
    }

    public void setVertex_main_objMap(Map<String, OwlObject> vertex_main_objMap) {
        this.vertex_main_objMap = vertex_main_objMap;
    }

    public Map<Object, String> getEdge_objMap() {
        return edge_objMap;
    }

    public void setEdge_objMap(Map<Object, String> edge_objMap) {
        this.edge_objMap = edge_objMap;
    }
}
