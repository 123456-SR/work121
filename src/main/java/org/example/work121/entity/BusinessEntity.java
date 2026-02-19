package org.example.work121.entity;

import java.util.Date;

/**
 * 业务基础实体类
 * 包含业务表共有的字段
 */
public class BusinessEntity extends BaseEntity {
    
    /** 委托编号/样品编号 */
    private String wtNum;

    /** 工程名称 */
    private String projectName;

    /** 施工部位 */
    private String constructionPart;

    /** 检测日期 */
    private Date testDate;

    /** 委托日期 */
    private Date commissionDate;

    /** 施工单位 */
    private String constructionUnit;

    /** 建设单位 */
    private String buildingUnit;

    /** 监理单位 */
    private String supervisionUnit;

    /** 设计单位 */
    private String designUnit;

    /** 勘察单位 */
    private String surveyUnit;

    /** 委托单位 */
    private String clientUnit;

    /** 委托人 */
    private String client;

    /** 委托人电话 */
    private String clientTel;

    /** 委托单位联系人 */
    private String clientUnitPerson;

    /** 委托单位邮编 */
    private String clientUnitPostalcode;

    /** 工程区域 */
    private String projectArea;

    /** 见证单位 */
    private String witnessUnit;

    /** 见证人 */
    private String witness;

    /** 样品名称 */
    private String sampleName;

    /** 检测类别 */
    private String testCategory;

    /** 检测依据 */
    private String testBasis;

    /** 仪器设备 */
    private String equipment;

    /** 检测结论 */
    private String conclusion;

    /** 批准人 */
    private String approver;

    /** 审核人 */
    private String reviewer;

    /** 检测人/主检 */
    private String tester;

    /** 报告日期 */
    private Date reportDate;

    /** 计算人 */
    private String calculator;

    /** 填写人/记录人 */
    private String filler;

    /** 记录表检验人 */
    private String recordTester;

    /** 记录表审核人 */
    private String recordReviewer;

    /** 记录表审核人签名 */
    private String recordReviewSign;

    /** 承接人签名 (For Entrustment) */
    private String wtManSign;

    /** 委托单审核人 */
    private String wtReviewer;

    /** 委托单审核人签名 */
    private String wtReviewSign;

    /** 检测方法 */
    private String testMethod;

    /** 
     * 流程状态 
     * 0: 草稿/未提交
     * 1: 待审核
     * 2: 已退回
     * 3: 审核通过/待签字
     * 4: 已签字/待批准
     * 5: 已批准/完成
     */
    private Integer status;

    /** 打回原因 */
    private String rejectReason;

    /** 下一步处理人（填表人/签字人/审核人/批准人） */
    private String nextHandler;

    /** 检测人/填表人签名照片 */
    private String inspectSignaturePhoto;

    /** 复核人/审核人签名照片 */
    private String reviewSignaturePhoto;

    /** 批准人签名照片 */
    private String approveSignaturePhoto;

    // ================== 人员真实姓名显示字段 ==================
    /** 检测人姓名 */
    private String testerName;
    /** 复核人姓名 */
    private String reviewerName;
    /** 批准人姓名 */
    private String approverName;
    /** 创建人姓名 */
    private String createByName;
    /** 更新人姓名 */
    private String updateByName;
    /** 登记人真实姓名 (用于前端显示) */
    private String clientRegRealName;


    public String getInspectSignaturePhoto() {
        return inspectSignaturePhoto;
    }

    public void setInspectSignaturePhoto(String inspectSignaturePhoto) {
        this.inspectSignaturePhoto = inspectSignaturePhoto;
    }

    public String getReviewSignaturePhoto() {
        return reviewSignaturePhoto;
    }

    public void setReviewSignaturePhoto(String reviewSignaturePhoto) {
        this.reviewSignaturePhoto = reviewSignaturePhoto;
    }

