package com.mxgraph.examples.swing.decode;

/**
 * 端口解析类
 */
public class CellPort {
    // fiber port style
    private static final String style_fiber= "rounded=1;fillColor=#AAEEAE;gradientColor=#EEEEFF;strokeWidth=1;strokeColor=#EEEEFF";
    // size
    private static final int fiberLongLenght = 25;
    private static final int fiberShortLenght = 12;

    private final String name = "port";
    private String attr;
    private String location;
    private String direction;
    private double x;
    private double y;
    private double g;
    private int width = 30;
    private int height = 30;
    private String style;

    public static String getStyleByAttr(String attr, String location, String direction) {
        if (attr == null || location == null || direction == null) {
            return null;
        }
        if(attr.equals("fiber")){
            return style_fiber;
        }else {
            return style_fiber;
        }
    }

    public static int getWidthByAttr(String attr, String location, String direction) {
        if ("fiber".equals(attr)) {
            if ("up".equals(location) || "down".equals(location)) {
                return fiberLongLenght;
            } else {
                return fiberShortLenght;
            }
        } else {
            return fiberShortLenght;
        }
    }

    public static int getHeightByAttr(String attr, String location, String direction) {
        if ("fiber".equals(attr)) {
            if ("left".equals(location) || "right".equals(location)) {
                return fiberLongLenght;
            } else {
                return fiberShortLenght;
            }
        } else {
            return fiberShortLenght;
        }
    }
    public String getName() {
        return name;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }


}
