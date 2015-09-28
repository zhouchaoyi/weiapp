package com.intel.assist.model.entity;

/**
 * Created by kunpeng on 2015/6/29.
 */
public class LoginUser {

    private String userName;
    private char[] pwd;
    private String wwid;
    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public char[] getPwd() {
        return pwd;
    }

    public void setPwd(char[] pwd) {
        this.pwd = pwd;
    }

    public String getWwid() {
        return wwid;
    }

    public void setWwid(String wwid) {
        this.wwid = wwid;
    }
}