    public String getApproveSignaturePhoto() {
        return approveSignaturePhoto;
    }

    public void setApproveSignaturePhoto(String approveSignaturePhoto) {
        this.approveSignaturePhoto = approveSignaturePhoto;
    }

    public String getWtNum() {
        return wtNum;
    }

    public void setWtNum(String wtNum) {
        this.wtNum = wtNum;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getConstructionPart() {
        return constructionPart;
    }

    public void setConstructionPart(String constructionPart) {
        this.constructionPart = constructionPart;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public Date getCommissionDate() {
        return commissionDate;
    }

    public void setCommissionDate(Date commissionDate) {
        this.commissionDate = commissionDate;
    }

    public String getConstructionUnit() {
        return constructionUnit;
    }

    public void setConstructionUnit(String constructionUnit) {
        this.constructionUnit = constructionUnit;
    }

    public String getBuildingUnit() {
        return buildingUnit;
    }

    public void setBuildingUnit(String buildingUnit) {
        this.buildingUnit = buildingUnit;
    }

    public String getSupervisionUnit() {
        return supervisionUnit;
    }

    public void setSupervisionUnit(String supervisionUnit) {
        this.supervisionUnit = supervisionUnit;
    }

    public String getDesignUnit() {
        return designUnit;
    }

    public void setDesignUnit(String designUnit) {
        this.designUnit = designUnit;
    }

    public String getSurveyUnit() {
        return surveyUnit;
    }

    public void setSurveyUnit(String surveyUnit) {
        this.surveyUnit = surveyUnit;
    }

    public String getClientUnit() {
        return clientUnit;
    }

    public void setClientUnit(String clientUnit) {
        this.clientUnit = clientUnit;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClientTel() {
        return clientTel;
    }

    public void setClientTel(String clientTel) {
        this.clientTel = clientTel;
    }

    public String getClientUnitPerson() {
        return clientUnitPerson;
    }

    public void setClientUnitPerson(String clientUnitPerson) {
        this.clientUnitPerson = clientUnitPerson;
    }

    public String getClientUnitPostalcode() {
        return clientUnitPostalcode;
    }

    public void setClientUnitPostalcode(String clientUnitPostalcode) {
        this.clientUnitPostalcode = clientUnitPostalcode;
    }

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getWitnessUnit() {
        return witnessUnit;
    }

    public void setWitnessUnit(String witnessUnit) {
        this.witnessUnit = witnessUnit;
    }

    public String getWitness() {
        return witness;
    }

    public void setWitness(String witness) {
        this.witness = witness;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public String getTestBasis() {
        return testBasis;
    }

    public void setTestBasis(String testBasis) {
        this.testBasis = testBasis;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getCalculator() {
        return calculator;
    }

    public void setCalculator(String calculator) {
        this.calculator = calculator;
    }

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

    public String getWtManSign() {
        return wtManSign;
    }

    public void setWtManSign(String wtManSign) {
        this.wtManSign = wtManSign;
    }

    public String getWtReviewer() {
        return wtReviewer;
    }

    public void setWtReviewer(String wtReviewer) {
        this.wtReviewer = wtReviewer;
    }

    public String getWtReviewSign() {
        return wtReviewSign;
    }

    public void setWtReviewSign(String wtReviewSign) {
        this.wtReviewSign = wtReviewSign;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    // ================== 人员真实姓名 getter/setter ==================
    public String getTesterName() { return testerName; }
    public void setTesterName(String testerName) { this.testerName = testerName; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }

    public String getCreateByName() { return createByName; }
    public void setCreateByName(String createByName) { this.createByName = createByName; }

    public String getUpdateByName() { return updateByName; }
    public void setUpdateByName(String updateByName) { this.updateByName = updateByName; }

    public String getClientRegRealName() { return clientRegRealName; }
    public void setClientRegRealName(String clientRegRealName) { this.clientRegRealName = clientRegRealName; }
}
