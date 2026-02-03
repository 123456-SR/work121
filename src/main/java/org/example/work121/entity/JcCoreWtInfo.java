package org.example.work121.entity;

import java.util.Date;

/**
 * 检测委托信息实体类
 */
public class JcCoreWtInfo extends BusinessEntity {
    
    private String wtRegId;
    private String wtRegName;
    private String wtStatus;
    
    private String customerId;
    private String datacid;
    private String ywdepartment;
    private String yyMan;
    private String yyManId;
    private String sfmon;
    private String discountMoney;
    private String realMoney;
    private String invoiceMoney;
    private String transferMoney;
    private String directMoney;
    private String reallocateMoney;
    private String accountNumber;
    
    private String jsStatus;
    private String jsUnitId;
    
    private String remainingMoney;
    private String backMoney;
    private String tozhMoney;
    private String alreadyMoney;
    private String xnMoney;
    
    private String gcCode;
    private String gcXmjl;
    private String gcZj;
    private String gcJianduId;
    private String gcAreaCode;
    
    private String kcUnitId;
    private String sgCode;
    private String sgUnitId;
    
    private String jdUnit;
    private String jdUnitId;
    private String jdMan;
    private String jdManId;
    private String jdManTel;
    private String jianduke;
    
    private String jlUnitId;
    
    private String jzUnit;
    private String jzUnitId;
    private String jzManId;
    private String jzMan;
    private String jzManTel;
    
    private String olWtNum;
    private String stationId;
    private String province;
    private String city;

    // ================= Compatibility Methods =================

    public String getWtId() {
        return getId();
    }

    public void setWtId(String wtId) {
        setId(wtId);
    }

    public Date getWtDate() {
        return getCommissionDate();
    }

    public void setWtDate(Date wtDate) {
        setCommissionDate(wtDate);
    }

    public String getWtMan() {
        return getClient();
    }

    public void setWtMan(String wtMan) {
        setClient(wtMan);
    }

    public String getWtUnit() {
        return getClientUnit();
    }

    public void setWtUnit(String wtUnit) {
        setClientUnit(wtUnit);
    }

    public String getGcName() {
        return getProjectName();
    }

    public void setGcName(String gcName) {
        setProjectName(gcName);
    }

    public String getGcArea() {
        return getProjectArea();
    }

    public void setGcArea(String gcArea) {
        setProjectArea(gcArea);
    }

    public String getSgUnit() {
        return getConstructionUnit();
    }

    public void setSgUnit(String sgUnit) {
        setConstructionUnit(sgUnit);
    }

    public String getJsUnit() {
        return getBuildingUnit();
    }

    public void setJsUnit(String jsUnit) {
        setBuildingUnit(jsUnit);
    }

    public String getJlUnit() {
        return getSupervisionUnit();
    }

    public void setJlUnit(String jlUnit) {
        setSupervisionUnit(jlUnit);
    }

    public String getKcUnit() {
        return getSurveyUnit();
    }

    public void setKcUnit(String kcUnit) {
        setSurveyUnit(kcUnit);
    }

    public String getBeizhu() {
        return getRemarks();
    }

    public void setBeizhu(String beizhu) {
        setRemarks(beizhu);
    }

    // ================= Existing Methods =================

    public String getWtRegId() {
        return wtRegId;
    }

    public void setWtRegId(String wtRegId) {
        this.wtRegId = wtRegId;
    }

    public String getWtRegName() {
        return wtRegName;
    }

    public void setWtRegName(String wtRegName) {
        this.wtRegName = wtRegName;
    }

    public String getWtStatus() {
        return wtStatus;
    }

