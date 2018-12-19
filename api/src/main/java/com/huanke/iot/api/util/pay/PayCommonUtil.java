package com.huanke.iot.api.util.pay;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-12-17 下午4:21
 */
public class PayCommonUtil {
    /**
     * 是否签名正确,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public static boolean isTenpaySign(String characterEncoding, SortedMap<Object, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set<?> es = packageParams.entrySet();
        Iterator<?> it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if(!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }

        sb.append("key=" + API_KEY);

        //算出摘要
        String mysign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toLowerCase();
        String tenpaySign = ((String)packageParams.get("sign")).toLowerCase();

        //System.out.println(tenpaySign + "    " + mysign);
        return tenpaySign.equals(mysign);
    }

    /**
     * @Description：sign签名
     * @param characterEncoding
     *          编码格式
     * @param packageParams
     *          请求参数
     * @param API_KEY
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String createSign(String characterEncoding, Map<Object, Object> packageParams, String API_KEY, Integer encryptionType) {
        StringBuffer sb = new StringBuffer();
        Set<?> es = packageParams.entrySet();
        Iterator<?> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
//            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k) && !"notify_url".equals(k)&& !"out_trade_no".equals(k)&& !"spbill_create_ip".equals(k)&& !"total_fee".equals(k)&& !"trade_type".equals(k)) {
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k) ) {
                sb.append(k + "=" + v + "&");
            }
        }
        String sign;
//        String stringA="appid=wxc7b425229b570867&mch_id=1406330002&nonce_str=1702585759&key=ab42e0b7aa6bce35164a2d14855d7264";
        if(encryptionType == 1){
            //MD5加密
            sb.append("key=" + API_KEY);
            System.out.println("sb.toString()->" + sb.toString());
            sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        }else{
            //SHA1加密
            sb.deleteCharAt(sb.length()-1);
            System.out.println("sb.toString()->" + sb.toString());
            sign = SHA1.encode(sb.toString());
        }
//        String sign = MD5Util.MD5Encode(stringA, characterEncoding).toUpperCase();
//        System.out.println(sign);
//        System.out.println(MD5Util.MD5Encode("appid=wxd930ea5d5a258f4f&body=test&device_info=1000&mch_id=10000100&nonce_str=ibuaiVcKdpRxkhJA&key=192006250b4c09247ec02edce69f6a2d", characterEncoding).toUpperCase());
//        System.out.println(sign);
        return sign;
    }

    /**
     * @author
     * @date 2016-4-22
     * @Description：将请求参数转换为xml格式的string
     * @param parameters
     *            请求参数
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getRequestXml(Map<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<?> es = parameters.entrySet();
        Iterator<?> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length
     *            int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    /**
     * 获取当前时间 yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }
}
