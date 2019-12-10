package com.mxgraph.examples.swing.mysqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 11:30 2018/8/7
 * @Modify By:
 */
public class DBConnection {
    //连接数据库
    public static Connection sqlConnect(){
        Connection con;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("加载驱动成功!!!");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/timeservice?useUnicode=true&" +
                    "characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true",
                    "root", "root");
            System.out.println("连接数据库成功!!!");
            return con;
        }
        catch(ClassNotFoundException cnfe){
            System.out.println("driver nao encontrado: " + cnfe.getMessage());
            return null;
        }
        catch(SQLException sql){
            System.out.println("SQLException: " + sql.getMessage());
            System.out.println("SQLState: " + sql.getSQLState());
            System.out.println("Erro: " + sql.getErrorCode());
            System.out.println("StackTrace: " + sql.getStackTrace());
            return null;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
