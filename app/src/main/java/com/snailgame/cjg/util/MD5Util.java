package com.snailgame.cjg.util;

import android.text.TextUtils;

import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String md5Calc(File f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int length = -1;
            if (md == null) {
                return null;
            }
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            fis.close();
            byte[] bytes = md.digest();


            if (bytes == null) {
                return null;
            }
            StringBuilder buf = new StringBuilder();
            for (byte aByte : bytes) {
                String md5s = Integer.toHexString(aByte & 0xff);
                if (md5s == null) {
                    return null;
                }
                if (md5s.length() == 1) {
                    buf.append("0");
                }
                buf.append(md5s);
            }
            return buf.toString();

        } catch (Exception e) {
            LogUtils.e(e.getMessage());
            return null;
        }
    }

    public synchronized static boolean checkMD5(String md5M, String fileName) {
        if (TextUtils.isEmpty(md5M)) {
            return false;
        }
        if (TextUtils.isEmpty(fileName)) return false;

        String md5N;
        File f = new File(fileName);
        if (f.exists()) {
            md5N = MD5Util.md5Calc(f);
            if (!TextUtils.isEmpty(md5N) && !md5M.toLowerCase().equals(md5N.toLowerCase()))
                return false;
        } else {
            return false;
        }
        return true;
    }


    /**
     * MD5加密
     *
     * @param plainText
     * @return
     */
    public static String md5Encrypt(String plainText) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e(e.getMessage());
        }
        md.update(plainText.getBytes());
        byte[] b = md.digest();
        StringBuilder output = new StringBuilder(32);
        for (byte aB : b) {
            String temp = Integer.toHexString(aB & 0xff);
            if (temp.length() < 2) {
                output.append("0");
            }
            output.append(temp);
        }
        return output.toString();
    }
}
