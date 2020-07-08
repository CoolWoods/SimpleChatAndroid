package com.simplechat.domain;

import java.io.Serializable;

public class Contact implements Serializable {
    private String username;
    private String fUsername;
    private  String remark;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFUsername() {
        return fUsername;
    }

    public void setFUsername(String fUsername) {
        this.fUsername = fUsername;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "username='" + username + '\'' +
                ", fUsername='" + fUsername + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
