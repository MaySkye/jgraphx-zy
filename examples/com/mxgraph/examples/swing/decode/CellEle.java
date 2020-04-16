package com.mxgraph.examples.swing.decode;

import java.util.List;


public class CellEle {

    private String name;
    private String type;

    private String icon;
    private String style;

    private String multiValue = "0";
    private String styles;

    private int width;
    private int height;
    private List<CellPort> ports;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getMultiValue() {
        return multiValue;
    }

    public void setMultiValue(String multiValue) {
        this.multiValue = multiValue;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<CellPort> getPorts() {
        return ports;
    }

    public void setPorts(List<CellPort> ports) {
        this.ports = ports;
    }

    @Override
    public String toString() {
        return "name: " + name + "type: " + type + " style: " + style + " ports:" + ports;
    }
}
