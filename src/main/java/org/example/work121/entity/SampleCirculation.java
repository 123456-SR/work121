package org.example.work121.entity;

import java.time.LocalDateTime;

public class SampleCirculation extends BaseEntity {
    private String testLab;          // 检测室
    private String sampleName;       // 样品名称
    private String specModel;        // 规格/型号
    private String sampleNumber;     // 样品编号
    private LocalDateTime receiveDate; // 接样日期
    private String sampleStatus;     // 样品状态
    private String sampleQuantity;   // 样品数量
    private String testItems;        // 检测项目
    private String testStandard;     // 检测依据
    private String sampleHistory;    // 样品历史及概况
    private String receivePerson;    // 接样人/发样人
    private String receivePersonId;  // 接样人/发样人ID
    private String withdrawPerson;   // 领样人
    private String withdrawPersonId; // 领样人ID
    private String remarks;          // 备注

    // Getters and Setters
    public String getTestLab() {
        return testLab;
    }

    public void setTestLab(String testLab) {
        this.testLab = testLab;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSpecModel() {
        return specModel;
    }

    public void setSpecModel(String specModel) {
        this.specModel = specModel;
    }

    public String getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(String sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public LocalDateTime getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(LocalDateTime receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getSampleStatus() {
        return sampleStatus;
    }

    public void setSampleStatus(String sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    public String getSampleQuantity() {
        return sampleQuantity;
    }

    public void setSampleQuantity(String sampleQuantity) {
        this.sampleQuantity = sampleQuantity;
    }

    public String getTestItems() {
        return testItems;
    }

    public void setTestItems(String testItems) {
        this.testItems = testItems;
    }

    public String getTestStandard() {
        return testStandard;
    }

    public void setTestStandard(String testStandard) {
        this.testStandard = testStandard;
    }

    public String getSampleHistory() {
        return sampleHistory;
    }

    public void setSampleHistory(String sampleHistory) {
        this.sampleHistory = sampleHistory;
    }

    public String getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    public String getReceivePersonId() {
        return receivePersonId;
    }

    public void setReceivePersonId(String receivePersonId) {
        this.receivePersonId = receivePersonId;
    }

    public String getWithdrawPerson() {
        return withdrawPerson;
    }

    public void setWithdrawPerson(String withdrawPerson) {
        this.withdrawPerson = withdrawPerson;
    }

    public String getWithdrawPersonId() {
        return withdrawPersonId;
    }

    public void setWithdrawPersonId(String withdrawPersonId) {
        this.withdrawPersonId = withdrawPersonId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}