package com.intel.assist.model.entity;

import java.util.Date;

/**
 * Created by kunpeng on 2015/6/24.
 */
public class FileDownload {

    private int downloadId;
    private int sendId;
    private Date downloadDate;
    private int status;

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public int getSendId() {
        return sendId;
    }

    public void setSendId(int sendId) {
        this.sendId = sendId;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
