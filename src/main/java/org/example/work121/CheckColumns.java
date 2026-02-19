package org.example.work121;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class CheckColumns implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(CheckColumns.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== CHECKING COLUMNS ===");
        try {
            checkTable("JC_CORE_WT_INFO");
            checkTable("EXO");
            checkTable("JZS_ENTRUSTMENT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=== DONE ===");
            // System.exit(0);
        }

        private void checkTable(String tableName) {
        String sql = "SELECT COLUMN_NAME FROM user_tab_columns WHERE table_name = ?";
        try {
            java.util.List<String> columns = jdbcTemplate.queryForList(sql, String.class, tableName);
            if (columns != null && !columns.isEmpty()) {
                System.out.println("Table " + tableName + " found with " + columns.size() + " columns:");
                for (String col : columns) {
                    System.out.print(col + " ");
                }
                System.out.println();
            } else {
                System.out.println("Table " + tableName + " not found.");
            }
        } catch (Exception e) {
            System.out.println("Error checking table " + tableName + ": " + e.getMessage());
        }
    }
}
