package com.neuedu.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MD5Utils {
    //全局数组
    private final static String[] strDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    public MD5Utils() {
    }

    private static String byteToArrayString(byte bByte) {


        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    //返回形式只为数字
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }
    //转换字节数组为16进制字串
    private static String byteToString(byte[] bByte){
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    /**
     * 加密
     */
    public static String getMD5Code(String strObj){
        String resultString = null;
        try {
            resultString = new String(strObj+"businesssdafaqj23ou89ZXcj@#$@#$#@KJdjklj;D../dSF.,&%@");
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteToString(md.digest(resultString.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return resultString;
    }




}
