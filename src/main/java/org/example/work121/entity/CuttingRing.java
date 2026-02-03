package org.example.work121.entity;

/**
 * 环刀法检测实体类
 */
public class CuttingRing extends Entrustment {
    
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

    /** 压实系数 */
    private String compactionCoefficient;

    /** 合格率 */
    private String qualifiedRate;

    /** 数据JSON */
    private String dataJson;

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
