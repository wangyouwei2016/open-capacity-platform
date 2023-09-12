package com.open.capacity.uaa.util;


import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 工具类
 *
 * @author xh
 * @date 2022年9月11日17:44:13
 */
@Slf4j
public class RsaTools {

    public static void main(String[] args) {
        Map<String,String> dataMap = new HashMap<>();
        // 生成token
        dataMap.put("client_id","yqfk_pt");
        dataMap.put("grant_type","authorization_code");
        dataMap.put("auth_code","9lM-d3");
        Map<String,String> dataMap1 = new HashMap<>();
        // 获取用户信息
        dataMap1.put("client_id","yqfk_pt");
        dataMap1.put("auth_token","56dfe3fd-35b1-4fb4-982d-bec86643ad5c");

        Map<String,String> dataMap2 = new HashMap<>();
        // 刷新token
        dataMap2.put("client_id","yqfk_pt");
        dataMap2.put("grant_type","refresh_token");
        dataMap2.put("refresh_token","a1aac4ec-67c5-42af-96b6-02f1e4e31da2");
        String privateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAjpOegX3iFZ1KrZjPtQPqhP7IxCUCV+PetmWwucW26B01y/aDIYBXHat3iAI8V/k/Nh3L+DGHSjd7fq+HepaLawIDAQABAkB3zakKDFoWaXYfyuD1rNW6bVEMKEEA01QIFgjbAT0BapDy9mnS+v8HXpLYEmxgVjPLBqAXOs5R3H3x5WlnVgNxAiEAwo8Ks4TcncAZ9a3vg3d3FEsl4WiW0Q1Tq6fMj3k5B5kCIQC7mh8bGusERx8hGY4CNPffWhk/OoiKybIV4mi6vbl9owIgGykW6FrK5abUuOxwPF0oHYgkIKSWMVb38EW7k+frYzkCIGpX3VYiFiLgpha/Q66gk/n6OuGKrqft2ZNqOm/Q6Hr9AiBD0UPq2tXJHlVvetTDOS4TM9K1KR+WsSs+oT3BqmOyIw==";
        System.out.println(getRsaSign(privateKey, dataMap));
        System.out.println(getRsaSign(privateKey, dataMap1));
        System.out.println(getRsaSign(privateKey, dataMap2));
    }

    /**
     * 检测字符串是否不为空(null,"","null")
     *
     * @param s
     * @return 不为空则返回true，否则返回false
     */
    public static boolean notEmpty(String s) {
        return s != null && !"".equals(s) && !"null".equals(s) && !"undefined".equals(s);
    }

    /**
     * 检测字符串是否为空(null,"","null")
     *
     * @param s
     * @return 为空则返回true，不否则返回false
     */
    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || "null".equals(s) || "undefined".equals(s);
    }

    /**
     * 生成sign
     * @param privateKey
     * @param paramsMap
     * @return
     */
    public static String getRsaSign (String privateKey, Map<String,String> paramsMap) {
        String rsaSign = "";
        try {
            String sortParams = JSON.toJSONString(paramsMap, SerializerFeature.WriteMapNullValue, SerializerFeature.MapSortField);
            String encryptParams = md5(sortParams).toUpperCase();
            System.out.println(encryptParams);
            rsaSign = encrypt(privateKey, encryptParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsaSign;
    }

    /**
     * RSA解密 -druid.version 1.2.9 ConfigTools RAS
     * @param publicKey
     * @param value
     * @return
     */
    public static String descrypt (String publicKey, String value) throws Exception {
        if (StringUtils.isNotEmpty(publicKey)
                && StringUtils.isNotEmpty(value)) {
            value = ConfigTools.decrypt(publicKey, value);
        }
        return value;
    }

    /**
     * RSA 加密
     * @param privateKey
     * @param value
     * @return
     */
    public static String encrypt (String privateKey, String value) throws Exception {
        String encryptData = "";
        if (StringUtils.isNotEmpty(privateKey)
                && StringUtils.isNotEmpty(value)) {
            encryptData = ConfigTools.encrypt(privateKey, value);
        }
        return encryptData;
    }

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return str;
    }

    /***
     * 加密字符串。
     * @param inStr String 需要加密处理的字符串
     * @return String 加密后的字符串
     * @throws Exception
     */
    public static String encrypt(String inStr) throws Exception {
        MessageDigest md = null;
        String out = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(inStr.getBytes());
            out = byte2hex(digest);
            //out = (new sun.misc.BASE64Encoder()).encode(out);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
        return out;
    }

    /**
     * 字节转换成十六进制
     * @param b byte[]
     * @return String 十六进制字符串
     */
    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
}


