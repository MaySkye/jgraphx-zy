package com.mxgraph.examples.swing.owl;

import org.apache.jena.rdf.model.Model;

import java.util.Map;


public class OwlResourceData implements java.io.Serializable {

    public Model model = null;
    public Map<String, OwlClass> classMap = null;
    public Map<String, OwlObjectAttribute> objAttrMap = null;
    public Map<String, OwlDataAttribute> dataAttrMap = null;
    public Map<String, OwlObject> objMap = null;
    public String title = null;

}
