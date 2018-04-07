package com.huanke.iot.api.wechart.js.util;

import com.huanke.iot.api.wechart.js.vo.WechatDeviceVo;
import com.huanke.iot.api.wechart.js.wechat.WechartUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ObtainProductInfoService {

    @Resource
    private WechartUtil wechartUtil;

    public WechatDeviceVo obtainDeviceInfo(String productId){

        wechartUtil.getAccessToken(false);

    }
}
