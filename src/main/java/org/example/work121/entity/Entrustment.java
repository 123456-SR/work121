package org.example.work121.entity;

import java.sql.Timestamp;

/**
 * 委托单实体类
 * 映射数据库委托单表的所有字段
 */
public class Entrustment extends BusinessEntity {
    private static final long serialVersionUID = 1L;

    // ===================== 基础信息字段 =====================
    // 统一编号（WT_ID） - 映射到 BaseEntity.id
    // 样品编号（WT_NUM） - 映射到 BusinessEntity.wtNum
    
    /** 客户ID（CUSTOMER_ID） */
    private String customerId;
    /** 数据ID（DATACID） */
    private String dataCid;
    // 样品名称（DATANAME） - 映射到 BusinessEntity.sampleName
    
    /** 业务部门ID（YWDEPARTMENTID） */
    private String ywDepartmentId;
    // 业务部门（YWDEPARTMENT）- 检测类别 - 映射到 BusinessEntity.testCategory
    
    /** 接收部门ID（DESTDEPARTMENTID） */
    private String destDepartmentId;
    /** 接收部门（DESTDEPARTMENT）- 报告发送部门 */
    private String reportSendDept;
    /** 接收人ID（DESTUSERID） */
    private String destUserId;
    /** 接收人（DESTUSER）- 报告接收人 */
    private String reportSendUser;
    /** 委托单位ID（WT_UNIT_ID） */
    private String clientUnitId;
    // 委托单位（WT_UNIT） - 映射到 BusinessEntity.clientUnit
    // 委托(送样)人（WT_MAN） - 映射到 BusinessEntity.client
    // 委托人电话（WT_MAN_TEL） - 映射到 BusinessEntity.clientTel
    
    /** 委托单位地址（WT_UNIT_ADDRESS） */
    private String clientUnitAddress;
    // 委托单位邮编（WT_UNIT_POSTALCODE） - 映射到 BusinessEntity.clientUnitPostalcode
    // 委托单位联系人（WT_UNIT_PERSON） - 映射到 BusinessEntity.clientUnitPerson
    
    /** 委托单位电话（WT_UNIT_TEL）- 委托单位地址及电话 */
    private String clientUnitTel;
    /** 合同编号（CONTRACT_CODE） */
    private String contractCode;
    /** 工程付款方式（GC_PAYMENT） */
    private String projectPayment;
    /** 工程编码（GC_CODE） */
    private String projectCode;
    // 工程名称（GC_NAME） - 映射到 BusinessEntity.projectName
    // 施工(使用)部位（GC_SUB） - 映射到 BusinessEntity.constructionPart
    
    /** 工程地址（GC_ADDRESS） */
    private String projectAddress;
    // 工程区域（GC_AREA） - 映射到 BusinessEntity.projectArea
    
    /** 建设单位ID（JS_UNIT_ID） */
    private String buildingUnitId;
    // 建设单位（JS_UNIT） - 映射到 BusinessEntity.buildingUnit
    
    /** 勘察单位代码（KC_UNIT_ID） */
    private String surveyUnitId;
    // 勘察单位（KC_UNIT） - 映射到 BusinessEntity.surveyUnit
    
    /** 施工编码（SG_CODE） */
    private String constructionCode;
    /** 施工单位ID（SG_UNIT_ID） */
    private String constructionUnitId;
    // 施工单位（SG_UNIT） - 映射到 BusinessEntity.constructionUnit
    
    /** 工程监督ID（GC_JIANDU_ID） */
    private String projectSuperviseId;
    /** 见证单位ID（JD_UNIT_ID） */
    private String witnessUnitId;
    // 见证单位（JD_UNIT） - 映射到 BusinessEntity.witnessUnit
    
    /** 监督科（JIANDUKE） */
    private String superviseDept;
    /** 见证人ID（JD_MAN_ID） */
    private String witnessId;
    // 见证人（JD_MAN） - 映射到 BusinessEntity.witness
    
    /** 见证人电话（JD_MAN_TEL） */
    private String witnessTel;
    /** 监理单位ID（JL_UNIT_ID） */
    private String supervisionUnitId;
    // 监理单位（JL_UNIT） - 映射到 BusinessEntity.supervisionUnit
    
    /** 建设人员ID（JZ_MAN_ID） */
    private String buildingManId;
    /** 建设人员（JZ_MAN） */
    private String buildingMan;
    /** 建设人员电话（JZ_MAN_TEL） */
    private String buildingManTel;
    /** 建设单位ID（JZ_UNIT_ID） */
    private String buildingUnitId2;
    /** 建设单位（JZ_UNIT） */
    private String buildingUnit2;
    // 委托日期（WT_DATE） - 映射到 BusinessEntity.commissionDate
    
