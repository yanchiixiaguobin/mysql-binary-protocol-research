package com.mysql.protocol.example;

import com.mysql.protocol.Connection;
import com.mysql.protocol.ConnectionManager;

public class InsertExample {

    private static Connection connection;

    static {
        ConnectionManager.init("127.0.0.1", 3306, "root", "123456", "appstore");
        connection = ConnectionManager.getConnection();
    }

    public static void main(String[] args) {
        String SQL = "insert into org(parent_id, `desc`) values(2, '盘古大厦')";
        connection.execInsert(SQL);
    }
}
