package com.mxgraph.examples.swing.db;

import com.mxgraph.examples.swing.util.MLog;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import net.sf.json.JSONObject;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:57 2019/4/23
 * @Modify By:
 */
public class DBDataCollector2 extends Thread{
    private static String sqlInsert = "insert into %s(%s, %s, %s) values('%s', '%s', '%s')";
    private static String sqlUpdate = "update %s set %s='%s', %s='%s' where %s='%s'";
    private static String sqlSelect = "select count(*) from %s where %s='%s'";

    private boolean running = true;
    private Map<String, mxCell> mxcell_property=new HashMap<>();
    public DBDataCollector2(Map<String, mxCell> mxcell_property) {
        this.mxcell_property = mxcell_property;
    }

    public void cancel() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {

            // 采集数据，写入实时数据库和持久化库
            //String data = dbDataGenerator.generatorData();
            String data="44";
            insertData(data);

            for(Map.Entry<String,mxCell> entry:mxcell_property.entrySet()){
                String sql = String.format(sqlInsert, "value", "rs_telemetry","resource_id", "");
                String result = null;
                try {
                    ResultSet resultSet = DBConnection.querySQL(sql);
                    if (resultSet.next()) {
                        result = resultSet.getString(1);
                    }
                    resultSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(30);//30ms刷新一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 写入实时数据库和持久化库
    public void insertData(String data) {

        JSONObject json = JSONObject.fromObject(data);
        String update_time = json.getString("update_time");
        String deviceId = json.getString("deviceId");
        String deviceData = json.getString("getDeviceData");

        //sqlInsert = "insert into %s(%s, %s, %s) values('%s', '%s', '%s')";
        //sqlUpdate = "update %s set %s='%s', %s='%s' where %s='%s'";
        //sqlSelect = "select count(*) from %s where %s='%s'";

        String sqlInsertHistory = String.format(sqlInsert, DBConfig.table, DBConfig.time_field, DBConfig.id_field,
                DBConfig.data_field, update_time, deviceId, deviceData);
        String sqlInsertRT = String.format(sqlInsert, DBConfig.rttable, DBConfig.time_field, DBConfig.id_field,
                DBConfig.data_field, update_time, deviceId, deviceData);
        String sqlUpdateRT = String.format(sqlUpdate, DBConfig.rttable, DBConfig.data_field, deviceData,
                DBConfig.time_field, update_time, DBConfig.id_field, deviceId);
        String sqlSelectRT = String.format(sqlSelect, DBConfig.rttable, DBConfig.id_field, deviceId);

        try {
            // 写入持久化库
            DBConnection.executeSQL(sqlInsertHistory);
            MLog.println("insert history sql: " + sqlInsertHistory);
            ResultSet resultSet = DBConnection.querySQL(sqlSelectRT);
            MLog.println("select rt sql: " + sqlSelectRT);
            if (resultSet.next() && resultSet.getInt(1) == 1) {
                // update
                DBConnection.executeSQL(sqlUpdateRT);
                MLog.println("update rt sql: " + sqlUpdateRT);
            } else {
                // insert
                DBConnection.executeSQL(sqlInsertRT);
                MLog.println("insert rt sql: " + sqlInsertRT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
