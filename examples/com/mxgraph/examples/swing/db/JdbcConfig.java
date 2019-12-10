package com.mxgraph.examples.swing.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

import java.util.Properties;

public class JdbcConfig {

    private  Properties properties;

    public JdbcConfig(Properties properties){
        this.properties=properties;
    }


    public JdbcConfig(){

    }

    public QueryRunner createQueryRunner() {
        return new QueryRunner(createDataSource(properties));
    }


    public DataSource createDataSource(Properties properties) {
        try {
            ComboPooledDataSource ds = new ComboPooledDataSource();
            ds.setDriverClass(properties.getProperty("jdbc.Driver"));
            ds.setJdbcUrl(properties.getProperty("jdbc.url"));
            ds.setUser(properties.getProperty("jdbc.user"));
            ds.setPassword(properties.getProperty("jdbc.password"));
            return ds;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
