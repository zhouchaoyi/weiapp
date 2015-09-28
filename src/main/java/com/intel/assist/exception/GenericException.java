package com.intel.assist.exception;

import com.intel.assist.utils.Consts;

/**
 * 所有异常的基类
 * Created with IntelliJ IDEA.
 * User: malone
 * Date: 13-12-10
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
public class GenericException extends RuntimeException {

    /**
     *  具体的异常信息
     */
    private String msg;

    /**
     *  异常对应的错误码(约定)
     */
    private String errorCode;

    protected void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    GenericException(String msg) {
        this.msg = msg;
        this.errorCode = Consts.COMMON_ERROR_CODE;
    }

    GenericException(String msg, String errorCode) {
        this.msg = msg;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