    public void setWtStatus(String wtStatus) {
        this.wtStatus = wtStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDatacid() {
        return datacid;
    }

    public void setDatacid(String datacid) {
        this.datacid = datacid;
    }

    public String getYwdepartment() {
        return ywdepartment;
    }

    public void setYwdepartment(String ywdepartment) {
        this.ywdepartment = ywdepartment;
    }

    public String getYyMan() {
        return yyMan;
    }

    public void setYyMan(String yyMan) {
        this.yyMan = yyMan;
    }

    public String getYyManId() {
        return yyManId;
    }

    public void setYyManId(String yyManId) {
        this.yyManId = yyManId;
    }

    public String getSfmon() {
        return sfmon;
    }

    public void setSfmon(String sfmon) {
        this.sfmon = sfmon;
    }

    public String getDiscountMoney() {
        return discountMoney;
    }

    public void setDiscountMoney(String discountMoney) {
        this.discountMoney = discountMoney;
    }

    public String getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(String realMoney) {
        this.realMoney = realMoney;
    }

    public String getInvoiceMoney() {
        return invoiceMoney;
    }

    public void setInvoiceMoney(String invoiceMoney) {
        this.invoiceMoney = invoiceMoney;
    }

    public String getTransferMoney() {
        return transferMoney;
    }

    public void setTransferMoney(String transferMoney) {
        this.transferMoney = transferMoney;
    }

    public String getDirectMoney() {
        return directMoney;
    }

    public void setDirectMoney(String directMoney) {
        this.directMoney = directMoney;
    }

    public String getReallocateMoney() {
        return reallocateMoney;
    }

    public void setReallocateMoney(String reallocateMoney) {
        this.reallocateMoney = reallocateMoney;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getJsStatus() {
        return jsStatus;
    }

    public void setJsStatus(String jsStatus) {
        this.jsStatus = jsStatus;
    }

    public String getJsUnitId() {
        return jsUnitId;
    }

    public void setJsUnitId(String jsUnitId) {
        this.jsUnitId = jsUnitId;
    }

    public String getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(String remainingMoney) {
        this.remainingMoney = remainingMoney;
    }

    public String getBackMoney() {
        return backMoney;
    }

    public void setBackMoney(String backMoney) {
        this.backMoney = backMoney;
    }

    public String getTozhMoney() {
        return tozhMoney;
    }

    public void setTozhMoney(String tozhMoney) {
        this.tozhMoney = tozhMoney;
    }

    public String getAlreadyMoney() {
        return alreadyMoney;
    }

    public void setAlreadyMoney(String alreadyMoney) {
        this.alreadyMoney = alreadyMoney;
    }

    public String getXnMoney() {
        return xnMoney;
    }

    public void setXnMoney(String xnMoney) {
        this.xnMoney = xnMoney;
    }

    public String getGcCode() {
        return gcCode;
    }

    public void setGcCode(String gcCode) {
        this.gcCode = gcCode;
    }

    public String getGcXmjl() {
        return gcXmjl;
    }

    public void setGcXmjl(String gcXmjl) {
        this.gcXmjl = gcXmjl;
    }

    public String getGcZj() {
        return gcZj;
    }

    public void setGcZj(String gcZj) {
        this.gcZj = gcZj;
    }

    public String getGcJianduId() {
        return gcJianduId;
    }

    public void setGcJianduId(String gcJianduId) {
        this.gcJianduId = gcJianduId;
    }

    public String getGcAreaCode() {
        return gcAreaCode;
    }

    public void setGcAreaCode(String gcAreaCode) {
        this.gcAreaCode = gcAreaCode;
    }

    public String getKcUnitId() {
        return kcUnitId;
    }

    public void setKcUnitId(String kcUnitId) {
        this.kcUnitId = kcUnitId;
    }

    public String getSgCode() {
        return sgCode;
    }

    public void setSgCode(String sgCode) {
        this.sgCode = sgCode;
    }

    public String getSgUnitId() {
        return sgUnitId;
    }

    public void setSgUnitId(String sgUnitId) {
        this.sgUnitId = sgUnitId;
    }

    public String getJdUnit() {
        return jdUnit;
    }

    public void setJdUnit(String jdUnit) {
        this.jdUnit = jdUnit;
    }

    public String getJdUnitId() {
        return jdUnitId;
    }

    public void setJdUnitId(String jdUnitId) {
        this.jdUnitId = jdUnitId;
    }

    public String getJdMan() {
        return jdMan;
    }

    public void setJdMan(String jdMan) {
        this.jdMan = jdMan;
    }

    public String getJdManId() {
        return jdManId;
    }

    public void setJdManId(String jdManId) {
        this.jdManId = jdManId;
    }

    public String getJdManTel() {
        return jdManTel;
    }

    public void setJdManTel(String jdManTel) {
        this.jdManTel = jdManTel;
    }

    public String getJianduke() {
        return jianduke;
    }

    public void setJianduke(String jianduke) {
        this.jianduke = jianduke;
    }

    public String getJlUnitId() {
        return jlUnitId;
    }

    public void setJlUnitId(String jlUnitId) {
        this.jlUnitId = jlUnitId;
    }

    public String getJzUnit() {
        return jzUnit;
    }

    public void setJzUnit(String jzUnit) {
        this.jzUnit = jzUnit;
    }

    public String getJzUnitId() {
        return jzUnitId;
    }

    public void setJzUnitId(String jzUnitId) {
        this.jzUnitId = jzUnitId;
    }

    public String getJzManId() {
        return jzManId;
    }

    public void setJzManId(String jzManId) {
        this.jzManId = jzManId;
    }

    public String getJzMan() {
        return jzMan;
    }

    public void setJzMan(String jzMan) {
        this.jzMan = jzMan;
    }

    public String getJzManTel() {
        return jzManTel;
    }

    public void setJzManTel(String jzManTel) {
        this.jzManTel = jzManTel;
    }

    public String getOlWtNum() {
        return olWtNum;
    }

    public void setOlWtNum(String olWtNum) {
        this.olWtNum = olWtNum;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
