package com.sportsinventory.Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class ConnectionFactory {

    static final String driver = "com.mysql.cj.jdbc.Driver";
    static final String url = "jdbc:mysql://localhost:3306/sportsinventory?user=root&password=Hbtchou123";
    static String username;
    static String password;

    Properties prop;

    Connection conn = null;
    Statement statement = null;
    ResultSet resultSet = null;

    public ConnectionFactory(){
        try {
            prop = new Properties();
            prop.loadFromXML(new FileInputStream("D:/VinUni/Spring 2023/COMP 1020/Project/VinEquip/SportsInventoryManagementSystem/src/com/sportsinventory/Database/DBCredentials.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        username = prop.getProperty("username");
        password = prop.getProperty("password");
        System.out.println(username + " " + password);
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            statement = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    //Login verification method
    public boolean checkLogin(String username, String password, String userType){
        String query = "SELECT * FROM users WHERE username='"
                + username
                + "' AND password='"
                + password
                + "' AND usertype='"
                + userType
                + "' LIMIT 1";

        try {
            resultSet = statement.executeQuery(query);
            if(resultSet.next()) return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
