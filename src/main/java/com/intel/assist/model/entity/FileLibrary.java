package com.intel.assist.model.entity;

import java.util.Date;

/**
 * Created by kunpeng on 2015/6/24.
 */
public class FileLibrary extends BusinessEntity{

    public final static String FILE_ID="id";
    public final static String FILE_SIZE="size";
    public final static String FILE_CATE="file_cate";
    public final static String FILE_NAME="file_name";
    public final static String FILE_DESC="file_desc";
    public final static String FILE_S_3_PATH="s3_path";
    public final static String FILE_CREATE_DATE="create_date";
    public final static String FILE_STATUS="status";
    public final static String FILE_CREATE_BY="create_by";

    private int fileId;
    private int fileSize;
    private int fileCate;
    private String fileName;
    private String fileDesc;
    private String sfilePath;
    private String createBy;
    private Date createDate;
    private int status;

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileCate() {
        return fileCate;
    }

    public void setFileCate(int fileCate) {
        this.fileCate = fileCate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public String getSfilePath() {
        return sfilePath;
    }

    public void setSfilePath(String sfilePath) {
        this.sfilePath = sfilePath;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
