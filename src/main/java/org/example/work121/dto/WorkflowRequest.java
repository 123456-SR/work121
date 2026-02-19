package org.example.work121.dto;

/**
 * 工作流请求对象
 */
public class WorkflowRequest {

    /**
     * 表格类型 (如: DENSITY_TEST, REBOUND_METHOD, ENTRUSTMENT)
     */
    private String tableType;

    /**
     * 记录ID (或 entrustmentId)
     */
    private String recordId;

    /**
     * 操作类型
     * SUBMIT: 提交/草稿 -> 待审核 (需要检测人签名)
     * SIGN_REVIEW: 复核人签字 (待审核 -> 待签字/待批准)
     * SIGN_APPROVE: 批准人签字 (待签字 -> 完成)
     * REJECT: 打回
     */
    private String action;

    /**
     * 签名照片数据 (Base64) 或 签名ID
     */
    private String signatureData;

    /**
     * 操作人账号
     */
    private String userAccount;

    /**
     * 打回原因 (仅当 action=REJECT 时有效)
     */
    private String rejectReason;

    /**
     * 下一步处理人 (可选)
     */
    private String nextHandler;

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSignatureData() {
        return signatureData;
    }

    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(String nextHandler) {
        this.nextHandler = nextHandler;
    }
}
