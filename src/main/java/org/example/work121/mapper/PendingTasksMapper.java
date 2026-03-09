package org.example.work121.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 待处理任务数据访问接口
 */
@Mapper
public interface PendingTasksMapper {

    /**
     * 获取所有待审核任务列表
     * @return 待审核任务列表
     */
    @Select("SELECT " +
            "'委托单' AS table_type, " +
            "ID AS data_id, " +
            "WT_NUM AS unified_number, " +
            "STATUS AS status, " +
            "REVIEWER AS reviewer " +
            "FROM T_ENTRUSTMENT " +
            "WHERE STATUS = '1' " +
            "AND WT_NUM IS NOT NULL " +
            "UNION ALL " +
            "SELECT " +
            "'贝克曼梁' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_BECKMAN_BEAM t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "UNION ALL " +
            "SELECT " +
            "'轻型动力触探' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ID) AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "UNION ALL " +
            "SELECT " +
            "'回弹法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_REBOUND_METHOD t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "UNION ALL " +
            "SELECT " +
            "'环刀法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_CUTTING_RING t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "UNION ALL " +
            "SELECT " +
            "'灌水法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_WATER_REPLACEMENT t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "UNION ALL " +
            "SELECT " +
            "'灌砂法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_SAND_REPLACEMENT t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "UNION ALL " +
            "SELECT " +
            "'核子密度' AS table_type, " +
            "t.ID AS data_id, " +
            "t.ENTRUSTMENT_ID AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_NUCLEAR_DENSITY t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "UNION ALL " +
            "SELECT " +
            "'密度试验' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_DENSITY_TEST t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "ORDER BY unified_number ASC")
    List<Map<String, Object>> getAllPendingTasks();

    /**
     * 根据任务类型搜索待审核任务
     * @param taskType 任务类型
     * @return 待审核任务列表
     */
    @Select("SELECT " +
            "'委托单' AS table_type, " +
            "ID AS data_id, " +
            "WT_NUM AS unified_number, " +
            "STATUS AS status, " +
            "REVIEWER AS reviewer " +
            "FROM T_ENTRUSTMENT " +
            "WHERE STATUS = '1' " +
            "AND WT_NUM IS NOT NULL " +
            "AND '委托单' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'贝克曼梁' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_BECKMAN_BEAM t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '贝克曼梁' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'轻型动力触探' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ID) AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '轻型动力触探' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'回弹法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_REBOUND_METHOD t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '回弹法' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'环刀法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_CUTTING_RING t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '环刀法' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'灌水法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_WATER_REPLACEMENT t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '灌水法' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'灌砂法' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_SAND_REPLACEMENT t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '灌砂法' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'核子密度' AS table_type, " +
            "t.ID AS data_id, " +
            "t.ENTRUSTMENT_ID AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_NUCLEAR_DENSITY t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '核子密度' LIKE CONCAT('%', #{taskType}, '%') " +
            "UNION ALL " +
            "SELECT " +
            "'密度试验' AS table_type, " +
            "t.ID AS data_id, " +
            "t.entrustment_id AS unified_number, " +
            "t.STATUS AS status, " +
            "t.REVIEWER AS reviewer " +
            "FROM T_DENSITY_TEST t " +
            "LEFT JOIN T_ENTRUSTMENT e ON t.ID = e.WT_NUM " +
            "WHERE t.STATUS = '1' " +
            "AND '密度试验' LIKE CONCAT('%', #{taskType}, '%') " +
            "ORDER BY unified_number ASC")
    List<Map<String, Object>> searchPendingTasks(String taskType);
}