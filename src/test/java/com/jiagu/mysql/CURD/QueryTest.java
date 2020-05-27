package com.jiagu.mysql.CURD;

/**
 * Created by jintx on 2018/11/1.
 */
public class QueryTest {

    public static void main(String args[]) throws Exception{
        Query query = new Query();
        String host = "127.0.0.1";
        int port = 3306;
        String user = "root";
        String password = "123456";
        String dataBase = "appstore";
        String sqlStr = "SELECT id, `desc` from org;";
        query.query(host,port,user,password,dataBase,sqlStr);
    }

}
