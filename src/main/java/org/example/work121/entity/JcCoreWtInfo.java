package org.example.work121.entity;

import java.util.Date;

/**
 * 检测委托信息实体类
 * 继承自 Entrustment，确保与 Entrustment 实体字段一致
 */
public class JcCoreWtInfo extends Entrustment {

    // ================= Compatibility Methods =================
    // 为了保持向后兼容性，保留原有的 getter/setter 方法，但底层指向 Entrustment 的字段

    public String getWtId() {
        return getId();
    }

    public void setWtId(String wtId) {
        setId(wtId);
    }

    public Date getWtDate() {
        return getCommissionDate();
    }

    public void setWtDate(Date wtDate) {
        setCommissionDate(wtDate);
    }

    public String getWtMan() {
        return getClient();
    }

    public void setWtMan(String wtMan) {
        setClient(wtMan);
    }

    public String getWtUnit() {
        return getClientUnit();
    }

    public void setWtUnit(String wtUnit) {
        setClientUnit(wtUnit);
    }

    public String getGcName() {
        return getProjectName();
    }

    public void setGcName(String gcName) {
        setProjectName(gcName);
    }

    public String getGcArea() {
        return getProjectArea();
    }

    public void setGcArea(String gcArea) {
        setProjectArea(gcArea);
    }

    public String getSgUnit() {
        return getConstructionUnit();
    }

    public void setSgUnit(String sgUnit) {
        setConstructionUnit(sgUnit);
    }

    public String getJsUnit() {
        return getBuildingUnit();
    }

    public void setJsUnit(String jsUnit) {
        setBuildingUnit(jsUnit);
    }

    public String getJlUnit() {
        return getSupervisionUnit();
    }

    public void setJlUnit(String jlUnit) {
        setSupervisionUnit(jlUnit);
    }

    public String getKcUnit() {
        return getSurveyUnit();
    }

    public void setKcUnit(String kcUnit) {
        setSurveyUnit(kcUnit);
    }

    public String getBeizhu() {
        return getRemarks();
    }

    public void setBeizhu(String beizhu) {
        setRemarks(beizhu);
    }

    public String getWtRegName() {
        return getClientRegName();
    }

    public void setWtRegName(String wtRegName) {
        setClientRegName(wtRegName);
    }

    public String getWtStatus() {
        return getSampleStatus();
    }

    public void setWtStatus(String wtStatus) {
        setSampleStatus(wtStatus);
    }

    public String getYwdepartment() {
        return getTestCategory();
    }

    public void setYwdepartment(String ywdepartment) {
        setTestCategory(ywdepartment);
    }
}
