package com.huanke.iot.api.util;

/*
 * 微信公众平台(JAVA) SDK
 *
 * Copyright (c) 2016, Ansitech Network Technology Co.,Ltd All rights reserved.
 * http://www.ansitech.com/weixin/sdk/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * <p>Title: SHA1算法</p>
 *
 * @author levi
 */
@Slf4j
public final class SignUtil {

    private static final String token = "Huanke";
    public static boolean checkSignature(String signature, String timestamp,
                                         String nonce) {
        String[] arra = new String[]{token,timestamp,nonce};
        //将signature,timestamp,nonce组成数组进行字典排序
        Arrays.sort(arra);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<arra.length;i++){
            sb.append(arra[i]);
        }
        MessageDigest md = null;
        String stnStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(sb.toString().getBytes());
            stnStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("",e);
        }
        //释放内存
        sb = null;
        //将sha1加密后的字符串与signature对比，标识该请求来源于微信
        return stnStr!=null?stnStr.equals(signature.toUpperCase()):false;
    }
    /**
     * 将字节数组转换成十六进制字符串
     * @param digestArra
     * @return
     */
    private static String byteToStr(byte[] digestArra) {
        // TODO Auto-generated method stub
        String digestStr = "";
        for(int i=0;i<digestArra.length;i++){
            digestStr += byteToHexStr(digestArra[i]);
        }
        return digestStr;
    }
    /**
     * 将字节转换成为十六进制字符串
     */
    private static String byteToHexStr(byte dByte) {
        char[] Digit = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] tmpArr = new char[2];
        tmpArr[0] = Digit[(dByte>>>4)&0X0F];
        tmpArr[1] = Digit[dByte&0X0F];
        String s = new String(tmpArr);
        return s;
    }

}
