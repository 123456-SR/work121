package org.example.work121.entity;

/**
 * 灌水法检测结果表实体类
 */
public class WaterReplacementResult extends BusinessEntity {

    /** 关联委托单ID */
    private String entrustmentId;

    /** 数据JSON */
    private String dataJson;

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
}
