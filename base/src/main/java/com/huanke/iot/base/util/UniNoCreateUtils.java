package com.huanke.iot.base.util;


import com.huanke.iot.base.constant.DeviceConstant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 描述:
 * 创建唯一标识工具类
 *
 * @author onlymark
 * @create 2018-10-26 下午4:15
 */
public class UniNoCreateUtils {
    private static final String PRE_TYPE = "HTY";
    private static final String PRE_MODEL = "HMD";
    private static final String PRE_DEVICE = "HDE";

    public static String createNo(Integer type) {
        StringBuilder sb = new StringBuilder();
        switch (type){
            case DeviceConstant.DEVICE_UNI_NO_TYPE:
                sb.append(PRE_TYPE).append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).append(randomStr(4));
                break;
            case DeviceConstant.DEVICE_UNI_NO_MODEl:
                sb.append(PRE_MODEL).append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).append(randomStr(4));
                break;
            case DeviceConstant.DEVICE_UNI_NO_DEVICE:
                sb.append(PRE_DEVICE).append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).append(randomStr(4));
                break;
                default:
                    break;
        }
        return sb.toString();
    }

    private static String randomStr(Integer length) {
        char[] chr = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(chr[random.nextInt(52)]);
        }
        return buffer.toString();
    }


}
