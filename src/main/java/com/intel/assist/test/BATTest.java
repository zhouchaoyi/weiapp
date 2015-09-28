package com.intel.assist.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Ecic Chen on 2015/8/5.
 */
public class BATTest {
    public static void main(String[] args) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = null;

        try {
            /*****填写数据库相关信息(请查找数据库详情页)*****/
            String databaseName = "uulpvOHFQbvDhuhtLmJt";
            String host = "sqld.duapp.com";
            String port = "4050";
            String username = "ebde167921374e61be8ad0c54134fcb7"; //用户AK
            String password = "80532aeeab8a4880b29e27263e80f20b"; //用户SK
            String driverName = "com.mysql.jdbc.Driver";
            String dbUrl = "jdbc:mysql://";
            String serverName = host + ":" + port + "/";
            String connName = dbUrl + serverName + databaseName+"&autoReconnect=true&failOverReadOnly=false&maxReconnects=10";

            /******接着连接并选择数据库名为databaseName的服务器******/
            Class.forName(driverName);
            connection = DriverManager.getConnection(connName, username,
                    password);
            stmt = connection.createStatement();
            /******至此连接已完全建立，就可对当前数据库进行相应的操作了*****/
            /******接下来就可以使用其它标准mysql函数操作进行数据库操作*****/
            //创建一个数据库表
            sql = "create table if not exists test_mysql(" +
                    "id int primary key auto_increment," + "no int, " +
                    "name varchar(1024)," + "key idx_no(no))";
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
