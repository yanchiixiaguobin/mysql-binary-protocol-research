package com.mysql.protocol;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionManager {

    private static String user;

    private static String passwd;

    private static String host;

    private static int port;

    private static String db;

    private static AtomicBoolean isInit = new AtomicBoolean(false);

    public static void init(String hostArg, int portArg, String userArg, String passwdArg, String dbArg) {
        if (isInit.compareAndSet(false, true)) {
            host = hostArg;
            port = portArg;
            user = userArg;
            passwd = passwdArg;
            db = dbArg;
        }
    }

    public static Connection getConnection() {
        if (!isInit.get()) return null;
        return new Connection();
    }

    public static String getUser() {
        return user;
    }

    public static String getPasswd() {
        return passwd;
    }

    public static int getPort() {
        return port;
    }

    public static String getHost() {
        return host;
    }

    public static String getDb() {
        return db;
    }
}
