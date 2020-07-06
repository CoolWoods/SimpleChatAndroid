package com.simplechat.chat;

import java.io.Serializable;
import java.util.Date;

public class Msg implements Serializable {
    public static final  int TYPE_RECEIVED=0;
    public static final  int TYPE_SEND=1;
    private Integer messageId;
    private String username;
    private  String fUsername;
    private String messageContent;
    private Date messageDate;
    private int type;

    public Msg(){}
    public Msg(String username, String fUsername, String messageContent, int type) {
        this.username = username;
        this.fUsername = fUsername;
        this.messageContent = messageContent;
        this.type = type;
    }
    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

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

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date  getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "messageId=" + messageId +
                ", username='" + username + '\'' +
                ", fUsername='" + fUsername + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", messageDate=" + messageDate +
                ", type=" + type +
                '}';
    }
}