    /** 业务员ID（YY_MAN_ID） */
    private String ywManId;
    /** 业务员（YY_MAN）- 承接(收样)人 */
    private String receiver;
    /** 标准总费用（SFMON）- 应缴检测(验)费 */
    private String standardMoney;
    /** 折扣总费用（DISCOUNT_MONEY） */
    private String discountMoney;
    /** 实际应收取总费用（REAL_MONEY） */
    private String realMoney;
    /** 开票总金额（INVOICE_MONEY） */
    private String invoiceMoney;
    /** 账号转入委托总费用（TRANSFER_MONEY） */
    private String transferMoney;
    /** 直接收取费用（DIRECT_MONEY） */
    private String directMoney;
    /** 委托已分配费用（REALLOCATE_MONEY） */
    private String reallocateMoney;
    /** 账号（ACCOUNT_NUMBER） */
    private String accountNumber;
    /** 备注（BEIZHU）- 样品历史及概况/备注 */
    private String remarks;
    /** 结算状态（JS_STATUS） */
    private String settleStatus;
    /** 委托余额（REMAINING_MONEY） */
    private String remainingMoney;
    /** 退款金额（BACK_MONEY） */
    private String backMoney;
    /** 委托结余返回账号金额（TOZH_MONEY） */
    private String toZhMoney;
    /** 已收金额（ALREADY_MONEY） */
    private String alreadyMoney;
    /** 委托状态（WT_STATUS）- 样品状态 */
    private String sampleStatus;
    /** 虚拟总费用（XN_MONEY） */
    private String virtualMoney;
    /** 检测参数（WT_JCCS）- 检测(验)项目及依据 */
    private String testItems;
    /** 建筑面积（JZ_JZMJ） */
    private String buildingArea;
    /** 建筑性质（JZ_JZXZ） */
    private String buildingNature;
    /** 建筑楼层（JZ_JZLC） */
    private String buildingFloor;
    /** 施工单位分部（SG_UNIT_SUB） */
    private String constructionUnitSub;
    /** 旧机构（OLD_ORG） */
    private String oldOrg;
    /** 规格/型号（GC_GCPQ） */
    private String spec;
    /** 项目监理（GC_XMJL） */
    private String projectSupervision;
    /** 项目总监（GC_ZJ） */
    private String projectDirector;
    /** 历史委托编号（OL_WT_NUM）- 批号 */
    private String batchNumber;
    /** 站点ID（STATION_ID） */
    private String stationId;
    /** 监理人（JL_MAN） */
    private String supervisionMan;
    /** 生产合格证号（PD_PASS_CODE）- 生产厂家或产地 */
    private String manufacturer;
    /** 见证方式（JZFS） */
    private String witnessMethod;
    /** 委托登记人ID（WT_REG_ID） */
    private String clientRegId;
    /** 委托登记人（WT_REG_NAME） */
    private String clientRegName;
    /** 设计单位（SJ_UNIT） - 映射到 BusinessEntity.designUnit (注意这里有重复字段，保留原样还是合并？BusinessEntity有designUnit) */
    // BusinessEntity 已经有 designUnit，这里不需要重复定义
    
