package com.simplechat.ui.message.domain;

import java.io.Serializable;
import java.util.Date;

public class MessageListItem implements Serializable {
    private String username;
    private  String nickname;
    private String fUsername;
    private String head;
    private String lastMsg;
    private Date lastMsgDate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFUsername() {
        return fUsername;
    }

    public void setFUsername(String fUsername) {
        this.fUsername = fUsername;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Date getLastMsgDate() {
        return lastMsgDate;
    }

    public void setLastMsgDate(Date lastMsgDate) {
        this.lastMsgDate = lastMsgDate;
    }
}
