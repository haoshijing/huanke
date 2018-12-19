package com.huanke.iot.api.pay.req;

import lombok.Data;

/**
 * 描述:
 * 支付req
 *
 * @author onlymark
 * @create 2018-12-19 上午7:14
 */
@Data
public class PayReq {
    private String openId;
    private Double price;
}