    /** 抽样单位（CY_UNIT） */
    private String samplingUnit;
    /** 抽样人（CY_MAN） */
    private String samplingMan;
    /** 抽样人ID（CY_MAN_ID） */
    private String samplingManId;
    /** 结构类型（JGLX） */
    private String structureType;
    /** 结构层数（JGCS） */
    private String structureFloor;
    /** 抽样地点（CY_PLACE） */
    private String samplingPlace;
    /** 省（PROVINCE） */
    private String province;
    /** 市（CITY） */
    private String city;
    /** 区/镇（AREA） */
    private String area;
    /** 区域代码（GC_AREA_CODE） */
    private String projectAreaCode;
    /** 工程类型（GC_TYPE） */
    private String projectType;
    /** 工程编码(交易中心)（GC_NUM） */
    private String projectNum;
    /** 设计单位代码（SJ_UNIT_ID） */
    private String designUnitId;
    /** 抽样人（CHOUYANGREN） */
    private String samplingMan2;
    /** 付款单位（FKDW） */
    private String payUnit;
    /** 抽样人编码（CHOUYANGREN_CODE） */
    private String samplingManCode;
    /** 关联合同号（CONTRACT_CODE_INTERACTION） */
    private String contractCodeInteraction;
    /** 付款单位编号（FKDW_NUM） */
    private String payUnitNum;
    /** 最后更新日期（LASTUPDATE） */
    private Timestamp lastUpdate;
    /** 检验类别（SG） */
    private String inspectType;
    /** 委托登记日期（WT_REG_DATE） */
    private Timestamp clientRegDate;
    /** 模块名称（MODULENAME） */
    private String moduleName;
    /** 接收人(任务会签人)编号（JC_TASK_JS_MAN_ID） */
    private String jcTaskJsManId;
    /** 接收人(任务会签人)（JC_TASK_JS_MAN） */
    private String jcTaskJsMan;
    /** 检测任务登记人编号（JC_TASK_REG_MAN_ID） */
    private String jcTaskRegManId;
    /** 检测任务登记人（JC_TASK_REG_MAN） */
    private String jcTaskRegMan;
    /** 检测任务登记日期（JC_TASK_REG_DATE） */
    private Timestamp jcTaskRegDate;
    /** 任务状态（JC_TASK_STATUS） */
    private String jcTaskStatus;
    /** 任务审核人编号（JC_TASK_SH_MAN_ID） */
    private String jcTaskShManId;
    /** 任务审核人（JC_TASK_SH_MAN） */
    private String jcTaskShMan;
    /** 是否生产归档（IS_PRODUCE_GD） */
    private String isProduceGd;
    /** 生产归档人编号（PRODUCE_GD_MAN_ID） */
    private String produceGdManId;
    /** 生产归档人（PRODUCE_GD_MAN） */
    private String produceGdMan;
    /** 生产归档日期（PRODUCE_GD_DATE） */
    private Timestamp produceGdDate;
    /** 报告交付份数（BGJFFS） */
    private String reportDeliverNum;
    /** 业务员（YW_MAN） */
    private String ywMan;
    /** 业务员ID（YW_MAN_ID） */
    private String ywManId2;
    /** 业务区域（YWQY） */
    private String ywArea;
    /** 建设单位联系人（JS_MAN） */
    private String buildingUnitContact;
    /** 建设单位联系人电话（JS_MAN_TEL） */
    private String buildingUnitContactTel;
    /** 监理单位联系人电话（JL_MAN_TEL） */
    private String supervisionUnitContactTel;
    /** 勘察单位联系人（KC_MAN） */
    private String surveyUnitContact;
    /** 勘察单位联系人电话（KC_MAN_TEL） */
    private String surveyUnitContactTel;
    /** 施工单位联系人（SG_MAN） */
    private String constructionUnitContact;
    /** 施工单位联系人电话（SG_MAN_TEL） */
    private String constructionUnitContactTel;
    /** 项目联系人电话（GC_XMJL_TEL） */
    private String projectContactTel;
    /** 设计单位联系人（SJ_MAN） */
    private String designUnitContact;
    /** 设计单位联系人电话（SJ_MAN_TEL） */
    private String designUnitContactTel;
    /** 任务下达人编号（JC_TASK_XD_MAN_ID） */
    private String jcTaskXdManId;
    /** 任务下达人（JC_TASK_XD_MAN） */
    private String jcTaskXdMan;
    /** 任务下达时间（JC_TASK_XD_DATE） */
    private Timestamp jcTaskXdDate;
    /** SK编号（SK_NUMBER） */
    private String skNumber;
    /** 现场委托登记号（XC_WT_NUM） */
    private String xcWtNum;
    /** 检测地点(预制厂,工程现场)（JCDD） */
    private String testPlace;
    /** 退回原因（SEND_BACK_REASON） */
    private String sendBackReason;
    /** 退回人ID（SEND_BACK_MAN_ID） */
    private String sendBackManId;
    /** 退回人（SEND_BACK_MAN） */
    private String sendBackMan;
    /** 工程备注（GC_BEIZHU） */
    private String projectRemarks;
    /** 出车类型（CAR_TYPE） */
    private String carType;
    /** 业务来源（YW_SOURCE） */
    private String ywSource;
    /** 盲检编号（BLIND_NUMBER） */
    private String blindNumber;
    /** 任务登记预检日期（JC_DATE） */
    private Timestamp jcDate;

    /** 复核人签名照片 */
    private String reviewSignaturePhoto;

    /** 检测人签名照片 */
    private String inspectSignaturePhoto;

    /** 批准人签名照片 */
    private String approveSignaturePhoto;

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
    /** 工程监管编号（GC_JGCODE） */
    private String projectSuperviseCode;
    /** 合同执行阶段（CONTRACT_PHASE） */
    private String contractPhase;
    /** 委托流水号（WT_SERIAL_NUM） */
    private String clientSerialNum;
    /** 检测计划编号-sdjn检测监管（PQT_PLAN_NO） */
    private String pqtPlanNo;
    /** 合同ID-顺德（CONTRACT_ID） */
    private String contractId;
    /** 任务提交人编号（JC_TASK_TJ_MAN_ID） */
    private String jcTaskTjManId;
    /** 任务提交人（JC_TASK_TJ_MAN） */
    private String jcTaskTjMan;
    /** 任务提交人时间（JC_TASK_TJ_DATE） */
    private Timestamp jcTaskTjDate;
    /** 璇佷功鍗曚綅（ZS_UNIT）- 证书单位（疑似乱码，保留字段） */
    private String zsUnit;
    /** 璇佷功鍗曚綅鍦板潃（ZS_UNIT_ADDRESS）- 证书单位地址（疑似乱码，保留字段） */
    private String zsUnitAddress;
    /** 客户名称-北京建材（CUSTOMER_NAME） */
    private String customerName;
    /** NC编号-北京建材（NC_CODE） */
    private String ncCode;
    /** 见证人身份证号（JZ_MAN_IDCARD） */
    private String witnessIdCard;
    /** 取样员身份证号（CY_MAN_IDCARD） */
    private String samplingManIdCard;
    /** 财务记账客户（RECORD_CUSTOMER） */
    private String recordCustomer;
    /** 财务记账客户NC编号（RECORD_NC） */
    private String recordNc;
    /** 收入成本类型-北京建材（INCOME_COST_TYPE） */
    private String incomeCostType;
    /** 鏍峰搧鏍囪瘑（YP_CODE）- 样品编码（疑似乱码，保留字段） */
    private String ypCode;
    /** 灞呴棿鍚堝悓（MEDIATION_CONTRACT）- 调解合同（疑似乱码，保留字段） */
    private String mediationContract;
    /** 灞呴棿鎶樻墸（MEDIATION_DISCOUNT）- 调解折扣（疑似乱码，保留字段） */
    private String mediationDiscount;

