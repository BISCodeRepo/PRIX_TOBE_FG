package com.prix.homepage.backend.livesearch.service.patternmatch;

import java.sql.*;
public class PatternMatch_SQL {


    private String id;
    private String passwd;
    private String db;
    private String ip;
    private Connection con;
    private Statement stmt;

    public PatternMatch_SQL(String id, String passwd, String ip, String db) {
        this.id = id;
        this.passwd = passwd;
        this.ip = ip;
//        this.db = db;

        try {
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public void Connect() {
        try {
            String var1 = "jdbc:mysql://" + this.ip + this.db;
            this.con = DriverManager.getConnection(var1, this.id, this.passwd);
            this.stmt = this.con.createStatement();
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }

    public ResultSet Swiss_protSequence(String var1, String[] var2, boolean var3) throws SQLException {
        int var4 = var2.length;
        StringBuffer var5 = new StringBuffer();
        var5.append("select ac_to_species.primary_ac, description, species, sequence");
        var5.append(" from ac_to_species, sequence_data, extra_information");
        var5.append(" where ac_to_species.primary_ac = sequence_data.primary_ac");
        var5.append(" && ac_to_species.primary_ac = extra_information.primary_ac ");
        int var6;
        if (var3) {
            var5.append("&& sequence rlike '");

            for(var6 = 0; var6 < var4; ++var6) {
                var5.append(var2[var6]);
                if (var6 != var4 - 1) {
                    var5.append(".*");
                }
            }

            var5.append("'");
        } else {
            for(var6 = 0; var6 < var4; ++var6) {
                var5.append("&& sequence rlike '");
                var5.append(var2[var6]);
                var5.append("'");
            }
        }

        var5.append(" && species like '");
        var5.append(var1);
        var5.append("%';");
        return this.stmt.executeQuery(var5.toString());
    }

    public ResultSet Swiss_protSequence(String[] var1, boolean var2) throws SQLException {
        int var3 = var1.length;
        StringBuffer var4 = new StringBuffer();
        var4.append("select ac_to_species.primary_ac, description, species, sequence");
        var4.append(" from ac_to_species, sequence_data, extra_information");
        var4.append(" where ac_to_species.primary_ac = sequence_data.primary_ac");
        var4.append(" && ac_to_species.primary_ac = extra_information.primary_ac");
        int var5;
        if (var2) {
            var4.append("\t&& sequence rlike '");

            for(var5 = 0; var5 < var3; ++var5) {
                var4.append(var1[var5]);
                if (var5 != var3 - 1) {
                    var4.append(".*");
                }
            }

            var4.append("';");
        } else {
            for(var5 = 0; var5 < var3; ++var5) {
                var4.append("\t&& sequence rlike '");
                var4.append(var1[var5]);
                var4.append("'");
            }

            var4.append(";");
        }

        return this.stmt.executeQuery(var4.toString());
    }

    public ResultSet GenbankSequence(String var1, String[] var2, boolean var3) throws SQLException {
        int var4 = var2.length;
        StringBuffer var5 = new StringBuffer();
        var5.append("select * from general_info where species = '");
        var5.append(var1);
        var5.append("'");
        int var6;
        if (var3) {
            var5.append("\t&& sequence rlike '");

            for(var6 = 0; var6 < var4; ++var6) {
                var5.append(var2[var6]);
                if (var6 != var4 - 1) {
                    var5.append(".*");
                }
            }

            var5.append("'");
        } else {
            for(var6 = 0; var6 < var4; ++var6) {
                var5.append("\t&& sequence rlike '");
                var5.append(var2[var6]);
                var5.append("'");
            }
        }

        var5.append(";");
        return this.stmt.executeQuery(var5.toString());
    }

    public ResultSet GenbankSequence(String[] var1, boolean var2) throws SQLException {
        int var3 = var1.length;
        StringBuffer var4 = new StringBuffer();
        var4.append("select * from general_info");
        int var5;
        if (var2) {
            var4.append(" where sequence rlike '");

            for(var5 = 0; var5 < var3; ++var5) {
                var4.append(var1[var5]);
                if (var5 != var3 - 1) {
                    var4.append(".*");
                }
            }

            var4.append("'");
        } else {
            for(var5 = 0; var5 < var3; ++var5) {
                if (var5 == 0) {
                    var4.append(" where sequence rlike '");
                } else {
                    var4.append(" && sequence rlike '");
                }

                var4.append(var1[var5]);
                var4.append("'");
            }
        }

        var4.append(";");
        return this.stmt.executeQuery(var4.toString());
    }

    public void Close() {
        try {
            this.stmt.close();
            this.con.close();
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }
}
