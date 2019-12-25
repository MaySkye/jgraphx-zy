package com.mxgraph.examples.swing.db;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 14:42 2019/12/23
 * @Modify By:
 */
public class SiteDTO {
    private long siteId;
    private long siteLevel;
    private double siteLocalx;
    private double siteLocaly;
    private String siteName;
    private String siteInfo;
    private String siteType;
    private String siteAddress;
    private String connect;

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }


    public long getSiteLevel() {
        return siteLevel;
    }

    public void setSiteLevel(long siteLevel) {
        this.siteLevel = siteLevel;
    }


    public double getSiteLocalx() {
        return siteLocalx;
    }

    public void setSiteLocalx(double siteLocalx) {
        this.siteLocalx = siteLocalx;
    }


    public double getSiteLocaly() {
        return siteLocaly;
    }

    public void setSiteLocaly(double siteLocaly) {
        this.siteLocaly = siteLocaly;
    }


    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }


    public String getSiteInfo() {
        return siteInfo;
    }

    public void setSiteInfo(String siteInfo) {
        this.siteInfo = siteInfo;
    }


    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }


    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }


    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }
}
