package com.prix.homepage.backend.basic.utils;

import org.springframework.context.annotation.Primary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;


public class PrixConnector {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mysql://166.104.110.37/prix?user=prix&password=Prix2024!@");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Primary
    public static void main(String[] args) {
        try {
            Connection conn = PrixConnector.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT id, type, name FROM px_data;");
            while (rs.next()) {
                System.out.println("[" + rs.getString(2) + "] " + rs.getString(3));
            }
            rs.close();
            state.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
