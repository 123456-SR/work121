package org.example.work121.entity;

/**
 * 核子密度仪法检测实体类
 */
public class NuclearDensity extends BusinessEntity {

    /** 核子仪型号 */
    private String nuclearModel;

    /** 检测深度 (cm) */
    private String testDepth;

    /** 数据JSON */
    private String dataJson;

    public String getNuclearModel() {
        return nuclearModel;
    }

    public void setNuclearModel(String nuclearModel) {
        this.nuclearModel = nuclearModel;
    }

    public String getTestDepth() {
        return testDepth;
    }

    public void setTestDepth(String testDepth) {
        this.testDepth = testDepth;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
