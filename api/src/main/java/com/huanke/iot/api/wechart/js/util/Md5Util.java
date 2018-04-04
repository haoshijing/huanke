package com.huanke.iot.api.wechart.js.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Md5Util {
    public static String sha1(String src) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        // 将三个参数字符串拼接成一个字符串进行sha1加密
        byte[] digest = md.digest(src.getBytes());

        return byteToStr(digest);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
