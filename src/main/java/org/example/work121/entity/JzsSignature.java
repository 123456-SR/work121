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
    
    /** 签名图片二进制数据 */
    private byte[] signatureBlob;

    // 无参构造器
    public JzsSignature() {
    }

    // 所有字段的getter/setter方法
    public String getSignatureId() { return signatureId; }
    public void setSignatureId(String signatureId) { this.signatureId = signatureId; }

    public String getUserAccount() { return userAccount; }
    public void setUserAccount(String userAccount) { this.userAccount = userAccount; }

    public byte[] getSignatureBlob() { return signatureBlob; }
    public void setSignatureBlob(byte[] signatureBlob) { this.signatureBlob = signatureBlob; }

    // toString方法
    @Override
    public String toString() {
        return "JzsSignature{" +
                "signatureId='" + signatureId + '\'' +
                ", userAccount='" + userAccount + '\'' +
                '}';
    }
}