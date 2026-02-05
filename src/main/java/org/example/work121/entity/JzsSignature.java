package org.example.work121.entity;

import java.io.Serializable;

/**
 * 签字存放表实体类
 * 映射数据库 JZS_SIGNATURE 表
 */
public class JzsSignature implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 签名ID */
    private String signatureId;
    
    /** 用户账号 */
    private String userAccount;

    /** 签名类型 */
    private String signatureType;
    
    /** 签名图片二进制数据 */
    private byte[] signatureBlob;

    /** 图片类型 */
    private String imageType;

    /** 图片大小 */
    private Long imageSize;

    /** 创建时间 */
    private java.util.Date createTime;

    /** 更新时间 */
    private java.util.Date updateTime;

    /** 备注 */
    private String remarks;

    // 无参构造器
    public JzsSignature() {
    }

    // 所有字段的getter/setter方法
    public String getSignatureId() { return signatureId; }
    public void setSignatureId(String signatureId) { this.signatureId = signatureId; }

    public String getUserAccount() { return userAccount; }
    public void setUserAccount(String userAccount) { this.userAccount = userAccount; }

    public String getSignatureType() { return signatureType; }
    public void setSignatureType(String signatureType) { this.signatureType = signatureType; }

    public byte[] getSignatureBlob() { return signatureBlob; }
    public void setSignatureBlob(byte[] signatureBlob) { this.signatureBlob = signatureBlob; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public Long getImageSize() { return imageSize; }
    public void setImageSize(Long imageSize) { this.imageSize = imageSize; }

    public java.util.Date getCreateTime() { return createTime; }
    public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }

    public java.util.Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(java.util.Date updateTime) { this.updateTime = updateTime; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    // toString方法
    @Override
    public String toString() {
        return "JzsSignature{" +
                "signatureId='" + signatureId + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", signatureType='" + signatureType + '\'' +
                '}';
    }
}