package com.mxgraph.examples.swing.owl;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:25 2018/8/3
 * @Modify By:
 */
public class DeviceAttribute implements java.io.Serializable{
    private String resource_id;
    private String resource_uri;
    private String resource_class;
    private String site_id;
    private String device_id;
    private String resource_name;
    private String resource_type;
    private String resource_unit;

    public DeviceAttribute() {
    }

    public DeviceAttribute(String resource_id, String resource_uri, String resource_class, String site_id, String device_id, String resource_name, String resource_type, String resource_unit) {
        this.resource_id = resource_id;
        this.resource_uri = resource_uri;
        this.resource_class = resource_class;
        this.site_id = site_id;
        this.device_id = device_id;
        this.resource_name = resource_name;
        this.resource_type = resource_type;
        this.resource_unit = resource_unit;
    }

    public String getResource_id() {
        return resource_id;
    }

    public String getResource_uri() {
        return resource_uri;
    }

    public String getResource_class() {
        return resource_class;
    }

    public String getSite_id() {
        return site_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getResource_name() {
        return resource_name;
    }

    public String getResource_type() {
        return resource_type;
    }

    public String getResource_unit() {
        return resource_unit;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public void setResource_uri(String resource_uri) {
        this.resource_uri = resource_uri;
    }

    public void setResource_class(String resource_class) {
        this.resource_class = resource_class;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setResource_name(String resource_name) {
        this.resource_name = resource_name;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public void setResource_unit(String resource_unit) {
        this.resource_unit = resource_unit;
    }
}
