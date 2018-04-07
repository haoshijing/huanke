package com.huanke.iot.api.wechart.js.vo;

import lombok.Data;

/*
{"base_resp":{"errcode":0,"errmsg":"ok"},"deviceid":"
gh_7f3ba47c70a3_f1c1cd2015ab27b6",
"qrticket":"http:\/\/we.qq.com\/d\/AQCQ5OPCMCYy3LJzNKVefirvXMbORpw7C0FHi3p4","
devicelicence":"93B69D3D856D0CCF757F7699715A4C2367F182A029018DCA09CFD05144BDF6178E3F4191ABBF735765364841B5480067AA6C6CF4A35ADF7279DC9DC5C024E89362A2DF2FEF81A4E962C61E8092AECE05"}
 */
@Data
public class WechatDeviceVo {
    private BaseResp base_resp;
    @Data
    public static class BaseResp{
        private int errorCode;
        private String errmsg;
    }
    private String deviceid;
    private String qrticket;
    private String devicelicence;
}
