package com.jiagu.mysql.CURD;

import com.mysql.protocol.Connection;
import com.mysql.protocol.ConnectionManager;

import java.util.List;
import java.util.Map;

public class SelectTest {

    private static Connection connection;

    static {
         ConnectionManager.init("127.0.0.1", 3306, "root", "123456", "appstore");
         connection = ConnectionManager.getConnection();
    }

    public static void main(String[] args) {
        String SQL = "select id, `desc` from org";
        //List<Map<String, String>> result = connection.execQueryRaw(SQL);
        List<Map<String, Object>> result = connection.execQuery(SQL);
        for (Map<String, Object> map : result) {
            System.out.println("map:" + map);
        }
    }
}
