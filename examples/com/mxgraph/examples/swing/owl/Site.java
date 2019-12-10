package com.mxgraph.examples.swing.owl;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 9:00 2018/8/3
 * @Modify By:
 */
public class Site implements java.io.Serializable{
    private String site_id; //站点id
    private String site_level; //站点级别
    private String site_local; //站点位置
    private String site_name;  //站点名称
    private String site_info; //站点描述

    public Site() {

    }

    public Site(String site_id, String site_level, String site_local, String site_name, String site_info) {
        this.site_id = site_id;
        this.site_level = site_level;
        this.site_local = site_local;
        this.site_name = site_name;
        this.site_info = site_info;
    }

    public String getSite_id() {
        return site_id;
    }

    public String getSite_level() {
        return site_level;
    }

    public String getSite_local() {
        return site_local;
    }

    public String getSite_name() {
        return site_name;
    }

    public String getSite_info() {
        return site_info;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public void setSite_level(String site_level) {
        this.site_level = site_level;
    }

    public void setSite_local(String site_local) {
        this.site_local = site_local;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public void setSite_info(String site_info) {
        this.site_info = site_info;
    }
}
