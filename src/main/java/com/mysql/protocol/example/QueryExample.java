package com.mysql.protocol.example;

import com.mysql.protocol.Connection;
import com.mysql.protocol.ConnectionManager;

import java.util.List;
import java.util.Map;

public class QueryExample {

    private static Connection connection;

    static {
        ConnectionManager.init("127.0.0.1", 3306, "root", "123456", "appstore");
        connection = ConnectionManager.getConnection();
    }

    public static void main(String[] args) {
        String SQL = "select id, `desc` from org";
        List<Map<String, String>> result = connection.execQueryRaw(SQL);
        for (Map<String, String> map : result) {
            System.out.println("map:" + map);
        }
    }
}
