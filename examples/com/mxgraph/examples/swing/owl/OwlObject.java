package com.mxgraph.examples.swing.owl;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class OwlObject implements java.io.Serializable{
    public String uri;
    public String id;
    public boolean visible=true;//可视的，默认为true,资源模板可以选择，fasle的不显示
    public OwlClass type;
    public Map<OwlDataAttribute, Set<Object>> dataAttrs;
    public Map<OwlObjectAttribute, Set<OwlObject>> objAttrs;
    public Map<String,String> annotations;

    public OwlObject() {
        this.id=null;
        this.uri=null;
        this.type=null;
        this.objAttrs = new HashMap<>();
        this.dataAttrs = new HashMap<>();
        this.annotations=new HashMap<>();
    }

    @Override
    public String toString() {
        return this.id;
    }
}
