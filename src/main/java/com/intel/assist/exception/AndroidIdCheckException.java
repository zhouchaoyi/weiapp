package com.intel.assist.exception;

/**
 * 操作错误exception
 * Created with IntelliJ IDEA.
 * User: malone
 * Date: 13-12-10
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class AndroidIdCheckException extends GenericException {

    public AndroidIdCheckException(String msg) {
        super(msg);
    }

    public AndroidIdCheckException(String msg, String errorCode) {
        super(msg, errorCode);
    }

}
