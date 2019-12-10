package com.mxgraph.examples.swing.db;

import java.sql.*;

public class DBConnection {

    private static Connection conn = null;

    static {
        try {
            Class.forName(DBConfig.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(DBConfig.url+"characterEncoding=utf8&useSSL=true", DBConfig.user, DBConfig.passwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("driver: " + DBConfig.driver);
        //System.out.println("url: " + DBConfig.url+"characterEncoding=utf8&useSSL=true");
        //System.out.println("user: " + DBConfig.user);
        //System.out.println("passwd: " + DBConfig.passwd);
    }

    public static Connection getConnection() {
        return conn;
    }

    public static Connection getConnection(String driver, String url, String user, String passwd) {
        return conn;
    }

    public static ResultSet querySQL(String sql) throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public static boolean executeSQL(String sql) throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        return statement.execute(sql);
    }

//    public static void main(String[] args) throws Exception {
//
//        String sql = "select resource_id from rs_telemetry";
//        ResultSet resultSet = querySQL(sql);
//        while (resultSet.next()) {
//            // 取出列值
//            String resource_id = resultSet.getString(1);
//            System.out.println("resource_id:"+resource_id );
//
//            /*String qSQL = String.format("select t1.update_time, t1.device_id, t2.name, t1.device_data from %s t1 join %s t2 on t1.device_id=t2.id", dataTable, deviceTable);
//            ResultSet resultSet1 = querySQL(qSQL);
//            while (resultSet1.next()) {
//                Timestamp time = resultSet1.getTimestamp(1);
//                long deviceId = resultSet1.getLong(2);
//                String deviceName = resultSet1.getString(3);
//                String getDeviceData = resultSet1.getString(4);
//
//                JSONObject json = JSONObject.fromObject(getDeviceData);
//                String temperature = (String) json.get("temperature");
//                System.out.println(time + " " + deviceId + " " + deviceName + " " + getDeviceData + " " + temperature);
//            }
//            System.out.printf("<<<<<<<<<<<<<<<<< %10s <<<<<<<<<<<<<<<<<<<<<\n", dataTable);*/
//        }
//        resultSet.close();
//
//
//    }
}



