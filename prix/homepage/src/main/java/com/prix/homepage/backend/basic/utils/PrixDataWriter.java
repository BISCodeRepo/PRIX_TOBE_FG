package com.prix.homepage.backend.basic.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class PrixDataWriter {
    //static String logdir = "E:\\PRIX\\logs\\db_error_";
    static String logdir = "C:/Users/82108/Desktop/prix/log";
    
    static public int write(String type, String name, InputStream is) throws java.sql.SQLException, java.io.FileNotFoundException, java.io.UnsupportedEncodingException {
        Connection conn = PrixConnector.getConnection();
        if (conn == null)
            return -1;

        int index = -1;
        String sql = "INSERT INTO px_data (type, name, content) values ('" + type + "', '" + name.replace("'", "\\'") + "', ?)";
        String result = "OK";
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBinaryStream(1, is);
            ps.executeUpdate();
            conn.commit();
            ps.close();

            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT max(id) FROM px_data;");
            if (rs.next())
                index = rs.getInt(1);
            rs.close();
            state.close();
        } catch (Exception e) {
            result = e.toString();
        } finally {
            conn.close();
        }

        Calendar cal = Calendar.getInstance();
        String date = String.format("%04d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        try (PrintStream ps = new PrintStream(new FileOutputStream(logdir + date + ".log", true), false, "UTF-8")) {
            ps.println("[" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "] " + sql);
            ps.println(result);
        }

        return index;
    }

    static public void replace(int id, InputStream is) throws java.sql.SQLException, java.io.FileNotFoundException, java.io.UnsupportedEncodingException {
        Connection conn = PrixConnector.getConnection();
        if (conn == null)
            return;

        String sql = "UPDATE px_data set content=? where id=" + id;
        String result = "OK";
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBinaryStream(1, is);
            ps.executeUpdate();
            conn.commit();
            ps.close();
        } catch (Exception e) {
            result = e.toString();
        } finally {
            conn.close();
        }

        Calendar cal = Calendar.getInstance();
        String date = String.format("%04d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        try (PrintStream ps = new PrintStream(new FileOutputStream(logdir + date + ".log", true), false, "UTF-8")) {
            ps.println("[" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "] " + sql);
            ps.println(result);
        }
    }
}
