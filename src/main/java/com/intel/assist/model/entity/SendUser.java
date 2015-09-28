package com.intel.assist.model.entity;

import java.util.Date;

/**
 * Created by kunpeng on 2015/6/24.
 */
public class SendUser {

    private int sendId;
    private String userId;
    private String storeId;
    private String userType;
    private int fileId;
    private int status;
    private Date sendDate;

    public int getSendId() {
        return sendId;
    }

    public void setSendId(int sendId) {
        this.sendId = sendId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
}
