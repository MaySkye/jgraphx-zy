package com.mxgraph.examples.swing.owl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OwlDataAttribute implements java.io.Serializable{
    public String uri;
    public String id;
    //public String label;
    //public String definition;
    //public String isDefinedBy;
    //public String comment;
    public OwlDataAttribute parentOda;
    public Map<String,String> annotations;
    public String range;
    public Set<OwlClass> domainIncludes;
    public Set<Object> rangeIncludes;

    public OwlDataAttribute() {
        this.uri=null;
        this.id=null;
        this.domainIncludes=new HashSet<>();
        this.rangeIncludes=new HashSet<>();
        this.annotations=new HashMap<>();
        this.parentOda=null;
    }
}
