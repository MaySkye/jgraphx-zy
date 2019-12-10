package com.mxgraph.examples.swing.decode;

import com.mxgraph.examples.swing.match.ResourceStaticData;

import java.util.Map;


public class GraphEle {

    private String name;
    private String imagePath;
    private String mxePath;
    private Map<String, Integer> cellCountMap; // deprecated
    private ResourceStaticData resourceStaticData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getMxePath() {
        return mxePath;
    }

    public void setMxePath(String mxePath) {
        this.mxePath = mxePath;
    }

    public Map<String, Integer> getCellCountMap() {
        return cellCountMap;
    }

    public void setCellCountMap(Map<String, Integer> cellCountMap) {
        this.cellCountMap = cellCountMap;
    }

    public ResourceStaticData getResourceStaticData() {
        return resourceStaticData;
    }

    public void setResourceStaticData(ResourceStaticData resourceStaticData) {
        this.resourceStaticData = resourceStaticData;
    }
}
