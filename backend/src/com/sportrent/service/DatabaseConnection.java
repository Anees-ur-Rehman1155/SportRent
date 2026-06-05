package com.sportrent.service;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static String url;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (!propFile.exists()) {
            // Check in parent directory just in case
            propFile = new File("backend/db.properties");
        }
        
        try (InputStream is = new FileInputStream(propFile)) {
            props.load(is);
            url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/playrent_db");
            user = props.getProperty("db.user", "root");
            password = props.getProperty("db.password", "your_password_here");
        } catch (IOException e) {
            System.err.println("[DatabaseConnection] Warning: db.properties not found, using default values.");
            url = "jdbc:mysql://localhost:3306/playrent_db";
            user = "root";
            password = "your_password_here";
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                // Error 1049 represents unknown database in MySQL
                if (e.getErrorCode() == 1049) {
                    System.out.println("[DatabaseConnection] Database playrent_db not found. Creating it...");
                    String serverUrl = url.substring(0, url.lastIndexOf("/"));
                    // Extract query params if any
                    String queryParams = "";
                    if (url.contains("?")) {
                        queryParams = url.substring(url.indexOf("?"));
                        serverUrl = url.substring(0, url.indexOf("?"));
                        serverUrl = serverUrl.substring(0, serverUrl.lastIndexOf("/"));
                    }
                    try (Connection tempConn = DriverManager.getConnection(serverUrl + "/" + queryParams, user, password);
                         Statement stmt = tempConn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS playrent_db");
                        System.out.println("[DatabaseConnection] Database playrent_db created successfully.");
                    }
                    // Retry connection
                    return DriverManager.getConnection(url, user, password);
                } else {
                    throw e;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Ensure the connector jar is in your classpath.", e);
        }
    }

    public static void closeConnection() {
        // No-op since we return fresh connections
    }
}
