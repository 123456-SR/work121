package org.example.work121.entity;

/**
 * 贝克曼梁法检测实体类
 */
public class BeckmanBeam extends BusinessEntity {
    
    /** 检测地点 */
    private String testPlace;

    /** 路基类型 */
    private String subgradeType;

    /** 出车类型 */
    private String carType;

    /** 业务来源 */
    private String ywSource;

    /** 盲检编号 */
    private String blindNumber;

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

    public String getTestPlace() {
        return testPlace;
    }

    public void setTestPlace(String testPlace) {
        this.testPlace = testPlace;
    }

    public String getSubgradeType() {
        return subgradeType;
    }

    public void setSubgradeType(String subgradeType) {
        this.subgradeType = subgradeType;
    }


    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getYwSource() {
        return ywSource;
    }

    public void setYwSource(String ywSource) {
        this.ywSource = ywSource;
    }

    public String getBlindNumber() {
        return blindNumber;
    }

    public void setBlindNumber(String blindNumber) {
        this.blindNumber = blindNumber;
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
