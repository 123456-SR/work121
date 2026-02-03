package org.example.work121.entity;

import java.io.Serializable;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // userId 映射到 BaseEntity.id
    
    private String userAccount;
    private String userPass;
    private String userStatus;
    private String userName;

    public String getUserId() {
        return getId();
    }

    public void setUserId(String userId) {
        setId(userId);
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}