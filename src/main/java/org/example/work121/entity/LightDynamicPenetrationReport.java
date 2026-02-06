package org.example.work121.entity;

/**
 * 轻型动力触探检测报告表实体类
 */
public class LightDynamicPenetrationReport extends BusinessEntity {

    /** 关联委托单ID */
    private String entrustmentId;

    /** 数据JSON */
    private String dataJson;

    /** 复核人签名照片 */
    private String reviewSignaturePhoto;

    /** 检测人签名照片 */
    private String inspectSignaturePhoto;

    /** 批准人签名照片 */
    private String approveSignaturePhoto;

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

    public String getReviewSignaturePhoto() {
        return reviewSignaturePhoto;
    }

    public void setReviewSignaturePhoto(String reviewSignaturePhoto) {
        this.reviewSignaturePhoto = reviewSignaturePhoto;
    }

    public String getInspectSignaturePhoto() {
        return inspectSignaturePhoto;
    }

    public void setInspectSignaturePhoto(String inspectSignaturePhoto) {
        this.inspectSignaturePhoto = inspectSignaturePhoto;
    }

    public String getApproveSignaturePhoto() {
        return approveSignaturePhoto;
    }

    public void setApproveSignaturePhoto(String approveSignaturePhoto) {
        this.approveSignaturePhoto = approveSignaturePhoto;
    }
}
