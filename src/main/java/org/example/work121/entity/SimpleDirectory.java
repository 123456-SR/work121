package org.example.work121.entity;

/**
 * 极简目录表实体类
 */
public class SimpleDirectory extends BaseEntity {

    /** 目录唯一标识 */
    private String dirId;

    /** 目录名称 */
    private String dirName;

    /** 关联表1类型 */
    private String table1Type;

    /** 关联表1主键ID */
    private String table1Id;

    /** 关联表2类型 */
    private String table2Type;

    /** 关联表2主键ID */
    private String table2Id;

    /** 关联表3类型 */
    private String table3Type;

    /** 关联表3主键ID */
    private String table3Id;

    /** 关联表4类型 */
    private String table4Type;

    /** 关联表4主键ID */
    private String table4Id;

    /** 关联表5类型 */
    private String table5Type;

    /** 关联表5主键ID */
    private String table5Id;

    /** 关联表6类型 */
    private String table6Type;

    /** 关联表6主键ID */
    private String table6Id;

    /** 关联表7类型 */
    private String table7Type;

    /** 关联表7主键ID */
    private String table7Id;

    /** 关联表8类型 */
    private String table8Type;

    /** 关联表8主键ID */
    private String table8Id;

    /** 关联表9类型 */
    private String table9Type;

    /** 关联表9主键ID */
    private String table9Id;

    /** 关联表10类型 */
    private String table10Type;

    /** 关联表10主键ID */
    private String table10Id;

    /** 状态：6个数字状态（0-5） */
    private Integer status;

    /** 检测人 (Deprecated) */
    private String tester;

    /** 复核人 (Deprecated) */
    private String reviewer;

    /** 批准人 (Deprecated) */
    private String approver;

    // --- New Granular Roles ---

    /** 委托承接人 */
    private String wtUndertaker;

    /** 委托审核人 */
    private String wtReviewer;

    /** 记录表填写人 */
    private String jcFiller;

    /** 记录表检验人 */
    private String jcTester;

    /** 记录审核人 */
    private String jcReviewer;

    /** 报告检验人 */
    private String bgTester;

    /** 报告审核人 */
    private String bgReviewer;

    /** 报告批准人 */
    private String bgApprover;

    // 无参构造器
    public SimpleDirectory() {
    }

    // New Getters and Setters

    public String getWtUndertaker() {
        return wtUndertaker;
    }

    public void setWtUndertaker(String wtUndertaker) {
        this.wtUndertaker = wtUndertaker;
    }

    public String getWtReviewer() {
        return wtReviewer;
    }

    public void setWtReviewer(String wtReviewer) {
        this.wtReviewer = wtReviewer;
    }

    public String getJcFiller() {
        return jcFiller;
    }

    public void setJcFiller(String jcFiller) {
        this.jcFiller = jcFiller;
    }

    public String getJcTester() {
        return jcTester;
    }

    public void setJcTester(String jcTester) {
        this.jcTester = jcTester;
    }

    public String getJcReviewer() {
        return jcReviewer;
    }

    public void setJcReviewer(String jcReviewer) {
        this.jcReviewer = jcReviewer;
    }

    public String getBgTester() {
        return bgTester;
    }

    public void setBgTester(String bgTester) {
        this.bgTester = bgTester;
    }

    public String getBgReviewer() {
        return bgReviewer;
    }

    public void setBgReviewer(String bgReviewer) {
        this.bgReviewer = bgReviewer;
    }

    public String getBgApprover() {
        return bgApprover;
    }

    public void setBgApprover(String bgApprover) {
        this.bgApprover = bgApprover;
    }

    // getter和setter方法
    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getDirId() {
        return dirId;
    }

    public void setDirId(String dirId) {
        this.dirId = dirId;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getTable1Type() {
        return table1Type;
    }

    public void setTable1Type(String table1Type) {
        this.table1Type = table1Type;
    }

    public String getTable1Id() {
        return table1Id;
    }

    public void setTable1Id(String table1Id) {
        this.table1Id = table1Id;
    }

    public String getTable2Type() {
        return table2Type;
    }

    public void setTable2Type(String table2Type) {
        this.table2Type = table2Type;
    }

    public String getTable2Id() {
        return table2Id;
    }

    public void setTable2Id(String table2Id) {
        this.table2Id = table2Id;
    }

    public String getTable3Type() {
        return table3Type;
    }

    public void setTable3Type(String table3Type) {
        this.table3Type = table3Type;
    }

    public String getTable3Id() {
        return table3Id;
    }

    public void setTable3Id(String table3Id) {
        this.table3Id = table3Id;
    }

    public String getTable4Type() {
        return table4Type;
    }

    public void setTable4Type(String table4Type) {
        this.table4Type = table4Type;
    }

    public String getTable4Id() {
        return table4Id;
    }

    public void setTable4Id(String table4Id) {
        this.table4Id = table4Id;
    }

    public String getTable5Type() {
        return table5Type;
    }

    public void setTable5Type(String table5Type) {
        this.table5Type = table5Type;
    }

    public String getTable5Id() {
        return table5Id;
    }

    public void setTable5Id(String table5Id) {
        this.table5Id = table5Id;
    }

    public String getTable6Type() {
        return table6Type;
    }

    public void setTable6Type(String table6Type) {
        this.table6Type = table6Type;
    }

    public String getTable6Id() {
        return table6Id;
    }

    public void setTable6Id(String table6Id) {
        this.table6Id = table6Id;
    }

    public String getTable7Type() {
        return table7Type;
    }

    public void setTable7Type(String table7Type) {
        this.table7Type = table7Type;
    }

    public String getTable7Id() {
        return table7Id;
    }

    public void setTable7Id(String table7Id) {
        this.table7Id = table7Id;
    }

    public String getTable8Type() {
        return table8Type;
    }

    public void setTable8Type(String table8Type) {
        this.table8Type = table8Type;
    }

    public String getTable8Id() {
        return table8Id;
    }

    public void setTable8Id(String table8Id) {
        this.table8Id = table8Id;
    }

    public String getTable9Type() {
        return table9Type;
    }

    public void setTable9Type(String table9Type) {
        this.table9Type = table9Type;
    }

    public String getTable9Id() {
        return table9Id;
    }

    public void setTable9Id(String table9Id) {
        this.table9Id = table9Id;
    }

    public String getTable10Type() {
        return table10Type;
    }

    public void setTable10Type(String table10Type) {
        this.table10Type = table10Type;
    }

    public String getTable10Id() {
        return table10Id;
    }

    public void setTable10Id(String table10Id) {
        this.table10Id = table10Id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
