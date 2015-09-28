package com.intel.assist.utils;

import com.intel.assist.exception.OperateFailureException;
import com.intel.assist.exception.ParamCheckException;
import net.sf.xsshtmlfilter.HTMLFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: malone
 * Date: 13-10-21
 * Time: 下午4:54
 * To change this template use File | Settings | File Templates.
 */
public class WebUtils {
    /**
     * 获取int值
     * @param request
     * @param paramName
     * @param defaultValue
     * @return
     */
    public static int getIntValue(HttpServletRequest request, String paramName, int defaultValue, boolean notNull)
            throws ParamCheckException {
        return getValue(request, paramName, defaultValue, notNull);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(HttpServletRequest request, String paramName, T defaultValue, boolean notNull)
            throws ParamCheckException {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null) {
            //过滤Scripting_Attack, Stored_XSS攻击
            paramValue = new HTMLFilter().filter(paramValue);
            //paramValue = TransactSQLInjection(paramValue);
        }
        checkValue(paramName, paramValue, notNull);
        if (paramValue != null) {
            if (defaultValue instanceof Integer) {
                isInteger(paramValue);
                return (T)(Integer.valueOf(paramValue));
            }
            if (defaultValue instanceof Double) {
                isDouble(paramValue);
                return (T)(Double.valueOf(paramValue));
            }
            if (defaultValue instanceof Long) {
                isLong(paramValue);
                return (T)(Long.valueOf(paramValue));
            }
            if (defaultValue instanceof Float) {
                isFloat(paramValue);
                return (T)(Float.valueOf(paramValue));
            }
            if (defaultValue instanceof String) {
                return (T)(paramValue);
            }
        }
        return defaultValue;
    }

    /**
     * 辅助方法
     * @param paramValue
     * @param notNull
     */
    private static void checkValue(String paramName, String paramValue, boolean notNull) throws ParamCheckException {
        if (notNull && paramValue == null) {
            throw new ParamCheckException("请传递参数：" + paramName);
        }
    }

    /**
     * 获取int值
     * @param request
     * @param paramName
     * @return
     */
    public static int getIntValue(HttpServletRequest request, String paramName) throws ParamCheckException {
        return getIntValue(request, paramName, -1, false);
    }

    /**
     * 获取int值
     * @param request
     * @param paramName
     * @param defaultValue
     * @return
     */
    public static int getIntValue(HttpServletRequest request, String paramName, int defaultValue) throws ParamCheckException {
        return getIntValue(request, paramName, defaultValue, false);
    }

    /**
     * 获取int值
     * @param request
     * @param paramName
     * @param notNull
     * @return
     */
    public static int getIntValue(HttpServletRequest request, String paramName, boolean notNull) throws ParamCheckException {
        return getIntValue(request, paramName, -1, notNull);
    }

    /**
     * 返回字符串，默认返回""
     * @param request
     * @param paramName
     * @return
     */
    public static String getStringValue(HttpServletRequest request, String paramName) throws ParamCheckException {
        return getStringValue(request, paramName, false);
    }

    /**
     * 获取String值
     * @param request
     * @param paramName
     * @param notNull
     * @return
     */
    public static String getStringValue(HttpServletRequest request, String paramName, boolean notNull)
            throws ParamCheckException {
        return getValue(request, paramName, "", notNull);
    }

    /**
     * 获取double值
     * @param request
     * @param paramName
     * @param defaultValue
     * @return
     */
    public static double getDoubleValue(HttpServletRequest request, String paramName, double defaultValue)
            throws ParamCheckException {
        return getDoubleValue(request, paramName, defaultValue, false);
    }

    /**
     * 获取double值
     * @param request
     * @param paramName
     * @param defaultValue
     * @return
     */
    public static double getDoubleValue(HttpServletRequest request, String paramName, double defaultValue, boolean notNull)
            throws ParamCheckException {
        return getValue(request, paramName, defaultValue, notNull);
    }

    public static double getDoubleValue(HttpServletRequest request, String paramName,  boolean notNull)
            throws ParamCheckException {
        return getDoubleValue(request, paramName, -1, notNull);
    }

    /**
     * 获取double值
     * @param request
     * @param paramName
     * @return
     */
    public static double getDoubleValue(HttpServletRequest request, String paramName)
            throws ParamCheckException {
        return getDoubleValue(request, paramName, 0);
    }

    /**
     * 获取boolean值
     * @param request
     * @param paramName
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanValue(HttpServletRequest request, String paramName, boolean defaultValue)
            throws ParamCheckException {
        return getBooleanValue(request, paramName, defaultValue, false);
    }

    /**
     * 获取boolean值
     * @param request
     * @param paramName
     * @param defaultValue
     * @param notNull
     * @return
     */
    public static boolean getBooleanValue(HttpServletRequest request, String paramName, boolean defaultValue, boolean notNull)
            throws ParamCheckException {
        String paramValue = request.getParameter(paramName);
        checkValue(paramName, paramValue, notNull);
        if (paramValue == null) {
            return defaultValue;
        }
        if (paramValue.equals("true")) {
            return true;
        }
        if (paramValue.equals("false")) {
            return false;
        }
        if (paramValue.equals("1")) {
            return true;
        }
        if (paramValue.equals("0")) {
            return false;
        }
        return false;
    }

    public static Long getLongValue(HttpServletRequest request, String paramName, long defaultValue, boolean notNull)
            throws ParamCheckException {
        return getValue(request, paramName, defaultValue, notNull);
    }

    /**
     * 获取boolean值
     * @param request
     * @param paramName
     * @return
     */
    public static boolean getBooleanValue(HttpServletRequest request, String paramName) throws ParamCheckException {
        return getBooleanValue(request, paramName, false);
    }

    /**
     * 对base64加密的字符串进行解码
     * @param str
     * @return
     */
    public static String decodeFromBase64(String str) throws UnsupportedEncodingException {
        if (str == null) {
            throw new UnsupportedEncodingException();
        }
        return new String(Base64Utils.decode(str), "UTF-8");
    }

    /**
     *  验证字符串长度
     * @param str
     * @param length
     * @throws OperateFailureException
     */
    public static void  checkStrLength(String str, int length) throws OperateFailureException{
        if (str == null) {
            throw new OperateFailureException("字符串不能为空！");
        }
        if (str.length() > length) {
            throw new OperateFailureException("字符串长度不能超过" + length);
        }
    }

    /**
     *  验证字符串是否是整形数字
     * @param str
     */
    public static void isInteger(String str) {
        try {
            Integer.valueOf(str);
        } catch (NumberFormatException e) {
            throw new OperateFailureException("该字符串应该为整形数字！");
        }
    }

    /**
     *  验证字符串是否是浮点型数字
     * @param str
     */
    public static void isDouble(String str) {
        try {
            Double.valueOf(str);
        } catch (NumberFormatException e) {
            throw new OperateFailureException("该字符串应该为浮点型数字！");
        }
    }

    /**
     *  判断字符串是否为长整形数字
     * @param str
     */
    public static void isLong(String str) {
        try {
            Long.valueOf(str);
        } catch (NumberFormatException e) {
            throw new OperateFailureException("该字符串应该为长整形数字！");
        }
    }

    /**
     * 浮点型数字
     * @param str
     */
    public static void isFloat(String str) {
        try {
            Float.valueOf(str);
        } catch (NumberFormatException e) {
            throw new OperateFailureException("该字符串应该为浮点型数字！");
        }
    }

}
