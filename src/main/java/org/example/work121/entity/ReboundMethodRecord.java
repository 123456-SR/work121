package org.example.work121.entity;

/**
 * 回弹法检测记录表实体类
 */
public class ReboundMethodRecord extends BusinessEntity {

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

    /** 填写人 */
    private String filler;

    /** 记录检测人 */
    private String recordTester;

    /** 记录审核人 */
    private String recordReviewer;

    /** 记录审核人签名 */
    private String recordReviewSign;

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public String getRecordTester() {
        return recordTester;
    }

    public void setRecordTester(String recordTester) {
        this.recordTester = recordTester;
    }

    public String getRecordReviewer() {
        return recordReviewer;
    }

    public void setRecordReviewer(String recordReviewer) {
        this.recordReviewer = recordReviewer;
    }

    public String getRecordReviewSign() {
        return recordReviewSign;
    }

    public void setRecordReviewSign(String recordReviewSign) {
        this.recordReviewSign = recordReviewSign;
    }

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
