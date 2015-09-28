package com.intel.assist.exception;

/**
 * 此异常专供销量上传与其他门店条码重复时使用
 * Created with IntelliJ IDEA.
 * User: malone
 * Date: 13-12-10
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class SaleDataRepeateException extends GenericException {

    /**
     * 已上传销量记录Id
     */
    private int saleDataId;

    public SaleDataRepeateException(String msg) {
        super(msg);
    }

    public SaleDataRepeateException(String msg, String errorCode) {
        super(msg, errorCode);
    }

    public SaleDataRepeateException (String msg, String errorCode, int saleDataId) {
        super(msg, errorCode);
        this.saleDataId = saleDataId;
    }

    public int getSaleDataId() {
        return saleDataId;
    }
}
