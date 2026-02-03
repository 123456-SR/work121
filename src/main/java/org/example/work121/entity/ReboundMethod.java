package org.example.work121.entity;

import java.util.Date;

/**
 * 回弹法检测实体类
 */
public class ReboundMethod extends BusinessEntity {
    
    /** 结构部位 */
    private String structurePart;

    /** 混凝土强度等级 */
    private String concreteGrade;

    /** 成型日期 */
    private Date moldingDate;

    /** 检测龄期 */
    private String age;

    /** 回弹仪型号 */
    private String instrumentModel;

    /** 检定证号 */
    private String calibrationNo;

    /** 测区数 */
    private String zoneCount;

    /** 浇筑方向 */
    private String pouringDirection;

    /** 检测角度 */
    private String testAngle;

    /** 泵送方式 */
    private String pumpingMethod;

    /** 泵送 */
    private String pumping;

    /** 设计强度 */
    private String designStrength;

    /** 碳化深度 */
    private String carbonationDepth;

    /** 检测结果 */
    private String testResult;

    /** 数据JSON */
    private String dataJson;

    public String getStructurePart() {
        return structurePart;
    }

    public void setStructurePart(String structurePart) {
        this.structurePart = structurePart;
    }

    public String getConcreteGrade() {
        return concreteGrade;
    }

    public void setConcreteGrade(String concreteGrade) {
        this.concreteGrade = concreteGrade;
    }

    public Date getMoldingDate() {
        return moldingDate;
    }

    public void setMoldingDate(Date moldingDate) {
        this.moldingDate = moldingDate;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getInstrumentModel() {
        return instrumentModel;
    }

    public void setInstrumentModel(String instrumentModel) {
        this.instrumentModel = instrumentModel;
    }

    public String getCalibrationNo() {
        return calibrationNo;
    }

    public void setCalibrationNo(String calibrationNo) {
        this.calibrationNo = calibrationNo;
    }

    public String getZoneCount() {
        return zoneCount;
    }

    public void setZoneCount(String zoneCount) {
        this.zoneCount = zoneCount;
    }

    public String getPouringDirection() {
        return pouringDirection;
    }

    public void setPouringDirection(String pouringDirection) {
        this.pouringDirection = pouringDirection;
    }

    public String getTestAngle() {
        return testAngle;
    }

    public void setTestAngle(String testAngle) {
        this.testAngle = testAngle;
    }

    public String getPumpingMethod() {
        return pumpingMethod;
    }

    public void setPumpingMethod(String pumpingMethod) {
        this.pumpingMethod = pumpingMethod;
    }

    public String getPumping() {
        return pumping;
    }

    public void setPumping(String pumping) {
        this.pumping = pumping;
    }

    public String getDesignStrength() {
        return designStrength;
    }

    public void setDesignStrength(String designStrength) {
        this.designStrength = designStrength;
    }

    public String getCarbonationDepth() {
        return carbonationDepth;
    }

    public void setCarbonationDepth(String carbonationDepth) {
        this.carbonationDepth = carbonationDepth;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
