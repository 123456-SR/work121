package org.example.work121.entity;

/**
 * 贝克曼梁法检测实体类
 */
public class BeckmanBeam extends Entrustment {
    
    /** 路基类型 */
    private String subgradeType;

    /** 弯沉仪类型 */
    private String deflectometerType;

    /** 后轴重 (kN) */
    private String axleWeight;

    /** 轮胎接地压强 (MPa) */
    private String tirePressure;

    /** 检测段长度 (m) */
    private String testLength;

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

    public String getSubgradeType() {
        return subgradeType;
    }

    public void setSubgradeType(String subgradeType) {
        this.subgradeType = subgradeType;
    }

    public String getDeflectometerType() {
        return deflectometerType;
    }

    public void setDeflectometerType(String deflectometerType) {
        this.deflectometerType = deflectometerType;
    }

    public String getAxleWeight() {
        return axleWeight;
    }

    public void setAxleWeight(String axleWeight) {
        this.axleWeight = axleWeight;
    }

    public String getTirePressure() {
        return tirePressure;
    }

    public void setTirePressure(String tirePressure) {
        this.tirePressure = tirePressure;
    }

    public String getTestLength() {
        return testLength;
    }

    public void setTestLength(String testLength) {
        this.testLength = testLength;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
