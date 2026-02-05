package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.Entrustment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface EntrustmentMapper {

    @Select("SELECT " +
            "ID, " +
            "WT_NUM as wtNum, " +
            "PROJECT_NAME as projectName, " +
            "CONSTRUCTION_PART as constructionPart, " +
            "COMMISSION_DATE as commissionDate, " +
            "CONSTRUCTION_UNIT as constructionUnit, " +
            "BUILDING_UNIT as buildingUnit, " +
            "SUPERVISION_UNIT as supervisionUnit, " +
            "DESIGN_UNIT as designUnit, " +
            "SURVEY_UNIT as surveyUnit, " +
            "CLIENT_UNIT as clientUnit, " +
            "CLIENT as client, " +
            "CLIENT_TEL as clientTel, " +
            "CLIENT_UNIT_PERSON as clientUnitPerson, " +
            "CLIENT_UNIT_POSTALCODE as clientUnitPostalcode, " +
            "PROJECT_AREA as projectArea, " +
            "SAMPLE_NAME as sampleName, " +
            "SPEC as spec, " +
            "MANUFACTURER as manufacturer, " +
            "SAMPLE_QUANTITY as sampleQuantity, " +
            "REPRESENTATIVE_BATCH as representativeBatch, " +
            "BATCH_NUMBER as batchNumber, " +
            "TEST_CATEGORY as testCategory, " +
            "REPORT_SEND as reportSend, " +
            "SAMPLE_DISPOSAL as sampleDisposal, " +
            "WITNESS as witness, " +
            "WITNESS_UNIT as witnessUnit, " +
            "DELIVERY_MODE as deliveryMode, " +
            "FEE as fee, " +
            "SAMPLE_HISTORY as sampleHistory, " +
            "SAMPLE_STATUS as sampleStatus, " +
            "TEST_ITEMS as testItems, " +
            "REMARK as remarks, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime, " +
            "DEL_FLAG as delFlag, " +
            "REVIEWER as reviewer, " +
            "INSPECTOR as inspector, " +
            "APPROVER as approver, " +
            "PAGE as page, " +
            "TOTAL_PAGES as totalPages, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM T_ENTRUSTMENT WHERE ID = #{id}")
    Entrustment selectNewById(String id);

    @Select("SELECT " +
            "UNIFIED_NUMBER as id, " +
            "SAMPLE_NUMBER as wtNum, " +
            "CLIENT_UNIT as clientUnit, " +
            "CLIENT_DATE as commissionDate, " +
            "CONSTRUCTION_UNIT as constructionUnit, " +
            "BUILDING_UNIT as buildingUnit, " +
            "PROJECT_NAME as projectName, " +
            "CONSTRUCTION_PART as constructionPart, " +
            "SAMPLE_NAME as sampleName, " +
            "SPEC as spec, " +
            "MANUFACTURER as manufacturer, " +
            "SAMPLE_QUANTITY as sampleQuantity, " +
            "REPRESENTATIVE_BATCH as representativeBatch, " +
            "BATCH_NUMBER as batchNumber, " +
            "TEST_CATEGORY as testCategory, " +
            "REPORT_SEND as reportSend, " +
            "SAMPLE_DISPOSAL as sampleDisposal, " +
            "WITNESS as witness, " +
            "WITNESS_UNIT as witnessUnit, " +
            "DELIVERY_MODE as deliveryMode, " +
            "FEE as fee, " +
            "REMARKS as remarks, " +
            "SAMPLE_HISTORY as sampleHistory, " +
            "SAMPLE_STATUS as sampleStatus, " +
            "TEST_ITEMS as testItems, " +
            "REVIEWER as reviewer, " +
            "INSPECTOR as inspector, " +
            "APPROVER as approver, " +
            "PAGE as page, " +
            "TOTAL_PAGES as totalPages, " +
            "CREATE_TIME as createTime, " +
            "CREATOR as createBy, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM JZS_ENTRUSTMENT WHERE UNIFIED_NUMBER = #{id}")
    Entrustment selectOldById(String id);

    default Entrustment selectById(String id) {
        Entrustment e = selectNewById(id);
        if (e == null) {
            e = selectOldById(id);
            if (e != null) e.setCanEdit(false);
        } else {
            e.setCanEdit(true);
        }
        return e;
    }

    @Select("SELECT " +
            "ID, " +
            "WT_NUM as wtNum, " +
            "PROJECT_NAME as projectName, " +
            "CONSTRUCTION_PART as constructionPart, " +
            "COMMISSION_DATE as commissionDate, " +
            "CONSTRUCTION_UNIT as constructionUnit, " +
            "BUILDING_UNIT as buildingUnit, " +
            "SUPERVISION_UNIT as supervisionUnit, " +
            "DESIGN_UNIT as designUnit, " +
            "SURVEY_UNIT as surveyUnit, " +
            "CLIENT_UNIT as clientUnit, " +
            "CLIENT as client, " +
            "CLIENT_TEL as clientTel, " +
            "CLIENT_UNIT_PERSON as clientUnitPerson, " +
            "CLIENT_UNIT_POSTALCODE as clientUnitPostalcode, " +
            "PROJECT_AREA as projectArea, " +
            "SAMPLE_NAME as sampleName, " +
            "SPEC as spec, " +
            "MANUFACTURER as manufacturer, " +
            "SAMPLE_QUANTITY as sampleQuantity, " +
            "REPRESENTATIVE_BATCH as representativeBatch, " +
            "BATCH_NUMBER as batchNumber, " +
            "TEST_CATEGORY as testCategory, " +
            "REPORT_SEND as reportSend, " +
            "SAMPLE_DISPOSAL as sampleDisposal, " +
            "WITNESS as witness, " +
            "WITNESS_UNIT as witnessUnit, " +
            "DELIVERY_MODE as deliveryMode, " +
            "FEE as fee, " +
            "SAMPLE_HISTORY as sampleHistory, " +
            "SAMPLE_STATUS as sampleStatus, " +
            "TEST_ITEMS as testItems, " +
            "REMARK as remarks, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime, " +
            "DEL_FLAG as delFlag, " +
            "REVIEWER as reviewer, " +
            "INSPECTOR as inspector, " +
            "APPROVER as approver, " +
            "PAGE as page, " +
            "TOTAL_PAGES as totalPages, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM T_ENTRUSTMENT " +
            "ORDER BY CREATE_TIME DESC")
    List<Entrustment> selectNewAll();

    @Select("SELECT " +
            "UNIFIED_NUMBER as id, " +
            "SAMPLE_NUMBER as wtNum, " +
            "CLIENT_UNIT as clientUnit, " +
            "CLIENT_DATE as commissionDate, " +
            "CONSTRUCTION_UNIT as constructionUnit, " +
            "BUILDING_UNIT as buildingUnit, " +
            "PROJECT_NAME as projectName, " +
            "CONSTRUCTION_PART as constructionPart, " +
            "SAMPLE_NAME as sampleName, " +
            "SPEC as spec, " +
            "MANUFACTURER as manufacturer, " +
            "SAMPLE_QUANTITY as sampleQuantity, " +
            "REPRESENTATIVE_BATCH as representativeBatch, " +
            "BATCH_NUMBER as batchNumber, " +
            "TEST_CATEGORY as testCategory, " +
            "REPORT_SEND as reportSend, " +
            "SAMPLE_DISPOSAL as sampleDisposal, " +
            "WITNESS as witness, " +
            "WITNESS_UNIT as witnessUnit, " +
            "DELIVERY_MODE as deliveryMode, " +
            "FEE as fee, " +
            "REMARKS as remarks, " +
            "SAMPLE_HISTORY as sampleHistory, " +
            "SAMPLE_STATUS as sampleStatus, " +
            "TEST_ITEMS as testItems, " +
            "REVIEWER as reviewer, " +
            "INSPECTOR as inspector, " +
            "APPROVER as approver, " +
            "PAGE as page, " +
            "TOTAL_PAGES as totalPages, " +
            "CREATE_TIME as createTime, " +
            "CREATOR as createBy, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM JZS_ENTRUSTMENT " +
            "ORDER BY CREATE_TIME DESC")
    List<Entrustment> selectOldAll();

    default List<Entrustment> selectAll() {
        List<Entrustment> newList = selectNewAll();
        newList.forEach(e -> e.setCanEdit(true));

        List<Entrustment> oldList = selectOldAll();
        oldList.forEach(e -> e.setCanEdit(false));

        // 合并两个列表，优先显示新表中的数据（如果ID相同）
        Map<String, Entrustment> map = new LinkedHashMap<>();
        
        // 先放入旧数据
        for (Entrustment e : oldList) {
            map.put(e.getId(), e);
        }
        
        // 再放入新数据（覆盖旧数据）
        for (Entrustment e : newList) {
            map.put(e.getId(), e);
        }

        // 重新转换为列表
        // 注意：由于是LinkedHashMap，顺序会大致保留，但可能不是完全的时间倒序
        // 如果需要严格的时间排序，可以在Service层或这里进行排序
        // 这里简单返回合并后的值
        return new ArrayList<>(map.values());
    }

    @Insert("INSERT INTO T_ENTRUSTMENT (" +
            "ID, WT_NUM, PROJECT_NAME, CONSTRUCTION_PART, COMMISSION_DATE, " +
            "CONSTRUCTION_UNIT, BUILDING_UNIT, SUPERVISION_UNIT, DESIGN_UNIT, SURVEY_UNIT, " +
            "CLIENT_UNIT, CLIENT, CLIENT_TEL, CLIENT_UNIT_PERSON, CLIENT_UNIT_POSTALCODE, PROJECT_AREA, " +
            "SAMPLE_NAME, SPEC, MANUFACTURER, SAMPLE_QUANTITY, REPRESENTATIVE_BATCH, BATCH_NUMBER, TEST_CATEGORY, " +
            "REPORT_SEND, SAMPLE_DISPOSAL, WITNESS, WITNESS_UNIT, DELIVERY_MODE, FEE, " +
            "SAMPLE_HISTORY, SAMPLE_STATUS, TEST_ITEMS, REMARK, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DEL_FLAG, " +
            "REVIEWER, INSPECTOR, APPROVER, PAGE, TOTAL_PAGES, " +
            "REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO" +
            ") VALUES (" +
            "#{id}, #{wtNum}, #{projectName}, #{constructionPart}, #{commissionDate}, " +
            "#{constructionUnit}, #{buildingUnit}, #{supervisionUnit}, #{designUnit}, #{surveyUnit}, " +
            "#{clientUnit}, #{client}, #{clientTel}, #{clientUnitPerson}, #{clientUnitPostalcode}, #{projectArea}, " +
            "#{sampleName}, #{spec}, #{manufacturer}, #{sampleQuantity}, #{representativeBatch}, #{batchNumber}, #{testCategory}, " +
            "#{reportSend}, #{sampleDisposal}, #{witness}, #{witnessUnit}, #{deliveryMode}, #{fee}, " +
            "#{sampleHistory}, #{sampleStatus}, #{testItems}, #{remarks}, " +
            "#{createBy}, #{createTime}, #{updateBy}, #{updateTime}, #{delFlag}, " +
            "#{reviewer}, #{inspector}, #{approver}, #{page}, #{totalPages}, " +
            "#{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}" +
            ")")
    int insert(Entrustment entrustment);

    @Update("UPDATE T_ENTRUSTMENT SET " +
            "WT_NUM = #{wtNum}, " +
            "PROJECT_NAME = #{projectName}, " +
            "CONSTRUCTION_PART = #{constructionPart}, " +
            "COMMISSION_DATE = #{commissionDate}, " +
            "CONSTRUCTION_UNIT = #{constructionUnit}, " +
            "BUILDING_UNIT = #{buildingUnit}, " +
            "SUPERVISION_UNIT = #{supervisionUnit}, " +
            "DESIGN_UNIT = #{designUnit}, " +
            "SURVEY_UNIT = #{surveyUnit}, " +
            "CLIENT_UNIT = #{clientUnit}, " +
            "CLIENT = #{client}, " +
            "CLIENT_TEL = #{clientTel}, " +
            "CLIENT_UNIT_PERSON = #{clientUnitPerson}, " +
            "CLIENT_UNIT_POSTALCODE = #{clientUnitPostalcode}, " +
            "PROJECT_AREA = #{projectArea}, " +
            "SAMPLE_NAME = #{sampleName}, " +
            "SPEC = #{spec}, " +
            "MANUFACTURER = #{manufacturer}, " +
            "SAMPLE_QUANTITY = #{sampleQuantity}, " +
            "REPRESENTATIVE_BATCH = #{representativeBatch}, " +
            "BATCH_NUMBER = #{batchNumber}, " +
            "TEST_CATEGORY = #{testCategory}, " +
            "REPORT_SEND = #{reportSend}, " +
            "SAMPLE_DISPOSAL = #{sampleDisposal}, " +
            "WITNESS = #{witness}, " +
            "WITNESS_UNIT = #{witnessUnit}, " +
            "DELIVERY_MODE = #{deliveryMode}, " +
            "FEE = #{fee}, " +
            "SAMPLE_HISTORY = #{sampleHistory}, " +
            "SAMPLE_STATUS = #{sampleStatus}, " +
            "TEST_ITEMS = #{testItems}, " +
            "REMARK = #{remarks}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "DEL_FLAG = #{delFlag}, " +
            "REVIEWER = #{reviewer}, " +
            "INSPECTOR = #{inspector}, " +
            "APPROVER = #{approver}, " +
            "PAGE = #{page}, " +
            "TOTAL_PAGES = #{totalPages}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto} " +
            "WHERE ID = #{id}")
    int update(Entrustment entrustment);

    @Delete("DELETE FROM T_ENTRUSTMENT WHERE ID = #{id}")
    int deleteById(String id);
}
