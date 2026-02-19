package org.example.work121.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            System.out.println("==================================================");
            System.out.println("Connected to Database: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Database Version: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("Database URL: " + conn.getMetaData().getURL());
            System.out.println("==================================================");
        }

        // Fix T_SIMPLE_DIRECTORY schema if needed
        fixSimpleDirectorySchema();
        
        // Fix JZS_SIGNATURE schema if needed
        fixJzsSignatureSchema();
        
        // Ensure JC_CORE_WT_INFO_EXT exists
        ensureJcCoreWtInfoExtExists();

        // Ensure T_ENTRUSTMENT exists (Replaces EXO)
        ensureTEntrustmentExists();

        // Ensure Test Tables exist
        ensureTestTablesExist();

        // Add columns to T_SIMPLE_DIRECTORY
        addColumnSafe("T_SIMPLE_DIRECTORY", "TESTER", "VARCHAR2(64)");
        addColumnSafe("T_SIMPLE_DIRECTORY", "REVIEWER", "VARCHAR2(64)");
        addColumnSafe("T_SIMPLE_DIRECTORY", "APPROVER", "VARCHAR2(64)");

        // Add columns to JC_CORE_WT_INFO_EXT (Main table)
        addColumnSafe("JC_CORE_WT_INFO_EXT", "BUILDING_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO_EXT", "SAMPLING_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO_EXT", "YW_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO_EXT", "REVIEW_SIGNATURE_PHOTO", "CLOB");
        addColumnSafe("JC_CORE_WT_INFO_EXT", "INSPECT_SIGNATURE_PHOTO", "CLOB");
        addColumnSafe("JC_CORE_WT_INFO_EXT", "APPROVE_SIGNATURE_PHOTO", "CLOB");
        
        // Ensure EXO table (if it exists as a separate table or synonym) has these columns
        // This is to fix ORA-00904 in JcCoreWtInfoMapper.insertExt
        try {
            if (tableExists("EXO")) {
                addColumnSafe("EXO", "REVIEW_SIGNATURE_PHOTO", "CLOB");
                addColumnSafe("EXO", "INSPECT_SIGNATURE_PHOTO", "CLOB");
                addColumnSafe("EXO", "APPROVE_SIGNATURE_PHOTO", "CLOB");
            }
        } catch (Exception e) {
            System.out.println("Error checking/updating EXO table: " + e.getMessage());
        }

        // Ensure T_ENTRUSTMENT has all necessary columns (for insertExt/updateExt if we switch to it)
        try {
            if (tableExists("T_ENTRUSTMENT")) {
                // New fields from EXO definition
                addColumnSafe("T_ENTRUSTMENT", "SAMPLE_NUMBER", "VARCHAR2(255)");
                addColumnSafe("T_ENTRUSTMENT", "SPEC", "VARCHAR2(255)");
                addColumnSafe("T_ENTRUSTMENT", "MANUFACTURER", "VARCHAR2(255)");
                addColumnSafe("T_ENTRUSTMENT", "SAMPLE_QUANTITY", "VARCHAR2(255)");
                addColumnSafe("T_ENTRUSTMENT", "REPRESENTATIVE_BATCH", "VARCHAR2(255)");
                addColumnSafe("T_ENTRUSTMENT", "BATCH_NUMBER", "VARCHAR2(255)");
                addColumnSafe("T_ENTRUSTMENT", "CLIENT_ADDRESS_PHONE", "VARCHAR2(500)");
                addColumnSafe("T_ENTRUSTMENT", "REPORT_SEND_MODE", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "REPORT_SEND_USER", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "SAMPLE_DISPOSAL", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "DELIVERY_MODE", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "DELIVERY_DATE", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "FEE", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "SAMPLE_HISTORY", "VARCHAR2(2000)");
                addColumnSafe("T_ENTRUSTMENT", "SAMPLE_STATUS", "VARCHAR2(2000)");
                addColumnSafe("T_ENTRUSTMENT", "TEST_ITEMS", "VARCHAR2(2000)");
                addColumnSafe("T_ENTRUSTMENT", "WITNESS_ID_CARD", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "SAMPLING_MAN_ID_CARD", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "WT_MAN_SIGN", "CLOB");
                addColumnSafe("T_ENTRUSTMENT", "WT_REVIEWER", "VARCHAR2(64)");
                addColumnSafe("T_ENTRUSTMENT", "WT_REVIEW_SIGN", "CLOB");
                
                // Signature photos
                addColumnSafe("T_ENTRUSTMENT", "REVIEW_SIGNATURE_PHOTO", "CLOB");
                addColumnSafe("T_ENTRUSTMENT", "INSPECT_SIGNATURE_PHOTO", "CLOB");
                addColumnSafe("T_ENTRUSTMENT", "APPROVE_SIGNATURE_PHOTO", "CLOB");
            }
        } catch (Exception e) {
            System.out.println("Error checking/updating T_ENTRUSTMENT table: " + e.getMessage());
        }


        // Add columns to JC_CORE_WT_INFO (Extension table)
        // Ensure extension columns exist
        addColumnSafe("JC_CORE_WT_INFO", "WT_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JD_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JZ_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JC_TASK_JS_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JC_TASK_REG_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JC_TASK_SH_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JC_TASK_XD_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JC_TASK_TJ_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "PRODUCE_GD_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "SEND_BACK_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "YW_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "CY_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "WT_UNIT_PERSON", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "YY_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "GC_ZJ", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JL_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "WT_REG_NAME", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "CHOUYANGREN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "JS_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "KC_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "SG_MAN", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "SJ_MAN", "VARCHAR2(64)");

        addColumnSafe("JC_CORE_WT_INFO", "JZ_UNIT", "VARCHAR2(255)");
        addColumnSafe("JC_CORE_WT_INFO", "KC_UNIT", "VARCHAR2(255)");
        addColumnSafe("JC_CORE_WT_INFO", "WT_MAN_TEL", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "WT_UNIT_ADDRESS", "VARCHAR2(255)");
        addColumnSafe("JC_CORE_WT_INFO", "WT_UNIT_TEL", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "GC_GCPQ", "VARCHAR2(255)");
        addColumnSafe("JC_CORE_WT_INFO", "PD_PASS_CODE", "VARCHAR2(255)");
        addColumnSafe("JC_CORE_WT_INFO", "OL_WT_NUM", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "WT_JCCS", "CLOB");
        addColumnSafe("JC_CORE_WT_INFO", "SAMPLE_QUANTITY", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "REPRESENTATIVE_BATCH", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "SAMPLE_DISPOSAL", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "REPORT_SEND_MODE", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "REPORT_SEND_USER", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "DELIVERY_MODE", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "DELIVERY_DATE", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "FEE", "VARCHAR2(64)");
        addColumnSafe("JC_CORE_WT_INFO", "SAMPLE_HISTORY", "VARCHAR2(1000)");

        // Add WT_MAN_SIGN to JC_CORE_WT_INFO and EXO
        addColumnSafe("JC_CORE_WT_INFO", "WT_MAN_SIGN", "CLOB");
        try {
            if (tableExists("EXO")) {
                addColumnSafe("EXO", "WT_MAN_SIGN", "CLOB");
            }
        } catch (Exception e) {
            System.out.println("Error checking/updating EXO table: " + e.getMessage());
        }

        // Add Record Role Columns to Test Tables
        String[] testTables = {
            "T_DENSITY_TEST",
            "T_REBOUND_METHOD",
            "T_SAND_REPLACEMENT",
            "JZS_LIGHT_DYNAMIC_PENETRATION",
            "T_BECKMAN_BEAM",
            "T_CUTTING_RING",
            "T_NUCLEAR_DENSITY",
            "T_WATER_REPLACEMENT"
        };
        
        for (String table : testTables) {
            addColumnSafe(table, "FILLER", "VARCHAR2(64)");
            addColumnSafe(table, "RECORD_TESTER", "VARCHAR2(64)");
            addColumnSafe(table, "RECORD_REVIEWER", "VARCHAR2(64)");
            addColumnSafe(table, "RECORD_REVIEW_SIGN", "CLOB");
            addColumnSafe(table, "REVIEW_SIGNATURE_PHOTO", "CLOB");
            addColumnSafe(table, "INSPECT_SIGNATURE_PHOTO", "CLOB");
            addColumnSafe(table, "APPROVE_SIGNATURE_PHOTO", "CLOB");
            addColumnSafe(table, "TESTER", "VARCHAR2(64)");
            addColumnSafe(table, "REVIEWER", "VARCHAR2(64)");
            addColumnSafe(table, "APPROVER", "VARCHAR2(64)");
        }
        addColumnSafe("JC_CORE_WT_INFO", "CLIENT_ADDRESS_PHONE", "VARCHAR2(255)");
        addColumnSafe("JC_CORE_WT_INFO", "GC_BEIZHU", "VARCHAR2(255)");
        addColumnSafe("JC_CORE_WT_INFO", "WT_STATUS", "VARCHAR2(64)");
    }
    
    private void fixSimpleDirectorySchema() {
        try {
            // Check if DIR_ID column exists
            jdbcTemplate.execute("SELECT DIR_ID FROM T_SIMPLE_DIRECTORY WHERE 1=0");
            System.out.println("T_SIMPLE_DIRECTORY schema is correct.");
        } catch (Exception e) {
            System.out.println("T_SIMPLE_DIRECTORY schema mismatch detected. Recreating table...");
            try {
                jdbcTemplate.execute("DROP TABLE T_SIMPLE_DIRECTORY");
            } catch (Exception dropEx) {
                // Ignore if table doesn't exist
            }
            try {
                String sql = "CREATE TABLE T_SIMPLE_DIRECTORY (" +
                        "    ID VARCHAR2(64) NOT NULL PRIMARY KEY," +
                        "    DIR_ID VARCHAR2(64)," +
                        "    DIR_NAME VARCHAR2(255)," +
                        "    TABLE1_TYPE VARCHAR2(64), TABLE1_ID VARCHAR2(64)," +
                        "    TABLE2_TYPE VARCHAR2(64), TABLE2_ID VARCHAR2(64)," +
                        "    TABLE3_TYPE VARCHAR2(64), TABLE3_ID VARCHAR2(64)," +
                        "    TABLE4_TYPE VARCHAR2(64), TABLE4_ID VARCHAR2(64)," +
                        "    TABLE5_TYPE VARCHAR2(64), TABLE5_ID VARCHAR2(64)," +
                        "    TABLE6_TYPE VARCHAR2(64), TABLE6_ID VARCHAR2(64)," +
                        "    TABLE7_TYPE VARCHAR2(64), TABLE7_ID VARCHAR2(64)," +
                        "    TABLE8_TYPE VARCHAR2(64), TABLE8_ID VARCHAR2(64)," +
                        "    TABLE9_TYPE VARCHAR2(64), TABLE9_ID VARCHAR2(64)," +
                        "    TABLE10_TYPE VARCHAR2(64), TABLE10_ID VARCHAR2(64)," +
                        "    STATUS NUMBER," +
                        "    TESTER VARCHAR2(64)," +
                        "    REVIEWER VARCHAR2(64)," +
                        "    APPROVER VARCHAR2(64)," +
                        "    CREATE_MAN VARCHAR2(64)," +
                        "    CREATE_TIME TIMESTAMP," +
                        "    UPDATE_MAN VARCHAR2(64)," +
                        "    UPDATE_TIME TIMESTAMP" +
                        ")";
                jdbcTemplate.execute(sql);
                System.out.println("T_SIMPLE_DIRECTORY table recreated successfully.");
            } catch (Exception createEx) {
                System.out.println("Failed to recreate T_SIMPLE_DIRECTORY: " + createEx.getMessage());
            }
        }
    }

    private void fixJzsSignatureSchema() {
        try {
            // Check if REMARKS column exists (this was the missing one causing errors)
            jdbcTemplate.execute("SELECT REMARKS FROM JZS_SIGNATURE WHERE 1=0");
            System.out.println("JZS_SIGNATURE schema is correct.");
        } catch (Exception e) {
            System.out.println("JZS_SIGNATURE schema mismatch detected. Recreating table...");
            try {
                jdbcTemplate.execute("DROP TABLE JZS_SIGNATURE");
            } catch (Exception dropEx) {
                // Ignore if table doesn't exist
            }
            try {
                String sql = "CREATE TABLE JZS_SIGNATURE (" +
                        "    SIGNATURE_ID VARCHAR2(64) NOT NULL PRIMARY KEY," +
                        "    USER_ACCOUNT VARCHAR2(64)," +
                        "    SIGNATURE_TYPE VARCHAR2(64)," +
                        "    SIGNATURE_BLOB BLOB," +
                        "    IMAGE_TYPE VARCHAR2(64)," +
                        "    IMAGE_SIZE NUMBER," +
                        "    CREATE_TIME TIMESTAMP," +
                        "    UPDATE_TIME TIMESTAMP," +
                        "    REMARKS VARCHAR2(500)" +
                        ")";
                jdbcTemplate.execute(sql);
                System.out.println("JZS_SIGNATURE table recreated successfully.");
            } catch (Exception createEx) {
                System.out.println("Failed to recreate JZS_SIGNATURE: " + createEx.getMessage());
            }
        }
    }

    private void ensureJcCoreWtInfoExtExists() {
        if (!tableExists("JC_CORE_WT_INFO_EXT")) {
            System.out.println("Table JC_CORE_WT_INFO_EXT missing. Creating...");
            try {
                String sql = "CREATE TABLE JC_CORE_WT_INFO_EXT (" +
                        "    ID VARCHAR2(64) NOT NULL PRIMARY KEY," +
                        "    WT_NUM VARCHAR2(64)," +
                        "    PROJECT_NAME VARCHAR2(255)," +
                        "    CLIENT_UNIT VARCHAR2(255)," +
                        "    STATUS VARCHAR2(64)," +
                        "    REJECT_REASON VARCHAR2(500)," +
                        "    NEXT_HANDLER VARCHAR2(64)," +
                        "    CREATE_BY VARCHAR2(64)," +
                        "    CREATE_TIME TIMESTAMP," +
                        "    UPDATE_BY VARCHAR2(64)," +
                        "    UPDATE_TIME TIMESTAMP," +
                        "    TESTER VARCHAR2(64)," +
                        "    REVIEWER VARCHAR2(64)," +
                        "    APPROVER VARCHAR2(64)," +
                        "    COMMISSION_DATE DATE," +
                        "    CLIENT VARCHAR2(64)," +
                        "    CONSTRUCTION_PART VARCHAR2(255)," +
                        "    CONSTRUCTION_UNIT VARCHAR2(255)," +
                        "    BUILDING_UNIT VARCHAR2(255)," +
                        "    SUPERVISION_UNIT VARCHAR2(255)," +
                        "    WITNESS_UNIT VARCHAR2(255)," +
                        "    WITNESS VARCHAR2(64)," +
                        "    TEST_CATEGORY VARCHAR2(64)," +
                        "    PROJECT_AREA VARCHAR2(255)," +
                        "    BEIZHU VARCHAR2(500)," +
                        "    SAMPLE_NAME VARCHAR2(255)," +
                        "    SAMPLE_NUMBER VARCHAR2(255)," +
                        "    SPEC VARCHAR2(255)," +
                        "    MANUFACTURER VARCHAR2(255)," +
                        "    SAMPLE_QUANTITY VARCHAR2(255)," +
                        "    REPRESENTATIVE_BATCH VARCHAR2(255)," +
                        "    BATCH_NUMBER VARCHAR2(255)," +
                        "    CLIENT_ADDRESS_PHONE VARCHAR2(500)," +
                        "    REPORT_SEND_MODE VARCHAR2(64)," +
                        "    REPORT_SEND_USER VARCHAR2(64)," +
                        "    SAMPLE_DISPOSAL VARCHAR2(64)," +
                        "    DELIVERY_MODE VARCHAR2(64)," +
                        "    DELIVERY_DATE VARCHAR2(64)," +
                        "    FEE VARCHAR2(64)," +
                        "    SAMPLE_HISTORY VARCHAR2(2000)," +
                        "    SAMPLE_STATUS VARCHAR2(2000)," +
                        "    TEST_ITEMS VARCHAR2(2000)," +
                        "    WITNESS_ID_CARD VARCHAR2(64)," +
                        "    SAMPLING_MAN_ID_CARD VARCHAR2(64)" +
                        ")";
                jdbcTemplate.execute(sql);
                System.out.println("Table JC_CORE_WT_INFO_EXT created successfully.");
            } catch (Exception e) {
                System.out.println("Failed to create JC_CORE_WT_INFO_EXT: " + e.getMessage());
            }
        } else {
             System.out.println("Table JC_CORE_WT_INFO_EXT already exists.");
        }
    }

    private void ensureTEntrustmentExists() {
        if (!tableExists("T_ENTRUSTMENT")) {
            System.out.println("Table T_ENTRUSTMENT missing. Creating...");
            try {
                String sql = "CREATE TABLE T_ENTRUSTMENT (" +
                        "    ID VARCHAR2(64) NOT NULL PRIMARY KEY," +
                        "    WT_NUM VARCHAR2(64)," +
                        "    PROJECT_NAME VARCHAR2(255)," +
                        "    CLIENT_UNIT VARCHAR2(255)," +
                        "    STATUS VARCHAR2(64)," +
                        "    REJECT_REASON VARCHAR2(500)," +
                        "    NEXT_HANDLER VARCHAR2(64)," +
                        "    CREATE_BY VARCHAR2(64)," +
                        "    CREATE_TIME TIMESTAMP," +
                        "    UPDATE_BY VARCHAR2(64)," +
                        "    UPDATE_TIME TIMESTAMP," +
                        "    TESTER VARCHAR2(64)," +
                        "    REVIEWER VARCHAR2(64)," +
                        "    APPROVER VARCHAR2(64)," +
                        "    COMMISSION_DATE DATE," +
                        "    CLIENT VARCHAR2(64)," +
                        "    CONSTRUCTION_PART VARCHAR2(255)," +
                        "    CONSTRUCTION_UNIT VARCHAR2(255)," +
                        "    BUILDING_UNIT VARCHAR2(255)," +
                        "    SUPERVISION_UNIT VARCHAR2(255)," +
                        "    WITNESS_UNIT VARCHAR2(255)," +
                        "    WITNESS VARCHAR2(64)," +
                        "    TEST_CATEGORY VARCHAR2(64)," +
                        "    PROJECT_AREA VARCHAR2(255)," +
                        "    BEIZHU VARCHAR2(500)," +
                        "    SAMPLE_NAME VARCHAR2(255)," +
                        "    SAMPLE_NUMBER VARCHAR2(255)," +
                        "    SPEC VARCHAR2(255)," +
                        "    MANUFACTURER VARCHAR2(255)," +
                        "    SAMPLE_QUANTITY VARCHAR2(255)," +
                        "    REPRESENTATIVE_BATCH VARCHAR2(255)," +
                        "    BATCH_NUMBER VARCHAR2(255)," +
                        "    CLIENT_ADDRESS_PHONE VARCHAR2(500)," +
                        "    REPORT_SEND_MODE VARCHAR2(64)," +
                        "    REPORT_SEND_USER VARCHAR2(64)," +
                        "    SAMPLE_DISPOSAL VARCHAR2(64)," +
                        "    DELIVERY_MODE VARCHAR2(64)," +
                        "    DELIVERY_DATE VARCHAR2(64)," +
                        "    FEE VARCHAR2(64)," +
                        "    SAMPLE_HISTORY VARCHAR2(2000)," +
                        "    SAMPLE_STATUS VARCHAR2(2000)," +
                        "    TEST_ITEMS VARCHAR2(2000)," +
                        "    WITNESS_ID_CARD VARCHAR2(64)," +
                        "    SAMPLING_MAN_ID_CARD VARCHAR2(64)," +
                        "    WT_MAN_SIGN CLOB," +
                        "    WT_REVIEWER VARCHAR2(64)," +
                        "    WT_REVIEW_SIGN CLOB," +
                        "    REVIEW_SIGNATURE_PHOTO CLOB," +
                        "    INSPECT_SIGNATURE_PHOTO CLOB," +
                        "    APPROVE_SIGNATURE_PHOTO CLOB" +
                        ")";
                jdbcTemplate.execute(sql);
                System.out.println("Table T_ENTRUSTMENT created successfully.");
            } catch (Exception e) {
                System.out.println("Failed to create T_ENTRUSTMENT: " + e.getMessage());
            }
        } else {
             System.out.println("Table T_ENTRUSTMENT already exists.");
        }
    }

    private void ensureTestTablesExist() {
        String[] testTables = {
            "T_DENSITY_TEST",
            "T_REBOUND_METHOD",
            "T_SAND_REPLACEMENT",
            "JZS_LIGHT_DYNAMIC_PENETRATION",
            "T_BECKMAN_BEAM",
            "T_CUTTING_RING",
            "T_NUCLEAR_DENSITY",
            "T_WATER_REPLACEMENT"
        };

        for (String tableName : testTables) {
            if (!tableExists(tableName)) {
                System.out.println("Table " + tableName + " missing. Creating...");
                try {
                    String sql = "CREATE TABLE " + tableName + " (" +
                            "    ID VARCHAR2(64) NOT NULL PRIMARY KEY," +
                            "    ENTRUSTMENT_ID VARCHAR2(64)," +
                            "    DATA_JSON CLOB," +
                            "    STATUS VARCHAR2(64)," +
                            "    REJECT_REASON VARCHAR2(500)," +
                            "    NEXT_HANDLER VARCHAR2(64)," +
                            "    TESTER_SIGNATURE_PHOTO CLOB," +
                            "    REVIEW_SIGNATURE_PHOTO CLOB," +
                            "    INSPECT_SIGNATURE_PHOTO CLOB," +
                            "    APPROVE_SIGNATURE_PHOTO CLOB," +
                            "    CREATE_BY VARCHAR2(64)," +
                            "    CREATE_TIME TIMESTAMP," +
                            "    UPDATE_BY VARCHAR2(64)," +
                            "    UPDATE_TIME TIMESTAMP," +
                            "    TESTER VARCHAR2(64)," +
                            "    REVIEWER VARCHAR2(64)," +
                            "    APPROVER VARCHAR2(64)," +
                            "    FILLER VARCHAR2(64)," +
                            "    RECORD_TESTER VARCHAR2(64)," +
                            "    RECORD_REVIEWER VARCHAR2(64)," +
                            "    RECORD_REVIEW_SIGN CLOB" +
                            ")";
                    jdbcTemplate.execute(sql);
                    System.out.println("Table " + tableName + " created successfully.");
                } catch (Exception e) {
                    System.out.println("Failed to create " + tableName + ": " + e.getMessage());
                }
            } else {
                 System.out.println("Table " + tableName + " already exists.");
            }
        }
    }

    private boolean tableExists(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM user_tables WHERE table_name = ?", 
                Integer.class, 
                tableName.toUpperCase()
            );
            return count != null && count > 0;
        } catch (Exception e) {
            System.out.println("Error checking table existence: " + e.getMessage());
            return false;
        }
    }
    
    private boolean columnExists(String tableName, String columnName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM user_tab_columns WHERE table_name = ? AND column_name = ?", 
                Integer.class, 
                tableName.toUpperCase(),
                columnName.toUpperCase()
            );
            return count != null && count > 0;
        } catch (Exception e) {
            System.out.println("Error checking column existence: " + e.getMessage());
            return false;
        }
    }

    private void addColumnSafe(String tableName, String columnName, String type) {
        if (columnExists(tableName, columnName)) {
            System.out.println("Column " + columnName + " already exists in " + tableName + " (checked via user_tab_columns)");
            return;
        }
        
        try {
            String sql = "ALTER TABLE " + tableName + " ADD (" + columnName + " " + type + ")";
            jdbcTemplate.execute(sql);
            System.out.println("Added column " + columnName + " to " + tableName);
        } catch (Exception e) {
            System.out.println("Failed to add column " + columnName + " to " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
