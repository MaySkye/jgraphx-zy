package com.mxgraph.examples.swing.graph;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @Author:zhoayi
 * @Description:定义连接边的属性
 * @Data: Created in 11:07 2018/9/27
 * @Modify By:
 */
public class EdgeLink implements Serializable {
    private String id;
    private String name;
    private String info;
    private String image;
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
