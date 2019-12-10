package com.mxgraph.examples.swing.owl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class OwlObjectAttribute implements java.io.Serializable{
    public String uri;
    public String id;//名字
    //public String label;
    //public String definition;
    //public String isDefinedBy;
    //public String comment;
    public Map<String,String> annotations;
    public OwlObjectAttribute inverseOfAttr;
    public Set<OwlClass> domain;
    public Set<OwlClass> range;


    public OwlObjectAttribute() {
        this.uri=null;
        this.id=null;
        this.annotations=new HashMap<>();
        this.inverseOfAttr=null;
        this.domain=new HashSet<>();
        this.range=new HashSet<>();
    }


}
