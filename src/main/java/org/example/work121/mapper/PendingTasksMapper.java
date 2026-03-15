package org.example.work121.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
     *
     * @param status 状态（0=待提交，1=待审核）
     * @return 待审核任务列表
     */
    @Select("SELECT " +
            "'委托单' AS table_type, " +
            "e.ID AS data_id, " +
            "e.WT_NUM AS unified_number, " +
            "NVL(e.STATUS, '0') AS status, " +
            "NVL(e.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN NVL(d.WT_UNDERTAKER, e.TESTER) WHEN #{status} IN ('4','5') THEN NVL(e.APPROVER, d.BG_APPROVER) ELSE NVL(d.WT_REVIEWER, e.REVIEWER) END) AS reviewer, " +
            "e.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_ENTRUSTMENT e " +
            "LEFT JOIN T_SIMPLE_DIRECTORY d ON d.DIR_NAME = e.WT_NUM " +
            "WHERE (NVL(e.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(e.STATUS, '0') = '2')) " +
            "AND e.WT_NUM IS NOT NULL " +
            "UNION ALL " +
            "SELECT " +
            "'贝克曼梁' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_BECKMAN_BEAM t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "UNION ALL " +
            "SELECT " +
            "'轻型动力触探' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "UNION ALL " +
            "SELECT " +
            "'回弹法' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_REBOUND_METHOD t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "UNION ALL " +
            "SELECT " +
            "'环刀法' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_CUTTING_RING t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "UNION ALL " +
            "SELECT " +
            "'灌水法' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_WATER_REPLACEMENT t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "UNION ALL " +
            "SELECT " +
            "'灌砂法' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_SAND_REPLACEMENT t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "UNION ALL " +
            "SELECT " +
            "'核子密度' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_NUCLEAR_DENSITY t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "UNION ALL " +
            "SELECT " +
            "'密度试验' AS table_type, " +
            "t.ID AS data_id, " +
            "NVL(e.WT_NUM, t.ENTRUSTMENT_ID) AS unified_number, " +
            "NVL(t.STATUS, '0') AS status, " +
            "NVL(t.NEXT_HANDLER, CASE WHEN #{status} = '0' THEN t.FILLER WHEN #{status} IN ('4','5') THEN t.APPROVER ELSE t.RECORD_REVIEWER END) AS reviewer, " +
            "t.CREATE_TIME AS create_time, " +
            "e.CLIENT_UNIT AS client_unit, " +
            "e.PROJECT_NAME AS project_name " +
            "FROM T_DENSITY_TEST t " +
            "LEFT JOIN T_ENTRUSTMENT e ON (t.ENTRUSTMENT_ID = e.ID OR t.ENTRUSTMENT_ID = e.WT_NUM) " +
            "WHERE (NVL(t.STATUS, '0') = #{status} OR (#{status} = '0' AND NVL(t.STATUS, '0') = '2')) " +
            "ORDER BY create_time DESC, unified_number ASC")
    List<Map<String, Object>> getAllPendingTasks(@Param("status") String status);

    /**
     * 根据任务类型搜索待审核任务
     *
     * @param taskType 任务类型
     * @param status   状态（0=待提交，1=待审核）
     * @return 待审核任务列表
     */
    @Select("<script>SELECT * FROM (  <!-- 待批准任务 (status=5) -->  <if test=\"status == '5'\">    SELECT '贝克曼梁' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_BECKMAN_BEAM t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '贝克曼梁' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '轻型动力触探' AS table_type,             t.ID AS data_id,             NVL(e.WT_NUM, t.ID) AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_LIGHT_DYNAMIC_PENETRATION t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '轻型动力触探' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '回弹法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_REBOUND_METHOD t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '回弹法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '环刀法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_CUTTING_RING t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '环刀法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '灌水法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_WATER_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '灌水法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '灌砂法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_SAND_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '灌砂法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '核子密度' AS table_type,             t.ID AS data_id,             t.ENTRUSTMENT_ID AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_NUCLEAR_DENSITY t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '核子密度' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '密度试验' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             t.APPROVER AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_DENSITY_TEST t      LEFT JOIN T_ENTRUSTMENT e ON t.ID = e.ID      WHERE t.STATUS = #{status}        AND '密度试验' LIKE CONCAT('%', #{taskType}, '%')  </if>  <!-- 待审核和待提交任务 (status=1 或 status=0) -->  <if test=\"status != '5'\">    SELECT '委托单' AS table_type,             ID AS data_id,             WT_NUM AS unified_number,             STATUS AS status,             CASE WHEN #{status} = '0' THEN TESTER ELSE REVIEWER END AS reviewer,             CREATE_TIME AS create_time,             CLIENT_UNIT AS client_unit,             PROJECT_NAME AS project_name      FROM T_ENTRUSTMENT      WHERE STATUS = #{status}        AND WT_NUM IS NOT NULL        AND '委托单' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '贝克曼梁' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_BECKMAN_BEAM t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '贝克曼梁' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '轻型动力触探' AS table_type,             t.ID AS data_id,             NVL(e.WT_NUM, t.ID) AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_LIGHT_DYNAMIC_PENETRATION t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '轻型动力触探' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '回弹法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_REBOUND_METHOD t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '回弹法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '环刀法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_CUTTING_RING t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '环刀法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '灌水法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_WATER_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '灌水法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '灌砂法' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_SAND_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '灌砂法' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '核子密度' AS table_type,             t.ID AS data_id,             t.ENTRUSTMENT_ID AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_NUCLEAR_DENSITY t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND '核子密度' LIKE CONCAT('%', #{taskType}, '%')      UNION ALL      SELECT '密度试验' AS table_type,             t.ID AS data_id,             t.entrustment_id AS unified_number,             t.STATUS AS status,             CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,             t.CREATE_TIME AS create_time,             e.CLIENT_UNIT AS client_unit,             e.PROJECT_NAME AS project_name      FROM T_DENSITY_TEST t      LEFT JOIN T_ENTRUSTMENT e ON t.ID = e.ID      WHERE t.STATUS = #{status}        AND '密度试验' LIKE CONCAT('%', #{taskType}, '%')  </if>) t ORDER BY t.create_time DESC, t.unified_number ASC</script>"
    )
    List<Map<String, Object>> searchPendingTasks(@Param("taskType") String taskType, @Param("status") String status);


    /**
     * 根据用户账号和状态查询待办任务
     *
     * @param userAccount 用户账号
     * @param status      任务状态（字符串类型，如"5"、"0"等）
     * @return 待办任务列表
     */
    @Select("<script>SELECT * FROM (  <!-- 状态为5的分支：查询各类试验表，按审批人筛选 -->  <if test='status != null and status.trim() == \"5\"'>    SELECT '贝克曼梁' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_BECKMAN_BEAM t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')      UNION ALL      SELECT '轻型动力触探' AS table_type,            t.ID AS data_id,            NVL(e.WT_NUM, t.ID) AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_LIGHT_DYNAMIC_PENETRATION t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')      UNION ALL      SELECT '回弹法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_REBOUND_METHOD t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')      UNION ALL      SELECT '环刀法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_CUTTING_RING t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')      UNION ALL      SELECT '灌水法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_WATER_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')      UNION ALL      SELECT '灌砂法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_SAND_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')      UNION ALL      SELECT '核子密度' AS table_type,            t.ID AS data_id,            t.ENTRUSTMENT_ID AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_NUCLEAR_DENSITY t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')      UNION ALL      SELECT '密度试验' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            t.APPROVER AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_DENSITY_TEST t      LEFT JOIN T_ENTRUSTMENT e ON t.ID = e.ID      WHERE t.STATUS = #{status}        AND t.APPROVER = #{userAccount}        AND (t.REPORT_STATUS IS NULL OR t.REPORT_STATUS = '')  </if>  <!-- 状态非5的分支：查询委托单+各类试验表，按填报人/审核人筛选 -->  <if test='status == null or status.trim() != \"5\"'>    SELECT '委托单' AS table_type,            ID AS data_id,            WT_NUM AS unified_number,            STATUS AS status,            CASE WHEN #{status} = '0' THEN TESTER ELSE REVIEWER END AS reviewer,            CREATE_TIME AS create_time,            CLIENT_UNIT AS client_unit,            PROJECT_NAME AS project_name      FROM T_ENTRUSTMENT      WHERE STATUS = #{status}        AND WT_NUM IS NOT NULL        AND CASE WHEN #{status} = '0' THEN TESTER ELSE REVIEWER END = #{userAccount}      UNION ALL      SELECT '贝克曼梁' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_BECKMAN_BEAM t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}      UNION ALL      SELECT '轻型动力触探' AS table_type,            t.ID AS data_id,            NVL(e.WT_NUM, t.ID) AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_LIGHT_DYNAMIC_PENETRATION t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}      UNION ALL      SELECT '回弹法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_REBOUND_METHOD t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}      UNION ALL      SELECT '环刀法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_CUTTING_RING t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}      UNION ALL      SELECT '灌水法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_WATER_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}      UNION ALL      SELECT '灌砂法' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_SAND_REPLACEMENT t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}      UNION ALL      SELECT '核子密度' AS table_type,            t.ID AS data_id,            t.ENTRUSTMENT_ID AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_NUCLEAR_DENSITY t      LEFT JOIN T_ENTRUSTMENT e ON t.ENTRUSTMENT_ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}      UNION ALL      SELECT '密度试验' AS table_type,            t.ID AS data_id,            t.entrustment_id AS unified_number,            t.STATUS AS status,            CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END AS reviewer,            t.CREATE_TIME AS create_time,            e.CLIENT_UNIT AS client_unit,            e.PROJECT_NAME AS project_name      FROM T_DENSITY_TEST t      LEFT JOIN T_ENTRUSTMENT e ON t.ID = e.ID      WHERE t.STATUS = #{status}        AND CASE WHEN #{status} = '0' THEN t.FILLER ELSE t.RECORD_REVIEWER END = #{userAccount}  </if>) t ORDER BY t.create_time DESC, t.unified_number ASC</script>"
    )
    List<Map<String, Object>> getPendingTasksByUser(
            @Param("userAccount") String userAccount,
            @Param("status") String status
    );

}
