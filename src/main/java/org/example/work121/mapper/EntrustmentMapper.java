package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.Entrustment;

@Mapper
public interface EntrustmentMapper {

    @Select("SELECT * FROM T_ENTRUSTMENT WHERE ID = #{id}")
    Entrustment selectById(@Param("id") String id);

    @Select("SELECT * FROM T_ENTRUSTMENT WHERE WT_NUM = #{wtNum}")
    Entrustment selectByWtNum(@Param("wtNum") String wtNum);

    @Insert("INSERT INTO T_ENTRUSTMENT (ID, WT_NUM, PROJECT_NAME, CLIENT_UNIT, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id}, #{wtNum}, #{projectName}, #{clientUnit}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto})")
    int insert(Entrustment entrustment);

    @Update("UPDATE T_ENTRUSTMENT SET " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto} " +
            "WHERE ID = #{id}")
    int updateSignatures(Entrustment entrustment);

    @Update("UPDATE T_ENTRUSTMENT SET " +
            "WT_NUM = #{wtNum}, " +
            "PROJECT_NAME = #{projectName}, " +
            "CLIENT_UNIT = #{clientUnit} " +
            "WHERE ID = #{id}")
    int update(Entrustment entrustment);

    @Select("SELECT * FROM T_ENTRUSTMENT")
    java.util.List<Entrustment> selectAll();
}
