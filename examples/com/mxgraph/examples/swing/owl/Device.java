package com.mxgraph.examples.swing.owl;

/**
 * @Author: zhoayi
 * @Description:
 * @Data:  Created in 9:58 2018/8/3
 * @Modify  By:
 */
public class Device implements java.io.Serializable {
    private String device_id;
    private String site_id;
    private String device_name;
    private String device_type;
    private String device_info;
    private String producer;
    private String device_manager;
    private String install_date;
    private String device_state;
    private String work_v;
    private String device_interface;
    private String standby_power;

    public Device() {
    }

    public Device(String device_id, String site_id, String device_name, String device_type, String device_info, String producer, String device_manager, String install_date, String device_state, String work_v, String device_interface, String standby_power) {
        this.device_id = device_id;
        this.site_id = site_id;
        this.device_name = device_name;
        this.device_type = device_type;
        this.device_info = device_info;
        this.producer = producer;
        this.device_manager = device_manager;
        this.install_date = install_date;
        this.device_state = device_state;
        this.work_v = work_v;
        this.device_interface = device_interface;
        this.standby_power = standby_power;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getSite_id() {
        return site_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getDevice_type() {
        return device_type;
    }

    public String getDevice_info() {
        return device_info;
    }

    public String getProducer() {
        return producer;
    }

    public String getDevice_manager() {
        return device_manager;
    }

    public String getInstall_date() {
        return install_date;
    }

    public String getDevice_state() {
        return device_state;
    }

    public String getWork_v() {
        return work_v;
    }

    public String getDevice_interface() {
        return device_interface;
    }

    public String getStandby_power() {
        return standby_power;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void setDevice_manager(String device_manager) {
        this.device_manager = device_manager;
    }

    public void setInstall_date(String install_date) {
        this.install_date = install_date;
    }

    public void setDevice_state(String device_state) {
        this.device_state = device_state;
    }

    public void setWork_v(String work_v) {
        this.work_v = work_v;
    }

    public void setDevice_interface(String device_interface) {
        this.device_interface = device_interface;
    }

    public void setStandby_power(String standby_power) {
        this.standby_power = standby_power;
    }
}
