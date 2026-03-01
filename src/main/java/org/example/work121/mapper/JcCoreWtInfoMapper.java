package org.example.work121.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.work121.entity.JcCoreWtInfo;
import java.util.List;

@Mapper
public interface JcCoreWtInfoMapper {

    @Select("SELECT " +
            "t2.WT_ID as id, " +
            "t2.WT_NUM as wtNum, " +
            "COALESCE(t1.COMMISSION_DATE, t2.WT_DATE) as commissionDate, " +
            "COALESCE(t1.CLIENT_UNIT, t2.WT_UNIT) as clientUnit, " +
            "t1.CLIENT as client, " +
            "COALESCE(t1.PROJECT_NAME, t2.GC_NAME) as projectName, " +
            "t1.PROJECT_AREA as projectArea, " +
            "COALESCE(t1.CONSTRUCTION_PART, t2.GC_GCPQ) as constructionPart, " +
            "t2.GC_BEIZHU as projectRemarks, " +
            "COALESCE(t1.CONSTRUCTION_UNIT, t2.SG_UNIT) as constructionUnit, " +
            "COALESCE(t1.BUILDING_UNIT, t2.JS_UNIT) as buildingUnit, " +
            "COALESCE(t1.SUPERVISION_UNIT, t2.JL_UNIT) as supervisionUnit, " +
            "COALESCE(t1.WITNESS_UNIT, t2.JZ_UNIT) as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "COALESCE(TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegName, " +
            "TO_CHAR(t1.CREATE_BY) as createBy, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as createByName, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegRealName, " +
            "COALESCE(TO_CHAR(t1.STATUS), '0') as status, " +
            "t1.SAMPLE_STATUS as sampleStatus, " +
            "TO_CHAR(t1.TESTER) as tester, " +
            "COALESCE(u_tester.USER_NAME, TO_CHAR(t1.TESTER)) as testerName, " +
            "TO_CHAR(t1.REVIEWER) as reviewer, " +
            "COALESCE(u_reviewer.USER_NAME, TO_CHAR(t1.REVIEWER)) as reviewerName, " +
            "TO_CHAR(t1.APPROVER) as approver, " +
            "COALESCE(u_approver.USER_NAME, TO_CHAR(t1.APPROVER)) as approverName, " +
            "TO_CHAR(t1.UPDATE_BY) as updateBy, " +
            "COALESCE(u_update.USER_NAME, TO_CHAR(t1.UPDATE_BY)) as updateByName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.BEIZHU as remarks, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t2.JZ_UNIT as buildingUnit2, " +
            "t2.KC_UNIT as surveyUnit, " +
            "t2.WT_MAN_TEL as clientTel, " +
            "t2.WT_UNIT_ADDRESS as clientUnitAddress, " +
            "t2.WT_UNIT_TEL as clientUnitTel, " +
            "t2.GC_GCPQ as spec, " +
            "t2.PD_PASS_CODE as manufacturer, " +
            "t2.OL_WT_NUM as batchNumber, " +
            "t2.WT_JCCS as testItems, " +
            "t2.SAMPLE_QUANTITY as sampleQuantity, " +
            "t2.REPRESENTATIVE_BATCH as representativeBatch, " +
            "t2.SAMPLE_DISPOSAL as sampleDisposal, " +
            "t2.REPORT_SEND_MODE as reportSendMode, " +
            "t2.DELIVERY_MODE as deliveryMode, " +
            "t2.DELIVERY_DATE as deliveryDate, " +
            "t2.FEE as fee, " +
            "t2.SAMPLE_HISTORY as sampleHistory, " +
            "t2.SEND_BACK_REASON as rejectReason, " +
            "t2.YY_MAN as receiver, " +
            "t1.REPORT_SEND_USER as reportSendUser, " +
            "t1.WITNESS_ID_CARD as witnessIdCard, " +
            "t1.SAMPLING_MAN_ID_CARD as samplingManIdCard, " +
            "t1.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t1.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.WT_MAN_SIGN as wtManSign, " +
            "t1.WT_REVIEWER as wtReviewer, " +
            "t1.WT_REVIEW_SIGN as wtReviewSign, " +
            "t2.CLIENT_ADDRESS_PHONE as clientAddressPhone " +
            "FROM JC_CORE_WT_INFO t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.WT_ID = t1.ID " +
            "LEFT JOIN JZS_USERS u_create ON u_create.USER_ACCOUNT = TO_CHAR(t1.CREATE_BY) " +
            "LEFT JOIN JZS_USERS u_tester ON u_tester.USER_ACCOUNT = TO_CHAR(t1.TESTER) " +
            "LEFT JOIN JZS_USERS u_reviewer ON u_reviewer.USER_ACCOUNT = TO_CHAR(t1.REVIEWER) " +
            "LEFT JOIN JZS_USERS u_approver ON u_approver.USER_ACCOUNT = TO_CHAR(t1.APPROVER) " +
            "LEFT JOIN JZS_USERS u_update ON u_update.USER_ACCOUNT = TO_CHAR(t1.UPDATE_BY) " +
            "WHERE UPPER(TRIM(t2.WT_NUM)) = UPPER(TRIM(#{wtNum})) AND ROWNUM <= 1")
    List<JcCoreWtInfo> selectByWtNum(@Param("wtNum") String wtNum);

    /**
     * 根据 WT_ID（委托主键）查询一条委托信息，用于通过记录表里的 ENTRUSTMENT_ID 反查统一编号等。
     */
    @Select("SELECT " +
            "t2.WT_ID as id, " +
            "t2.WT_NUM as wtNum, " +
            "COALESCE(TO_CHAR(t1.STATUS), '0') as status " +
            "FROM JC_CORE_WT_INFO t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.WT_ID = t1.ID " +
            "WHERE t2.WT_ID = #{wtId} AND ROWNUM <= 1")
    JcCoreWtInfo selectByWtId(@Param("wtId") String wtId);

    @Select("SELECT t1.ID, t1.WT_NUM, t1.PROJECT_NAME, t1.CREATE_BY, t1.TESTER, t1.REVIEWER, t1.APPROVER FROM T_ENTRUSTMENT t1 WHERE ROWNUM <= 10")
    List<JcCoreWtInfo> debugSelectAll();

    @Select("<script>" +
            "SELECT DISTINCT " +
            "t2.WT_ID as id, " +
            "t2.WT_NUM as wtNum, " +
            "COALESCE(t1.COMMISSION_DATE, t2.WT_DATE) as commissionDate, " +
            "COALESCE(t1.CLIENT_UNIT, t2.WT_UNIT) as clientUnit, " +
            "t1.CLIENT as client, " +
            "COALESCE(t1.PROJECT_NAME, t2.GC_NAME) as projectName, " +
            "t1.PROJECT_AREA as projectArea, " +
            "COALESCE(t1.CONSTRUCTION_UNIT, t2.SG_UNIT) as constructionUnit, " +
            "COALESCE(t1.BUILDING_UNIT, t2.JS_UNIT) as buildingUnit, " +
            "COALESCE(t1.SUPERVISION_UNIT, t2.JL_UNIT) as supervisionUnit, " +
            "COALESCE(t1.WITNESS_UNIT, t2.JZ_UNIT) as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "COALESCE(TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegName, " +
            "TO_CHAR(t1.CREATE_BY) as createBy, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as createByName, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegRealName, " +
            "COALESCE(TO_CHAR(t1.STATUS), '0') as status, " +
            "t1.SAMPLE_STATUS as sampleStatus, " +
            "TO_CHAR(t1.TESTER) as tester, " +
            "COALESCE(u_tester.USER_NAME, TO_CHAR(t1.TESTER)) as testerName, " +
            "TO_CHAR(t1.REVIEWER) as reviewer, " +
            "COALESCE(u_reviewer.USER_NAME, TO_CHAR(t1.REVIEWER)) as reviewerName, " +
            "TO_CHAR(t1.APPROVER) as approver, " +
            "COALESCE(u_approver.USER_NAME, TO_CHAR(t1.APPROVER)) as approverName, " +
            "TO_CHAR(t1.UPDATE_BY) as updateBy, " +
            "COALESCE(u_update.USER_NAME, TO_CHAR(t1.UPDATE_BY)) as updateByName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.BEIZHU as remarks, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t2.JZ_UNIT as buildingUnit2, " +
            "t2.KC_UNIT as surveyUnit, " +
            "t2.WT_MAN_TEL as clientTel, " +
            "t2.WT_UNIT_ADDRESS as clientUnitAddress, " +
            "t2.WT_UNIT_TEL as clientUnitTel, " +
            "t2.GC_GCPQ as spec, " +
            "t2.PD_PASS_CODE as manufacturer, " +
            "t2.OL_WT_NUM as batchNumber, " +
            "t2.WT_JCCS as testItems, " +
            "t2.SAMPLE_QUANTITY as sampleQuantity, " +
            "t2.REPRESENTATIVE_BATCH as representativeBatch, " +
            "t2.SAMPLE_DISPOSAL as sampleDisposal, " +
            "t2.REPORT_SEND_MODE as reportSendMode, " +
            "t2.DELIVERY_MODE as deliveryMode, " +
            "t2.DELIVERY_DATE as deliveryDate, " +
            "t2.FEE as fee, " +
            "t2.SAMPLE_HISTORY as sampleHistory, " +
            "t1.REPORT_SEND_USER as reportSendUser, " +
            "t1.WITNESS_ID_CARD as witnessIdCard, " +
            "t1.SAMPLING_MAN_ID_CARD as samplingManIdCard, " +
            "t2.CLIENT_ADDRESS_PHONE as clientAddressPhone " +
            "FROM JC_CORE_WT_INFO t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.WT_ID = t1.ID " +
            "LEFT JOIN JZS_USERS u_create ON u_create.USER_ACCOUNT = TO_CHAR(t1.CREATE_BY) " +
            "LEFT JOIN JZS_USERS u_tester ON u_tester.USER_ACCOUNT = TO_CHAR(t1.TESTER) " +
            "LEFT JOIN JZS_USERS u_reviewer ON u_reviewer.USER_ACCOUNT = TO_CHAR(t1.REVIEWER) " +
            "LEFT JOIN JZS_USERS u_approver ON u_approver.USER_ACCOUNT = TO_CHAR(t1.APPROVER) " +
            "LEFT JOIN JZS_USERS u_update ON u_update.USER_ACCOUNT = TO_CHAR(t1.UPDATE_BY) " +
            "LEFT JOIN T_SIMPLE_DIRECTORY t3 ON t3.DIR_NAME = t2.WT_NUM " +
            "LEFT JOIN T_DENSITY_TEST t_density ON t_density.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_NUCLEAR_DENSITY t_nuclear ON t_nuclear.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_SAND_REPLACEMENT t_sand ON t_sand.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_WATER_REPLACEMENT t_water ON t_water.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_CUTTING_RING t_cutting ON t_cutting.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_REBOUND_METHOD t_rebound ON t_rebound.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN JZS_LIGHT_DYNAMIC_PENETRATION t_light ON t_light.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_BECKMAN_BEAM t_beckman ON t_beckman.ENTRUSTMENT_ID = t2.WT_ID " +
            "<where>" +
            "<if test='wtNum != null and wtNum != \"\"'>" +
            "t2.WT_NUM = #{wtNum} " +
            "</if>" +
            "<if test='names != null'>" +
            " AND (" +
            "<foreach collection='names' item='name' separator=' OR '>" +
            "   UPPER(TRIM(TO_CHAR(t2.WT_REG_NAME))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.WT_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JD_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JZ_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.CY_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.YW_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_JS_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_REG_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_SH_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_XD_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_TJ_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.PRODUCE_GD_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.SEND_BACK_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.REPORT_SEND_USER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.WT_UNIT_PERSON))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.YY_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.GC_ZJ))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JL_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.CHOUYANGREN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JS_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.KC_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.SG_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.SJ_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.NEXT_HANDLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.CLIENT))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.WITNESS))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.WT_UNDERTAKER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.WT_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.CREATE_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.UPDATE_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "</foreach> " +
            ")" +
            "</if>" +
            "</where>" +
            "ORDER BY commissionDate DESC" +
            "</script>")
    List<JcCoreWtInfo> selectByRegName(@Param("names") List<String> names, @Param("wtNum") String wtNum);

    @Select("<script>" +
            "SELECT DISTINCT " +
            "t2.WT_ID as id, " +
            "t2.WT_NUM as wtNum, " +
            "COALESCE(t1.COMMISSION_DATE, t2.WT_DATE) as commissionDate, " +
            "COALESCE(t1.CLIENT_UNIT, t2.WT_UNIT) as clientUnit, " +
            "t1.CLIENT as client, " +
            "COALESCE(t1.PROJECT_NAME, t2.GC_NAME) as projectName, " +
            "t1.PROJECT_AREA as projectArea, " +
            "COALESCE(t1.CONSTRUCTION_UNIT, t2.SG_UNIT) as constructionUnit, " +
            "COALESCE(t1.BUILDING_UNIT, t2.JS_UNIT) as buildingUnit, " +
            "COALESCE(t1.SUPERVISION_UNIT, t2.JL_UNIT) as supervisionUnit, " +
            "COALESCE(t1.WITNESS_UNIT, t2.JZ_UNIT) as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "COALESCE(TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegName, " +
            "TO_CHAR(t1.CREATE_BY) as createBy, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as createByName, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegRealName, " +
            "COALESCE(TO_CHAR(t1.STATUS), '0') as status, " +
            "t1.SAMPLE_STATUS as sampleStatus, " +
            "TO_CHAR(t1.TESTER) as tester, " +
            "COALESCE(u_tester.USER_NAME, TO_CHAR(t1.TESTER)) as testerName, " +
            "TO_CHAR(t1.REVIEWER) as reviewer, " +
            "COALESCE(u_reviewer.USER_NAME, TO_CHAR(t1.REVIEWER)) as reviewerName, " +
            "TO_CHAR(t1.APPROVER) as approver, " +
            "COALESCE(u_approver.USER_NAME, TO_CHAR(t1.APPROVER)) as approverName, " +
            "TO_CHAR(t1.UPDATE_BY) as updateBy, " +
            "COALESCE(u_update.USER_NAME, TO_CHAR(t1.UPDATE_BY)) as updateByName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.BEIZHU as remarks, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t2.JZ_UNIT as buildingUnit2, " +
            "t2.KC_UNIT as surveyUnit, " +
            "t2.WT_MAN_TEL as clientTel, " +
            "t2.WT_UNIT_ADDRESS as clientUnitAddress, " +
            "t2.WT_UNIT_TEL as clientUnitTel, " +
            "t2.GC_GCPQ as spec, " +
            "t2.PD_PASS_CODE as manufacturer, " +
            "t2.OL_WT_NUM as batchNumber, " +
            "t2.WT_JCCS as testItems, " +
            "t2.SAMPLE_QUANTITY as sampleQuantity, " +
            "t2.REPRESENTATIVE_BATCH as representativeBatch, " +
            "t2.SAMPLE_DISPOSAL as sampleDisposal, " +
            "t2.REPORT_SEND_MODE as reportSendMode, " +
            "t2.DELIVERY_MODE as deliveryMode, " +
            "t2.DELIVERY_DATE as deliveryDate, " +
            "t2.FEE as fee, " +
            "t2.SAMPLE_HISTORY as sampleHistory, " +
            "t1.REPORT_SEND_USER as reportSendUser, " +
            "t1.WITNESS_ID_CARD as witnessIdCard, " +
            "t1.SAMPLING_MAN_ID_CARD as samplingManIdCard, " +
            "t2.CLIENT_ADDRESS_PHONE as clientAddressPhone " +
            "FROM JC_CORE_WT_INFO t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.WT_ID = t1.ID " +
            "LEFT JOIN JZS_USERS u_create ON u_create.USER_ACCOUNT = TO_CHAR(t1.CREATE_BY) " +
            "LEFT JOIN JZS_USERS u_tester ON u_tester.USER_ACCOUNT = TO_CHAR(t1.TESTER) " +
            "LEFT JOIN JZS_USERS u_reviewer ON u_reviewer.USER_ACCOUNT = TO_CHAR(t1.REVIEWER) " +
            "LEFT JOIN JZS_USERS u_approver ON u_approver.USER_ACCOUNT = TO_CHAR(t1.APPROVER) " +
            "LEFT JOIN JZS_USERS u_update ON u_update.USER_ACCOUNT = TO_CHAR(t1.UPDATE_BY) " +
            "LEFT JOIN T_SIMPLE_DIRECTORY t3 ON t3.DIR_NAME = t2.WT_NUM " +
            "LEFT JOIN T_DENSITY_TEST t_density ON t_density.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_NUCLEAR_DENSITY t_nuclear ON t_nuclear.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_SAND_REPLACEMENT t_sand ON t_sand.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_WATER_REPLACEMENT t_water ON t_water.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_CUTTING_RING t_cutting ON t_cutting.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_REBOUND_METHOD t_rebound ON t_rebound.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN JZS_LIGHT_DYNAMIC_PENETRATION t_light ON t_light.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_BECKMAN_BEAM t_beckman ON t_beckman.ENTRUSTMENT_ID = t2.WT_ID " +
            "<where>" +
            "<if test='categories != null'>" +
            " (" +
            "<foreach collection='categories' item='category' separator=' OR '>" +
            "t1.TEST_CATEGORY LIKE '%' || #{category} || '%'" +
            "</foreach>" +
            ")" +
            "</if>" +
            "<if test='wtNum != null and wtNum != \"\"'>" +
            " AND t2.WT_NUM = #{wtNum} " +
            "</if>" +
            "<if test='names != null'>" +
            " AND (" +
            "<foreach collection='names' item='name' separator=' OR '>" +
            "   UPPER(TRIM(TO_CHAR(t2.WT_REG_NAME))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.WT_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JD_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JZ_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.CY_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.YW_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_JS_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_REG_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_SH_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_XD_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JC_TASK_TJ_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.PRODUCE_GD_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.SEND_BACK_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.REPORT_SEND_USER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.WT_UNIT_PERSON))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.YY_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.GC_ZJ))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JL_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.CHOUYANGREN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.JS_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.KC_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.SG_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t2.SJ_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.NEXT_HANDLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.CLIENT))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t1.WITNESS))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.WT_UNDERTAKER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.WT_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.CREATE_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.UPDATE_MAN))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.CREATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.UPDATE_BY))) LIKE '%' || UPPER(#{name}) || '%' " +
            "</foreach> " +
            ")" +
            "</if>" +
            "</where>" +
            "ORDER BY commissionDate DESC" +
            "</script>")
    List<JcCoreWtInfo> selectByCategory(@Param("categories") List<String> categories, @Param("names") List<String> names, @Param("wtNum") String wtNum);

    @Select("<script>" +
            "SELECT DISTINCT " +
            "t2.WT_ID as id, " +
            "t2.WT_NUM as wtNum, " +
            "COALESCE(t1.COMMISSION_DATE, t2.WT_DATE) as commissionDate, " +
            "COALESCE(t1.CLIENT_UNIT, t2.WT_UNIT) as clientUnit, " +
            "t1.CLIENT as client, " +
            "COALESCE(t1.PROJECT_NAME, t2.GC_NAME) as projectName, " +
            "t1.PROJECT_AREA as projectArea, " +
            "COALESCE(t1.CONSTRUCTION_UNIT, t2.SG_UNIT) as constructionUnit, " +
            "COALESCE(t1.BUILDING_UNIT, t2.JS_UNIT) as buildingUnit, " +
            "COALESCE(t1.SUPERVISION_UNIT, t2.JL_UNIT) as supervisionUnit, " +
            "COALESCE(t1.WITNESS_UNIT, t2.JZ_UNIT) as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "COALESCE(TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegName, " +
            "TO_CHAR(t1.CREATE_BY) as createBy, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as createByName, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegRealName, " +
            // recordStatus：优先取各“记录表”自身的 STATUS，不受本查询 JOIN/人员过滤影响；
            // 实现方式：分别对各记录表按 WT_NUM/WT_ID 取最大 STATUS，然后按顺序 COALESCE，最后默认 '0'
            // 为了兼容历史数据中 STATUS 存在中文等非数字值的情况，这里使用 REGEXP_LIKE 过滤，只保留纯数字状态；
            // 非数字状态一律按 '0' 处理，避免前端 parseInt 得到 NaN 导致显示为“未知”。
            "NVL( " +
            "  COALESCE( " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(d.STATUS, '^[0-9]+$') THEN TO_CHAR(d.STATUS) ELSE '0' END) FROM T_DENSITY_TEST d " +
            "      WHERE d.ENTRUSTMENT_ID = t2.WT_NUM OR d.ENTRUSTMENT_ID = t2.WT_ID), " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(n.STATUS, '^[0-9]+$') THEN TO_CHAR(n.STATUS) ELSE '0' END) FROM T_NUCLEAR_DENSITY n " +
            "      WHERE n.ENTRUSTMENT_ID = t2.WT_NUM OR n.ENTRUSTMENT_ID = t2.WT_ID), " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(s.STATUS, '^[0-9]+$') THEN TO_CHAR(s.STATUS) ELSE '0' END) FROM T_SAND_REPLACEMENT s " +
            "      WHERE s.ENTRUSTMENT_ID = t2.WT_NUM OR s.ENTRUSTMENT_ID = t2.WT_ID), " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(w.STATUS, '^[0-9]+$') THEN TO_CHAR(w.STATUS) ELSE '0' END) FROM T_WATER_REPLACEMENT w " +
            "      WHERE w.ENTRUSTMENT_ID = t2.WT_NUM OR w.ENTRUSTMENT_ID = t2.WT_ID), " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(c.STATUS, '^[0-9]+$') THEN TO_CHAR(c.STATUS) ELSE '0' END) FROM T_CUTTING_RING c " +
            "      WHERE c.ENTRUSTMENT_ID = t2.WT_NUM OR c.ENTRUSTMENT_ID = t2.WT_ID), " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(r.STATUS, '^[0-9]+$') THEN TO_CHAR(r.STATUS) ELSE '0' END) FROM T_REBOUND_METHOD r " +
            "      WHERE r.ENTRUSTMENT_ID = t2.WT_NUM OR r.ENTRUSTMENT_ID = t2.WT_ID), " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(l.STATUS, '^[0-9]+$') THEN TO_CHAR(l.STATUS) ELSE '0' END) FROM JZS_LIGHT_DYNAMIC_PENETRATION l " +
            "      WHERE l.ENTRUSTMENT_ID = t2.WT_NUM OR l.ENTRUSTMENT_ID = t2.WT_ID), " +
            "    (SELECT MAX(CASE WHEN REGEXP_LIKE(b.STATUS, '^[0-9]+$') THEN TO_CHAR(b.STATUS) ELSE '0' END) FROM T_BECKMAN_BEAM b " +
            "      WHERE b.ENTRUSTMENT_ID = t2.WT_NUM OR b.ENTRUSTMENT_ID = t2.WT_ID) " +
            "  ), " +
            "  '0' " +
            ") as recordStatus, " +
            // status：保持原有委托状态，供兼容使用
            "COALESCE(TO_CHAR(t1.STATUS), TO_CHAR(t2.WT_STATUS), '0') as status, " +
            "t1.SAMPLE_STATUS as sampleStatus, " +
            "TO_CHAR(COALESCE(" +
            "t_density.RECORD_TESTER, t_nuclear.RECORD_TESTER, t_sand.RECORD_TESTER, " +
            "t_water.RECORD_TESTER, t_cutting.RECORD_TESTER, t_rebound.RECORD_TESTER, " +
            "t_light.RECORD_TESTER, t_beckman.RECORD_TESTER" +
            ")) as tester, " +
            "TO_CHAR(COALESCE(" +
            "t_density.RECORD_TESTER, t_nuclear.RECORD_TESTER, t_sand.RECORD_TESTER, " +
            "t_water.RECORD_TESTER, t_cutting.RECORD_TESTER, t_rebound.RECORD_TESTER, " +
            "t_light.RECORD_TESTER, t_beckman.RECORD_TESTER" +
            ")) as testerName, " +
            "TO_CHAR(COALESCE(" +
            "t_density.RECORD_REVIEWER, t_nuclear.RECORD_REVIEWER, t_sand.RECORD_REVIEWER, " +
            "t_water.RECORD_REVIEWER, t_cutting.RECORD_REVIEWER, t_rebound.RECORD_REVIEWER, " +
            "t_light.RECORD_REVIEWER, t_beckman.RECORD_REVIEWER" +
            ")) as reviewer, " +
            "TO_CHAR(COALESCE(" +
            "t_density.RECORD_REVIEWER, t_nuclear.RECORD_REVIEWER, t_sand.RECORD_REVIEWER, " +
            "t_water.RECORD_REVIEWER, t_cutting.RECORD_REVIEWER, t_rebound.RECORD_REVIEWER, " +
            "t_light.RECORD_REVIEWER, t_beckman.RECORD_REVIEWER" +
            ")) as reviewerName, " +
            "TO_CHAR(COALESCE(" +
            "t_density.APPROVER, t_nuclear.APPROVER, t_sand.APPROVER, " +
            "t_water.APPROVER, t_cutting.APPROVER, t_rebound.APPROVER, " +
            "t_light.APPROVER, t_beckman.APPROVER" +
            ")) as approver, " +
            "TO_CHAR(COALESCE(" +
            "t_density.APPROVER, t_nuclear.APPROVER, t_sand.APPROVER, " +
            "t_water.APPROVER, t_cutting.APPROVER, t_rebound.APPROVER, " +
            "t_light.APPROVER, t_beckman.APPROVER" +
            ")) as approverName, " +
            "TO_CHAR(t1.UPDATE_BY) as updateBy, " +
            "COALESCE(u_update.USER_NAME, TO_CHAR(t1.UPDATE_BY)) as updateByName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.BEIZHU as remarks, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t2.JZ_UNIT as buildingUnit2, " +
            "t2.KC_UNIT as surveyUnit, " +
            "t2.WT_MAN_TEL as clientTel, " +
            "t2.WT_UNIT_ADDRESS as clientUnitAddress, " +
            "t2.WT_UNIT_TEL as clientUnitTel, " +
            "t2.GC_GCPQ as spec, " +
            "t2.PD_PASS_CODE as manufacturer, " +
            "t2.OL_WT_NUM as batchNumber, " +
            "t2.WT_JCCS as testItems, " +
            "t2.SAMPLE_QUANTITY as sampleQuantity, " +
            "t2.REPRESENTATIVE_BATCH as representativeBatch, " +
            "t2.SAMPLE_DISPOSAL as sampleDisposal, " +
            "t2.REPORT_SEND_MODE as reportSendMode, " +
            "t2.DELIVERY_MODE as deliveryMode, " +
            "t2.DELIVERY_DATE as deliveryDate, " +
            "t2.FEE as fee, " +
            "t2.SAMPLE_HISTORY as sampleHistory, " +
            "t1.REPORT_SEND_USER as reportSendUser, " +
            "t1.WITNESS_ID_CARD as witnessIdCard, " +
            "t1.SAMPLING_MAN_ID_CARD as samplingManIdCard, " +
            "t2.CLIENT_ADDRESS_PHONE as clientAddressPhone " +
            "FROM JC_CORE_WT_INFO t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.WT_ID = t1.ID " +
            "LEFT JOIN JZS_USERS u_create ON u_create.USER_ACCOUNT = TO_CHAR(t1.CREATE_BY) " +
            "LEFT JOIN JZS_USERS u_tester ON u_tester.USER_ACCOUNT = TO_CHAR(t1.TESTER) " +
            "LEFT JOIN JZS_USERS u_reviewer ON u_reviewer.USER_ACCOUNT = TO_CHAR(t1.REVIEWER) " +
            "LEFT JOIN JZS_USERS u_approver ON u_approver.USER_ACCOUNT = TO_CHAR(t1.APPROVER) " +
            "LEFT JOIN JZS_USERS u_update ON u_update.USER_ACCOUNT = TO_CHAR(t1.UPDATE_BY) " +
            "LEFT JOIN T_SIMPLE_DIRECTORY t3 ON t3.DIR_NAME = t2.WT_NUM " +
            "LEFT JOIN T_DENSITY_TEST t_density ON t_density.ENTRUSTMENT_ID = t2.WT_NUM " +
            // 核子法历史数据有两种关联方式：ENTRUSTMENT_ID 可能是 WT_NUM 或 WT_ID，这里同时兼容
            "LEFT JOIN T_NUCLEAR_DENSITY t_nuclear ON (t_nuclear.ENTRUSTMENT_ID = t2.WT_NUM OR t_nuclear.ENTRUSTMENT_ID = t2.WT_ID) " +
            "LEFT JOIN T_SAND_REPLACEMENT t_sand ON t_sand.ENTRUSTMENT_ID = t2.WT_NUM " +
            "LEFT JOIN T_WATER_REPLACEMENT t_water ON t_water.ENTRUSTMENT_ID = t2.WT_NUM " +
            "LEFT JOIN T_CUTTING_RING t_cutting ON t_cutting.ENTRUSTMENT_ID = t2.WT_NUM " +
            "LEFT JOIN T_REBOUND_METHOD t_rebound ON t_rebound.ENTRUSTMENT_ID = t2.WT_NUM " +
            // 轻型动力触探表 JZS_LIGHT_DYNAMIC_PENETRATION 的 ENTRUSTMENT_ID 关联的是 JC_CORE_WT_INFO.WT_ID
            "LEFT JOIN JZS_LIGHT_DYNAMIC_PENETRATION t_light ON t_light.ENTRUSTMENT_ID = t2.WT_ID " +
            "LEFT JOIN T_BECKMAN_BEAM t_beckman ON t_beckman.ENTRUSTMENT_ID = t2.WT_NUM " +
            "<where>" +
            "<if test='categories != null'>" +
            " (" +
            "<foreach collection='categories' item='category' separator=' OR '>" +
            "t1.TEST_CATEGORY LIKE '%' || #{category} || '%'" +
            "</foreach>" +
            ")" +
            "</if>" +
            "<if test='wtNum != null and wtNum != \"\"'>" +
            " AND t2.WT_NUM = #{wtNum} " +
            "</if>" +
            "<if test='names != null'>" +
            " AND (" +
            "<foreach collection='names' item='name' separator=' OR '>" +
            "   UPPER(TRIM(TO_CHAR(t_density.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_density.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_nuclear.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_sand.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_water.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_cutting.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_rebound.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_light.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.FILLER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.RECORD_TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.RECORD_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.TESTER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t_beckman.APPROVER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.WT_UNDERTAKER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "OR UPPER(TRIM(TO_CHAR(t3.WT_REVIEWER))) LIKE '%' || UPPER(#{name}) || '%' " +
            "</foreach> " +
            ")" +
            "</if>" +
            "</where>" +
            "ORDER BY commissionDate DESC" +
            "</script>")
    List<JcCoreWtInfo> selectRecordsByCategory(@Param("categories") java.util.List<String> categories,
                                               @Param("names") java.util.List<String> names,
                                               @Param("wtNum") String wtNum);

    @Select("SELECT " +
            "t2.WT_ID as id, " +
            "t2.WT_NUM as wtNum, " +
            "COALESCE(t1.COMMISSION_DATE, t2.WT_DATE) as commissionDate, " +
            "COALESCE(t1.CLIENT_UNIT, t2.WT_UNIT) as clientUnit, " +
            "t1.CLIENT as client, " +
            "COALESCE(t1.PROJECT_NAME, t2.GC_NAME) as projectName, " +
            "t1.PROJECT_AREA as projectArea, " +
            "COALESCE(t1.CONSTRUCTION_PART, t2.GC_GCPQ) as constructionPart, " +
            "t2.GC_BEIZHU as projectRemarks, " +
            "COALESCE(t1.CONSTRUCTION_UNIT, t2.SG_UNIT) as constructionUnit, " +
            "COALESCE(t1.BUILDING_UNIT, t2.JS_UNIT) as buildingUnit, " +
            "COALESCE(t1.SUPERVISION_UNIT, t2.JL_UNIT) as supervisionUnit, " +
            "COALESCE(t1.WITNESS_UNIT, t2.JZ_UNIT) as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "COALESCE(TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegName, " +
            "TO_CHAR(t1.CREATE_BY) as createBy, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as createByName, " +
            "COALESCE(u_create.USER_NAME, TO_CHAR(t1.CREATE_BY), t2.WT_REG_NAME) as clientRegRealName, " +
            "COALESCE(TO_CHAR(t1.STATUS), TO_CHAR(t2.WT_STATUS), '0') as status, " +
            "t1.SAMPLE_STATUS as sampleStatus, " +
            "TO_CHAR(t1.TESTER) as tester, " +
            "COALESCE(u_tester.USER_NAME, TO_CHAR(t1.TESTER)) as testerName, " +
            "TO_CHAR(t1.REVIEWER) as reviewer, " +
            "COALESCE(u_reviewer.USER_NAME, TO_CHAR(t1.REVIEWER)) as reviewerName, " +
            "TO_CHAR(t1.APPROVER) as approver, " +
            "COALESCE(u_approver.USER_NAME, TO_CHAR(t1.APPROVER)) as approverName, " +
            "TO_CHAR(t1.UPDATE_BY) as updateBy, " +
            "COALESCE(u_update.USER_NAME, TO_CHAR(t1.UPDATE_BY)) as updateByName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.BEIZHU as remarks, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t2.JZ_UNIT as buildingUnit2, " +
            "t2.KC_UNIT as surveyUnit, " +
            "t2.WT_MAN_TEL as clientTel, " +
            "t2.WT_UNIT_ADDRESS as clientUnitAddress, " +
            "t2.WT_UNIT_TEL as clientUnitTel, " +
            "t2.GC_GCPQ as spec, " +
            "t2.PD_PASS_CODE as manufacturer, " +
            "t2.OL_WT_NUM as batchNumber, " +
            "t2.WT_JCCS as testItems, " +
            "t2.SAMPLE_QUANTITY as sampleQuantity, " +
            "t2.REPRESENTATIVE_BATCH as representativeBatch, " +
            "t2.SAMPLE_DISPOSAL as sampleDisposal, " +
            "t2.REPORT_SEND_MODE as reportSendMode, " +
            "t2.DELIVERY_MODE as deliveryMode, " +
            "t2.DELIVERY_DATE as deliveryDate, " +
            "t2.FEE as fee, " +
            "t2.SAMPLE_HISTORY as sampleHistory, " +
            "t2.SEND_BACK_REASON as rejectReason, " +
            "t2.YY_MAN as receiver, " +
            "t1.REPORT_SEND_USER as reportSendUser, " +
            "t1.WITNESS_ID_CARD as witnessIdCard, " +
            "t1.SAMPLING_MAN_ID_CARD as samplingManIdCard, " +
            "t1.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t1.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.WT_MAN_SIGN as wtManSign, " +
            "t1.WT_REVIEWER as wtReviewer, " +
            "t1.WT_REVIEW_SIGN as wtReviewSign, " +
            "t2.CLIENT_ADDRESS_PHONE as clientAddressPhone " +
            "FROM JC_CORE_WT_INFO t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.WT_ID = t1.ID " +
            "LEFT JOIN JZS_USERS u_create ON u_create.USER_ACCOUNT = TO_CHAR(t1.CREATE_BY) " +
            "LEFT JOIN JZS_USERS u_tester ON u_tester.USER_ACCOUNT = TO_CHAR(t1.TESTER) " +
            "LEFT JOIN JZS_USERS u_reviewer ON u_reviewer.USER_ACCOUNT = TO_CHAR(t1.REVIEWER) " +
            "LEFT JOIN JZS_USERS u_approver ON u_approver.USER_ACCOUNT = TO_CHAR(t1.APPROVER) " +
            "LEFT JOIN JZS_USERS u_update ON u_update.USER_ACCOUNT = TO_CHAR(t1.UPDATE_BY) " +
            "WHERE t2.WT_ID = #{id}")
    JcCoreWtInfo selectById(@Param("id") String id);

    @org.apache.ibatis.annotations.Delete("DELETE FROM T_ENTRUSTMENT WHERE ID = #{id}")
    int deleteExtById(String id);

    @org.apache.ibatis.annotations.Delete("DELETE FROM JC_CORE_WT_INFO WHERE WT_ID = #{id}")
    int deleteCoreById(String id);

    @org.apache.ibatis.annotations.Delete("DELETE FROM T_ENTRUSTMENT WHERE WT_NUM = #{wtNum}")
    int deleteExtByWtNum(@Param("wtNum") String wtNum);

    @org.apache.ibatis.annotations.Delete("DELETE FROM JC_CORE_WT_INFO WHERE WT_NUM = #{wtNum}")
    int deleteCoreByWtNum(@Param("wtNum") String wtNum);

    @org.apache.ibatis.annotations.Insert("INSERT INTO T_ENTRUSTMENT (" +
            "ID, WT_NUM, PROJECT_NAME, CLIENT_UNIT, CLIENT, COMMISSION_DATE, PROJECT_AREA, " +
            "CONSTRUCTION_UNIT, BUILDING_UNIT, SUPERVISION_UNIT, WITNESS_UNIT, WITNESS, " +
            "TEST_CATEGORY, BEIZHU, SAMPLE_NAME, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, " +
            "TESTER, REVIEWER, APPROVER, CONSTRUCTION_PART, STATUS, NEXT_HANDLER, REJECT_REASON, " +
            "SAMPLE_NUMBER, SPEC, MANUFACTURER, SAMPLE_QUANTITY, REPRESENTATIVE_BATCH, BATCH_NUMBER, " +
            "CLIENT_ADDRESS_PHONE, REPORT_SEND_MODE, SAMPLE_DISPOSAL, DELIVERY_MODE, DELIVERY_DATE, " +
            "FEE, SAMPLE_HISTORY, SAMPLE_STATUS, TEST_ITEMS, WITNESS_ID_CARD, SAMPLING_MAN_ID_CARD, " +
            "REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "WT_MAN_SIGN, WT_REVIEWER, WT_REVIEW_SIGN" +
            ") VALUES (" +
            "#{id, jdbcType=VARCHAR}, #{wtNum, jdbcType=VARCHAR}, #{projectName, jdbcType=VARCHAR}, #{clientUnit, jdbcType=VARCHAR}, #{client, jdbcType=VARCHAR}, #{commissionDate, jdbcType=TIMESTAMP}, #{projectArea, jdbcType=VARCHAR}, " +
            "#{constructionUnit, jdbcType=VARCHAR}, #{buildingUnit, jdbcType=VARCHAR}, #{supervisionUnit, jdbcType=VARCHAR}, #{witnessUnit, jdbcType=VARCHAR}, #{witness, jdbcType=VARCHAR}, " +
            "#{testCategory, jdbcType=VARCHAR}, #{remarks, jdbcType=VARCHAR}, #{sampleName, jdbcType=VARCHAR}, #{createBy, jdbcType=VARCHAR}, #{createTime, jdbcType=TIMESTAMP}, #{updateBy, jdbcType=VARCHAR}, #{updateTime, jdbcType=TIMESTAMP}, " +
            "#{tester, jdbcType=VARCHAR}, #{reviewer, jdbcType=VARCHAR}, #{approver, jdbcType=VARCHAR}, #{constructionPart, jdbcType=VARCHAR}, #{status, jdbcType=VARCHAR}, #{nextHandler, jdbcType=VARCHAR}, #{rejectReason, jdbcType=VARCHAR}, " +
            "#{wtNum, jdbcType=VARCHAR}, #{spec, jdbcType=VARCHAR}, #{manufacturer, jdbcType=VARCHAR}, #{sampleQuantity, jdbcType=VARCHAR}, #{representativeBatch, jdbcType=VARCHAR}, #{batchNumber, jdbcType=VARCHAR}, " +
            "#{clientAddressPhone, jdbcType=VARCHAR}, #{reportSendMode, jdbcType=VARCHAR}, #{sampleDisposal, jdbcType=VARCHAR}, #{deliveryMode, jdbcType=VARCHAR}, #{deliveryDate, jdbcType=VARCHAR}, " +
            "#{fee, jdbcType=VARCHAR}, #{sampleHistory, jdbcType=VARCHAR}, #{sampleStatus, jdbcType=VARCHAR}, #{testItems, jdbcType=VARCHAR}, #{witnessIdCard, jdbcType=VARCHAR}, #{samplingManIdCard, jdbcType=VARCHAR}, " +
            "#{reviewSignaturePhoto, jdbcType=VARCHAR}, #{inspectSignaturePhoto, jdbcType=VARCHAR}, #{approveSignaturePhoto, jdbcType=VARCHAR}, " +
            "#{wtManSign, jdbcType=VARCHAR}, #{wtReviewer, jdbcType=VARCHAR}, #{wtReviewSign, jdbcType=VARCHAR}" +
            ")")
    int insertExt(JcCoreWtInfo info);

    @org.apache.ibatis.annotations.Update("UPDATE T_ENTRUSTMENT SET " +
            "WT_NUM = #{wtNum, jdbcType=VARCHAR}, " +
            "PROJECT_NAME = #{projectName, jdbcType=VARCHAR}, " +
            "CLIENT_UNIT = #{clientUnit, jdbcType=VARCHAR}, " +
            "CLIENT = #{client, jdbcType=VARCHAR}, " +
            "COMMISSION_DATE = #{commissionDate, jdbcType=TIMESTAMP}, " +
            "PROJECT_AREA = #{projectArea, jdbcType=VARCHAR}, " +
            "CONSTRUCTION_UNIT = #{constructionUnit, jdbcType=VARCHAR}, " +
            "BUILDING_UNIT = #{buildingUnit, jdbcType=VARCHAR}, " +
            "SUPERVISION_UNIT = #{supervisionUnit, jdbcType=VARCHAR}, " +
            "WITNESS_UNIT = #{witnessUnit, jdbcType=VARCHAR}, " +
            "WITNESS = #{witness, jdbcType=VARCHAR}, " +
            "TEST_CATEGORY = #{testCategory, jdbcType=VARCHAR}, " +
            "BEIZHU = #{remarks, jdbcType=VARCHAR}, " +
            "SAMPLE_NAME = #{sampleName, jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy, jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime, jdbcType=TIMESTAMP}, " +
            "TESTER = #{tester, jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer, jdbcType=VARCHAR}, " +
            "APPROVER = #{approver, jdbcType=VARCHAR}, " +
            "CONSTRUCTION_PART = #{constructionPart, jdbcType=VARCHAR}, " +
            "STATUS = #{status, jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler, jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason, jdbcType=VARCHAR}, " +
            "SAMPLE_NUMBER = #{wtNum, jdbcType=VARCHAR}, " +
            "SPEC = #{spec, jdbcType=VARCHAR}, " +
            "MANUFACTURER = #{manufacturer, jdbcType=VARCHAR}, " +
            "SAMPLE_QUANTITY = #{sampleQuantity, jdbcType=VARCHAR}, " +
            "REPRESENTATIVE_BATCH = #{representativeBatch, jdbcType=VARCHAR}, " +
            "BATCH_NUMBER = #{batchNumber, jdbcType=VARCHAR}, " +
            "CLIENT_ADDRESS_PHONE = #{clientAddressPhone, jdbcType=VARCHAR}, " +
            "REPORT_SEND_MODE = #{reportSendMode, jdbcType=VARCHAR}, " +
            "SAMPLE_DISPOSAL = #{sampleDisposal, jdbcType=VARCHAR}, " +
            "DELIVERY_MODE = #{deliveryMode, jdbcType=VARCHAR}, " +
            "DELIVERY_DATE = #{deliveryDate, jdbcType=VARCHAR}, " +
            "FEE = #{fee, jdbcType=VARCHAR}, " +
            "SAMPLE_HISTORY = #{sampleHistory, jdbcType=VARCHAR}, " +
            "SAMPLE_STATUS = #{sampleStatus, jdbcType=VARCHAR}, " +
            "TEST_ITEMS = #{testItems, jdbcType=VARCHAR}, " +
            "WITNESS_ID_CARD = #{witnessIdCard, jdbcType=VARCHAR}, " +
            "SAMPLING_MAN_ID_CARD = #{samplingManIdCard, jdbcType=VARCHAR}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto, jdbcType=VARCHAR}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto, jdbcType=VARCHAR}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto, jdbcType=VARCHAR}, " +
            "WT_MAN_SIGN = #{wtManSign, jdbcType=VARCHAR}, " +
            "WT_REVIEWER = #{wtReviewer, jdbcType=VARCHAR}, " +
            "WT_REVIEW_SIGN = #{wtReviewSign, jdbcType=VARCHAR} " +
            "WHERE ID = #{id}")
    int updateExt(JcCoreWtInfo info);

    @org.apache.ibatis.annotations.Insert("INSERT INTO JC_CORE_WT_INFO (" +
            "WT_ID, WT_NUM, WT_UNIT, GC_NAME, WT_DATE, WT_REG_NAME, " +
            "WT_STATUS, JZ_UNIT, KC_UNIT, WT_MAN_TEL, WT_UNIT_ADDRESS, WT_UNIT_TEL, " +
            "GC_GCPQ, PD_PASS_CODE, OL_WT_NUM, WT_JCCS, SAMPLE_QUANTITY, REPRESENTATIVE_BATCH, " +
            "SAMPLE_DISPOSAL, REPORT_SEND_MODE, DELIVERY_MODE, DELIVERY_DATE, FEE, SAMPLE_HISTORY, " +
            "CLIENT_ADDRESS_PHONE, GC_BEIZHU, CUSTOMER_ID, DATACID" +
            ") VALUES (" +
            "#{id, jdbcType=VARCHAR}, #{wtNum, jdbcType=VARCHAR}, #{clientUnit, jdbcType=VARCHAR}, #{projectName, jdbcType=VARCHAR}, #{commissionDate, jdbcType=TIMESTAMP}, #{createBy, jdbcType=VARCHAR}, " +
            "#{status, jdbcType=VARCHAR}, #{buildingUnit2, jdbcType=VARCHAR}, #{surveyUnit, jdbcType=VARCHAR}, #{clientTel, jdbcType=VARCHAR}, #{clientUnitAddress, jdbcType=VARCHAR}, #{clientUnitTel, jdbcType=VARCHAR}, " +
            "#{spec, jdbcType=VARCHAR}, #{manufacturer, jdbcType=VARCHAR}, #{batchNumber, jdbcType=VARCHAR}, #{testItems, jdbcType=VARCHAR}, #{sampleQuantity, jdbcType=VARCHAR}, #{representativeBatch, jdbcType=VARCHAR}, " +
            "#{sampleDisposal, jdbcType=VARCHAR}, #{reportSendMode, jdbcType=VARCHAR}, #{deliveryMode, jdbcType=VARCHAR}, #{deliveryDate, jdbcType=VARCHAR}, #{fee, jdbcType=VARCHAR}, #{sampleHistory, jdbcType=VARCHAR}, " +
            "#{clientAddressPhone, jdbcType=VARCHAR}, #{projectRemarks, jdbcType=VARCHAR}, #{customerId, jdbcType=VARCHAR}, #{dataCid, jdbcType=VARCHAR}" +
            ")")
    int insert(JcCoreWtInfo info);

    @org.apache.ibatis.annotations.Update("UPDATE JC_CORE_WT_INFO SET " +
            "WT_STATUS = NVL(#{status, jdbcType=VARCHAR}, WT_STATUS), " +
            "JZ_UNIT = #{buildingUnit2, jdbcType=VARCHAR}, " +
            "KC_UNIT = #{surveyUnit, jdbcType=VARCHAR}, " +
            "WT_MAN_TEL = #{clientTel, jdbcType=VARCHAR}, " +
            "WT_UNIT_ADDRESS = #{clientUnitAddress, jdbcType=VARCHAR}, " +
            "WT_UNIT_TEL = #{clientUnitTel, jdbcType=VARCHAR}, " +
            "GC_GCPQ = #{spec, jdbcType=VARCHAR}, " +
            "PD_PASS_CODE = #{manufacturer, jdbcType=VARCHAR}, " +
            "OL_WT_NUM = #{batchNumber, jdbcType=VARCHAR}, " +
            "WT_JCCS = #{testItems, jdbcType=VARCHAR}, " +
            "SAMPLE_QUANTITY = #{sampleQuantity, jdbcType=VARCHAR}, " +
            "REPRESENTATIVE_BATCH = #{representativeBatch, jdbcType=VARCHAR}, " +
            "SAMPLE_DISPOSAL = #{sampleDisposal, jdbcType=VARCHAR}, " +
            "REPORT_SEND_MODE = #{reportSendMode, jdbcType=VARCHAR}, " +
            "DELIVERY_MODE = #{deliveryMode, jdbcType=VARCHAR}, " +
            "DELIVERY_DATE = #{deliveryDate, jdbcType=VARCHAR}, " +
            "FEE = #{fee, jdbcType=VARCHAR}, " +
            "SAMPLE_HISTORY = #{sampleHistory, jdbcType=VARCHAR}, " +
            "CLIENT_ADDRESS_PHONE = #{clientAddressPhone, jdbcType=VARCHAR}, " +
            "CUSTOMER_ID = NVL(#{customerId, jdbcType=VARCHAR}, CUSTOMER_ID), " +
            "DATACID = NVL(#{dataCid, jdbcType=VARCHAR}, DATACID), " +
            "GC_BEIZHU = #{projectRemarks, jdbcType=VARCHAR} " +
            "WHERE WT_ID = #{id, jdbcType=VARCHAR}")
    int update(JcCoreWtInfo info);
}