    // ===================== 你原有无关字段（分页/版本） =====================
    /** 版本（非数据库字段） */
    private String version;
    /** 日期（非数据库字段） */
    private String date;
    /** 页码（非数据库字段） */
    private String page;
    /** 总页数（非数据库字段） */
    private String totalPages;
    /** 样品数量（非数据库字段，业务扩展） */
    private String sampleQuantity;
    /** 代表批量（非数据库字段，业务扩展） */
    private String representativeBatch;
    /** 样品处置（非数据库字段，业务扩展） */
    private String sampleDisposal;
    /** 费用（非数据库字段，对应standardMoney） */
    private String fee;

    /** 是否可编辑（非数据库字段，用于前端控制修改/删除按钮） */
    private boolean canEdit;

    // 无参构造器
    public Entrustment() {
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    // ===================== 所有字段的getter/setter方法 =====================
    // 基础信息字段
    
    // 兼容性方法：unifiedNumber -> id
    public String getUnifiedNumber() { return getId(); }
    public void setUnifiedNumber(String unifiedNumber) { setId(unifiedNumber); }

    // 兼容性方法：sampleNumber -> wtNum
    public String getSampleNumber() { return getWtNum(); }
    public void setSampleNumber(String sampleNumber) { setWtNum(sampleNumber); }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getDataCid() { return dataCid; }
    public void setDataCid(String dataCid) { this.dataCid = dataCid; }

    // BusinessEntity methods: getSampleName, setSampleName are inherited

    public String getYwDepartmentId() { return ywDepartmentId; }
    public void setYwDepartmentId(String ywDepartmentId) { this.ywDepartmentId = ywDepartmentId; }

    // BusinessEntity methods: getTestCategory, setTestCategory are inherited

    public String getDestDepartmentId() { return destDepartmentId; }
    public void setDestDepartmentId(String destDepartmentId) { this.destDepartmentId = destDepartmentId; }

    public String getReportSendDept() { return reportSendDept; }
    public void setReportSendDept(String reportSendDept) { this.reportSendDept = reportSendDept; }

    public String getDestUserId() { return destUserId; }
    public void setDestUserId(String destUserId) { this.destUserId = destUserId; }

    public String getReportSendUser() { return reportSendUser; }
    public void setReportSendUser(String reportSendUser) { this.reportSendUser = reportSendUser; }
    
    public String getClientUnitId() { return clientUnitId; }
    public void setClientUnitId(String clientUnitId) { this.clientUnitId = clientUnitId; }

    // BusinessEntity methods: getClientUnit, setClientUnit are inherited
    // BusinessEntity methods: getClient, setClient are inherited
    // BusinessEntity methods: getClientTel, setClientTel are inherited

    public String getClientUnitAddress() { return clientUnitAddress; }
    public void setClientUnitAddress(String clientUnitAddress) { this.clientUnitAddress = clientUnitAddress; }

    // BusinessEntity methods: getClientUnitPostalcode, setClientUnitPostalcode are inherited
    // BusinessEntity methods: getClientUnitPerson, setClientUnitPerson are inherited

    public String getClientUnitTel() { return clientUnitTel; }
    public void setClientUnitTel(String clientUnitTel) { this.clientUnitTel = clientUnitTel; }

    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }

    public String getProjectPayment() { return projectPayment; }
    public void setProjectPayment(String projectPayment) { this.projectPayment = projectPayment; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    // BusinessEntity methods: getProjectName, setProjectName are inherited
    // BusinessEntity methods: getConstructionPart, setConstructionPart are inherited

    public String getProjectAddress() { return projectAddress; }
    public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }

    // BusinessEntity methods: getProjectArea, setProjectArea are inherited

    public String getBuildingUnitId() { return buildingUnitId; }
    public void setBuildingUnitId(String buildingUnitId) { this.buildingUnitId = buildingUnitId; }

    // BusinessEntity methods: getBuildingUnit, setBuildingUnit are inherited

    public String getSurveyUnitId() { return surveyUnitId; }
    public void setSurveyUnitId(String surveyUnitId) { this.surveyUnitId = surveyUnitId; }

    // BusinessEntity methods: getSurveyUnit, setSurveyUnit are inherited

