package com.mxgraph.examples.swing.db;


public class TelemetryDTO {


    private Integer id;

    private String SiteName;

    private String DeviceName;

    private String DataName;

    private String Timestamp;

    private String DetectedValue;

    private String Base;

    private Integer Offset;

    private Integer Ratio;

    private String UpperLimit;

    private String LowerLimit;

    private String AlarmUpperLimit;

    private String AlarmLowerLimit;

    private Integer State;

    private Integer Blocked;

    private String AlarmState;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getDataName() {
        return DataName;
    }

    public void setDataName(String dataName) {
        DataName = dataName;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.Timestamp = timestamp;
    }

    public String getDetectedValue() {
        return DetectedValue;
    }

    public void setDetectedValue(String detectedValue) {
        DetectedValue = detectedValue;
    }

    public String getBase() {
        return Base;
    }

    public void setBase(String base) {
        Base = base;
    }

    public Integer getOffset() {
        return Offset;
    }

    public void setOffset(Integer offset) {
        Offset = offset;
    }

    public Integer getRatio() {
        return Ratio;
    }

    public void setRatio(Integer ratio) {
        Ratio = ratio;
    }

    public String getUpperLimit() {
        return UpperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        UpperLimit = upperLimit;
    }

    public String getLowerLimit() {
        return LowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        LowerLimit = lowerLimit;
    }

    public String getAlarmUpperLimit() {
        return AlarmUpperLimit;
    }

    public void setAlarmUpperLimit(String alarmUpperLimit) {
        AlarmUpperLimit = alarmUpperLimit;
    }

    public String getAlarmLowerLimit() {
        return AlarmLowerLimit;
    }

    public void setAlarmLowerLimit(String alarmLowerLimit) {
        AlarmLowerLimit = alarmLowerLimit;
    }

    public Integer getState() {
        return State;
    }

    public void setState(Integer state) {
        State = state;
    }

    public Integer getBlocked() {
        return Blocked;
    }

    public void setBlocked(Integer blocked) {
        Blocked = blocked;
    }

    public String getAlarmState() {
        return AlarmState;
    }

    public void setAlarmState(String alarmState) {
        AlarmState = alarmState;
    }
}
