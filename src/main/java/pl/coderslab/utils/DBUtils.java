package pl.coderslab.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static final String DB_URL = "jdbc:mysql://localhost:3306";
    private static final String DB_PARAM = "?useSSL=false&characterEncoding=utf8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "coderslab";
//    private static final String createDB = "CREATE DATABASE if not exists workshop2 " +
//            "CHARACTER SET utf8mb4 " +
//            "COLLATE utf8mb4_unicode_ci;";
//    private static final String useDB = "USE movies_library";
//    private static final String createTable = "CREATE TABLE if not exists users (\n" +
//            "id INT(11) AUTO_INCREMENT,\n" +
//            "username VARCHAR(255) NOT NULL,\n" +
//            "email VARCHAR(255) UNIQUE,\n" +
//            "password VARCHAR(60) NOT NULL,\n" +
//            "PRIMARY KEY(id)\n" +
//            ");";

    public static Connection getConnection(String database) throws SQLException {
        String url = DB_URL + (database != null ? "/" + database : "") + DB_PARAM;
        Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
        return connection;
    }

//    public static void createDB() throws SQLException {
//        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);) {
//            Statement stm = connection.createStatement();
//            stm.executeUpdate(createDB);
//            stm.executeQuery(useDB);
//            stm.executeUpdate(createTable);
//        }
//    }
}