    public String getConstructionCode() { return constructionCode; }
    public void setConstructionCode(String constructionCode) { this.constructionCode = constructionCode; }

    public String getConstructionUnitId() { return constructionUnitId; }
    public void setConstructionUnitId(String constructionUnitId) { this.constructionUnitId = constructionUnitId; }

    // BusinessEntity methods: getConstructionUnit, setConstructionUnit are inherited

    public String getProjectSuperviseId() { return projectSuperviseId; }
    public void setProjectSuperviseId(String projectSuperviseId) { this.projectSuperviseId = projectSuperviseId; }

    public String getWitnessUnitId() { return witnessUnitId; }
    public void setWitnessUnitId(String witnessUnitId) { this.witnessUnitId = witnessUnitId; }

    // BusinessEntity methods: getWitnessUnit, setWitnessUnit are inherited

    public String getSuperviseDept() { return superviseDept; }
    public void setSuperviseDept(String superviseDept) { this.superviseDept = superviseDept; }

    public String getWitnessId() { return witnessId; }
    public void setWitnessId(String witnessId) { this.witnessId = witnessId; }

    // BusinessEntity methods: getWitness, setWitness are inherited

    public String getWitnessTel() { return witnessTel; }
    public void setWitnessTel(String witnessTel) { this.witnessTel = witnessTel; }

    public String getSupervisionUnitId() { return supervisionUnitId; }
    public void setSupervisionUnitId(String supervisionUnitId) { this.supervisionUnitId = supervisionUnitId; }

    // BusinessEntity methods: getSupervisionUnit, setSupervisionUnit are inherited

    public String getBuildingManId() { return buildingManId; }
    public void setBuildingManId(String buildingManId) { this.buildingManId = buildingManId; }

    public String getBuildingMan() { return buildingMan; }
    public void setBuildingMan(String buildingMan) { this.buildingMan = buildingMan; }

    public String getBuildingManTel() { return buildingManTel; }
    public void setBuildingManTel(String buildingManTel) { this.buildingManTel = buildingManTel; }

    public String getBuildingUnitId2() { return buildingUnitId2; }
    public void setBuildingUnitId2(String buildingUnitId2) { this.buildingUnitId2 = buildingUnitId2; }

    public String getBuildingUnit2() { return buildingUnit2; }
    public void setBuildingUnit2(String buildingUnit2) { this.buildingUnit2 = buildingUnit2; }

    // 兼容性方法：clientDate -> commissionDate
    public Timestamp getClientDate() {
        if (getCommissionDate() == null) return null;
        return new Timestamp(getCommissionDate().getTime());
    }
    public void setClientDate(Timestamp clientDate) {
        setCommissionDate(clientDate);
    }

