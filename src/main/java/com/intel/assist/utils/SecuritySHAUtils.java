package com.intel.assist.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2014/12/17.
 */
public class SecuritySHAUtils {
    public static byte[] getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());

    }

    public static String bin2hex(String strForEncrypt) {
        byte[] data = getHash(strForEncrypt);
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
}
