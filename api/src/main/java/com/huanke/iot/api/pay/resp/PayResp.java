package com.huanke.iot.api.pay.resp;

import lombok.Data;

/**
 * 描述:
 * 支付返回rsp
 *
 * @author onlymark
 * @create 2018-12-19 上午8:58
 */
@Data
public class PayResp {
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String package1;
    private String signType;
    private String paySign;
}