    public String getYwManId() { return ywManId; }
    public void setYwManId(String ywManId) { this.ywManId = ywManId; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getStandardMoney() { return standardMoney; }
    public void setStandardMoney(String standardMoney) { this.standardMoney = standardMoney; }

    public String getDiscountMoney() { return discountMoney; }
    public void setDiscountMoney(String discountMoney) { this.discountMoney = discountMoney; }

    public String getRealMoney() { return realMoney; }
    public void setRealMoney(String realMoney) { this.realMoney = realMoney; }

    public String getInvoiceMoney() { return invoiceMoney; }
    public void setInvoiceMoney(String invoiceMoney) { this.invoiceMoney = invoiceMoney; }

    public String getTransferMoney() { return transferMoney; }
    public void setTransferMoney(String transferMoney) { this.transferMoney = transferMoney; }

    public String getDirectMoney() { return directMoney; }
    public void setDirectMoney(String directMoney) { this.directMoney = directMoney; }

    public String getReallocateMoney() { return reallocateMoney; }
    public void setReallocateMoney(String reallocateMoney) { this.reallocateMoney = reallocateMoney; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getSettleStatus() { return settleStatus; }
    public void setSettleStatus(String settleStatus) { this.settleStatus = settleStatus; }

    public String getRemainingMoney() { return remainingMoney; }
    public void setRemainingMoney(String remainingMoney) { this.remainingMoney = remainingMoney; }

    public String getBackMoney() { return backMoney; }
    public void setBackMoney(String backMoney) { this.backMoney = backMoney; }

    public String getToZhMoney() { return toZhMoney; }
    public void setToZhMoney(String toZhMoney) { this.toZhMoney = toZhMoney; }

    public String getAlreadyMoney() { return alreadyMoney; }
    public void setAlreadyMoney(String alreadyMoney) { this.alreadyMoney = alreadyMoney; }

    public String getSampleStatus() { return sampleStatus; }
    public void setSampleStatus(String sampleStatus) { 
        this.sampleStatus = sampleStatus; 
        try {
            if (sampleStatus != null) {
                super.setStatus(sampleStatus);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
        if (status != null) {
            this.sampleStatus = status;
        }
    }

    public String getVirtualMoney() { return virtualMoney; }
    public void setVirtualMoney(String virtualMoney) { this.virtualMoney = virtualMoney; }

    public String getTestItems() { return testItems; }
    public void setTestItems(String testItems) { this.testItems = testItems; }

    public String getBuildingArea() { return buildingArea; }
    public void setBuildingArea(String buildingArea) { this.buildingArea = buildingArea; }

    public String getBuildingNature() { return buildingNature; }
    public void setBuildingNature(String buildingNature) { this.buildingNature = buildingNature; }

    public String getBuildingFloor() { return buildingFloor; }
    public void setBuildingFloor(String buildingFloor) { this.buildingFloor = buildingFloor; }

    public String getConstructionUnitSub() { return constructionUnitSub; }
    public void setConstructionUnitSub(String constructionUnitSub) { this.constructionUnitSub = constructionUnitSub; }

    public String getOldOrg() { return oldOrg; }
    public void setOldOrg(String oldOrg) { this.oldOrg = oldOrg; }

    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }

    public String getProjectSupervision() { return projectSupervision; }
    public void setProjectSupervision(String projectSupervision) { this.projectSupervision = projectSupervision; }

    public String getProjectDirector() { return projectDirector; }
    public void setProjectDirector(String projectDirector) { this.projectDirector = projectDirector; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }

    public String getSupervisionMan() { return supervisionMan; }
    public void setSupervisionMan(String supervisionMan) { this.supervisionMan = supervisionMan; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getWitnessMethod() { return witnessMethod; }
    public void setWitnessMethod(String witnessMethod) { this.witnessMethod = witnessMethod; }

    public String getClientRegId() { return clientRegId; }
    public void setClientRegId(String clientRegId) { this.clientRegId = clientRegId; }

    public String getClientRegName() { return clientRegName; }
    public void setClientRegName(String clientRegName) { this.clientRegName = clientRegName; }

    public String getDesignUnit() { return super.getDesignUnit(); }
    public void setDesignUnit(String designUnit) { super.setDesignUnit(designUnit); }

    public String getSamplingUnit() { return samplingUnit; }
    public void setSamplingUnit(String samplingUnit) { this.samplingUnit = samplingUnit; }

    public String getSamplingMan() { return samplingMan; }
    public void setSamplingMan(String samplingMan) { this.samplingMan = samplingMan; }

    public String getSamplingManId() { return samplingManId; }
    public void setSamplingManId(String samplingManId) { this.samplingManId = samplingManId; }

    public String getStructureType() { return structureType; }
    public void setStructureType(String structureType) { this.structureType = structureType; }

    public String getStructureFloor() { return structureFloor; }
    public void setStructureFloor(String structureFloor) { this.structureFloor = structureFloor; }

    public String getSamplingPlace() { return samplingPlace; }
    public void setSamplingPlace(String samplingPlace) { this.samplingPlace = samplingPlace; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getProjectAreaCode() { return projectAreaCode; }
    public void setProjectAreaCode(String projectAreaCode) { this.projectAreaCode = projectAreaCode; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public String getProjectNum() { return projectNum; }
    public void setProjectNum(String projectNum) { this.projectNum = projectNum; }

    public String getDesignUnitId() { return designUnitId; }
    public void setDesignUnitId(String designUnitId) { this.designUnitId = designUnitId; }

    public String getSamplingMan2() { return samplingMan2; }
    public void setSamplingMan2(String samplingMan2) { this.samplingMan2 = samplingMan2; }

    public String getPayUnit() { return payUnit; }
    public void setPayUnit(String payUnit) { this.payUnit = payUnit; }

    public String getSamplingManCode() { return samplingManCode; }
    public void setSamplingManCode(String samplingManCode) { this.samplingManCode = samplingManCode; }

    public String getContractCodeInteraction() { return contractCodeInteraction; }
    public void setContractCodeInteraction(String contractCodeInteraction) { this.contractCodeInteraction = contractCodeInteraction; }

    public String getPayUnitNum() { return payUnitNum; }
    public void setPayUnitNum(String payUnitNum) { this.payUnitNum = payUnitNum; }

    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }

    public String getInspectType() { return inspectType; }
    public void setInspectType(String inspectType) { this.inspectType = inspectType; }

    public Timestamp getClientRegDate() { return clientRegDate; }
    public void setClientRegDate(Timestamp clientRegDate) { this.clientRegDate = clientRegDate; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getJcTaskJsManId() { return jcTaskJsManId; }
    public void setJcTaskJsManId(String jcTaskJsManId) { this.jcTaskJsManId = jcTaskJsManId; }

    public String getJcTaskJsMan() { return jcTaskJsMan; }
    public void setJcTaskJsMan(String jcTaskJsMan) { this.jcTaskJsMan = jcTaskJsMan; }

    public String getJcTaskRegManId() { return jcTaskRegManId; }
    public void setJcTaskRegManId(String jcTaskRegManId) { this.jcTaskRegManId = jcTaskRegManId; }

    public String getJcTaskRegMan() { return jcTaskRegMan; }
    public void setJcTaskRegMan(String jcTaskRegMan) { this.jcTaskRegMan = jcTaskRegMan; }

    public Timestamp getJcTaskRegDate() { return jcTaskRegDate; }
    public void setJcTaskRegDate(Timestamp jcTaskRegDate) { this.jcTaskRegDate = jcTaskRegDate; }

    public String getJcTaskStatus() { return jcTaskStatus; }
    public void setJcTaskStatus(String jcTaskStatus) { this.jcTaskStatus = jcTaskStatus; }

    public String getJcTaskShManId() { return jcTaskShManId; }
    public void setJcTaskShManId(String jcTaskShManId) { this.jcTaskShManId = jcTaskShManId; }

    public String getJcTaskShMan() { return jcTaskShMan; }
    public void setJcTaskShMan(String jcTaskShMan) { this.jcTaskShMan = jcTaskShMan; }

    public String getIsProduceGd() { return isProduceGd; }
    public void setIsProduceGd(String isProduceGd) { this.isProduceGd = isProduceGd; }

    public String getProduceGdManId() { return produceGdManId; }
    public void setProduceGdManId(String produceGdManId) { this.produceGdManId = produceGdManId; }

    public String getProduceGdMan() { return produceGdMan; }
    public void setProduceGdMan(String produceGdMan) { this.produceGdMan = produceGdMan; }

    public Timestamp getProduceGdDate() { return produceGdDate; }
    public void setProduceGdDate(Timestamp produceGdDate) { this.produceGdDate = produceGdDate; }

    public String getReportDeliverNum() { return reportDeliverNum; }
    public void setReportDeliverNum(String reportDeliverNum) { this.reportDeliverNum = reportDeliverNum; }

    public String getYwMan() { return ywMan; }
    public void setYwMan(String ywMan) { this.ywMan = ywMan; }

    public String getYwManId2() { return ywManId2; }
    public void setYwManId2(String ywManId2) { this.ywManId2 = ywManId2; }

    public String getYwArea() { return ywArea; }
    public void setYwArea(String ywArea) { this.ywArea = ywArea; }

    public String getBuildingUnitContact() { return buildingUnitContact; }
    public void setBuildingUnitContact(String buildingUnitContact) { this.buildingUnitContact = buildingUnitContact; }

    public String getBuildingUnitContactTel() { return buildingUnitContactTel; }
    public void setBuildingUnitContactTel(String buildingUnitContactTel) { this.buildingUnitContactTel = buildingUnitContactTel; }

    public String getSupervisionUnitContactTel() { return supervisionUnitContactTel; }
    public void setSupervisionUnitContactTel(String supervisionUnitContactTel) { this.supervisionUnitContactTel = supervisionUnitContactTel; }

    public String getSurveyUnitContact() { return surveyUnitContact; }
    public void setSurveyUnitContact(String surveyUnitContact) { this.surveyUnitContact = surveyUnitContact; }

    public String getSurveyUnitContactTel() { return surveyUnitContactTel; }
    public void setSurveyUnitContactTel(String surveyUnitContactTel) { this.surveyUnitContactTel = surveyUnitContactTel; }

    public String getConstructionUnitContact() { return constructionUnitContact; }
    public void setConstructionUnitContact(String constructionUnitContact) { this.constructionUnitContact = constructionUnitContact; }

    public String getConstructionUnitContactTel() { return constructionUnitContactTel; }
    public void setConstructionUnitContactTel(String constructionUnitContactTel) { this.constructionUnitContactTel = constructionUnitContactTel; }

    public String getProjectContactTel() { return projectContactTel; }
    public void setProjectContactTel(String projectContactTel) { this.projectContactTel = projectContactTel; }

    public String getDesignUnitContact() { return designUnitContact; }
    public void setDesignUnitContact(String designUnitContact) { this.designUnitContact = designUnitContact; }

    public String getDesignUnitContactTel() { return designUnitContactTel; }
    public void setDesignUnitContactTel(String designUnitContactTel) { this.designUnitContactTel = designUnitContactTel; }

    public String getJcTaskXdManId() { return jcTaskXdManId; }
    public void setJcTaskXdManId(String jcTaskXdManId) { this.jcTaskXdManId = jcTaskXdManId; }

    public String getJcTaskXdMan() { return jcTaskXdMan; }
    public void setJcTaskXdMan(String jcTaskXdMan) { this.jcTaskXdMan = jcTaskXdMan; }

    public Timestamp getJcTaskXdDate() { return jcTaskXdDate; }
    public void setJcTaskXdDate(Timestamp jcTaskXdDate) { this.jcTaskXdDate = jcTaskXdDate; }

    public String getSkNumber() { return skNumber; }
    public void setSkNumber(String skNumber) { this.skNumber = skNumber; }

    public String getXcWtNum() { return xcWtNum; }
    public void setXcWtNum(String xcWtNum) { this.xcWtNum = xcWtNum; }

    public String getTestPlace() { return testPlace; }
    public void setTestPlace(String testPlace) { this.testPlace = testPlace; }

    public String getSendBackReason() { return sendBackReason; }
    public void setSendBackReason(String sendBackReason) { this.sendBackReason = sendBackReason; }

    public String getSendBackManId() { return sendBackManId; }
    public void setSendBackManId(String sendBackManId) { this.sendBackManId = sendBackManId; }

    public String getSendBackMan() { return sendBackMan; }
    public void setSendBackMan(String sendBackMan) { this.sendBackMan = sendBackMan; }

    public String getProjectRemarks() { return projectRemarks; }
    public void setProjectRemarks(String projectRemarks) { this.projectRemarks = projectRemarks; }

    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }

    public String getYwSource() { return ywSource; }
    public void setYwSource(String ywSource) { this.ywSource = ywSource; }

    public String getBlindNumber() { return blindNumber; }
    public void setBlindNumber(String blindNumber) { this.blindNumber = blindNumber; }

    public Timestamp getJcDate() { return jcDate; }
    public void setJcDate(Timestamp jcDate) { this.jcDate = jcDate; }

    public String getProjectSuperviseCode() { return projectSuperviseCode; }
    public void setProjectSuperviseCode(String projectSuperviseCode) { this.projectSuperviseCode = projectSuperviseCode; }

    public String getContractPhase() { return contractPhase; }
    public void setContractPhase(String contractPhase) { this.contractPhase = contractPhase; }

    public String getClientSerialNum() { return clientSerialNum; }
    public void setClientSerialNum(String clientSerialNum) { this.clientSerialNum = clientSerialNum; }

    public String getPqtPlanNo() { return pqtPlanNo; }
    public void setPqtPlanNo(String pqtPlanNo) { this.pqtPlanNo = pqtPlanNo; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getJcTaskTjManId() { return jcTaskTjManId; }
    public void setJcTaskTjManId(String jcTaskTjManId) { this.jcTaskTjManId = jcTaskTjManId; }

    public String getJcTaskTjMan() { return jcTaskTjMan; }
    public void setJcTaskTjMan(String jcTaskTjMan) { this.jcTaskTjMan = jcTaskTjMan; }

    public Timestamp getJcTaskTjDate() { return jcTaskTjDate; }
    public void setJcTaskTjDate(Timestamp jcTaskTjDate) { this.jcTaskTjDate = jcTaskTjDate; }

    public String getZsUnit() { return zsUnit; }
    public void setZsUnit(String zsUnit) { this.zsUnit = zsUnit; }

    public String getZsUnitAddress() { return zsUnitAddress; }
    public void setZsUnitAddress(String zsUnitAddress) { this.zsUnitAddress = zsUnitAddress; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getNcCode() { return ncCode; }
    public void setNcCode(String ncCode) { this.ncCode = ncCode; }

    public String getWitnessIdCard() { return witnessIdCard; }
    public void setWitnessIdCard(String witnessIdCard) { this.witnessIdCard = witnessIdCard; }

    public String getSamplingManIdCard() { return samplingManIdCard; }
    public void setSamplingManIdCard(String samplingManIdCard) { this.samplingManIdCard = samplingManIdCard; }

    public String getRecordCustomer() { return recordCustomer; }
    public void setRecordCustomer(String recordCustomer) { this.recordCustomer = recordCustomer; }

    public String getRecordNc() { return recordNc; }
    public void setRecordNc(String recordNc) { this.recordNc = recordNc; }

    public String getIncomeCostType() { return incomeCostType; }
    public void setIncomeCostType(String incomeCostType) { this.incomeCostType = incomeCostType; }

    public String getYpCode() { return ypCode; }
    public void setYpCode(String ypCode) { this.ypCode = ypCode; }

    public String getMediationContract() { return mediationContract; }
    public void setMediationContract(String mediationContract) { this.mediationContract = mediationContract; }

    public String getMediationDiscount() { return mediationDiscount; }
    public void setMediationDiscount(String mediationDiscount) { this.mediationDiscount = mediationDiscount; }

    // 原有无关字段
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPage() { return page; }
    public void setPage(String page) { this.page = page; }

    public String getTotalPages() { return totalPages; }
    public void setTotalPages(String totalPages) { this.totalPages = totalPages; }

    public String getSampleQuantity() { return sampleQuantity; }
    public void setSampleQuantity(String sampleQuantity) { this.sampleQuantity = sampleQuantity; }

    public String getRepresentativeBatch() { return representativeBatch; }
    public void setRepresentativeBatch(String representativeBatch) { this.representativeBatch = representativeBatch; }

    public String getSampleDisposal() { return sampleDisposal; }
    public void setSampleDisposal(String sampleDisposal) { this.sampleDisposal = sampleDisposal; }

    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }

    @Override
    public String toString() {
        return "Entrustment{" +
                "unifiedNumber='" + getId() + '\'' +
                ", sampleNumber='" + getWtNum() + '\'' +
                ", clientUnit='" + getClientUnit() + '\'' +
                ", projectName='" + getProjectName() + '\'' +
                ", sampleName='" + getSampleName() + '\'' +
                '}';
    }
}