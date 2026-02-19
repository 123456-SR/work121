package org.example.work121;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManualDbCheckTest {

    public static void main(String[] args) {
        new ManualDbCheckTest().checkDataMismatch();
    }

    public void checkDataMismatch() {
        String url = "jdbc:oracle:thin:@LAPTOP-DPCTMJRC:1521/FREE";
        String user = "SYSTEM";
        String pass = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("=== Checking JZS_USERS ===");
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT USER_ACCOUNT, USER_NAME FROM JZS_USERS");
                while (rs.next()) {
                    System.out.println("User: Account='" + rs.getString("USER_ACCOUNT") + "', Name='" + rs.getString("USER_NAME") + "'");
                }
            }

            System.out.println("\n=== Checking JC_CORE_WT_INFO Person Columns (Sample) ===");
            String[] cols = {"WT_MAN", "TESTER", "REVIEWER", "APPROVER", "SAMPLING_MAN"};
            for (String col : cols) {
                // Check if column exists first to avoid error
                if (isColumnExist(conn, "JC_CORE_WT_INFO", col) || isColumnExist(conn, "JC_CORE_WT_INFO_EXT", col)) {
                     String table = isColumnExist(conn, "JC_CORE_WT_INFO", col) ? "JC_CORE_WT_INFO" : "JC_CORE_WT_INFO_EXT";
                     try (Statement stmt = conn.createStatement()) {
                        ResultSet rs = stmt.executeQuery("SELECT DISTINCT " + col + " FROM " + table + " WHERE " + col + " IS NOT NULL FETCH FIRST 5 ROWS ONLY");
                        System.out.print(col + " values: ");
                        while (rs.next()) {
                            System.out.print("'" + rs.getString(1) + "' ");
                        }
                        System.out.println();
                    } catch (Exception e) {
                        System.out.println("Error querying " + col + ": " + e.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isColumnExist(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        try (ResultSet rs = md.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }
}
