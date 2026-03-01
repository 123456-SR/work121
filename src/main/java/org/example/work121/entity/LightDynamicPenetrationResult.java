package org.example.work121.entity;

/**
 * 轻型动力触探检测结果表实体类
 */
public class LightDynamicPenetrationResult extends BusinessEntity {

    /** 关联委托单ID */
    private String entrustmentId;

    /** 数据JSON */
    private String dataJson;

    /** 备注 */
    private String remarks;

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
}
