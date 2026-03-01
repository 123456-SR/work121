package org.example.work121.entity;

/**
 * 轻型动力触探检测记录表实体类
 */
public class LightDynamicPenetrationRecord extends BusinessEntity {

    /** 关联委托单ID */
    private String entrustmentId;

    /** 数据JSON */
    private String dataJson;

    /** 备注 */
    private String remarks;

    /** 岩土性状 */
    private String soilProperty;

    /** 设计承载力 (kPa) */
    private String designCapacity;

    /** 锤重量 (kg) */
    private String hammerWeight;

    /** 落距 (cm) */
    private String dropDistance;

    /** 数据块 */
    private String dataBlocks;

    public String getEntrustmentId() {
        return entrustmentId;
    }

    public void setEntrustmentId(String entrustmentId) {
        this.entrustmentId = entrustmentId;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSoilProperty() {
        return soilProperty;
    }

    public void setSoilProperty(String soilProperty) {
        this.soilProperty = soilProperty;
    }

    public String getDesignCapacity() {
        return designCapacity;
    }

    public void setDesignCapacity(String designCapacity) {
        this.designCapacity = designCapacity;
    }

    public String getHammerWeight() {
        return hammerWeight;
    }

    public void setHammerWeight(String hammerWeight) {
        this.hammerWeight = hammerWeight;
    }

    public String getDropDistance() {
        return dropDistance;
    }

    public void setDropDistance(String dropDistance) {
        this.dropDistance = dropDistance;
    }

    public String getDataBlocks() {
        return dataBlocks;
    }

    public void setDataBlocks(String dataBlocks) {
        this.dataBlocks = dataBlocks;
    }
}
