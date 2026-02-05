package org.example.work121.entity;

/**
 * 密度试验实体类
 */
public class DensityTest extends Entrustment {
    
    /** 土的种类 */
    private String soilType;

    /** 环刀体积 (cm³) */
    private String ringVolume;

    /** 试样湿重 (g) */
    private String wetWeight;

    /** 试样干重 (g) */
    private String dryWeight;

    /** 含水率 (%) */
    private String waterContent;

    /** 湿密度 (g/cm³) */
    private String wetDensity;

    /** 干密度 (g/cm³) */
    private String dryDensity;

    /** 最大干密度 */
    private String maxDryDensity;

    /** 最小干密度 */
    private String minDryDensity;

    /** 最优含水率 */
    private String optimumMoisture;

    /** 压实系数 */
    private String compactionCoefficient;

    /** 合格率 */
    private String qualifiedRate;

    /** 样品名称及状态 */
    private String sampleNameStatus;



    /** 设计指标 */
    private String designIndex;

    /** 检测结果 */
    private String testResult;

    /** 数据JSON */
    private String dataJson;

    /** 关联委托单ID */
    private String entrustmentId;

    public String getEntrustmentId() {
        return entrustmentId;
    }

    public void setEntrustmentId(String entrustmentId) {
        this.entrustmentId = entrustmentId;
    }

    public String getSampleNameStatus() {
        return sampleNameStatus;
    }

    public void setSampleNameStatus(String sampleNameStatus) {
        this.sampleNameStatus = sampleNameStatus;
    }

    public String getDesignIndex() {
        return designIndex;
    }

    public void setDesignIndex(String designIndex) {
        this.designIndex = designIndex;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getRingVolume() {
        return ringVolume;
    }

    public void setRingVolume(String ringVolume) {
        this.ringVolume = ringVolume;
    }

    public String getWetWeight() {
        return wetWeight;
    }

    public void setWetWeight(String wetWeight) {
        this.wetWeight = wetWeight;
    }

    public String getDryWeight() {
        return dryWeight;
    }

    public void setDryWeight(String dryWeight) {
        this.dryWeight = dryWeight;
    }

    public String getWaterContent() {
        return waterContent;
    }

    public void setWaterContent(String waterContent) {
        this.waterContent = waterContent;
    }

    public String getWetDensity() {
        return wetDensity;
    }

    public void setWetDensity(String wetDensity) {
        this.wetDensity = wetDensity;
    }

    public String getDryDensity() {
        return dryDensity;
    }

    public void setDryDensity(String dryDensity) {
        this.dryDensity = dryDensity;
    }

    public String getMaxDryDensity() {
        return maxDryDensity;
    }

    public void setMaxDryDensity(String maxDryDensity) {
        this.maxDryDensity = maxDryDensity;
    }

    public String getMinDryDensity() {
        return minDryDensity;
    }

    public void setMinDryDensity(String minDryDensity) {
        this.minDryDensity = minDryDensity;
    }

    public String getOptimumMoisture() {
        return optimumMoisture;
    }

    public void setOptimumMoisture(String optimumMoisture) {
        this.optimumMoisture = optimumMoisture;
    }

    public String getCompactionCoefficient() {
        return compactionCoefficient;
    }

    public void setCompactionCoefficient(String compactionCoefficient) {
        this.compactionCoefficient = compactionCoefficient;
    }

    public String getQualifiedRate() {
        return qualifiedRate;
    }

    public void setQualifiedRate(String qualifiedRate) {
        this.qualifiedRate = qualifiedRate;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
