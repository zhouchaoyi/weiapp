package com.intel.assist.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommonUtils {


    public static Map<String, String> getSubStatus(Boolean success, String errorCode, String errorMsg) {
        Map<String, String> subStatus = new HashMap<String, String>();
        subStatus.put("success", success.toString());
        subStatus.put("errorCode", errorCode);
        subStatus.put("errorMsg", errorMsg);
        return subStatus;
    }


    public static Map<String, String> getSubStatus(String errorMsg) {
        return getSubStatus(true, Consts.COMMON_SUCCESS_CODE, errorMsg);
    }


    public static Map<String, String> getSubStatus(String errorCode, String errorMsg) {
        return getSubStatus(true, errorCode, errorMsg);
    }


    public static <T> String collectionToStr (Collection<T> collection) {
        StringBuilder result = new StringBuilder("");
        if (collection == null) {
            return result.toString();
        }
        T[] array = (T[])collection.toArray();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i != array.length -1) {
                result.append(",");
            }
        }
        return result.toString();
    }


    public static String randomAccessSixNum () {
        return String.valueOf(nextInt(100000, 999999));
    }


    public static int nextInt(final int min, final int max) {
        Random rand = null;
        try {
            rand = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            //throw new OperateFailureException("");
        }
        int tmp = Math.abs(rand.nextInt());
        return tmp % (max - min + 1) + min;
    }

}
